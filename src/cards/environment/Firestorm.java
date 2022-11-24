package cards.environment;

import cards.Card;
import fileio.CardInput;
import game.GameBoard;

import java.util.ArrayList;

public final class Firestorm extends Card {
    public Firestorm(final CardInput cardInput) {
        super(cardInput);
    }

    @Override
    public void useEnvironment(final GameBoard gameBoard, final int row) {
        ArrayList<Card> copyRow = new ArrayList<>(gameBoard.getCards().get(row));
        for (Card card : copyRow) {
            card.setHealth(card.getHealth() - 1);
            if (card.getHealth() <= 0) {
                gameBoard.getCards().get(row).remove(card);
            }
        }
    }
}
