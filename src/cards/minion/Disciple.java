package cards.minion;

import cards.Card;
import fileio.CardInput;

public final class Disciple extends Card {
    public Disciple(final CardInput cardInput) {
        super(cardInput);
    }

    public Disciple(final Card card) {
        super(card);
    }

    @Override
    public void cardUsesAbility(final Card attackedCard) {
        attackedCard.setHealth(attackedCard.getHealth() + 2);
    }
}
