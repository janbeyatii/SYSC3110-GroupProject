package GUI;

import src.AIPlayer;
import src.Helpers;
import src.ScoreCalculation;
import src.TileBag;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * The ScrabbleController class manages game settings, player data, and board state for a Scrabble game.
 * It initializes player names and scores, manages tile draws and placements, and controls the turn-based logic.
 */
public class ScrabbleController {

    // Game Settings and State Management
    private static int playercount;
    private static boolean isInitialized = false;
    private static boolean firstTurn = true;

    // Player Information
    private static ArrayList<String> playerNames = new ArrayList<>();
    private static ArrayList<Integer> playerScores = new ArrayList<>();
    private static Map<String, List<Character>> playerTilesMap = new HashMap<>();
    private static int currentPlayerIndex = 0;

    // Board and Tile Management
    public static char[][] board = new char[15][15];
    public static TileBag tileBag = new TileBag();
    private static List<Point> placedTileCoordinates = new ArrayList<>();
    private static ArrayList<JButton> placedButtons = new ArrayList<>();
    private static ScrabbleView view;

    /**
     * Constructor for the ScrabbleController class.
     *
     * @param view the ScrabbleView instance representing the GUI.
     */
    public ScrabbleController(ScrabbleView view) {
        // Default Constructor
    }

    /**
     * Initializes game settings, such as player names, scores, and tiles, and sets up the game state.
     * Ensures that players are only initialized once.
     */
    public static void initializeGameSettings() {
        System.out.println("Initializing players: " + playerNames.size());

        if (!isInitialized) {
            playerNames = setupPlayers();
            playercount = playerNames.size();
            initializePlayerScores();

            System.out.println("Adding blank tiles to the player tile pool");

            for (String playerName : playerNames) {
                System.out.println("Assigning tiles to: " + playerName);
                List<Character> initialTiles = tileBag.drawTiles(7);
                playerTilesMap.put(playerName, initialTiles);
            }

            isInitialized = true;
        }
    }

    /**
     * Initializes player scores by setting all player scores to 0.
     * Clears any existing scores and resets the scores list based on the number of players.
     */
    public static void initializePlayerScores() {
        playerScores.clear();
        for (int i = 0; i < playercount; i++) {
            playerScores.add(0);
        }
    }

    /**
     * Retrieves the names of all players in the game.
     *
     * @return A list of player names.
     */
    public static List<String> getPlayerNames() {
        return playerNames;
    }

    /**
     * Retrieves the index of the current player in the turn rotation.
     *
     * @return The index of the current player.
     */
    public static int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    /**
     * Retrieves the list of buttons representing tiles that have been placed on the board during the current turn.
     *
     * @return An ArrayList of JButton objects representing placed tiles.
     */
    public static ArrayList<JButton> getPlacedButtons() {
        return placedButtons;
    }

    /**
     * Retrieves the ScrabbleView instance representing the game's GUI.
     *
     * @return The ScrabbleView instance.
     */
    public static ScrabbleView getView() {
        return view;
    }

    /**
     * Sets the ScrabbleView instance to be used by the controller.
     *
     * @param scrabbleView The ScrabbleView instance to set.
     */
    public static void setView(ScrabbleView scrabbleView) {
        view = scrabbleView;
    }

    /**
     * Retrieves the mapping of player names to their current tiles.
     *
     * @return A Map where the keys are player names and the values are lists of characters (tiles) assigned to each player.
     */
    public static Map<String, List<Character>> getPlayerTilesMap() {
        return playerTilesMap;
    }

    /**
     * Retrieves the tiles of the current player.
     *
     * @return A list of characters representing the current player's tiles.
     */
    public static List<Character> getCurrentPlayerTiles() {
        String currentPlayer = playerNames.get(currentPlayerIndex);
        System.out.println("Fetching tiles for player: " + currentPlayer);
        return playerTilesMap.get(currentPlayer);
    }

    /**
     * Retrieves the name of the current player.
     *
     * @return The name of the current player.
     */
    public static String getCurrentPlayerName() {
        return playerNames.get(currentPlayerIndex);
    }

    /**
     * Retrieves the list of tile coordinates placed during the current turn.
     *
     * @return A list of Points representing the placed tile coordinates.
     */
    public static List<Point> getPlacedTileCoordinates() {
        return placedTileCoordinates;
    }

    /**
     * Retrieves the score of a player by their index.
     *
     * @param index The index of the player.
     * @return The player's score.
     */
    public static int getPlayerScore(int index) {
        return playerScores.get(index);
    }

