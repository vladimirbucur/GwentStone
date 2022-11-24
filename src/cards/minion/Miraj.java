package cards.minion;

import cards.Card;
import fileio.CardInput;

public final class Miraj extends Card {
    public Miraj(final CardInput cardInput) {
        super(cardInput);
    }

    public Miraj(final Card card) {
        super(card);
    }

    @Override
    public void cardUsesAbility(final Card attackedCard) {
        int tempHealth = this.getHealth();
        this.setHealth(attackedCard.getHealth());
        attackedCard.setHealth(tempHealth);
    }
}
