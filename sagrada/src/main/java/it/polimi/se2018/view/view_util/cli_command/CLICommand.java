package it.polimi.se2018.view.view_util.cli_command;

import it.polimi.se2018.view.app.CLIApp;

/**
 * Class to handle CLI command during game
 *
 * @author Mathyas Giudici
 */

public abstract class CLICommand {

    protected String message;

    protected CLIApp app;

    /**
     * Class constructor
     *
     * @param message contains the command text displayed
     * @param app     contains the CLIApp reference
     */
    public CLICommand(String message, CLIApp app) {
        this.message = message;
        this.app = app;
    }

    /**
     * To perform the action of the command
     */
    public abstract void doAction();

    /**
     * Returns the message of the command in System.out
     * @return the message to display
     */
    public String display() {
        return this.message;
    }
}