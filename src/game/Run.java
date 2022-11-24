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
            newGame(player1, player2, gameBoard);

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
                    case "cardUsesAttack" -> cardUsesAttack(output, objectMapper, gameBoard,
                            action);
                    case "cardUsesAbility" -> cardUsesAbility(output, objectMapper, gameBoard,
                            action);
                    case "useAttackHero" -> useAttackHero(output, objectMapper, player1, player2,
                            gameBoard, action);
                    case "useHeroAbility" -> useHeroAbility(output, objectMapper, player1, player2,
                            gameBoard, action);
                    case "useEnvironmentCard" -> useEnvironmentCard(output, objectMapper, player1,
                            player2, gameBoard, action);
                    case "getCardsInHand" -> getCardsInHand(output, objectMapper, player1, player2,
                            action);
                    case "getPlayerDeck" -> getPlayerDeck(output, objectMapper, player1, player2,
                            action);
                    case "getCardsOnTable" -> getCardsOnTable(output, objectMapper, gameBoard);
                    case "getPlayerTurn" -> getPlayerTurn(output, objectMapper);
                    case "getPlayerHero" -> getPlayerHero(output, objectMapper, player1, player2,
                            action);
                    case "getCardAtPosition" -> getCardAtPosition(output, objectMapper, gameBoard,
                            action);
                    case "getPlayerMana" -> getPlayerMana(output, objectMapper, player1, player2,
                            action);
                    case "getEnvironmentCardsInHand" -> getEnvironmentCardsInHand(output,
                            objectMapper, player1, player2, action);
                    case "getFrozenCardsOnTable" -> getFrozenCardsOnTable(output, objectMapper,
                            gameBoard);
                    case "getTotalGamesPlayed" -> getTotalGamesPlayed(output, objectMapper,
                            player1);
                    case "getPlayerOneWins" -> getPlayerOneWins(output, objectMapper,
                            player1);
                    case "getPlayerTwoWins" -> getPlayerTwoWins(output, objectMapper,
                            player2);
                    default -> {
                        ObjectNode objectNode = objectMapper.createObjectNode();
                        objectNode.put("command", "Invalid command");
                    }
                }
            }
        }
    }

    /**
     * The method that prepares the players and the game board for the start of a new match
     * @param player1
     * @param player2
     * @param gameBoard
     */
    private static void newGame(final Player player1, final Player player2,
                                final GameBoard gameBoard) {
        player1.newGame();
        player2.newGame();
        gameBoard.newGame();
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
     * The method that sends to the output how many matches player 2 won
     * @param output
     * @param objectMapper
     * @param player2
     */
    private static void getPlayerTwoWins(final ArrayNode output, final ObjectMapper objectMapper,
                                         final Player player2) {
        output.add(player2.getPlayerTwoWins(objectMapper));
    }

    /**
     * The method that sends to the output how many matches player 1 won
     * @param output
     * @param objectMapper
     * @param player1
     */
    private static void getPlayerOneWins(final ArrayNode output, final ObjectMapper objectMapper,
                                         final Player player1) {
        output.add(player1.getPlayerOneWins(objectMapper));
    }


    /**
     * The method that sends to the output how many matches have been played up to that moment
     * @param output
     * @param objectMapper
     * @param player1
     */
    private static void getTotalGamesPlayed(final ArrayNode output, final ObjectMapper
                                            objectMapper, final Player player1) {
        output.add(player1.getTotalGamesPlayed(objectMapper));
    }

    /**
     *
     * @param output
     * @param objectMapper
     * @param player1
     * @param player2
     * @param gameBoard
     * @param action
     */
    private void useHeroAbility(final ArrayNode output, final ObjectMapper objectMapper,
                                final Player player1, final Player player2,
                                final GameBoard gameBoard, final ActionsInput action) {
        if (currentPlayerIndex == CURRENTPLAYER1CODE) {
            useHeroAbilityForEachPlayer(output, objectMapper, player1, gameBoard, action);
        } else {
            useHeroAbilityForEachPlayer(output, objectMapper, player2, gameBoard, action);
        }
    }

    /**
     * The method that performs the necessary actions for using the current player's hero ability
     * @param output
     * @param objectMapper
     * @param player1
     * @param gameBoard
     * @param action
     */
    private void useHeroAbilityForEachPlayer(final ArrayNode output, final ObjectMapper
                                             objectMapper, final Player player1, final GameBoard
                                                     gameBoard, final ActionsInput action) {
        int canUseHeroAbility = player1.usesHeroAbilityCommand(action,
                gameBoard, currentPlayerIndex);
        if (canUseHeroAbility != 0) {
            output.add(player1.usesHeroAbilityError(action, objectMapper,
                    canUseHeroAbility));
        }
    }

    /**
     * The method that performs the command "useAttackHero"
     * @param output
     * @param objectMapper
     * @param player1
     * @param player2
     * @param gameBoard
     * @param action
     */
    private void useAttackHero(final ArrayNode output, final ObjectMapper objectMapper,
                               final Player player1, final Player player2,
                               final GameBoard gameBoard, final ActionsInput action) {
        if (this.currentPlayerIndex == CURRENTPLAYER1CODE) {
            this.attackHeroForEachPlayer(output, objectMapper, player1, player2, gameBoard,
                    action, "one");
        } else {
            this.attackHeroForEachPlayer(output, objectMapper, player2, player1, gameBoard,
                    action, "two");
        }
    }

    /**
     * The method that performs the command "cardUsesAbility"
     * @param output
     * @param objectMapper
     * @param gameBoard
     * @param action
     */
    private void cardUsesAbility(final ArrayNode output, final ObjectMapper objectMapper,
                                 final GameBoard gameBoard, final ActionsInput action) {
        int canUsesAbility = gameBoard.cardUsesAbilityCommand(action,
                this.getCurrentPlayerIndex());
        if (canUsesAbility != 0) {
            output.add(gameBoard.cardUsesAbilityError(action, objectMapper,
                    canUsesAbility));
        }
    }

    /**
     * The method that performs the command "cardUsesAttack"
     * @param output
     * @param objectMapper
     * @param gameBoard
     * @param action
     */
    private void cardUsesAttack(final ArrayNode output, final ObjectMapper objectMapper,
                                final GameBoard gameBoard, final ActionsInput action) {
        int canUseAttack = gameBoard.cardUsesAttackCommand(action,
                this.getCurrentPlayerIndex());
        if (canUseAttack != 0) {
            output.add(gameBoard.cardUsesAttackError(action, objectMapper,
                    canUseAttack));
        }
    }

    /**
     * The method that performs the command "getFrozenCardsOnTable"
     * @param output
     * @param objectMapper
     * @param gameBoard
     */
    private static void getFrozenCardsOnTable(final ArrayNode output, final ObjectMapper
                                              objectMapper, final GameBoard gameBoard) {
        output.add(gameBoard.getFrozenCardsOnTable(objectMapper));
    }

    /**
     * The method that performs the command "getCardAtPosition"
     * @param output
     * @param objectMapper
     * @param gameBoard
     * @param action
     */
    private static void getCardAtPosition(final ArrayNode output, final ObjectMapper objectMapper,
                                          final GameBoard gameBoard, final ActionsInput action) {
        output.add(gameBoard.getCardAtPosition(action, objectMapper));
    }

    /**
     * The method that performs the command "getEnvironmentCardsInHand"
     * @param output
     * @param objectMapper
     * @param player1
     * @param player2
     * @param action
     */
    private static void getEnvironmentCardsInHand(final ArrayNode output, final ObjectMapper
                                                  objectMapper, final Player player1, final Player
                                                  player2, final ActionsInput action) {
        if (action.getPlayerIdx() == 1) {
            output.add(player1.getEnvironmentCardsInHand(action,
                    objectMapper));
        } else {
            output.add(player2.getEnvironmentCardsInHand(action,
                    objectMapper));
        }
    }

    /**
     * The method that performs the command "useEnvironmentCard"
     * @param output
     * @param objectMapper
     * @param player1
     * @param player2
     * @param gameBoard
     * @param action
     */
    private void useEnvironmentCard(final ArrayNode output, final ObjectMapper objectMapper,
                                    final Player player1, final Player player2,
                                    final GameBoard gameBoard, final ActionsInput action) {
        if (this.getCurrentPlayerIndex() == CURRENTPLAYER1CODE) {
            useEnvironmentCardForEachPlayer(output, objectMapper, player1, gameBoard, action);
        } else {
            useEnvironmentCardForEachPlayer(output, objectMapper, player2, gameBoard, action);
        }
    }

    /**
     * The method that performs the necessary actions for using an "environment" card of
     * the current player
     * @param output
     * @param objectMapper
     * @param player1
     * @param gameBoard
     * @param action
     */
    private void useEnvironmentCardForEachPlayer(final ArrayNode output, final ObjectMapper
                                                 objectMapper, final Player player1,
                                                 final GameBoard gameBoard,
                                                 final ActionsInput action) {
        int canUseEnvironmentCard = player1.useEnvironmentCardCommand(action,
                gameBoard, this.getCurrentPlayerIndex());
        if (canUseEnvironmentCard != 0) {
            output.add(player1.useEnvironmentCardError(action,
                    objectMapper, canUseEnvironmentCard));
        }
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

    /**
     * The method that performs the necessary actions, placing and attacking the opposing hero
     * by the current player
     * @param output
     * @param objectMapper
     * @param player1
     * @param player2
     * @param gameBoard
     * @param action
     * @param winnerPlayerNumber
     */
    private void attackHeroForEachPlayer(final ArrayNode output, final ObjectMapper objectMapper,
                                         final Player player1, final Player player2,
                                         final GameBoard gameBoard, final ActionsInput action,
                                         final String winnerPlayerNumber) {
        int canAttackHero = player1.useAttackHeroCommand(action, gameBoard, currentPlayerIndex);
        if (canAttackHero != 0) {
            output.add(player1.useAttackHeroError(action, objectMapper, canAttackHero));
        } else {
            if (action.getCardAttacker().getY() >= gameBoard.getCards().get(action.
                    getCardAttacker().getX()).size()) {
                return;
            }

            Card attackerCard = gameBoard.getCards().get(action.getCardAttacker().getX()).
                    get(action.getCardAttacker().getY());
            attackerCard.setIsUsed(true);
            player2.getHeroCard().takeDamage(attackerCard.getAttackDamage());

            if (player2.getHeroCard().getHealth() <= 0) {
                ObjectNode attackHeroNode = objectMapper.createObjectNode();
                attackHeroNode.put("gameEnded", "Player " + winnerPlayerNumber
                        + " killed the enemy hero.");
                output.add(attackHeroNode);

                player1.setPlayedGames(player1.getPlayedGames() + 1);
                player2.setPlayedGames(player2.getPlayedGames() + 1);
                player1.setWonGames(player1.getWonGames() + 1);
            }
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
