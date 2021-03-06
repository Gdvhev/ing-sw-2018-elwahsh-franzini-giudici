package it.polimi.se2018.events.messages;

import it.polimi.se2018.controller.game.client.ClientController;
import it.polimi.se2018.events.ServerMessageHandler;
import it.polimi.se2018.model.IntColorPair;
import it.polimi.se2018.observable.CardView;
import it.polimi.se2018.observable.PlayerView;
import it.polimi.se2018.observable.ReserveView;
import it.polimi.se2018.observable.RoundTrackerView;
import it.polimi.se2018.util.SingleCardView;
import it.polimi.se2018.view.app.App;

import java.util.List;

public class ServerMessageHandlerImpl implements ServerMessageHandler {

    private final ClientController controller;
    private final App app;
    private final List<PlayerView> players;
    private final ReserveView reserve;
    private final RoundTrackerView roundTrack;
    private final CardView cards;

    public ServerMessageHandlerImpl(ClientController controller, App app, List<PlayerView> players, ReserveView reserve, RoundTrackerView roundTrack, CardView cards){
        this.controller=controller;
        this.app=app;
        this.players=players;
        this.reserve=reserve;
        this.roundTrack=roundTrack;
        this.cards=cards;
    }


    @Override
    public void handle(CardInfo move) {
        cards.setCardView(move);
        cards.uniqueNotify();
    }

    @Override
    public void handle(InvalidMove move) {
        if(move.getMove().toString().equals(controller.getLastAction().toString()))
        {
            if(move.isPlacement())
                app.setDieResult(false,move.toString());
            else
                app.useToolCardResult(false,move.toString());
        }
    }

    @Override
    public void handle(SetThisDie move) {
        app.setDieOnGrid(move.getDie());
    }

    @Override
    public void handle(ConfirmMove move) {

        if(move.getMove().toString().equals(controller.getLastAction().toString()))
        {
            if(move.isPlacement())
                app.setDieResult(true,null);
            else
                app.useToolCardResult(true,null);
        }
    }

    @Override
    public void handle(MatchStart move) {
        if(move.recover){
            app.animation(false);
            app.askPattern(null,null,null,null,cards);
            app.animation(true);
        }

        app.initGame(players,reserve,roundTrack);
    }

    @Override
    public void handle(PatternSelect move) {
        controller.addPatternView(move);
    }

    @Override
    public void handle(PlayerStatus playerStatus) {
        for(PlayerView p: players)
            if(p.getPlayerName().equals(playerStatus.getName()))
            {
                p.setPlayerGrid(playerStatus.getGrid());
                p.setCardRights(playerStatus.isCardRights());
                p.setPlacementRights(playerStatus.isPlacementRights());
                p.setPlayerFavours(playerStatus.getFavourPoints());
                if(playerStatus.getPattern()!= null && p.getPlayerTemplate() != null)
                    p.setPlayerTemplate(playerStatus.getPattern());
                p.uniqueNotify();
            }
    }

    @Override
    public void handle(PrivateObjectiveStatus move) {
        cards.setPrivateObjectiveCard(new SingleCardView(move.getCardId(),0));
    }

    @Override
    public void handle(CardExecutionError move) {
        app.showError(move.toString());
    }

    @Override
    public void handle(SetDie move) {
        IntColorPair die = reserve.getReserve()[move.getIndex()];
        app.setDieOnGrid(die);
    }

    @Override
    public void handle(AskPlacements move) {
        app.askNumbersOfPlacement();
    }

    @Override
    public void handle(AskDieByColor move) {
        app.selectDieFromGridByColor(move.getColorModel());
    }

    @Override
    public void handle(AskNewFace move) {
        app.selectFace(new IntColorPair(move.getValue(),move.getColor()));
    }

    @Override
    public void handle(AskDieFromRoundTrack move) {
        app.selectDieFromRoundTracker();
    }

    @Override
    public void handle(AskDieFromGrid move) {
        app.selectDieFromGrid();
    }

    @Override
    public void handle(AskNewValue move) {
        app.selectNewValueForDie(move.getVal1(),move.getVal2());
    }

    @Override
    public void handle(ReserveStatus move) {
        reserve.setReserve(move.getDice());
        reserve.uniqueNotify();
    }

    @Override
    public void handle(RoundTrackStatus move) {
        roundTrack.setRound(move.round());
        roundTrack.setRoundTracker(move.getDice());
        roundTrack.uniqueNotify();
    }

    @Override
    public void handle(AskDieFromReserve move) {
        app.selectDieFromReserve();
    }

    @Override
    public void handle(TurnStart move) {
        if(move.getName().equals(controller.getLocalPlayer())){
            app.startTurn();
        }
        else if(move.getOldPlayer()!= null && move.getOldPlayer().equals(controller.getLocalPlayer()))
            app.passTurnResult(true);
        else if(move.getOldPlayer()!= null)
            app.passTurnUpdate(move.getOldPlayer());
    }

    @Override
    public void handle(ReadyView move) {
        //Should not happen
    }
}
