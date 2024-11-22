
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
    public static ArrayList<Character> playerTiles;
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

    public static void initializeGameSettings() {
        System.out.println("Initializing players: " + playerNames.size());

        if (!isInitialized) {
            playerNames = setupPlayers();
            playercount = playerNames.size();
            initializePlayerScores();

            for (String playerName : playerNames) {
                System.out.println("Assigning tiles to: " + playerName);
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

    public static List<String> getPlayerNames() {
        return playerNames;
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
        String currentPlayer = playerNames.get(currentPlayerIndex);
        System.out.println("Fetching tiles for player: " + currentPlayer);
        return playerTilesMap.get(currentPlayer);
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
     * Prompts the user to set up the game with one human player and up to three AI players.
     *
     * @return a list of player names (1 human and up to 3 AI).
     */
    public static ArrayList<String> setupPlayers() {
        System.out.println("Players setup: " + playerNames);

        ArrayList<String> playerNames = new ArrayList<>();
        int totalAIPlayers  = 0;

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


    public static void setFirstTurnCompleted() {
        firstTurn = false;
    }

    public static void setView(ScrabbleView scrabbleView) {
        view = scrabbleView;
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



    public static boolean isFirstTurn() {
        return firstTurn;
    }

    public static void switchToNextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % playercount;

        if (getCurrentPlayerName().startsWith("AI Player")) {
            handleAITurn();
        }
    }

}
