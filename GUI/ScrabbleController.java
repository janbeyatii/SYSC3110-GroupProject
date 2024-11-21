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

    // Game Settings and State Management
    private static int playercount;
    private static boolean isInitialized = false;
    private static boolean firstTurn = true;

    // Player Information
    private static ArrayList<String> playerNames = new ArrayList<>();
    private static ArrayList<Integer> playerScores = new ArrayList<>();
    private static Map<String, List<Character>> playerTilesMap = new HashMap<>();
    public static ArrayList<Character> playerTiles;
    private static int currentPlayerIndex = 0;

    // Board and Tile Management
    public static char[][] board = new char[15][15];
    public static TileBag tileBag = new TileBag();
    private static List<Point> placedTileCoordinates = new ArrayList<>();
    private static ArrayList<JButton> placedButtons = new ArrayList<>();

    /**
     * Constructor for the ScrabbleController class.
     *
     * @param view the ScrabbleView instance representing the GUI.
     */
    public ScrabbleController(ScrabbleView view) {
        // Default Constructor
    }

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

    public static void initializePlayerScores() {
        playerScores.clear();
        for (int i = 0; i < playercount; i++) {
            playerScores.add(0);
        }
    }

    public static ArrayList<Character> getPlayerTiles() {
        playerTiles = new ArrayList<>(tileBag.drawTiles(7));
        return playerTiles;
    }

    public static int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public static ArrayList<JButton> getPlacedButtons() {
        return placedButtons;
    }

    public static Map<String, List<Character>> getPlayerTilesMap() {
        return playerTilesMap;
    }

    public static List<Character> getCurrentPlayerTiles() {
        String currentPlayer = getCurrentPlayerName();
        return playerTilesMap.getOrDefault(currentPlayer, new ArrayList<>());
    }

    public static String getCurrentPlayerName() {
        return playerNames.get(currentPlayerIndex);
    }

    public static List<Point> getPlacedTileCoordinates() {
        return placedTileCoordinates;
    }

    public static int getPlayerScore(int index) {
        return playerScores.get(index);
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

    public static void setFirstTurnCompleted() {
        firstTurn = false;
    }

    public static void addScoreToPlayer(int playerIndex, int score) {
        int currentScore = playerScores.get(playerIndex);
        playerScores.set(playerIndex, currentScore + score);
    }

    public static void addPlacedTiles(List<JButton> placedButtons) {
        for (JButton button : placedButtons) {
            int row = (Integer) button.getClientProperty("row");
            int col = (Integer) button.getClientProperty("col");
            placedTileCoordinates.add(new Point(row, col));
        }
    }

    public static boolean isFirstTurn() {
        return firstTurn;
    }

    public static void switchToNextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % playercount;
    }

}
