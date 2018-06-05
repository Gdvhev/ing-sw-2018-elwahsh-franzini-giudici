package it.polimi.se2018.view.observer;

import it.polimi.se2018.observable.CardView;
import it.polimi.se2018.view.app.App;

import java.util.Observable;
import java.util.Observer;

/**
 * CardView observable class
 *
 * @author Mathyas Giudici
 */

public class CardViewObserver extends ModelObserver implements Observer {

    public CardViewObserver(App app) {
        super(app);
    }

    @Override
    public void update(Observable o, Object arg) {
        CardView cardView = (CardView) arg;
        this.app.getCardViewCreator().setPrivateObjectiveCard(cardView.getPrivateObjectiveCard());
        this.app.getCardViewCreator().setPublicObjectiveCards(cardView.getPublicObjectiveCards());
        this.app.getCardViewCreator().setToolCards(cardView.getToolCards());
    }
}