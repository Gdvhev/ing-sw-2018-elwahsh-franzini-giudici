package it.polimi.se2018.view.tools;

import it.polimi.se2018.model.IntColorPair;

/**
 * Interface to define RoundTrackerViewCreator's Object
 *
 * @author Mathyas Giudici
 */

public abstract class RoundTrackerViewCreator<E> {

    protected static final int MAX_DIE_IN_ROUNDS = 9;

    protected DieViewCreator dieViewCreator;

    protected int round;

    protected IntColorPair[][] roundTracker;

    /**
     * Basic Class constructor that initializes elements at default value
     */
    protected RoundTrackerViewCreator() {
        this.round = -1;
        this.roundTracker = null;
    }

    /**
     * Class constructor
     *
     * @param round        contains the round
     * @param roundTracker contains the round tracker
     */
    protected RoundTrackerViewCreator(int round, IntColorPair[][] roundTracker) {
        this.round = round;
        this.roundTracker = roundTracker;
    }

    /**
     * Use to show the current round tracker
     *
     * @return round tracker
     */
    public abstract E display();

    /**
     * Getter for round
     *
     * @return the current round
     */
    public synchronized int getRound() {
        return round;
    }

    /**
     * Setter for round
     *
     * @param round contains the round to set
     */
    public synchronized void setRound(int round) {
        this.round = round;
    }

    /**
     * Getter for round tracker
     *
     * @return the round tracker
     */
    public synchronized IntColorPair[][] getRoundTracker() {
        return roundTracker;
    }

    /**
     * Setter for round tracker
     *
     * @param roundTracker contains the round tracker to set
     */
    public synchronized void setRoundTracker(IntColorPair[][] roundTracker) {
        this.roundTracker = roundTracker;
    }
}
