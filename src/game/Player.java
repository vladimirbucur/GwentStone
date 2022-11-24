package game;

import cards.Card;
import cards.environment.Firestorm;
import cards.environment.HeartHound;
import cards.environment.Winterfell;
import cards.hero.EmpressThorina;
import cards.hero.GeneralKocioraw;
import cards.hero.KingMudface;
import cards.hero.LordRoyce;
import cards.minion.Disciple;
import cards.minion.Miraj;
import cards.minion.TheCursedOne;
import cards.minion.TheRipper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;
import fileio.CardInput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public final class Player {
    private int mana;
    private ArrayList<Card> deck = new ArrayList<>();
    private ArrayList<Card> handCards = new ArrayList<>();
    private Card heroCard;
    private int playedGames;
    private int wonGames;
    static final int ERRORCODE1 = 1;
    static final int ERRORCODE2 = 2;
    static final int ERRORCODE3 = 3;
    static final int ERRORCODE4 = 4;
    static final int CURRENTPLAYER1CODE = 1;
    static final int ROWCODE = 0;
    static final int ROWCODE1 = 1;
    static final int ROWCODE2 = 2;
    static final int ROWCODE3 = 3;
    static final int MAXROWSIZE = 5;
    static final int MAXMANA = 10;

    /**
     * The method by which the player chooses the deck
     * @param decks
     * @param deckIndex
     */
    public void chooseDeck(final ArrayList<ArrayList<CardInput>> decks, final int deckIndex) {
        ArrayList<CardInput> choosenDeck = decks.get(deckIndex);
        for (CardInput cardInput : choosenDeck) {
            switch (cardInput.getName()) {
                case "Sentinel", "Berserker", "Goliath", "Warden" -> this.deck.
                        add(new Card(cardInput));
                case "Miraj" -> this.deck.add(new Miraj(cardInput));
                case "The Ripper" -> this.deck.add(new TheRipper(cardInput));
                case "Disciple" -> this.deck.add(new Disciple(cardInput));
                case "The Cursed One" -> this.deck.add(new TheCursedOne(cardInput));
                case "Firestorm" -> this.deck.add(new Firestorm(cardInput));
                case "Winterfell" -> this.deck.add(new Winterfell(cardInput));
                case "Heart Hound" -> this.deck.add(new HeartHound(cardInput));
                default -> this.deck.add(new Card(cardInput));
            }
        }

        for (Card card : this.getDeck()) {
            card.cardTank();
        }
    }

    /**
     * The method that randomizes the deck
     * @param seed
     */
    public void randomisedDeck(final int seed) {
        Random random = new Random(seed);
        Collections.shuffle(this.deck, random);
    }

    /**
     * The method that performs the setting of the hero at the beginning of the match
     * @param cardInput
     */
    public void setHero(final CardInput cardInput) {
        if (cardInput.getName().equals("Lord Royce")) {
            this.heroCard = new LordRoyce(cardInput);
        }
        if (cardInput.getName().equals("Empress Thorina")) {
            this.heroCard = new EmpressThorina(cardInput);
        }
        if (cardInput.getName().equals("King Mudface")) {
            this.heroCard = new KingMudface(cardInput);
        }
        if (cardInput.getName().equals("General Kocioraw")) {
            this.heroCard = new GeneralKocioraw(cardInput);
        }
    }

    /**
     * The method that realizes the reception of mana by the player at the beginning of each round
     * @param currentRound
     */
    void receiveMana(final int currentRound) {
        if (currentRound <= MAXMANA) {
            this.setMana(this.getMana() + currentRound);
        } else {
            this.setMana(this.getMana() + MAXMANA);
        }
    }

    /**
     * The method that sets the cards as unfrozen
     * @param gameBoard
     * @param currentPlayerIndex
     */
    void setUnfreezCards(final GameBoard gameBoard, final int currentPlayerIndex) {
        if (currentPlayerIndex == CURRENTPLAYER1CODE) {
            for (Card card : gameBoard.getCards().get(ROWCODE2)) {
                card.setIsFrozen(false);
            }
            for (Card card : gameBoard.getCards().get(ROWCODE3)) {
                card.setIsFrozen(false);
            }
        } else {
            for (Card card : gameBoard.getCards().get(ROWCODE1)) {
                card.setIsFrozen(false);
            }
            for (Card card : gameBoard.getCards().get(ROWCODE)) {
                card.setIsFrozen(false);
            }
        }
    }

    /**
     * The method that sets the cards as unused
     * @param gameBoard
     * @param currentPlayerIndex
     */
    void setUnusedCards(final GameBoard gameBoard, final int currentPlayerIndex) {
        if (currentPlayerIndex == CURRENTPLAYER1CODE) {
            for (Card card : gameBoard.getCards().get(ROWCODE2)) {
                card.setIsUsed(false);
            }
            for (Card card : gameBoard.getCards().get(ROWCODE3)) {
                card.setIsUsed(false);
            }
        } else {
            for (Card card : gameBoard.getCards().get(ROWCODE1)) {
                card.setIsUsed(false);
            }
            for (Card card : gameBoard.getCards().get(ROWCODE)) {
                card.setIsUsed(false);
            }
        }
    }

    /**
     * The method that creates and returns an ObjectNode to send the output for
     * the command "getPlayerDeck"
     * @param actionsInput
     * @param objectMapper
     * @return
     */
    ObjectNode getPlayerDeck(final ActionsInput actionsInput, final ObjectMapper objectMapper) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", "getPlayerDeck");
        objectNode.put("playerIdx", actionsInput.getPlayerIdx());
        ArrayNode arrayNodeCards = objectMapper.createArrayNode();

        for (Card card : this.deck) {
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

        objectNode.set("output", arrayNodeCards);
        return objectNode;
    }

    /**
     * The method that creates and returns an ObjectNode to send the output for
     * the command "getPlayerHero"
     * @param actionsInput
     * @param objectMapper
     * @return
     */
    ObjectNode getPlayerHero(final ActionsInput actionsInput, final ObjectMapper objectMapper) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", "getPlayerHero");
        objectNode.put("playerIdx", actionsInput.getPlayerIdx());

        ObjectNode cardNode = objectMapper.createObjectNode();

        cardNode.put("mana", this.getHeroCard().getMana());
        cardNode.put("description", this.getHeroCard().getDescription());

        ArrayNode arrayNodeColors = objectMapper.createArrayNode();
        for (String color : this.getHeroCard().getColors()) {
            arrayNodeColors.add(color);
        }
        cardNode.set("colors", arrayNodeColors);
        cardNode.put("name", this.getHeroCard().getName());
        cardNode.put("health", this.getHeroCard().getHealth());

        objectNode.put("output", cardNode);

        return objectNode;
    }


    /**
     * The method that returns an error code if needed, otherwise it returns the order and
     * executes the command
     * @param actionsInput
     * @param gameBoard
     * @param currentPlayerIndex
     * @return
     */
    int placeCardCommand(final ActionsInput actionsInput, final GameBoard gameBoard,
                         final int currentPlayerIndex) {
        if (actionsInput.getHandIdx() >= this.getHandCards().size()) {
            return -1;
        }
        if (this.getHandCards().get(actionsInput.getHandIdx()).isEnvironment()) {
            return ERRORCODE1; // environment card
        }

        if (this.getHandCards().get(actionsInput.getHandIdx()).getMana() > this.getMana()) {
            return ERRORCODE2; // not enough mana
        }

        if (currentPlayerIndex == CURRENTPLAYER1CODE) {
            if (gameBoard.getCards().get(ROWCODE2).size() >= MAXROWSIZE) {
                return ERRORCODE3; // row is full
            }
            if (this.handCards.get(actionsInput.getHandIdx()).positionOnTable() == 1) {
                switch (this.handCards.get(actionsInput.getHandIdx()).getName()) {
                    case "The Ripper" -> gameBoard.getCards().get(ROWCODE2).add(new TheRipper(
                            this.handCards.get(actionsInput.getHandIdx())));
                    case "Miraj" -> gameBoard.getCards().get(ROWCODE2).add(new Miraj(
                            this.handCards.get(actionsInput.getHandIdx())));
                    case "The Cursed One" -> gameBoard.getCards().get(ROWCODE2).add(new
                            TheCursedOne(this.handCards.get(actionsInput.getHandIdx())));
                    case "Disciple" -> gameBoard.getCards().get(ROWCODE2).add(new Disciple(
                            this.handCards.get(actionsInput.getHandIdx())));
                    default -> gameBoard.getCards().get(ROWCODE2).add(new Card(
                            this.handCards.get(actionsInput.getHandIdx())));
                }
                return 0; // the card was placed
            }

            if (gameBoard.getCards().get(ROWCODE3).size() >= MAXROWSIZE) {
                return ERRORCODE3; // row is full
            }
            if (this.handCards.get(actionsInput.getHandIdx()).positionOnTable() == ROWCODE2) {
                switch (this.handCards.get(actionsInput.getHandIdx()).getName()) {
                    case "The Ripper" -> gameBoard.getCards().get(ROWCODE3).add(new TheRipper(
                            this.handCards.get(actionsInput.getHandIdx())));
                    case "Miraj" -> gameBoard.getCards().get(ROWCODE3).add(new Miraj(
                            this.handCards.get(actionsInput.getHandIdx())));
                    case "The Cursed One" -> gameBoard.getCards().get(ROWCODE3).add(new
                            TheCursedOne(this.handCards.get(actionsInput.getHandIdx())));
                    case "Disciple" -> gameBoard.getCards().get(ROWCODE3).add(new Disciple(
                            this.handCards.get(actionsInput.getHandIdx())));
                    default -> gameBoard.getCards().get(ROWCODE3).add(new Card(
                            this.handCards.get(actionsInput.getHandIdx())));
                }
                return 0; // the card was placed
            }
        } else {
            if (gameBoard.getCards().get(1).size() >= MAXROWSIZE) {
                return ERRORCODE3; // row is full
            }
            if (this.handCards.get(actionsInput.getHandIdx()).positionOnTable() == ROWCODE1) {
                switch (this.handCards.get(actionsInput.getHandIdx()).getName()) {
                    case "The Ripper" -> gameBoard.getCards().get(ROWCODE1).add(new TheRipper(
                            this.handCards.get(actionsInput.getHandIdx())));
                    case "Miraj" -> gameBoard.getCards().get(ROWCODE1).add(new Miraj(
                            this.handCards.get(actionsInput.getHandIdx())));
                    case "The Cursed One" -> gameBoard.getCards().get(ROWCODE1).add(new
                            TheCursedOne(this.handCards.get(actionsInput.getHandIdx())));
                    case "Disciple" -> gameBoard.getCards().get(ROWCODE1).add(new Disciple(
                            this.handCards.get(actionsInput.getHandIdx())));
                    default -> gameBoard.getCards().get(ROWCODE1).add(new Card(
                            this.handCards.get(actionsInput.getHandIdx())));
                }
                return 0; // the card was placed
            }

            if (gameBoard.getCards().get(0).size() >= MAXROWSIZE) {
                return ERRORCODE3; // row is full
            }
            if (this.handCards.get(actionsInput.getHandIdx()).positionOnTable() == ROWCODE2) {
                switch (this.handCards.get(actionsInput.getHandIdx()).getName()) {
                    case "The Ripper" -> gameBoard.getCards().get(ROWCODE).add(new TheRipper(
                            this.handCards.get(actionsInput.getHandIdx())));
                    case "Miraj" -> gameBoard.getCards().get(ROWCODE).add(new Miraj(
                            this.handCards.get(actionsInput.getHandIdx())));
                    case "The Cursed One" -> gameBoard.getCards().get(ROWCODE).add(new
                            TheCursedOne(this.handCards.get(actionsInput.getHandIdx())));
                    case "Disciple" -> gameBoard.getCards().get(ROWCODE).add(new Disciple(
                            this.handCards.get(actionsInput.getHandIdx())));
                    default -> gameBoard.getCards().get(ROWCODE).add(new Card(
                            this.handCards.get(actionsInput.getHandIdx())));
                }
                return 0; // the card was placed
            }
        }

        return 0; // the card was placed
    }

    /**
     * The method that creates and returns an ObjectNode to send the output for
     * the command "placeCard" in case there is an error
     * @param actionsInput
     * @param objectMapper
     * @param canPlaceCard
     * @return
     */
    ObjectNode placeCardError(final ActionsInput actionsInput, final ObjectMapper objectMapper,
                              final int canPlaceCard) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", "placeCard");
        objectNode.put("handIdx", actionsInput.getHandIdx());

        switch (canPlaceCard) {
            case ERRORCODE1 -> objectNode.put("error", "Cannot place environment card on "
                    + "table.");
            case ERRORCODE2 -> objectNode.put("error", "Not enough mana to place card on"
                    + " table.");
            case ERRORCODE3 -> objectNode.put("error", "Cannot place card on table since "
                    + "row is full.");
            default -> {
            }
        }

        return objectNode;
    }

    /**
     * The method that creates and returns an ObjectNode to send the output for
     * the command "getCardsInHand"
     * @param actionsInput
     * @param objectMapper
     * @return
     */
    ObjectNode getCardsInHand(final ActionsInput actionsInput, final ObjectMapper objectMapper) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", "getCardsInHand");
        objectNode.put("playerIdx", actionsInput.getPlayerIdx());
        ArrayNode arrayNodeCards = objectMapper.createArrayNode();

        for (Card card : this.getHandCards()) {
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

        objectNode.set("output", arrayNodeCards);
        return objectNode;
    }

    /**
     * The method that creates and returns an ObjectNode to send the output for
     * the command "getPlayerMana"
     * @param actionsInput
     * @param objectMapper
     * @return
     */
    ObjectNode getPlayerMana(final ActionsInput actionsInput, final ObjectMapper objectMapper) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", "getPlayerMana");
        objectNode.put("playerIdx", actionsInput.getPlayerIdx());
        objectNode.put("output", this.getMana());

        return objectNode;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(final int mana) {
        this.mana = mana;
    }

    public ArrayList<Card> getDeck() {
        return deck;
    }

    public void setDeck(final ArrayList<Card> deck) {
        this.deck = deck;
    }

    public ArrayList<Card> getHandCards() {
        return handCards;
    }

    public void setHandCards(final ArrayList<Card> handCards) {
        this.handCards = handCards;
    }

    public Card getHeroCard() {
        return heroCard;
    }

    public void setHeroCard(final Card heroCard) {
        this.heroCard = heroCard;
    }

    public int getPlayedGames() {
        return playedGames;
    }

    public void setPlayedGames(final int playedGames) {
        this.playedGames = playedGames;
    }

    public int getWonGames() {
        return wonGames;
    }

    public void setWonGames(final int wonGames) {
        this.wonGames = wonGames;
    }
}
