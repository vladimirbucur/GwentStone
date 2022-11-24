package game;

import cards.Card;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;
import fileio.DecksInput;
import fileio.GameInput;
import fileio.Input;
import fileio.StartGameInput;

import java.util.ArrayList;

public final class Run {
    private int currentPlayerIndex;
    private int startPlayerIndex;
    private int currentRound;
    static final int CURRENTPLAYER1CODE = 1;
    static final int CURRENTPLAYER2CODE = 2;

    /**
     * The entry point for solving the program
     * The method that performs the preparation of the players and the game, the beginning of
     * the first round, and checks which commands are received in the command line and then calls
     * the related methods
     * @param inputData
     * @param output
     * @param objectMapper
     */
    public void startGames(final Input inputData, final ArrayNode output, final ObjectMapper
            objectMapper) {
        Player player1 = new Player();
        Player player2 = new Player();
        GameBoard gameBoard = new GameBoard();

        DecksInput decks1 = inputData.getPlayerOneDecks();
        DecksInput decks2 = inputData.getPlayerTwoDecks();

        for (int i = 0; i < inputData.getGames().size(); i++) {

            GameInput game = inputData.getGames().get(i);
            StartGameInput startGame = game.getStartGame();

            gamePreparation(player1, player2, decks1, decks2, startGame);

            startFirstRound(player1, player2);

            ArrayList<ActionsInput> actions = game.getActions();

            for (ActionsInput action : actions) {
                switch (action.getCommand()) {
                    case "endPlayerTurn" -> endPlayerTurn(player1, player2, gameBoard);
                    case "placeCard" ->  placeCard(output, objectMapper, player1, player2,
                            gameBoard, action);

                    case "getCardsInHand" -> getCardsInHand(output, objectMapper, player1, player2,
                            action);
                    case "getPlayerDeck" -> getPlayerDeck(output, objectMapper, player1, player2,
                            action);
                    case "getCardsOnTable" -> getCardsOnTable(output, objectMapper, gameBoard);
                    case "getPlayerTurn" -> getPlayerTurn(output, objectMapper);
                    case "getPlayerHero" -> getPlayerHero(output, objectMapper, player1, player2,
                            action);
                    case "getPlayerMana" -> getPlayerMana(output, objectMapper, player1, player2,
                            action);
                    default -> {
                        ObjectNode objectNode = objectMapper.createObjectNode();
                        objectNode.put("command", "Invalid command");
                    }
                }
            }
        }
    }

    /**
     * The method that performs the necessary actions to start a round
     * @param player1
     * @param player2
     */
    private static void startFirstRound(final Player player1, final Player player2) {
        player1.getHandCards().add(player1.getDeck().get(0));
        player1.getDeck().remove(0);
        player2.getHandCards().add(player2.getDeck().get(0));
        player2.getDeck().remove(0);

        player1.setMana(1);
        player2.setMana(1);
    }

    /**
     * The method that realizes the preparation of a new game
     * @param player1
     * @param player2
     * @param decks1
     * @param decks2
     * @param startGame
     */
    private void gamePreparation(final Player player1, final Player player2, final DecksInput
            decks1, final DecksInput decks2, final StartGameInput startGame) {
        player1.chooseDeck(decks1.getDecks(), startGame.getPlayerOneDeckIdx());
        player2.chooseDeck(decks2.getDecks(), startGame.getPlayerTwoDeckIdx());

        player1.randomisedDeck(startGame.getShuffleSeed());
        player2.randomisedDeck(startGame.getShuffleSeed());

        player1.setHero(startGame.getPlayerOneHero());
        player2.setHero(startGame.getPlayerTwoHero());

        this.setStartPlayerIndex(startGame.getStartingPlayer());
        this.setCurrentPlayerIndex(startGame.getStartingPlayer());
        this.setCurrentRound(1);
    }

    /**
     * The method that performs the command "getCardsOnTable"
     * @param output
     * @param objectMapper
     * @param gameBoard
     */
    private static void getCardsOnTable(final ArrayNode output, final ObjectMapper objectMapper,
                                        final GameBoard gameBoard) {
        output.add(gameBoard.getCardsOnTable(objectMapper));
    }

    /**
     * The method that performs the command "getPlayerMana"
     * @param output
     * @param objectMapper
     * @param player1
     * @param player2
     * @param action
     */
    private static void getPlayerMana(final ArrayNode output, final ObjectMapper objectMapper,
                                      final Player player1, final Player player2,
                                      final ActionsInput action) {
        if (action.getPlayerIdx() == 1) {
            output.add(player1.getPlayerMana(action, objectMapper));
        } else {
            output.add(player2.getPlayerMana(action, objectMapper));
        }
    }

    /**
     * The method that performs the command "getPlayerTurn"
     * @param output
     * @param objectMapper
     */
    private void getPlayerTurn(final ArrayNode output, final ObjectMapper objectMapper) {
        ObjectNode getPlayerTurnNode = objectMapper.createObjectNode();

        getPlayerTurnNode.put("command", "getPlayerTurn");
        getPlayerTurnNode.put("output", this.getCurrentPlayerIndex());

        output.add(getPlayerTurnNode);
    }

