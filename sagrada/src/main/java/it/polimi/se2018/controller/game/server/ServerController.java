package it.polimi.se2018.controller.game.server;

import it.polimi.se2018.controller.game.EventBus;
import it.polimi.se2018.controller.game.server.handlers.RandomDice;
import it.polimi.se2018.controller.network.server.MatchController;
import it.polimi.se2018.controller.network.server.MatchNetworkInterface;
import it.polimi.se2018.events.actions.*;
import it.polimi.se2018.events.messages.*;
import it.polimi.se2018.model.Board;

import it.polimi.se2018.model.ColorModel;
import it.polimi.se2018.model.Player;
import it.polimi.se2018.model.cards.Deck;
import it.polimi.se2018.model.cards.PatternCard;
import it.polimi.se2018.model.cards.PrivateObjectiveCard;
import it.polimi.se2018.util.MatchIdentifier;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static it.polimi.se2018.model.ColorModel.WHITE;


/**
 * Principal class of the controller inside the server
 *
 * @author Alì El Wahsh
 */
public class ServerController implements MatchController, Runnable {

    private final MatchIdentifier mId;
    private final ArrayList<Player> players = new ArrayList<>();
    private final ArrayList<Player> offlinePlayers = new ArrayList<>();
    private final Board board;
    private final Round round;
    private final EventBus inBus;
    private final MatchNetworkInterface network;
    private Timer timer = new Timer();
    private static final int TIME = 90000;
    private final ArrayList<PatternCard> patternSent = new ArrayList<>();
    private int playersReady = 0;
    private final Thread t;
    private final ArrayList<PrivateObjectiveCard> privateObjectiveSent = new ArrayList<>();
    private RandomDice randomDice;
    private final ArrayList<Thread> handlers = new ArrayList<>();


    /**
     * ServerController's constructor
     *
     * @param mId     Match identifier
     * @param network network output
     */
    ServerController(MatchIdentifier mId, MatchNetworkInterface network) {
        this.mId = mId;
        List<String> temp = new ArrayList<>();
        temp.add(mId.player0);
        temp.add(mId.player1);
        temp.add(mId.player2);
        temp.add(mId.player3);
        for (int i = 0; i < mId.playerCount; i++)
            players.add(new Player(temp.get(i), i));


        this.board = new Board();
        this.round = new Round(this.players);
        for (Player p : players) {
            p.setPlacementRights(true, true);
            p.setPlacementRights(false, true);
            p.setCardRights(true, true);
            p.setCardRights(false, true);
        }
        this.inBus = new EventBus();
        new Thread(inBus, "eventBus" + mId.hashCode()).start();
        t = new Thread(this);
        t.start();
        this.network = network;
    }


    /**
     * Starts this match
     */
    private void startMatch() {
        board.rollDiceOnReserve(players.size());
        for (Player p : players)
            sendMatchStatus(p);

        network.sendObj(new MatchStart(false));
        network.sendObj(new TurnStart(null, round.getCurrentPlayer().getName()));
        timer = new Timer();
        timer.schedule(new TimeSUp(round.getCurrentPlayer().getName()), TIME);
        randomDice = new RandomDice();
    }

    /**
     * Timer class to handle turn time limits
     */
    private class TimeSUp extends TimerTask {
        private final String name;

        private TimeSUp(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            if (name.equals(round.getCurrentPlayer().getName())) {
                newTurn();
            }
        }
    }

    @Override
    public void handleRequest(String sender, Serializable req) {
        for (Player p : players) {
            if (!offlinePlayers.contains(p) && p.getName().equals(sender))
                try {
                    managePlayerMove((PlayerMove) req);
                } catch (Exception ex) {
                    Logger.getGlobal().log(Level.WARNING, "Client sent illegal request " + sender + " " + ex.getMessage(), ex);
                }
        }
    }

    @Override
    public void kill() {
        inBus.stopListening();
        t.interrupt();
        timer.cancel();
        clearToolCards();
    }

    @Override
    public void userReconnected(String username) {
        for (Player p : players) {
            if (offlinePlayers.contains(p) && p.getName().equals(username)) {
                offlinePlayers.remove(p);
                network.sendReq(new CardInfo(board.getTools(), board.getObjectives()), p.getName());
                network.sendReq(new PrivateObjectiveStatus(privateObjectiveSent.get(mId.findPos(p.getName()))), p.getName());
                sendMatchStatus(p);

                network.sendReq(new MatchStart(true), username);
                network.sendReq(new TurnStart(null, round.getCurrentPlayer().getName()), username);
            }
        }
    }

