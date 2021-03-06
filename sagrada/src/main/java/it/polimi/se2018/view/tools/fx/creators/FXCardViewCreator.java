package it.polimi.se2018.view.tools.fx.creators;

import it.polimi.se2018.util.SingleCardView;
import it.polimi.se2018.view.tools.CardViewCreator;
import javafx.scene.image.Image;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to create cards in GUI
 *
 * @author Mathyas Giudici
 */

public class FXCardViewCreator extends CardViewCreator<Image> {

    /**
     * Basic Class constructor that initializes elements at default value
     */
    public FXCardViewCreator() {
        super();
    }

    /**
     * Class constructor
     *
     * @param privateObjectiveCard contains the private objective
     * @param publicObjectiveCards contains the public objectives
     * @param toolCards            contains the tool cards
     */
    FXCardViewCreator(SingleCardView privateObjectiveCard, List<SingleCardView> publicObjectiveCards, List<SingleCardView> toolCards) {
        super(privateObjectiveCard, publicObjectiveCards, toolCards);
    }

    @Override
    public Image makeCard(int cardID) {
        //Check private objective card
        if (cardID == privateObjectiveCard.cardID) {
            String url = "/it/polimi/se2018/view/images/private_cards/privateObjective" + cardID + ".jpg";
            return makeImage(url);
        }

        //Check public objective cards
        for (SingleCardView publicObjectiveCard : this.publicObjectiveCards) {
            if (cardID == publicObjectiveCard.cardID) {
                int card = cardID - 10;
                String url = "/it/polimi/se2018/view/images/public_cards/publicObjective" + card + ".jpg";
                return makeImage(url);
            }
        }

        //Check tool cards
        for (SingleCardView toolCard : this.toolCards) {
            if (cardID == toolCard.cardID) {
                int card = cardID - 20;
                String url = "/it/polimi/se2018/view/images/tool_cards/toolCard" + card + ".jpg";
                return makeImage(url);
            }
        }

        //Problems
        String message = "Qualcosa non va nella creazione ne della carta: " + cardID;
        Logger.getGlobal().log(Level.WARNING, message);
        return null;

    }

    /**
     * Loads image
     *
     * @param url contain's image url
     * @return Image object
     */
    Image makeImage(String url) {
        return new Image(url);
    }
}
