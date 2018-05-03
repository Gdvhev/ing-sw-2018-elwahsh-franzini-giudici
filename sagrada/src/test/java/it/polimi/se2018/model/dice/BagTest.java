package it.polimi.se2018.model.dice;

import it.polimi.se2018.model.ColorModel;
import org.junit.Test;

import java.util.ArrayList;
import java.util.logging.Logger;
import static org.junit.Assert.*;

public class BagTest {

    private static final Logger BATTLESCRIBER = Logger.getAnonymousLogger();
    private Bag test = Bag.getInstance();


    /**
     * Counter for dice of a single color
     * @param c the desired color
     * @return number of dice with c as a color
     */
    private int countSameColor(ColorModel c)
    {
        int temp = 0;
        for(int i = 0; i<Bag.MAX_SIZE; i++)
        {
            if(test.getDie(i).getColor() == c)
                temp++;
        }
        return temp;
    }

    /**
     * Checking public info about the Bag
     */
    @Test
    public void testPublicInfo()
    {
        assertEquals(Bag.MAX_SIZE, test.size());
        assertEquals(Bag.DICE_FOR_COLOR, countSameColor(ColorModel.RED));
        /*No white dice*/
        assertEquals(0,countSameColor(ColorModel.WHITE));
    }

    /**
     * Checking if getDie is working properly
     */
    @Test
    public void testGetDie()
    {
        assertNotEquals(null, test.getDie(0));
        assertNotEquals(null,test.getDie(89));
        assertEquals(null,test.getDie(-2));
    }

    /**
     * Testing add and pop methods
     */
    @Test
    public void testAddAndPop()
    {
        Die d = new Die(ColorModel.RED);
        ArrayList<Die> dice;


        test.add(d);
        /*Size can't be more than MAX_SIZE*/
        assertEquals(Bag.MAX_SIZE, test.size());

        /*pop one die*/
        dice = new ArrayList<>(test.popDice(1));
        assertNotEquals(null,dice.get(0));
        test.add(dice.remove(0));

        /*pop more than MAX_SIZE dice*/
        dice = new ArrayList<>(test.popDice(100));
        assertNotEquals(null,dice.get(0));

        /*Size changed*/
        assertEquals(0, test.size());

        /*All back to normal*/
        for(int i=0;i<Bag.MAX_SIZE;i++) {
            test.add(dice.remove(0));
        }
        assertEquals(Bag.MAX_SIZE, test.size());


    }



}