    @Override
    public void playerLeft(String username, boolean permanent) {
        for (Player p : players) {
            if (!offlinePlayers.contains(p) && p.getName().equals(username))
                offlinePlayers.add(p);
        }
        if (username.equals(round.getCurrentPlayer().getName()))
            newTurn();
    }


    /**
     * Sets all the necessary flags for a new round and initialize the new order
     * If round 10 has passed, ends the game
     *
     * @param previousPlayer previous player
     */
    private void manageNewRound(String previousPlayer) {

        if (round.getRoundNumber() < 10) {
            for (Player p : players) {
                p.setPlacementRights(true, true);
                p.setPlacementRights(false, true);
                p.setCardRights(true, true);
                p.setCardRights(false, true);
            }
            round.prepareNextRound();
            while (offlinePlayers.contains(round.getCurrentPlayer())) {
                round.nextTurn();
            }
            board.putReserveOnRoundTrack();
            board.rollDiceOnReserve(players.size());
            network.sendObj(new TurnStart(previousPlayer, round.getCurrentPlayer().getName()));
            for (Player p : players)
                sendMatchStatus(p);
            timer = new Timer();
            timer.schedule(new TimeSUp(round.getCurrentPlayer().getName()), TIME);
        } else {
            int[] scores = new int[4];
            for (int i = 0; i < players.size(); i++) {
                scores[i] = scorePlayer(players.get(i));
            }

            network.end(scores[0]
                    , scores[1]
                    , scores[2]
                    , scores[3]);
            inBus.stopListening();
        }
    }

    /**
     * Sets a new turn and if the round is ended, calls manageNewRound
     * Notifies all player of the new turn
     */
    public synchronized void newTurn() {
        inBus.deleteObservers();
        clearToolCards();
        String temp = round.getCurrentPlayer().getName();
        timer.cancel();
        round.nextTurn();
        while (offlinePlayers.contains(round.getCurrentPlayer())) {
            round.nextTurn();
        }
        randomDice = new RandomDice();
        if (round.getCurrentPlayer() == null)
            manageNewRound(temp);
        else {
            if (!round.getFirstTurn()) {
                for (Player p : players)
                    network.sendObj(new PlayerStatus(p, false));
            }
            network.sendObj(new TurnStart(temp, round.getCurrentPlayer().getName()));
            timer = new Timer();
            timer.schedule(new TimeSUp(round.getCurrentPlayer().getName()), TIME);
        }
    }

    /**
     * Handles a generic player move
     * If it's a placement it is handled by a DiePlacementHandler
     * If it's a tool card, by a CardHandler (called bya factory)
     * If it's a pass turn it ends the player turn
     * Else it will be handled by running handlers
     *
     * @param move player move
     */
    private synchronized void managePlayerMove(PlayerMove move) {
        if (mId.findPos(move.getPlayerName()) <= -1 || (
                playersReady != players.size() && !move.toString().equals("Pattern selected"))) return;

        if (playersReady != players.size()) {
            handlePatternCard((PatternChoice) move);
        } else if (move.getPlayerName().equals(round.getCurrentPlayer().getName())) {
            PlayerMoveHandler.handle(this, move, round.getCurrentPlayer(), board, round, network, randomDice);
        }
    }

    /**
     * Handles a pattern card received from client
     *
     * @param choice the PatternChoice that has been received
     */
    private void handlePatternCard(PatternChoice choice) {
        int temp = mId.findPos(choice.getPlayerName());
        if (players.get(temp).getPattern() != null) return;

        for (int i = temp * 2; i < temp * 2 + 2; i++) {
            if (patternSent.get(i).getFrontSide().getName().equals(choice.getPatterName())) {
                players.get(temp).setPattern(patternSent.get(i).getFrontSide());
                players.get(temp).setFavourPoints(patternSent.get(i).getFrontSide().getFavourPoints());
                playersReady++;
            } else if (patternSent.get(i).getBackSide().getName().equals(choice.getPatterName())) {
                players.get(temp).setPattern(patternSent.get(i).getBackSide());
                players.get(temp).setFavourPoints(patternSent.get(i).getBackSide().getFavourPoints());
                playersReady++;
            }
        }
        if (playersReady == players.size()) startMatch();

    }

