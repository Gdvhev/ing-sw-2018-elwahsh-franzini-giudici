package it.polimi.se2018.controller.game.server.handlers;


import it.polimi.se2018.controller.network.server.MatchNetworkInterface;
import it.polimi.se2018.events.actions.*;
import it.polimi.se2018.events.messages.*;
import it.polimi.se2018.model.Board;
import it.polimi.se2018.model.IntColorPair;
import it.polimi.se2018.model.Player;
import it.polimi.se2018.model.cards.toolcards.*;
import it.polimi.se2018.model.dice.Die;
import it.polimi.se2018.util.Pair;


import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handler for all tool cards effects
 * @author Alì El Wahsh
 */
public class ToolCardsHandler implements Runnable,Observer  {

    private final Board board;
    private final Player player;
    private final UseToolCardMove move;
    private final boolean firsTurn;
    private final MatchNetworkInterface network;
    private static final String NO_PERMISSION = "Non hai i permessi per usare questa carta";
    private static final String TOO_SLOW = "Troppo lento";
    private static final String OH_NO = "Qualcosa è andato storto";
    private static final String DIESET = "DieSet";
    private PlayerMove response;
    private CountDownLatch latch;
    private int cardPosition;
    private final RandomDice randomDice;
    /**
     * Constructor
     * @param move card move
     * @param player player using the card
     * @param board game board
     * @param firstTurn true if first turn, false otherwise
     * @param networkInterface network interface
     */
    public ToolCardsHandler(UseToolCardMove move, Player player, Board board, boolean firstTurn, MatchNetworkInterface networkInterface, RandomDice randomDice)
    {
        this.move = move;
        this.player = player;
        this.board = board;
        this.firsTurn = firstTurn;
        this.network = networkInterface;
        this.randomDice = randomDice;
    }

    /**
     * Notifies failure sending a invalid move message
     * @param error error message
     */
    private void notifyFailure(String error)
    {
        network.sendReq(new InvalidMove(move,error,false),player.getName());
    }

    /**
     * Notifies success sending a confirm move message
     */
    private void notifySuccess()
    {
        network.sendReq(new ConfirmMove(move,false),player.getName());
    }

    /**
     * For each card checks its permission and executes its effect
     */
    private void checksEffect()
    {
        boolean isInGame = false;
        boolean canPlay = false;
        for(int i = 0; i<3; i++)
        {
            if(board.getTool(i).getId() == move.getCardID())
            {
                isInGame = true;
                cardPosition = i;
            }
        }

        if(player.getFavourPoints()>=2 || !board.getTool(cardPosition).isUsed() && player.getFavourPoints()>=1)
            canPlay = true;

        if(isInGame && canPlay) {
            switch (move.getCardID()) {
                case 20:
                    grozingPliers();
                    break;
                case 21:
                    eglomiseBrush();
                    break;
                case 22:
                    copperFoilBurnisher();
                    break;
                case 23:
                    lathekin();
                    break;
                case 24:
                    lensCutter();
                    break;
                case 25:
                    fluxBrush();
                    break;
                case 26:
                    glazinHammer();
                    break;
                case 27:
                    runningPliers();
                    break;
                case 28:
                    corkBackedStraightedge();
                    break;
                case 29:
                    grindingStone();
                    break;
                case 30:
                    fluxRemover();
                    break;
                case 31:
                    tapWheel();
                    break;
                default:
                    notifyFailure("Carta inesistente");
            }
        }
        else
            notifyFailure("Non puoi giocare questa carta");
    }

    @Override
    public void run() {
        checksEffect();
    }

    @Override
    public void update(Observable o, Object arg) {
        if(arg!= null) {
            response = (PlayerMove) arg;
            latch.countDown();
        }
    }

