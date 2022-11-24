package cards.environment;

import cards.Card;
import fileio.CardInput;
import game.GameBoard;

public final class HeartHound extends Card {
    static final int MAXROWSIZE = 5;
    public HeartHound(final CardInput cardInput) {
        super(cardInput);
    }

    @Override
    public void useEnvironment(final GameBoard gameBoard, final int row) {
        int maxHealth = 0;
        for (Card card : gameBoard.getCards().get(row)) {
            if (card.getHealth() > maxHealth) {
                maxHealth = card.getHealth();
            }
        }

        Card maxHealthCard = null;
        for (Card card : gameBoard.getCards().get(row)) {
            if (card.getHealth() == maxHealth) {
                maxHealthCard = new Card(card);
            }
        }

        int mirroredRow = gameBoard.getNoRows() - 1 - row;
        if (gameBoard.getCards().get(mirroredRow).size() >= MAXROWSIZE) {
            return;
        }

        gameBoard.getCards().get(mirroredRow).add(maxHealthCard);
    }
}