    /**
     * Sends the status of the match to a single player
     *
     * @param player player to be updated about the status of the match
     */
    private void sendMatchStatus(Player player) {
        for (Player p : players)
            network.sendReq(new PlayerStatus(p, round.getFirstTurn()), player.getName());


        network.sendReq(new ReserveStatus(board.getReserve()), player.getName());
        network.sendReq(new RoundTrackStatus(board.getRoundTrack()), player.getName());

    }

    /**
     * Sends pattern loaded from resources to the players
     */
    private void sendPatternCards() {
        ArrayList<PatternCard> temp = new ArrayList<>();

        try {
            String customDir = "patternCards";
            File jarFile = new File(ServerController.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            String inputFilePath = jarFile.getParent() + File.separator + customDir;
            File node1 = new File(inputFilePath);
            if(!node1.exists() &&!node1.mkdir()){
                throw new IOException("Could not create pattern card directory");
            }
            copyPatterns(inputFilePath);

            if (node1.isDirectory()) {
                String[] subNote = node1.list();
                if (subNote == null) throw new IOException("Error accessing pattern cards");
                for (String filename : subNote) {
                    temp.add(new PatternCard(inputFilePath + File.separator + filename));
                }
            }
        } catch (Exception e) {
            Logger.getGlobal().log(Level.WARNING, e.toString());
        }


        Deck<PatternCard> deck = new Deck<>(temp);
        deck.shuffle();
        patternSent.addAll(deck.draw(mId.playerCount * 2));
        for (Player p : players) {
            for (int i = p.getId() * 2; i < p.getId() * 2 + 2; i++) {
                network.sendReq(new PatternSelect(patternSent.get(i).getFrontSide()), p.getName());
                network.sendReq(new PatternSelect(patternSent.get(i).getBackSide()), p.getName());
            }
        }
    }

    /**
     * Copies the patterns to the external dir
     * @param inputFilePath filename of the external dir
     * @throws IOException if an error occurs
     */
    private void copyPatterns(String inputFilePath) throws IOException {
        for(int i=1;i<=12;i++){
            String filename="patterncard"+((i<10)?"0":"")+i+".xml";
            InputStream iS=getClass().getResourceAsStream("/resources/"+filename);
            Path p= Paths.get(inputFilePath+File.separator+filename);
            if(!p.toFile().exists()){
                Files.copy(iS,p);
            }
        }
    }

    /**
     * Sends to all players their own private objective card
     */
    private void sendPrivateObjectives() {
        ArrayList<PrivateObjectiveCard> temp = new ArrayList<>();
        Stream.of(ColorModel.values()).filter(c -> c != WHITE).forEach(c -> temp.add(new PrivateObjectiveCard(c)));
        Deck<PrivateObjectiveCard> deck = new Deck<>(temp);
        deck.shuffle();

        for (int i = 0; i < players.size(); i++) {
            privateObjectiveSent.add(deck.draw(1).get(0));
            players.get(i).setPrivateObjective(privateObjectiveSent.get(i));
        }

        for (Player p : players) {
            network.sendReq(new PrivateObjectiveStatus(privateObjectiveSent.get(mId.findPos(p.getName()))), p.getName());
        }

    }

    @Override
    public void run() {
        sendReqToAll(new CardInfo(board.getTools(), board.getObjectives()));
        sendPrivateObjectives();
        for (Player p : players)
            sendReqToAll(new PlayerStatus(p, round.getFirstTurn()));



        sendPatternCards();


    }

    /**
     * Sends a request to all clients
     *
     * @param req the request to send
     */
    private void sendReqToAll(Serializable req) {
        players.forEach(c -> network.sendReq(req, c.getName()));
    }

    /**
     * Interrupts all pending handlers and removes them from the list
     */
    private void clearToolCards() {
        try {
            for (Thread hand : handlers) {
                hand.interrupt();
                hand.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        handlers.clear();
    }

    /**
     * Registers a thread in the handler list
     *
     * @param handler the handler to register
     */
    public void registerHandler(Thread handler) {
        handlers.add(handler);
    }

    /**
     * Getter for InBus
     *
     * @return InBus
     */
    public EventBus getInBus() {
        return inBus;
    }

    /**
     * Calculates the score for the given player
     *
     * @param player player to score
     * @return player's score
     */
    private int scorePlayer(Player player) {
        int out;

        out = board.totalScore(player);
        out += player.getFavourPoints();
        out += player.getPrivateObjective().score(player);

        //20- number of placed dice= number of unplaced dice
        out += -20 + player.getGrid().getPlacedDice();

        if (out < 0) out = 0;
        return out;
    }
}