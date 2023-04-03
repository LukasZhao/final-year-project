package games.catan.actions;

import core.AbstractGameState;
import core.components.Deck;
import games.catan.CatanGameState;
import games.catan.components.CatanCard;

import java.util.Optional;

public class PlayKnightCard extends MoveRobberAndSteal {

    public PlayKnightCard(int x, int y, int player, int targetPlayer) {
        super(x, y, player, targetPlayer);
    }

    @Override
    public boolean execute(AbstractGameState gs) {
        CatanGameState cgs = (CatanGameState)gs;
        Deck<CatanCard> playerDevDeck = cgs.getPlayerDevCards(player);

        Optional<CatanCard> knight = playerDevDeck.stream()
                .filter(card -> card.cardType == CatanCard.CardType.KNIGHT_CARD)
                .findFirst();
        if (knight.isPresent()){
            CatanCard card = knight.get();
            cgs.addKnight(player);
            playerDevDeck.remove(card);
            cgs.setDevelopmentCardPlayed(true);
        } else {
            throw new AssertionError("Cannot use a Knight card that is not in hand.");
        }

        return super.execute(gs);
    }

    @Override
    public PlayKnightCard copy() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof PlayKnightCard && super.equals(o);
    }

    @Override
    public String toString() {
        return player + " plays Knight Card";
    }
    @Override
    public String getString(AbstractGameState gameState) {
        return toString();
    }
}
