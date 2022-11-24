package cards.minion;

import cards.Card;
import fileio.CardInput;

public final class TheCursedOne extends Card {
    public TheCursedOne(final CardInput cardInput) {
        super(cardInput);
    }

    public TheCursedOne(final Card card) {
        super(card);
    }

    @Override
    public void cardUsesAbility(final Card attackedCard) {
        int temp = attackedCard.getHealth();
        attackedCard.setHealth(attackedCard.getAttackDamage());
        attackedCard.setAttackDamage(temp);
    }
}
