package cards.hero;

import cards.Card;
import fileio.CardInput;

import java.util.ArrayList;

public final class LordRoyce extends Card {
    static final int MAXHEROHEALTH = 30;
    public LordRoyce(final CardInput cardInput) {
        super(cardInput);
        this.setHealth(MAXHEROHEALTH);
    }

    @Override
    public void heroUsesAbility(final ArrayList<Card> attackedRow) {
        int maxAttackDamage = 0;
        for (Card card : attackedRow) {
            if (card.getAttackDamage() > maxAttackDamage) {
                maxAttackDamage = card.getAttackDamage();
            }
        }

        Card maxAttackDamageCard = null;
        for (Card card : attackedRow) {
            if (card.getAttackDamage() == maxAttackDamage) {
                maxAttackDamageCard = card;
            }
        }

        maxAttackDamageCard.setIsFrozen(true);
    }
}
