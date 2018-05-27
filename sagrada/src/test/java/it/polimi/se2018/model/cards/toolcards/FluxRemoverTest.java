package it.polimi.se2018.model.cards.toolcards;

import it.polimi.se2018.model.Player;
import org.junit.Test;

import static org.junit.Assert.*;

public class FluxRemoverTest {

    private FluxRemover test = new FluxRemover();

    @Test
    public void testIsUsable()
    {
        Player player = new Player("Commissar", 0);
        assertFalse(test.isUsable(player,true));
    }

}