    /**
     * Grozing Pliers Card effect
     */
    private void grozingPliers() {
        int index;
        int newValue;
        int oldValue;
        if (new GrozingPliers().isUsable(player, firsTurn)) {
            notifySuccess();
            index = askDieFromReserve();
            if(index >-1)
            {
                oldValue = board.getReserve().get(index).getValue();
                newValue = askNewValue(index);
                String result = CardEffects.changeValue(board.getReserve().get(index),false,newValue);
                network.sendReq(new ReserveStatus(board.getReserve()),player.getName());
                if(result == null)
                {
                        result = setDie(index,true);
                        if (result == null) {
                            player.setCardRights(firsTurn, false);
                            player.setPlacementRights(firsTurn, false);
                            useCard(player);
                            updateGameState();
                        } else
                        {
                            board.getReserve().get(index).setFace(oldValue);
                            network.sendReq(new CardExecutionError(result),player.getName());
                            updateGameState();
                        }
                }
                else
                    network.sendReq(new CardExecutionError(result),player.getName());
                }
        }
        else
            notifyFailure(NO_PERMISSION);
        }

    /**
     * Eglomise Brush card effect
     */
    private void eglomiseBrush()
        {
            int h;
            int w;
            Pair<Integer,Integer> coor;
            if(new EglomiseBrush().isUsable(player,firsTurn))
            {
                notifySuccess();
                coor =askDieFromGrid();
                if(coor != null)
                {
                     h = coor.getFirst();
                     w = coor.getSecond();
                     String result = setDieFromGrid(h,w,false,true);
                     if(result == null)
                     {
                         player.setCardRights(firsTurn, false);
                         useCard(player);
                         updateGameState();
                     }
                     else
                     {
                         network.sendReq(new CardExecutionError(result),player.getName());
                     }
                }
            }
            else notifyFailure(NO_PERMISSION);

        }

    /**
     * Copper Foil Burnisher card effect
     */
    private void copperFoilBurnisher()
    {
        int h;
        int w;
        Pair<Integer,Integer> coor;
        if(new CopperFoilBurnisher().isUsable(player,firsTurn))
        {
            notifySuccess();
            coor =askDieFromGrid();
            if(coor != null)
            {
                h = coor.getFirst();
                w = coor.getSecond();
                String result = setDieFromGrid(h,w,true,false);
                if(result == null)
                {
                    player.setCardRights(firsTurn, false);
                    useCard(player);
                    updateGameState();
                }
                else
                {
                    network.sendReq(new CardExecutionError(result),player.getName());
                }
            }
        }
        else
            notifyFailure(NO_PERMISSION);
    }

    /**
     * Lathekin card effect
     */
    private void lathekin()
    {
        int h;
        int w;
        Pair<Integer,Integer> coor;
        int h2;
        int w2;
        Pair<Integer,Integer> coor2;

        if(new Lathekin().isUsable(player,firsTurn))
        {
            notifySuccess();
            coor = askDieFromGrid();
            coor2 = askDieFromGrid();
            if(coor != null && coor2 != null )
            {
                h = coor.getFirst();
                w = coor.getSecond();
                h2 = coor2.getFirst();
                w2 = coor2.getSecond();
                String result = setDoubleDieFromGrid(h,w,h2,w2);
                if(result == null)
                {
                    player.setCardRights(firsTurn,false);
                    useCard(player);
                    updateGameState();
                }
                else
                    network.sendReq(new CardExecutionError(result),player.getName());
            }
        }
        else notifyFailure(NO_PERMISSION);
    }

    /**
     * Lens Cutter card effect
     */
    private void lensCutter()
    {
        int round;
        int diePosition;
        int reserveIndex;
        Pair<Integer,Integer> die;
        if(new LensCutter().isUsable(player,firsTurn) && board.getRoundTrack().lastFilledRound()>0)
        {
            notifySuccess();
            reserveIndex = askDieFromReserve();
            if(reserveIndex >-1)
            {
                die = askDieFromRoundTrack();
                if(die != null)
                {
                    round = die.getFirst();
                    diePosition = die.getSecond();
                    String result = setDieFromRoundTrack(round,diePosition,reserveIndex);
                    if(result == null)
                    {
                        player.setCardRights(firsTurn,false);
                        player.setPlacementRights(firsTurn,false);
                        useCard(player);
                        updateGameState();

                    }
                    else
                        network.sendReq(new CardExecutionError(result),player.getName());
                }
            }
        }
        else
            notifyFailure(NO_PERMISSION);
    }

