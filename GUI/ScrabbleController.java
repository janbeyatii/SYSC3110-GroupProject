package GUI;

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

    private static Map<String, List<Character>> playerTilesMap = new HashMap<>();
    private static int playercount;  // Store player count here
    private static boolean isInitialized = false;  // Ensure settings are initialized only once
    private static ArrayList<String> playerNames = new ArrayList<>();
    private static ArrayList<Integer> playerScores = new ArrayList<>();
    private static int currentPlayerIndex = 0;
    public static TileBag tileBag = new TileBag();
    public static ArrayList<Character> playerTiles;
    public static char[][] board = new char[15][15];
    private static List<Point> placedTileCoordinates = new ArrayList<>();
    private ScrabbleView view; // Reference to the GUI

    /**
     * Initializes game settings such as player count, names, and scores.
     * Also distributes initial tiles to each player.
     * This method only runs once at the start of the game.
     */
    public static void initializeGameSettings() {
        if (!isInitialized) {
            playercount = getPlayercount();
            getPlayerNames();
            initializePlayerScores();
            for (String playerName : playerNames) {
                List<Character> initialTiles = tileBag.drawTiles(7);
                playerTilesMap.put(playerName, initialTiles);
            }
            isInitialized = true;
        }
    }

    /**
     * Constructor for the ScrabbleController class.
     *
     * @param view the ScrabbleView instance representing the GUI.
     */
    public ScrabbleController(ScrabbleView view) {
        this.view = view;
    }

    /**
     * Draws 7 tiles from the tile bag and assigns them to the player.
     *
     * @return a list of characters representing the player's tiles.
     */
    public static ArrayList<Character> getPlayerTiles() {
        playerTiles = new ArrayList<>(tileBag.drawTiles(7));
        return playerTiles;
    }

    public static int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    /**
     * Adds the coordinates of placed tiles to a list for reference.
     *
     * @param placedButtons the list of buttons where tiles have been placed.
     */
    public static void addPlacedTiles(List<JButton> placedButtons) {
        for (JButton button : placedButtons) {
            int row = (Integer) button.getClientProperty("row");
            int col = (Integer) button.getClientProperty("col");
            placedTileCoordinates.add(new Point(row, col));
        }
    }
    /**
     * Prompts the user to enter the number of players (2-4).
     *
     * @return the number of players as an integer.
     */
    public static int getPlayercount() {

        if (playercount != 0) {
            return playercount;
        }
        while (true) {
            String input = JOptionPane.showInputDialog("Number of players (2-4): ");
            try {
                int count = Integer.parseInt(input);
                if (count == 2 || count == 3 || count == 4) {
                    playercount = count;
                    return playercount;
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a number between 2 and 4.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Please enter a valid integer.");
            }
        }
    }

    /**
     * Prompts the user to enter names for each player and stores them in a list.
     *
     * @return a list of player names.
     */
    public static ArrayList<String> getPlayerNames() {
        if (playerNames.isEmpty()) {
            for (int i = 0; i < playercount; i++) {
                while (true) {
                    String playerName = JOptionPane.showInputDialog("Enter name for player " + (i + 1) + ":");
                    if (playerName != null && !playerName.trim().isEmpty()) {
                        playerNames.add(playerName.trim());
                        break;
                    } else {
                        JOptionPane.showMessageDialog(null, "Player name cannot be empty. Please enter a valid name.");
                    }
                }
            }
        }
        return playerNames;
    }

    /**
     * Retrieves the current player's name.
     *
     * @return the name of the current player.
     */
    public static String getCurrentPlayerName() {
        return playerNames.get(currentPlayerIndex);
    }

    /**
     * Retrieves the map of players and their corresponding tiles.
     *
     * @return a map associating each player with their list of tiles.
     */
    public static Map<String, List<Character>> getPlayerTilesMap() {
        return playerTilesMap;
    }

    /**
     * Gets the tiles of the current player.
     *
     * @return a list of characters representing the current player's tiles.
     */
    public static List<Character> getCurrentPlayerTiles() {
        String currentPlayer = getCurrentPlayerName();
        return playerTilesMap.getOrDefault(currentPlayer, new ArrayList<>());
    }

    /**
     * Adds a specified score to the current player's total score.
     *
     * @param score the score to add to the current player's total.
     */
    public static void addScoreToCurrentPlayer(int score) {
        int currentScore = playerScores.get(currentPlayerIndex);
        playerScores.set(currentPlayerIndex, currentScore + score);
    }

    /**
     * Initializes the scores for all players to zero at the start of the game.
     */
    public static void initializePlayerScores() {
        playerScores.clear();
        for (int i = 0; i < playercount; i++) {
            playerScores.add(0);
        }
    }

    /**
     * Retrieves the score of a specified player.
     *
     * @param index the index of the player in the list.
     * @return the score of the specified player.
     */
    public static int getPlayerScore(int index) {
        return playerScores.get(index);
    }

    /**
     * Switches the turn to the next player in a circular order.
     */
    public static void switchToNextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % playercount;
    }

    public static List<Point> getPlacedTileCoordinates() {
        return placedTileCoordinates;
    }
}
