package cards.hero;

import cards.Card;
import fileio.CardInput;

import java.util.ArrayList;

public final class GeneralKocioraw extends Card {
    static final int MAXHEROHEALTH = 30;
    public GeneralKocioraw(final CardInput cardInput) {
        super(cardInput);
        this.setHealth(MAXHEROHEALTH);
    }

    @Override
    public void heroUsesAbility(final ArrayList<Card> attackedRow) {
        for (Card card : attackedRow) {
            card.setAttackDamage(card.getAttackDamage() + 1);
        }
    }
}
