package it.polimi.se2018.controller.network.server;

import it.polimi.se2018.controller.network.ThreadHandler;

/**
 * Object that empties the inbound queue of the server
 * @author Francesco Franzini
 */
class ServerInQueueEmptier extends ThreadHandler {
    private final Client client;

    /**
     * Builds this handler with the given parameters
     * @param client the client to call
     * @param isReq flag to choose request or object stream
     */
    public ServerInQueueEmptier(Client client, boolean isReq) {
        this.client=client;
        throw new UnsupportedOperationException();
    }

    @Override
    protected void methodToCall() {
        throw new UnsupportedOperationException();
    }


}