    /**
     * Flux Brush card effect
     */
    private void fluxBrush()
    {
        int index;
        int oldValue;
        if(new FluxBrush().isUsable(player,firsTurn))
        {
            notifySuccess();
            if(randomDice.getRollDieIndex()<0) {
                index = askDieFromReserve();
                if (index >-1)
                {
                   oldValue = board.getReserve().get(index).getValue();
                   board.getReserve().get(index).roll();
                   String result = setDie(index,true);
                   if(result == null)
                   {
                       player.setCardRights(firsTurn,false);
                       player.setPlacementRights(firsTurn,false);
                       useCard(player);
                       updateGameState();
                   }
                   else
                   {
                       randomDice.setRollDieIndex(index);
                       randomDice.setRollDie(board.getReserve().get(index));
                       board.getReserve().get(index).setFace(oldValue);
                       network.sendReq(new CardExecutionError(result),player.getName());
                   }
                }
            }
            else
            {
               noCheatFluxBrush();
            }
        }
        else
            notifyFailure(NO_PERMISSION);
    }

    /**
     * Does not permit player to call the card again with a new value
     */
    private void noCheatFluxBrush()
    {
        int index = randomDice.getRollDieIndex();
        int oldValue = board.getReserve().get(index).getValue();
        board.getReserve().get(index).setFace(randomDice.getRollDie().getValue());
        String result = setDie(index,true);
        if(result == null)
        {
            player.setCardRights(firsTurn,false);
            player.setPlacementRights(firsTurn,false);
            useCard(player);
            updateGameState();
        }
        else
        {
            board.getReserve().get(index).setFace(oldValue);
            network.sendReq(new CardExecutionError(result),player.getName());
        }
    }

    /**
     * Glazing Hammer card effect
     */
    private void glazinHammer()
    {
        if(new GlazingHammer().isUsable(player,firsTurn))
        {
            notifySuccess();
            CardEffects.reRoll(true,board.getReserve(),null);
            player.setCardRights(firsTurn,false);
            useCard(player);
            updateGameState();
        }
        else notifyFailure(NO_PERMISSION);
    }


    /**
     * Cork Backed Straightedge card effect
     */
    private void corkBackedStraightedge()
    {
        int index;
        if(new CorkBackedStraightedge().isUsable(player,firsTurn))
        {
            notifySuccess();
            index = askDieFromReserve();
            if(index >-1)
            {
                String result = setDie(index,false);
                if(result == null)
                {
                    player.setCardRights(firsTurn,false);
                    player.setPlacementRights(firsTurn,false);
                    useCard(player);
                    updateGameState();
                }
                else
                    network.sendReq(new CardExecutionError(result),player.getName());
            }
        }
        else
            notifyFailure(NO_PERMISSION);
    }


    /**
     * Running Pliers card effect
     */
    private void runningPliers()
    {
        int reserveIndex;
        if(new RunningPliers().isUsable(player,firsTurn))
        {
            notifySuccess();
            reserveIndex = askDieFromReserve();
            if(reserveIndex >-1)
            {
                String result = setDie(reserveIndex,true);
                if(result == null)
                {
                    player.setPlacementRights(false,false);
                    player.setCardRights(false,false);
                    player.setCardRights(true,false);
                    useCard(player);
                    updateGameState();
                }
                else
                    network.sendReq(new CardExecutionError(result),player.getName());
            }
        }
        else
            notifyFailure(NO_PERMISSION);
    }

    /**
     * Grinding stone card effect
     */
    private void grindingStone()
    {
        int index;
        if(new GrindingStone().isUsable(player,firsTurn))
        {
            notifySuccess();
            index = askDieFromReserve();
            if(index >-1)
            {
                CardEffects.changeValue(board.getReserve().get(index),true,0);
                network.sendReq(new ReserveStatus(board.getReserve()),player.getName());
                String result = setDie(index,true);
                if(result == null)
                {
                    player.setPlacementRights(firsTurn,false);
                    player.setCardRights(firsTurn,false);
                    useCard(player);
                    updateGameState();
                }
                else
                {
                    network.sendReq(new CardExecutionError(result),player.getName());
                    CardEffects.changeValue(board.getReserve().get(index),true,0);
                    updateGameState();
                }
            }
        }
        else
            notifyFailure(NO_PERMISSION);
    }

