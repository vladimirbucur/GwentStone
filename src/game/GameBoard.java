package game;

import cards.Card;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;

public final class GameBoard {
    private final int noRows = 4;
    private ArrayList<ArrayList<Card>> cards;
    static final int ROWCODE4 = 4;

    public GameBoard() {
        this.cards = new ArrayList<>();
        for (int i = 0; i < this.noRows; i++) {
            this.cards.add(new ArrayList<>());
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
