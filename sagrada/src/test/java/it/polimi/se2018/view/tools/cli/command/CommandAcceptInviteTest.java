package it.polimi.se2018.view.tools.cli.command;

import it.polimi.se2018.util.MatchIdentifier;
import it.polimi.se2018.view.ViewActions;
import it.polimi.se2018.view.app.CLIApp;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertEquals;

/**
 * Tests for CommandAcceptInvite class
 *
 * @author Mathyas Giudici
 */

public class CommandAcceptInviteTest extends AbsCommandTest {

    /**
     * Mock class of ViewActions object
     */
    private class FakeViewAction extends ViewActions {
        private FakeViewAction() {
            super(null);
        }

        @Override
        public void acceptInvite(MatchIdentifier matchIdentifier) {
            assertEquals("TestName1", matchIdentifier.player0);
            assertEquals("TestName2", matchIdentifier.player1);
            assertEquals("TestName3", matchIdentifier.player2);
            assertEquals("TestName4", matchIdentifier.player3);
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
        String input = "0" + enter;
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        app = new FakeApp();

        app.getInvites().add(new MatchIdentifier("TestName1", "TestName2", "TestName3", "TestName4"));

        CommandAcceptInvite commandAcceptInvite = new CommandAcceptInvite(app);
        commandAcceptInvite.doAction();

        assertEquals("Inserire ID invito (numerazione da 0)Opzione: ", savedStream.toString().replaceAll(regexControl, emptyString));
    }
}