    /**
     * Prompts the user to set up the game with one human player and up to three AI players.
     *
     * @return A list of player names (1 human and up to 3 AI).
     */
    public static ArrayList<String> setupPlayers() {
        System.out.println("Players setup: " + playerNames);

        ArrayList<String> playerNames = new ArrayList<>();
        int totalAIPlayers = 0;

        while (true) {
            String input = JOptionPane.showInputDialog("How many AIs do you want to play against? (1-3) ");
            try {
                int count = Integer.parseInt(input);
                if (count >= 1 && count <= 3) {
                    totalAIPlayers = count;
                    break;
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a number between 1 and 3.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Please enter a valid integer.");
            }
        }

        while (true) {
            String humanName = JOptionPane.showInputDialog("Enter your name:");
            if (humanName != null && !humanName.trim().isEmpty()) {
                playerNames.add(humanName.trim());
                break;
            } else {
                JOptionPane.showMessageDialog(null, "Player name cannot be empty. Please enter a valid name.");
            }
        }

        // Add AI players
        for (int i = 1; i <= totalAIPlayers; i++) {
            playerNames.add("AI Player " + i);
        }

        return playerNames;
    }

    /**
     * Marks the first turn of the game as completed.
     */
    public static void setFirstTurnCompleted() {
        firstTurn = false;
    }

    /**
     * Adds a score to the specified player.
     *
     * @param playerIndex The index of the player.
     * @param score       The score to add.
     */
    public static void addScoreToPlayer(int playerIndex, int score) {
        int currentScore = playerScores.get(playerIndex);
        playerScores.set(playerIndex, currentScore + score);
    }

    /**
     * Adds the coordinates of placed tiles to the game state.
     *
     * @param placedButtons A list of JButton objects representing the placed tiles.
     */
    public static void addPlacedTiles(List<JButton> placedButtons) {
        for (JButton button : placedButtons) {
            int row = (Integer) button.getClientProperty("row");
            int col = (Integer) button.getClientProperty("col");
            placedTileCoordinates.add(new Point(row, col));
        }
    }

    /**
     * Handles the AI player's turn, including word placement, scoring, and tile updates.
     */
    private static void handleAITurn() {
        String aiPlayerName = getCurrentPlayerName();
        List<Character> aiTiles = playerTilesMap.get(aiPlayerName);

        Set<String> formedWords = AIPlayer.makeMove(board, aiTiles);

        if (!formedWords.isEmpty()) {
            for (String word : formedWords) {
                ArrayList<JButton> placedButtonsDummy = new ArrayList<>();

                for (int i = 0; i < word.length(); i++) {
                    JButton dummyButton = new JButton(String.valueOf(word.charAt(i)));
                    dummyButton.putClientProperty("row", 0); // Replace with the actual row if available
                    dummyButton.putClientProperty("col", i); // Replace with the actual column if available
                    placedButtonsDummy.add(dummyButton);
                }

                ScoreCalculation scoreCalculation = new ScoreCalculation(word, placedButtonsDummy);
                int wordScore = scoreCalculation.getTotalScore();

                addScoreToPlayer(currentPlayerIndex, wordScore);

                if (view != null) {
                    view.wordHistoryArea.append(aiPlayerName + " placed: " + word + " (" + wordScore + " points)\n");
                }

                assert view != null;
                view.updateAITiles();

                System.out.println(aiPlayerName + " placed the word: " + word);
            }

            Helpers.updateOldTileCoordinates();
        }

        if (formedWords.isEmpty()) {
            System.out.println(aiPlayerName + " passed their turn.");
        }

        if (view != null) {
            view.updateBoardDisplay();
            view.updatePlayerTiles();
            view.turnLabel.setText("Turn: " + getCurrentPlayerName());
        }

        switchToNextPlayer();
    }

    /**
     * Checks if it is the first turn of the game.
     *
     * @return True if it is the first turn; otherwise, false.
     */
    public static boolean isFirstTurn() {
        return firstTurn;
    }

    /**
     * Switches to the next player's turn, including handling AI turns.
     */
    public static void switchToNextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % playercount;

        if (getCurrentPlayerName().startsWith("AI Player")) {
            handleAITurn();
        }
    }

    /**
     * Sets the triple word score tiles on the board.
     *
     * @param i The row index of the tile.
     * @param j The column index of the tile.
     */
    public static void tripleword(int i, int j) {
        if ((i == 0 || i == 7 || i == 14) && (j == 0 || j == 14) ||
                (i == 0 || i == 14) && (j == 7)) {
            ScrabbleView.boardButtons[i][j].setBackground(new Color(220, 50, 50));
        }
    }

    /**
     * Sets the triple letter score tiles on the board.
     *
     * @param i The row index of the tile.
     * @param j The column index of the tile.
     */
    public static void tripleletter(int i, int j) {
        if ((i == 1 || i == 5 || i == 9 || i == 13) && (j == 5 || j == 9) ||
                (i == 5 || i == 9) && (j == 1 || j == 13)) {
            ScrabbleView.boardButtons[i][j].setBackground(new Color(65, 105, 225));
        }
    }

    /**
     * Sets the double word score tiles on the board.
     *
     * @param i The row index of the tile.
     * @param j The column index of the tile.
     */
    public static void doubleword(int i, int j) {
        if ((i == 1) && (j == 1 || j == 13) || (i == 2) && (j == 2 || j == 12)
                || (i == 3) && (j == 3 || j == 11) || (i == 4) && (j == 4 || j == 10)
                || (i == 10) && (j == 4 || j == 10) || (i == 11) && (j == 3 || j == 11)
                || (i == 12) && (j == 2 || j == 12) || (i == 13) && (j == 1 || j == 13)) {
            ScrabbleView.boardButtons[i][j].setBackground(new Color(230, 100, 100));
        }
    }

    /**
     * Sets the double letter score tiles on the board.
     *
     * @param i The row index of the tile.
     * @param j The column index of the tile.
     */
    public static void doubleletter(int i, int j) {
        if ((i == 0 || i == 14) && (j == 3 || j == 11) ||
                (i == 2 || i == 12) && (j == 6 || j == 8) ||
                (i == 3 || i == 11) && (j == 0 || j == 14) ||
                (i == 6 || i == 8) && (j == 2 || j == 6 || j == 8 || j == 12) ||
                (i == 7) && (j == 3 || j == 11) ||
                (i == 3 || i == 11) && (j == 7)) {
            ScrabbleView.boardButtons[i][j].setBackground(new Color(173, 216, 230));
        }
    }
}
