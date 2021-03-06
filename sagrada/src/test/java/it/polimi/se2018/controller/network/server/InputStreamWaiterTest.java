package it.polimi.se2018.controller.network.server;

import it.polimi.se2018.controller.network.AbsReq;
import it.polimi.se2018.controller.network.client.Comm;
import it.polimi.se2018.controller.network.client.CommUtilizer;
import it.polimi.se2018.util.SafeSocket;
import org.junit.After;
import org.junit.Test;


import java.io.IOException;
import java.io.Serializable;

import static org.junit.Assert.*;

/**
 * Tester class for the InputStreamWaiter class
 * @author Francesco Franzini
 */
public class InputStreamWaiterTest {
    private InputStreamWaiter uut;
    private String recObj;
    private boolean req;

    /**
     * Resets the object for the next test
     */
    @After
    public void tearDown(){
        recObj=null;
    }

    /**
     * Tests that objects are correctly handled
     * @throws Exception if an error occurs
     */
    @Test
    public void testObj() throws Exception{
        req=false;
        uut=new InputStreamWaiter(new TestSSocket(new TestAbsReq()),false,new TestCComm());
        uut.methodToCall();
        assertEquals("Obj",recObj);
    }

    /**
     * Tests that requests are correctly handled
     * @throws Exception if an error occurs
     */
    @Test
    public void testReq() throws Exception{
        req=true;
        uut=new InputStreamWaiter(new TestSSocket(new TestAbsReq()),true,new TestCComm());
        uut.methodToCall();
        assertEquals("Req",recObj);
    }

    /**
     * Mock ClientComm used to intercept method calls
     */
    private class TestCComm extends SocClientComm{
        TestCComm(){
            super(null,null,null);
        }
        @Override
        public void pushInObj(Serializable obj){
            recObj=(String)obj;
        }
        @Override
        public void pushInReq(AbsReq obj){
            recObj=((TestAbsReq)obj).str;
        }
    }

    /**
     * Mock SafeSocket used to intercept method calls
     */
    private class TestSSocket extends SafeSocket {
        AbsReq requ;
        TestSSocket(AbsReq requ) throws IOException {
            super(100);
            this.requ=requ;
        }

        @Override
        public Serializable receive(){
            if(req)return this.requ;
            return "Obj";
        }

    }

    /**
     * Mock request used as a payload
     */
    private class TestAbsReq implements AbsReq {
        final String str="Req";

        @Override
        public void serverVisit(ServerVisitor sV) {

        }

        @Override
        public void clientHandle(Comm clientComm, CommUtilizer commUtilizer) {

        }

        @Override
        public boolean checkValid() {
            return false;
        }
    }
}