    /**
     * Flux Remover Card effect
     */
    private void fluxRemover()
    {
        int index;
        if(new FluxRemover().isUsable(player,firsTurn))
        {
            notifySuccess();
            if(randomDice.getBagDie() == -1) {
                index = askDieFromReserve();
                if (index > -1) {
                    String result = setDieFromBag(index);
                    if (result == null) {
                        player.setPlacementRights(firsTurn, false);
                        player.setCardRights(firsTurn, false);
                        useCard(player);
                        updateGameState();
                    } else {
                        network.sendReq(new CardExecutionError(result), player.getName());
                        randomDice.setBagDie(index);
                    }
                }
            }
            else
            {
                noCheatFluxRemover();
            }
        }
        else
            notifyFailure(NO_PERMISSION);
    }

    /**
     * Avoids cheats from players calling more than one time the same card
     */
    private void noCheatFluxRemover()
    {
        String result = setDieFromBag(randomDice.getBagDie());
        if (result == null) {
            player.setPlacementRights(firsTurn, false);
            player.setCardRights(firsTurn, false);
            useCard(player);
            updateGameState();
        }
        else
            network.sendReq(new CardExecutionError(result), player.getName());
    }


    /**
     * Tap Wheel Card Effect
     */
    private void tapWheel()
    {
        Pair<Integer,Integer> roundDie;
        int round;
        int diePosition;
        int h;
        int w;
        Pair<Integer,Integer> coor;
        int h2;
        int w2;
        Pair<Integer,Integer> coor2;

        if(new TapWheel().isUsable(player,firsTurn))
        {
            notifySuccess();
            roundDie =askDieFromRoundTrack();
            coor = askDieFromGrid();
            coor2 = askDieFromGrid();
            if(coor != null && coor2 != null && roundDie != null)
            {
                round = roundDie.getFirst();
                diePosition = roundDie.getSecond();
                h = coor.getFirst();
                w = coor.getSecond();
                h2 = coor2.getFirst();
                w2 = coor2.getSecond();
                Die roundTrackDie = board.getRoundTrack().getDie(round,diePosition);
                Die d1 = player.getGrid().getDie(h,w);
                Die d2 = player.getGrid().getDie(h2,w2);
                if( roundTrackDie.getColor() == d1.getColor() && roundTrackDie.getColor() == d2.getColor() ) {
                    String result = setDoubleDieFromGrid(h, w, h2, w2);
                    if (result == null) {
                        player.setCardRights(firsTurn, false);
                        useCard(player);
                        updateGameState();
                    } else
                        network.sendReq(new CardExecutionError(result), player.getName());
                }
                else
                    network.sendReq(new CardExecutionError("Colori non uguali"),player.getName());
            }
        }
        else notifyFailure(NO_PERMISSION);
    }


    /**
     * Asks and wait for a die from the reserve
     * @return die's position inside the reserve
     */
    private int askDieFromReserve() {
        latch = new CountDownLatch(1);
        network.sendReq(new AskDieFromReserve(), player.getName());
        if (waitUpdate() && response.toString().equals("DieFromReserve")) {
            DieFromReserve dieFromReserve = (DieFromReserve) response;
            return dieFromReserve.getIndex();
        }
        network.sendReq(new CardExecutionError(TOO_SLOW),player.getName());
        return -1;
    }


    /**
     * Asks and wait for a die from the grid
     * @return die's position inside the grid
     */
    private Pair<Integer,Integer> askDieFromGrid() {
        latch = new CountDownLatch(1);
        network.sendReq(new AskDieFromGrid(), player.getName());
        if (waitUpdate() && response.toString().equals("DieFromGrid")) {
            DieFromGrid dieFromGrid = (DieFromGrid) response;
            return new Pair<>(dieFromGrid.getH(),dieFromGrid.getW());
        }
        network.sendReq(new CardExecutionError(TOO_SLOW),player.getName());
        return null;
    }

