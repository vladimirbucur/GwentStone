package cards.hero;

import cards.Card;
import fileio.CardInput;

import java.util.ArrayList;

public final class KingMudface extends Card {
    static final int MAXHEROHEALTH = 30;
    public KingMudface(final CardInput cardInput) {
        super(cardInput);
        this.setHealth(MAXHEROHEALTH);
    }

    @Override
    public void heroUsesAbility(final ArrayList<Card> attackedRow) {
        for (Card card : attackedRow) {
            card.setHealth(card.getHealth() + 1);
        }
    }
}
