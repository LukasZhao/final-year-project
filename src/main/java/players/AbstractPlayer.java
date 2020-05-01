package players;

import actions.IAction;
import observations.Observation;

import java.util.List;

public abstract class AbstractPlayer {

    public final int playerID;

    protected AbstractPlayer(int playerID) {
        this.playerID = playerID;
    }

    /**
     * Initialize agent given an observation of the initial game-state.
     * e.g. load weights, initialize neural network
     * @param observation observation of the initial game-state
     */
    public abstract void initializePlayer(Observation observation);

    /**
     * Finalize agent given an observation of the final game-state.
     * e.g. store variables after training, modify weights, etc.
     * @param observation observation of the final game-state
     */
    public abstract void finalizePlayer(Observation observation);

    /**
     * Initialize agent given an observation of the initial game-state.
     * @param observation observation of the initial game-state
     */
    public abstract int getAction(Observation observation, List<IAction> actions);
}
