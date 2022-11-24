package cards.hero;

import cards.Card;
import fileio.CardInput;

import java.util.ArrayList;

public final class EmpressThorina extends Card {
    static final int MAXHEROHEALTH = 30;
    public EmpressThorina(final CardInput cardInput) {
        super(cardInput);
        this.setHealth(MAXHEROHEALTH);
    }

    @Override
    public void heroUsesAbility(final ArrayList<Card> attackedRow) {
        int maxHealth = 0;
        for (Card card : attackedRow) {
            if (card.getHealth() > maxHealth) {
                maxHealth = card.getHealth();
            }
        }

        Card maxHealthCard = null;
        for (Card card : attackedRow) {
            if (card.getHealth() == maxHealth) {
                maxHealthCard = card;
            }
        }

        attackedRow.remove(maxHealthCard);
    }
}
