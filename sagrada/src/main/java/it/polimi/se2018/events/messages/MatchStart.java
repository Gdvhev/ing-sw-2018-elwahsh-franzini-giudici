package it.polimi.se2018.events.messages;

import it.polimi.se2018.events.ServerMessageHandler;

/**
 * Message representing the end of the match setup phase
 * @author Francesco Franzini
 */
public class MatchStart extends ServerMessage {
    public MatchStart() {
        this.description="MatchStart";
    }

    @Override
    public void visit(ServerMessageHandler handler) {
        handler.handle(this);
    }
}
