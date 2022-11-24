package cards.minion;

import cards.Card;
import fileio.CardInput;

public final class TheRipper extends Card {
    public TheRipper(final CardInput cardInput) {
        super(cardInput);
    }

    public TheRipper(final Card card) {
        super(card);
    }

    @Override
    public void cardUsesAbility(final Card attackedCard) {
        attackedCard.setAttackDamage(attackedCard.getAttackDamage() - 2);
    }
}
