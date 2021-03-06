package it.polimi.se2018.model.cards.toolcards;

import it.polimi.se2018.model.Player;
import it.polimi.se2018.model.cards.ToolCard;


/**
 * Model representation of the TapWheel tool card
 * @author Alì El Wahsh
 */
public class TapWheel extends ToolCard {


    /**
     * TapWheel's constructor
     */
    public TapWheel() {
        this.id = 31;
        this.text = "Muovi fino a due dadi dello stesso colore di un solo dado sul Tracciato dei Round" +
                "Devi rispettare tutte le restrizioni di piazzamento";
    }

    /**
     * Checks if the player can use this card on his turn
     * @param player the player using the card
     * @param firstTurn true on first turn, false on second
     * @return true if the player can use the card, else false
     */
    @Override
    public boolean isUsable(Player player, boolean firstTurn) {

        return  player.canUseCardOnThisTurn(firstTurn);
    }
}
