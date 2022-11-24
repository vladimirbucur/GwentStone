package cards.environment;

import cards.Card;
import fileio.CardInput;
import game.GameBoard;

public final class Winterfell extends Card {
    public Winterfell(final CardInput cardInput) {
        super(cardInput);
    }

    @Override
    public void useEnvironment(final GameBoard gameBoard, final int row) {
        for (Card card : gameBoard.getCards().get(row)) {
            card.setIsFrozen(true);
        }
    }
}
