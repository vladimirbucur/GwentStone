package game;

import cards.Card;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;

import java.util.ArrayList;

public final class GameBoard {
    private final int noRows = 4;
    private ArrayList<ArrayList<Card>> cards;
    static final int ERRORCODE1 = 1;
    static final int ERRORCODE2 = 2;
    static final int ERRORCODE3 = 3;
    static final int ERRORCODE4 = 4;
    static final int ERRORCODE5 = 5;

    static final int CURRENTPLAYER1CODE = 1;
    static final int CURRENTPLAYER2CODE = 2;
    static final int ROWCODE = 0;
    static final int ROWCODE1 = 1;
    static final int ROWCODE2 = 2;
    static final int ROWCODE3 = 3;
    static final int ROWCODE4 = 4;

    public GameBoard() {
        this.cards = new ArrayList<>();
        for (int i = 0; i < this.noRows; i++) {
            this.cards.add(new ArrayList<>());
        }

    }

    /**
     * The method that performs the deletion of cards from the game board
     */
    public void newGame() {
        ArrayList<Card> row;
        for (int i = 0; i < this.getNoRows(); i++) {
            row = this.getCards().get(i);
            while (!row.isEmpty()) {
                row.remove(0);
            }
        }
    }

    /**
     * The method that creates and returns an ObjectNode to send the output for
     * the command "getCardsOnTable"
     * @param objectMapper
     * @return
     */
    ObjectNode getCardsOnTable(final ObjectMapper objectMapper) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        ArrayList<ArrayList<Card>> cardsAux = new ArrayList<>();
        for (int i = 0; i < ROWCODE4; i++) {
            cardsAux.add(new ArrayList<>());
            for (int j = 0; j < this.getCards().get(i).size(); j++) {
                cardsAux.get(i).add(new Card(this.getCards().get(i).get(j)));
            }
        }