    /**
     * Asks and wait for a die from the round track
     * @return die's position inside the round track
     */
    private Pair<Integer,Integer> askDieFromRoundTrack() {
        latch = new CountDownLatch(1);
        network.sendReq(new AskDieFromRoundTrack(), player.getName());
        if (waitUpdate() && response.toString().equals("DieFromRoundTrack")) {
            DieFromRoundTrack dieFromRoundTrack = (DieFromRoundTrack) response;
            return new Pair<>(dieFromRoundTrack.getRound(),dieFromRoundTrack.getDiePosition());
        }
        network.sendReq(new CardExecutionError(TOO_SLOW),player.getName());
        return null;
    }


    /**
     * Asks and wait for a new value for a die
     * @param index die's position inside the reserve
     * @return die' new value
     */
    private int askNewValue(int index)
    {
        latch = new CountDownLatch(1);
        int val1 = board.getReserve().get(index).getValue() -1;
        int val2 = board.getReserve().get(index).getValue() +1;
        network.sendReq(new AskNewValue(val1,val2), player.getName());
        if (waitUpdate() && response.toString().equals("NewValue")) {
            NewValue newValue = (NewValue) response;
            return newValue.getNewValue();
        }
        network.sendReq(new CardExecutionError(TOO_SLOW),player.getName());
        return -1;
    }

    /**
     * Asks for a die to be set and sets it if everything is respected
     * @param index die's position inside the reserve
     * @return Error message or null if no error occurred
     */
    private String setDie(int index, boolean adjacency)
    {
        latch = new CountDownLatch(1);
        network.sendReq(new SetDie(index), player.getName());
        if (waitUpdate() && response.toString().equals(DIESET)) {
            DieSet dieSet = (DieSet) response;
            String result = DiePlacementLogic.insertDie(player,dieSet.getH(),dieSet.getW(),board.getReserve().get(index),true,true,adjacency);
            if(result == null)
            {
                player.getGrid().setDie(dieSet.getH(),dieSet.getW(),board.getReserve().popDie(index));
            }
            return result;
        }
        network.sendReq(new CardExecutionError(TOO_SLOW),player.getName());
        return OH_NO ;
    }


    /**
     * Asks for a die to be set from inside the grid and sets it if everything is respected
     * @param h height inside the grid
     * @param w width inside the grid
     * @param color restriction enabled
     * @param value value restriction enabled
     * @return Error message or null if no error occurred
     */
    private String setDieFromGrid(int h, int w, boolean color, boolean value)
    {
        latch = new CountDownLatch(1);
        Die die = player.getGrid().setDie(h,w,null);
        network.sendReq(new SetThisDie(new IntColorPair(die.getValue(),die.getColor())), player.getName());
        if (waitUpdate() && response.toString().equals(DIESET)) {
            DieSet dieSet = (DieSet) response;
            String result = DiePlacementLogic.insertDie(player,dieSet.getH(),dieSet.getW(),die,color,value,true);
            if(result == null)
            {
                player.getGrid().setDie(dieSet.getH(),dieSet.getW(),die);
            }
            else
            {
               player.getGrid().setDie(h,w,die);
            }
            return result;
        }
        else {
            player.getGrid().setDie(h,w,die);
            network.sendReq(new CardExecutionError(TOO_SLOW), player.getName());
        }
        return OH_NO;
    }


    /**
     * Sets a die from the bag and puts a die from the reserve inside the bag
     * @param diePosition position of the die inside the reserve
     * @return null, or an error message
     */
    private String setDieFromBag(int diePosition)
    {
        latch = new CountDownLatch(1);
        Die die = board.getBag().getDie(0);
        network.sendReq(new SetThisDie(new IntColorPair(die.getValue(),die.getColor())),player.getName());
        if(waitUpdate() && response.toString().equals(DIESET))
        {
            DieSet dieSet = (DieSet) response;
            String result = DiePlacementLogic.insertDie(player,dieSet.getH(),dieSet.getW(),die,true,true,true);
            if(result == null)
            {
                player.getGrid().setDie(dieSet.getH(),dieSet.getW(),board.getBag().popDice(1).get(0));
                board.getBag().add(board.getReserve().popDie(diePosition));
            }
                return result;
        }

            return OH_NO;
    }


