package it.polimi.se2018.controller.network.server;

import it.polimi.se2018.controller.network.AbsReq;
import it.polimi.se2018.util.MatchIdentifier;
import org.junit.Before;
import org.junit.Test;


import java.net.InetAddress;

import static org.junit.Assert.*;

/**
 * Tester class for the PendingApprovalMatch class
 * @author Francesco Franzini
 */
public class PendingApprovalMatchTest {
    private PendingApprovalMatch uut;
    private MatchIdentifier mId;
    private ClientTest c1,c2,c3,c4;
    private ServerMain s;

    /**
     * Prepares the objects to be used
     * @throws Exception if an error occurs
     */
    @Before
    public void setUp() throws Exception{
        s=new ServerMain(0,0,0,"test",InetAddress.getLocalHost(),new MockFactory());
        c1=new ClientTest("us1");
        c2=new ClientTest("us2");
        c3=new ClientTest("us3");
        c4=new ClientTest("us4");
        mId=new MatchIdentifier("us1","us2","us3","us4");
    }

    /**
     * Tests that illegal arguments are correctly detected
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidInit() {
        c1=new ClientTest("a");
        uut=new PendingApprovalMatch(1000,mId,null,c1);
    }

    /**
     * Tests the {@code accept} method
     */
    @Test
    public void testAccept(){
        uut=new PendingApprovalMatch(1000,mId,s,c1);
        assertTrue(uut.clientAccepted(c2));
        assertFalse(uut.clientAccepted(new Client("a",null)));
        c3.acceptInvite();
        assertFalse(uut.clientAccepted(c3));
    }

    /**
     * Tests that a complete Pending Approval Match is converted into a match
     */
    @Test
    public void testComplete(){
        uut=new PendingApprovalMatch(1000,mId,s,c1);
        uut.clientAccepted(c2);
        uut.clientAccepted(c3);
        uut.clientAccepted(c4);
        assertNull(s.getPendingMatch(mId));
        assertNotNull(s.getMatch(mId));
    }

    /**
     * Tests the {@code abort} method
     */
    @Test
    public void testAbort(){
        uut=new PendingApprovalMatch(1000,mId,s,c1);
        uut.abort();
        assertTrue(c1.aborted);
        assertNull(s.getPendingMatch(mId));
    }

    /**
     * Tests that the timeout kicks in
     */
    @Test(timeout = 1000)
    public void testTimeout(){
        uut=new PendingApprovalMatch(0,mId,s,c1);
        while(c1.getPAM()!=null);

    }

    /**
     * Mock client used to intercept method calls
     */
    private class ClientTest extends Client{
        boolean aborted=false;
        ClientTest(String usn) {
            super(usn, null);
        }
        @Override
        public void pushOutReq(AbsReq req){
            aborted=true;
        }
    }

    /**
     * Mock controller factory used to avoid NullPointer exceptions
     */
    private class MockFactory implements MatchControllerFactory{

        @Override
        public MatchController buildMatch(MatchIdentifier mId, MatchNetworkInterface network) {
            return null;
        }
    }

}