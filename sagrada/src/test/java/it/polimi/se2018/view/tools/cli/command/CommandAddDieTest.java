package it.polimi.se2018.view.tools.cli.command;

import it.polimi.se2018.model.ColorModel;
import it.polimi.se2018.model.IntColorPair;
import it.polimi.se2018.view.ViewActions;
import it.polimi.se2018.view.app.CLIApp;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertEquals;

/**
 * Tests for CommandAddDie class
 *
 * @author Mathyas Giudici
 */

public class CommandAddDieTest extends AbsCommandTest {

    /**
     * Mock class of ViewActions object
     */
    private class FakeViewAction extends ViewActions {
        private FakeViewAction() {
            super(null);
        }

        @Override
        public void setDie(int reserveIndex, int height, int width) {
            assertEquals(0, reserveIndex);
            assertEquals(2, width);
            assertEquals(3, height);
        }
    }

    /**
     * Mock class of CLIApp object
     */
    private class FakeApp extends CLIApp {
        private FakeApp() {
            super(new FakeViewAction(), null);
        }
    }


    /**
     * Checks command perform
     *
     * @throws Exception if an error occurs during reading
     */
    @Test
    public void testDoAction() throws Exception {
        String input = "0" + enter + "2" + enter + "3" + enter;
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        app = new FakeApp();

        IntColorPair[] fakeReserve = new IntColorPair[1];
        fakeReserve[0] = new IntColorPair(1, ColorModel.RED);
        app.getReserveViewCreator().setReserve(fakeReserve);

        IntColorPair[][] fakePattern;
        IntColorPair[][] fakeGrid = new IntColorPair[1][2];
        fakePattern = new IntColorPair[1][2];
        fakePattern[0][0] = new IntColorPair(1, ColorModel.RED);
        fakePattern[0][1] = new IntColorPair(1, ColorModel.RED);
        fakeGrid[0][0] = new IntColorPair(1, ColorModel.RED);
        fakeGrid[0][1] = new IntColorPair(1, ColorModel.RED);
        app.getGridViewCreator().setGridPattern(fakePattern);
        app.getGridViewCreator().setGrid(fakeGrid);

        CommandAddDie commandAddDie = new CommandAddDie(app);
        commandAddDie.doAction();
    }
}