        objectNode.put("command", "getCardsOnTable");
        objectNode.putPOJO("output", cardsAux);
        return objectNode;
    }

    /**
     * The method that creates and returns an ObjectNode to send the output for
     * the command "getCardAtPosition"
     * @param actionsInput
     * @param objectMapper
     * @return
     */
    ObjectNode getCardAtPosition(final ActionsInput actionsInput, final ObjectMapper
            objectMapper) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", "getCardAtPosition");
        objectNode.put("x", actionsInput.getX());
        objectNode.put("y", actionsInput.getY());
        if (this.getCards().get(actionsInput.getX()).size() < actionsInput.getY()) {
            objectNode.put("output", "No card available at that position.");
            return objectNode;
        }

        if (actionsInput.getY() >= this.getCards().get(actionsInput.getX()).size()) {
            return null;
        }
        objectNode.putPOJO("output", new Card(this.getCards().get(actionsInput.getX()).
                get(actionsInput.getY())));
        return objectNode;
    }

    /**
     * The method that creates and returns an ObjectNode to send the output for
     * the command "getFrozenCardsOnTable"
     * @param objectMapper
     * @return
     */
    ObjectNode getFrozenCardsOnTable(final ObjectMapper objectMapper) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", "getFrozenCardsOnTable");
        ArrayNode arrayNodeCards = objectMapper.createArrayNode();

        for (ArrayList<Card> cardsRow : this.getCards()) {
            for (Card card : cardsRow) {
                if (card.getIsFrozen()) {
                    ObjectNode cardNode = objectMapper.createObjectNode();
                    cardNode.put("mana", card.getMana());
                    if (!card.isEnvironment() && !card.isHero()) {
                        cardNode.put("attackDamage", card.getAttackDamage());
                        cardNode.put("health", card.getHealth());
                    }
                    cardNode.put("description", card.getDescription());

                    ArrayNode colors = objectMapper.createArrayNode();
                    for (String color : card.getColors()) {
                        colors.add(color);
                    }

                    cardNode.set("colors", colors);

                    cardNode.put("name", card.getName());

                    arrayNodeCards.add(cardNode);
                }
            }
        }
        objectNode.set("output", arrayNodeCards);
        return objectNode;
    }

    /**
     * The method that returns an error code if needed, otherwise it returns the order and
     * executes the command
     * @param actionsInput
     * @param currentPlayerIndex
     * @return
     */
    int cardUsesAttackCommand(final ActionsInput actionsInput, final int currentPlayerIndex) {
        if (actionsInput.getCardAttacked().getY() >= this.getCards().get(actionsInput.
                getCardAttacked().getX()).size()) {
            return -1;
        }
        if (actionsInput.getCardAttacker().getY() >= this.getCards().get(actionsInput.
                getCardAttacker().getX()).size()) {
            return -1;
        }

        Card attackerCard = this.getCards().get(actionsInput.getCardAttacker().getX()).
                get(actionsInput.getCardAttacker().getY());
        Card attackedCard = this.getCards().get(actionsInput.getCardAttacked().getX()).
                get(actionsInput.getCardAttacked().getY());

        if (attackerCard.getIsFrozen()) {
            return ERRORCODE1; // atacker card is frozen;
        }
        if (attackerCard.getIsUsed()) {
            return ERRORCODE2; // atacker card is used this turn
        }
        if (currentPlayerIndex == CURRENTPLAYER1CODE && (actionsInput.getCardAttacked().
                getX() == ROWCODE2 || actionsInput.getCardAttacked().getX() == ROWCODE3)) {
            return ERRORCODE3; // Attacked card does not belong to the enemy.
        }
        if (currentPlayerIndex == CURRENTPLAYER2CODE && (actionsInput.getCardAttacked().
                getX() == ROWCODE || actionsInput.getCardAttacked().getX() == ROWCODE1)) {
            return ERRORCODE3; // Attacked card does not belong to the enemy.
        }

        if (currentPlayerIndex == CURRENTPLAYER1CODE) {
            for (Card card : this.getCards().get(1)) {
                if (!attackedCard.getIsTank() && card.getIsTank()) {
                    return ERRORCODE4; // Attacked card is not of type 'Tank'.
                }
            }
        } else {
            for (Card card : this.getCards().get(2)) {
                if (!attackedCard.getIsTank() && card.getIsTank()) {
                    return ERRORCODE4; // Attacked card is not of type 'Tank'.
                }
            }
        }

        attackedCard.setHealth(attackedCard.getHealth() - attackerCard.getAttackDamage());
        if (attackedCard.getHealth() <= 0) {
            this.cards.get(actionsInput.getCardAttacked().getX()).remove(actionsInput.
                    getCardAttacked().getY());
        }
        attackerCard.setIsUsed(true);
        return 0; // card used attack
    }

    /**
     * The method that creates and returns an ObjectNode to send the output for the command
     * "cardUsesAttack" in case there is an error
     * @param actionsInput
     * @param objectMapper
     * @param canUsesAttack
     * @return
     */
    ObjectNode cardUsesAttackError(final ActionsInput actionsInput, final ObjectMapper
            objectMapper, final int canUsesAttack) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", "cardUsesAttack");
        objectNode.putPOJO("cardAttacker", actionsInput.getCardAttacker());
        objectNode.putPOJO("cardAttacked", actionsInput.getCardAttacked());

        switch (canUsesAttack) {
            case ERRORCODE1 -> objectNode.put("error", "Attacker card is frozen.");
            case ERRORCODE2 -> objectNode.put("error", "Attacker card has already attacked "
                    + "this turn.");
            case ERRORCODE3 -> objectNode.put("error", "Attacked card does not belong to "
                    + "the enemy.");
            case ERRORCODE4 -> objectNode.put("error", "Attacked card is not of type 'Tank'.");
            default -> {
            }
        }

        return objectNode;
    }

    /**
     * The method that returns an error code if needed, otherwise it returns the order and
     * executes the command
     * @param actionsInput
     * @param currentPlayerIndex
     * @return
     */
    int cardUsesAbilityCommand(final ActionsInput actionsInput, final int currentPlayerIndex) {
        if (actionsInput.getCardAttacked().getY() >= this.getCards().get(actionsInput.
                getCardAttacked().getX()).size()) {
            return -1;
        }
        if (actionsInput.getCardAttacker().getY() >= this.getCards().get(actionsInput.
                getCardAttacker().getX()).size()) {
            return -1;
        }

        Card attackerCard = this.getCards().get(actionsInput.getCardAttacker().getX()).
                get(actionsInput.getCardAttacker().getY());
        Card attackedCard = this.getCards().get(actionsInput.getCardAttacked().getX()).
                get(actionsInput.getCardAttacked().getY());

        if (attackerCard.getIsFrozen()) {
            return ERRORCODE1; // atacker card is frozen;
        }
        if (attackerCard.getIsUsed()) {
            return ERRORCODE2; // atacker card is used this turn
        }

        if (attackerCard.getName().equals("Disciple")) {
            if (currentPlayerIndex == CURRENTPLAYER1CODE && (actionsInput.getCardAttacked().
                    getX() == ROWCODE || actionsInput.getCardAttacked().getX() == ROWCODE1)) {
                return ERRORCODE3; // Attacked card does not belong to the current player.
            }
            if (currentPlayerIndex == CURRENTPLAYER2CODE && (actionsInput.getCardAttacked().
                    getX() == ROWCODE2 || actionsInput.getCardAttacked().getX() == ROWCODE3)) {
                return ERRORCODE3; // Attacked card does not belong to the current player.
            }
        } else if (attackerCard.getName().equals("The Ripper")
                || attackerCard.getName().equals("Miraj")
                || attackerCard.getName().equals("The Cursed One")) {
            if (currentPlayerIndex == CURRENTPLAYER1CODE && (actionsInput.getCardAttacked().
                    getX() == ROWCODE2 || actionsInput.getCardAttacked().getX() == ROWCODE3)) {
                return ERRORCODE4; // Attacked card does not belong to the enemy.
            }
            if (currentPlayerIndex == CURRENTPLAYER2CODE && (actionsInput.getCardAttacked().
                    getX() == ROWCODE || actionsInput.getCardAttacked().getX() == ROWCODE1)) {
                return ERRORCODE4; // Attacked card does not belong to the enemy.
            }

            if (currentPlayerIndex == CURRENTPLAYER1CODE) {
                for (Card card : this.getCards().get(ROWCODE1)) {
                    if (!attackedCard.getIsTank() && card.getIsTank()) {
                        return ERRORCODE5; // Attacked card is not of type 'Tank'.
                    }
                }
            } else if (currentPlayerIndex == CURRENTPLAYER2CODE) {
                for (Card card : this.getCards().get(ROWCODE2)) {
                    if (!attackedCard.getIsTank() && card.getIsTank()) {
                        return ERRORCODE5; // Attacked card is not of type 'Tank'.
                    }
                }
            }
        }

        attackerCard.cardUsesAbility(attackedCard);
        attackerCard.setIsUsed(true);
        if (attackedCard.getHealth() <= 0) {
            this.getCards().get(actionsInput.getCardAttacked().getX()).remove(actionsInput.
                    getCardAttacked().getY());
        }

        if (attackedCard.getAttackDamage() < 0) {
            attackedCard.setAttackDamage(0);
        }

        return 0; // card used ability
    }

    /**
     * The method that creates and returns an ObjectNode to send the output for the command
     * "cardUsesAbility" in case there is an error
     * @param actionsInput
     * @param objectMapper
     * @param canUsesAbility
     * @return
     */
    ObjectNode cardUsesAbilityError(final ActionsInput actionsInput, final ObjectMapper
            objectMapper, final int canUsesAbility) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", "cardUsesAbility");
        objectNode.putPOJO("cardAttacker", actionsInput.getCardAttacker());
        objectNode.putPOJO("cardAttacked", actionsInput.getCardAttacked());

        switch (canUsesAbility) {
            case ERRORCODE1 -> objectNode.put("error", "Attacker card is frozen.");
            case ERRORCODE2 -> objectNode.put("error", "Attacker card has already attacked "
                    + "this turn.");
            case ERRORCODE3 -> objectNode.put("error", "Attacked card does not belong to "
                    + "the current player.");
            case ERRORCODE4 -> objectNode.put("error", "Attacked card does not belong to the "
                    + "enemy.");
            case ERRORCODE5 -> objectNode.put("error", "Attacked card is not of type 'Tank'.");
            default -> {

            }
        }

        return objectNode;
    }

    public int getNoRows() {
        return noRows;
    }

    public ArrayList<ArrayList<Card>> getCards() {
        return cards;
    }

    public void setCards(final ArrayList<ArrayList<Card>> cards) {
        this.cards = cards;
    }
}
