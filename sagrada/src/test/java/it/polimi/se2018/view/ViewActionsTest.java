package it.polimi.se2018.view;

import it.polimi.se2018.controller.game.client.ActionSender;
import it.polimi.se2018.util.MatchIdentifier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for ViewActions class
 *
 * @author Mathyas Giudici
 */

public class ViewActionsTest {

    private boolean assertion;

    private ViewActions viewActions;

    private ActionSender actionSender;

    /**
     * Sets assertion flag at false
     */
    @Before
    public void testInit() {
        assertion = false;
    }

    /**
     * Checks assertion flags
     */
    @After
    public void testUltimateCheck() {
        assertTrue(assertion);
    }

    /**
     * Tests login request
     */
    @Test
    public void testLogin() {
        actionSender = new FakeActionSender() {
            @Override
            public String login(String host, int requestPort, int objectPort, String username, String password, boolean newUser, boolean useRMI) {
                assertEquals("TestHost", host);
                assertEquals(1, requestPort);
                assertEquals(10, objectPort);
                assertEquals("Test", username);
                assertEquals("test", password);
                assertTrue(newUser);
                assertTrue(useRMI);
                assertion = true;
                return null;
            }
        };

        viewActions = new ViewActions(actionSender);
        assertNull(viewActions.login("Test", "test", true, "TestHost", true, 10, 1));
    }

    /**
     * Tests change layer request
     */
    @Test
    public void testChangeLayer() {
        actionSender = new FakeActionSender() {
            @Override
            public void changeLayer(boolean toRMI, int requestPort, int objectPort) {
                assertTrue(toRMI);
                assertEquals(1, requestPort);
                assertEquals(-1, objectPort);
                assertion = true;
            }
        };

        viewActions = new ViewActions(actionSender);
        viewActions.changeLayer(true, -1, 1);
    }

    /**
     * Tests leave match request
     */
    @Test
    public void testLeaveMatch() {
        actionSender = new FakeActionSender() {
            @Override
            public void leaveMatch() {
                assertion = true;
            }
        };

        viewActions = new ViewActions(actionSender);
        viewActions.leaveMatch();
    }

    /**
     * Tests logout request
     */
    @Test
    public void testLogout() {
        actionSender = new FakeActionSender() {
            @Override
            public void logout() {
                assertion = true;
            }
        };

        viewActions = new ViewActions(actionSender);
        viewActions.logout();
    }

    /**
     * Tests lobby request
     */
    @Test
    public void testAskLobby() {
        actionSender = new FakeActionSender() {
            @Override
            public void askLobby() {
                assertion = true;
            }
        };

        viewActions = new ViewActions(actionSender);
        viewActions.askLobby();
    }

    /**
     * Tests pulling invite request
     */
    @Test
    public void testPushInvite() {
        MatchIdentifier matchIdentifier = new MatchIdentifier("A", "B", null, null);

        actionSender = new FakeActionSender() {
            @Override
            public void pushInvite(MatchIdentifier invite) {
                assertEquals(matchIdentifier, invite);
                assertion = true;
            }
        };

        viewActions = new ViewActions(actionSender);
        viewActions.pushInvite(matchIdentifier);
    }

    /**
     * Tests joining matchmaking request
     */
    @Test
    public void testJoinMatchMaking() {
        actionSender = new FakeActionSender() {
            @Override
            public void joinMatchMaking() {
                assertion = true;
            }
        };

        viewActions = new ViewActions(actionSender);
        viewActions.joinMatchMaking();
    }

    /**
     * Tests leaving matchmaking request
     */
    @Test
    public void testLeaveMatchMaking() {
        actionSender = new FakeActionSender() {
            @Override
            public void leaveMatchMaking() {
                assertion = true;
            }
        };

        viewActions = new ViewActions(actionSender);
        viewActions.leaveMatchMaking();
    }

    /**
     * Tests accept invite request
     */
    @Test
    public void testAcceptInvite() {
        MatchIdentifier matchIdentifier = new MatchIdentifier("A", "B", null, null);

        actionSender = new FakeActionSender() {
            @Override
            public void acceptInvite(MatchIdentifier invite) {
                assertEquals(matchIdentifier, invite);
                assertion = true;
            }
        };

        viewActions = new ViewActions(actionSender);
        viewActions.acceptInvite(matchIdentifier);
    }

    /**
     * Tests pattern card selection request
     */
    @Test
    public void testSelectedPattern() {
        actionSender = new FakeActionSender() {
            @Override
            public void selectedPattern(String pattern) {
                assertEquals("TestPattern", pattern);
                assertion = true;
            }
        };

        viewActions = new ViewActions(actionSender);
        viewActions.selectedPattern("TestPattern");
    }

    /**
     * Tests game end request
     */
    @Test
    public void testEndInitGame() {
        actionSender = new FakeActionSender() {
            @Override
            public void endInitGame() {
                assertion = true;
            }
        };

        viewActions = new ViewActions(actionSender);
        viewActions.endInitGame();
    }

    /**
     * Tests set die request
     */
    @Test
    public void testSetDie() {
        actionSender = new FakeActionSender() {
            @Override
            public void setDie(int reserveIndex, int height, int width) {
                assertEquals(0, reserveIndex);
                assertEquals(1, height);
                assertEquals(2, width);
                assertion = true;
            }
        };

        viewActions = new ViewActions(actionSender);
        viewActions.setDie(0, 1, 2);
    }

    /**
     * Tests tool card use request
     */
    @Test
    public void testUseToolCard() {
        actionSender = new FakeActionSender() {
            @Override
            public void userToolCard(int id) {
                assertEquals(10, id);
                assertion = true;
            }
        };

        viewActions = new ViewActions(actionSender);
        viewActions.useToolCard(10);
    }

    /**
     * Tests pass turn request
     */
    @Test
    public void testPassTurn() {
        actionSender = new FakeActionSender() {
            @Override
            public void passTurn() {
                assertion = true;
            }
        };

        viewActions = new ViewActions(actionSender);
        viewActions.passTurn();
    }

    /**
     * Mock class of ActionSender
     */
    private abstract class FakeActionSender extends ActionSender {
        FakeActionSender() {
            //Nothing
        }
    }
}