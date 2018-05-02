package it.polimi.se2018.view;

import it.polimi.se2018.util.MessageTypes;

/**
 * Interface for Controller -> View communication
 * @author Mathyas Giudici
 */
public interface ControllerMessage {

    /**
     * To receive a message
     * @param sender contains the message's sender
     * @param type contains the message's type
     * @param message contains the message
     */
    void receiveMessagge(String sender, MessageTypes type, String message );
}