    /**
     * Asks for a die to be set from roundTrack and it swap the die with one from the reserve
     * @param round round inside round track
     * @param diePosition die's position inside the round
     * @param index die position inside the round track
     * @return Error message or null if no error occurred
     */
    private String setDieFromRoundTrack(int round, int diePosition, int index)
    {
        latch = new CountDownLatch(1);
        Die die = board.getRoundTrack().getDie(round,diePosition);
        network.sendReq(new SetThisDie(new IntColorPair(die.getValue(),die.getColor())), player.getName());
        if (waitUpdate() && response.toString().equals(DIESET)) {
            DieSet dieSet = (DieSet) response;
            String result = DiePlacementLogic.insertDie(player,dieSet.getH(),dieSet.getW(),die,true,true,true);
            if(result == null)
            {
                CardEffects.reserveRoundTrackSwap(index,round,diePosition,board.getReserve(),board.getRoundTrack());
                player.getGrid().setDie(dieSet.getH(),dieSet.getW(), board.getReserve().popDie(board.getReserve().size()-1));
            }
            return result;
        }
        network.sendReq(new CardExecutionError(TOO_SLOW),player.getName());
        return OH_NO ;
    }

    /**
     * Asks and wait for a double die placement
     * @param h height of the first die
     * @param w width of the first die
     * @param h2 height of the second die
     * @param w2 width of the second die
     * @return error message or null in case of no error
     */
    private String setDoubleDieFromGrid(int h, int w, int h2, int w2)
    {
        int newH;
        int newW;
        latch = new CountDownLatch(1);
        Die die = player.getGrid().getDie(h,w);
        network.sendReq(new SetThisDie(new IntColorPair(die.getValue(),die.getColor())), player.getName());
        if (waitUpdate() && response.toString().equals(DIESET)) {
            DieSet dieSet = (DieSet) response;
            String result = DiePlacementLogic.insertDie(player, dieSet.getH(), dieSet.getW(), die, true,true,true);
            if(result == null)
            {
                newH = dieSet.getH();
                newW = dieSet.getW();
                latch = new CountDownLatch(1);
                die = player.getGrid().getDie(h2,w2);
                network.sendReq(new SetThisDie(new IntColorPair(die.getValue(),die.getColor())), player.getName());
                if (waitUpdate() && response.toString().equals(DIESET)) {
                     dieSet = (DieSet) response;
                     result = DiePlacementLogic.insertDie(player, dieSet.getH(), dieSet.getW(), die, true, true, true);
                     if(result == null)
                     {
                         player.getGrid().setDie(dieSet.getH(),dieSet.getW(),player.getGrid().setDie(h2,w2,null));
                         player.getGrid().setDie(newH,newW,player.getGrid().setDie(h,w,null));
                     }
                }
            }
            return result;
        }
        return OH_NO;
    }

    /**
     * Waits for the requested response
     * @return true if a response has come, false otherwise
     */
    private boolean waitUpdate()
    {
        try {
            return latch.await(20, TimeUnit.SECONDS);
        } catch (InterruptedException e)
        {
            Logger.getGlobal().log(Level.WARNING, "Interrupted while waiting");
            Thread.currentThread().interrupt();
        }

        return false;
    }


    /**
     * Sets all parameters after using a card
     * @param player player affected
     */
    private void useCard(Player player)
    {
        board.getTool(cardPosition).burnFavourPoints(player);
        board.getTool(cardPosition).updateUsed();
    }

    /**
     * Updates game stets on all clients
     */
    private void updateGameState()
    {
        network.sendObj(new CardInfo(board.getTools(),board.getObjectives()));
        network.sendObj(new PlayerStatus(player, firsTurn));
        network.sendObj(new ReserveStatus(board.getReserve()));
        network.sendObj(new RoundTrackStatus(board.getRoundTrack()));
    }

}