    /**
     * The method that performs the command "getCardsInHand"
     * @param output
     * @param objectMapper
     * @param player1
     * @param player2
     * @param action
     */
    private static void getCardsInHand(final ArrayNode output, final ObjectMapper objectMapper,
                                       final Player player1, final Player player2,
                                       final ActionsInput action) {
        if (action.getPlayerIdx() == 1) {
            output.add(player1.getCardsInHand(action, objectMapper));
        } else {
            output.add(player2.getCardsInHand(action, objectMapper));
        }
    }

    /**
     * The method that performs the command "getPlayerHero"
     * @param output
     * @param objectMapper
     * @param player1
     * @param player2
     * @param action
     */
    private static void getPlayerHero(final ArrayNode output, final ObjectMapper objectMapper,
                                      final Player player1, final Player player2,
                                      final ActionsInput action) {
        if (action.getPlayerIdx() == 1) {
            output.add(player1.getPlayerHero(action, objectMapper));
        } else {
            output.add(player2.getPlayerHero(action, objectMapper));
        }
    }

    /**
     * The method that performs the command "getPlayerDeck"
     * @param output
     * @param objectMapper
     * @param player1
     * @param player2
     * @param action
     */
    private static void getPlayerDeck(final ArrayNode output, final ObjectMapper objectMapper,
                                      final Player player1, final Player player2,
                                      final ActionsInput action) {
        if (action.getPlayerIdx() == 1) {
            output.add(player1.getPlayerDeck(action, objectMapper));
        } else {
            output.add(player2.getPlayerDeck(action, objectMapper));
        }
    }

    /**
     * The method that performs the command "endPlayerTurn"
     * @param player1
     * @param player2
     * @param gameBoard
     */
    private void endPlayerTurn(final Player player1, final Player player2,
                               final GameBoard gameBoard) {
        if (this.getCurrentPlayerIndex() == CURRENTPLAYER1CODE) {
            endPlayerTurnForEachPlayer(player1, gameBoard, CURRENTPLAYER1CODE, CURRENTPLAYER2CODE);
        } else {
            endPlayerTurnForEachPlayer(player2, gameBoard, CURRENTPLAYER2CODE, CURRENTPLAYER1CODE);
        }
        if (this.getCurrentPlayerIndex() == this.getStartPlayerIndex()) {
            this.setCurrentRound(this.getCurrentRound() + 1);

            if (player1.getDeck().size() != 0) {
                player1.getHandCards().add(player1.getDeck().get(0));
                player1.getDeck().remove(0);

            }
            if (player2.getDeck().size() != 0) {
                player2.getHandCards().add(player2.getDeck().get(0));
                player2.getDeck().remove(0);
            }

            player1.receiveMana(this.getCurrentRound());
            player2.receiveMana(this.getCurrentRound());
        }
    }

    /**
     * The method that performs the command "cardUsesAbility"
     * @param player
     * @param gameBoard
     * @param currentPlayerIndex
     * @param newCurrentPlayerIndex
     */
    private void endPlayerTurnForEachPlayer(final Player player, final GameBoard gameBoard,
                                            final int currentPlayerIndex,
                                            final int newCurrentPlayerIndex) {
        player.setUnfreezCards(gameBoard, currentPlayerIndex);
        player.setUnusedCards(gameBoard, currentPlayerIndex);
        player.getHeroCard().setIsUsed(false);
        this.setCurrentPlayerIndex(newCurrentPlayerIndex);
    }

    /**
     * The method that performs the necessary actions of placing a card on the game board of the current player
     * @param output
     * @param objectMapper
     * @param player1
     * @param gameBoard
     * @param action
     */
    private void placeCardForEachPlayer(final ArrayNode output, final ObjectMapper objectMapper,
                                        final Player player1, final GameBoard gameBoard,
                                        final ActionsInput action) {
        int canPlaceCard = player1.placeCardCommand(action, gameBoard,
                this.getCurrentPlayerIndex());
        if (canPlaceCard != 0) {
            output.add(player1.placeCardError(action, objectMapper,
                    canPlaceCard));
        } else {
            player1.setMana(player1.getMana() - player1.getHandCards().
                    get(action.getHandIdx()).getMana());
            player1.getHandCards().remove(action.getHandIdx());
        }
    }

    /**
     * The method that performs the command "placeCard"
     * @param output
     * @param objectMapper
     * @param player1
     * @param player2
     * @param gameBoard
     * @param action
     */
    private void placeCard(final ArrayNode output, final ObjectMapper objectMapper,
                           final Player player1, final Player player2,
                           final GameBoard gameBoard, final ActionsInput action) {
        if (this.getCurrentPlayerIndex() == CURRENTPLAYER1CODE) {
            placeCardForEachPlayer(output, objectMapper, player1, gameBoard, action);
        } else {
            placeCardForEachPlayer(output, objectMapper, player2, gameBoard, action);
        }
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setCurrentPlayerIndex(final int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }

    public int getStartPlayerIndex() {
        return startPlayerIndex;
    }

    public void setStartPlayerIndex(int startPlayerIndex) {
        this.startPlayerIndex = startPlayerIndex;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(final int currentRound) {
        this.currentRound = currentRound;
    }

}
