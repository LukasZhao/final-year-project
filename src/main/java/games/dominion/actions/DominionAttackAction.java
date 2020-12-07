package games.dominion.actions;

import core.AbstractGameState;
import games.dominion.*;
import games.dominion.cards.CardType;

import java.util.Arrays;
import java.util.Objects;

public abstract class DominionAttackAction extends DominionAction implements IExtendedSequence {

    public DominionAttackAction(CardType type, int playerId) {
        super(type, playerId);
    }

    int currentTarget;
    Boolean[] reactionsInitiated;
    Boolean[] attacksComplete;

    @Override
    public boolean execute(AbstractGameState ags) {
        // first we do the housekeeping from DominionAction.
        // This will include calling _execute on the concrete Attack implementation to account
        // for any state changes due to the immediate effects of the card on the player.
        super.execute(ags);
        // then what we need to do is the cycling through each of the other players to allow them
        // to play Reaction cards, and then suffer the direct attack effects of the card.
        DominionGameState state = (DominionGameState) ags;
        state.setActionInProgress(this);
        currentTarget = (player + 1) % state.getNPlayers();
        reactionsInitiated = new Boolean[state.getNPlayers()];
        nextPhaseOfReactionAttackCycle(state);
        return true;
    }

    private void nextPhaseOfReactionAttackCycle(DominionGameState state) {
        // we cycle through each other player in turn to give them the opportunity to play Reactions
        // Then, after they have played their Reactions, we execute the actual attack code (in the sub-class)
        // if they are undefended
        do {
            if (reactionsInitiated[currentTarget] && attacksComplete[currentTarget]) {
                throw new AssertionError("Should not be here");
            }
            if (reactionsInitiated[currentTarget] && !attacksComplete[currentTarget]) {
                // we need to finish off attack via followOnActions
                if (isAttackComplete(currentTarget)) {
                    attacksComplete[currentTarget] = true;
                    currentTarget = (currentTarget + 1) % state.getNPlayers();
                    continue;
                } else {
                    return;
                }
            }

            // if we are here, then we need to initiate the AttackReaction for the currentTarget
            AttackReaction reaction = new AttackReaction(state, player, currentTarget);
            reactionsInitiated[currentTarget] = true;
            if (!reaction.executionComplete(state)) {
                state.setActionInProgress(reaction);
                return;
            } else {
                if (state.isDefended(currentTarget)) {
                    attacksComplete[currentTarget] = true;
                } else {
                    executeAttack(currentTarget, state);
                    if (isAttackComplete(currentTarget)) {
                        attacksComplete[currentTarget] = true;
                    } else {
                        return;
                    }
                }
            }
            currentTarget = (currentTarget + 1) % state.getNPlayers();
        } while (!(state.currentActionInProgress() == this) && currentTarget != player);
    }

    @Override
    public boolean executionComplete(DominionGameState state) {
        // this will only be called if this is top of the Reaction Stack (i.e. if the last reaction has completed)
        return Arrays.stream(attacksComplete).allMatch(c -> c);
    }

    @Override
    public DominionAttackAction copy() {
        DominionAttackAction retValue = this._copy();
        retValue.currentTarget = currentTarget;
        retValue.reactionsInitiated = reactionsInitiated != null ? reactionsInitiated.clone() : null;
        retValue.attacksComplete = attacksComplete != null ? attacksComplete.clone() : null;
        return retValue;
    }

    /**
     * Delegates copying of the state of the subclass.
     * The returned value will then be updated with the copied state of DominionAttackAction (in copy())
     *
     * @return Instance of the sub-class with all local state copied
     */
    public abstract DominionAttackAction _copy();

    public abstract void executeAttack(int victim, DominionGameState state);

    public abstract boolean isAttackComplete(int currentTarget);

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DominionAttackAction) {
            DominionAttackAction other = (DominionAttackAction) obj;
            return other.type == type && other.player == player
                    && other.currentTarget == currentTarget
                    && Arrays.equals(reactionsInitiated, other.reactionsInitiated)
                    && Arrays.equals(attacksComplete, other.attacksComplete);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, type, currentTarget, reactionsInitiated, attacksComplete);
    }
}
