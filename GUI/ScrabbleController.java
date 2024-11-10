package GUI;

import src.TileBag;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ScrabbleController {

    private static int playercount;  // Store player count here
    private static boolean isInitialized = false;  // Ensure settings are initialized only once
    private static ArrayList<String> playerNames = new ArrayList<>();
    private static ArrayList<Integer> playerScores = new ArrayList<>();
    private static int currentPlayerIndex = 0;
    private static boolean firstMove = true;
    private static final int BOARD_SIZE = 15;
    private static final int MIDDLE_POSITION = BOARD_SIZE / 2;
    private static String selectedCharacter = null;
    private static JButton selectedButton = null;
    static TileBag tileBag = new TileBag();
    public static ArrayList<Character> playerTiles;

    public static void initializeGameSettings() {
        // Check if already initialized
        if (!isInitialized) {
            // Initialize player count and names at the start of the game
            playercount = getPlayercount();
            getPlayerNames();
            initializePlayerScores();
            isInitialized = true;  // Set flag to prevent reinitialization
        }
    }

    public static ArrayList<Character> getPlayerTiles() {
        playerTiles = new ArrayList<>(tileBag.drawTiles(7));
        return playerTiles;
    }

    public static int getPlayercount() {
        // If playercount is already set, return it without prompting
        if (playercount != 0) {
            return playercount;
        }
        // Prompt for the number of players and validate input
        while (true) {
            String input = JOptionPane.showInputDialog("Number of players (2-4): ");
            try {
                int count = Integer.parseInt(input);
                if (count == 2 || count == 3 || count == 4) {
                    playercount = count;  // Store the valid count
                    return playercount;
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a number between 2 and 4.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Please enter a valid integer.");
            }
        }
    }

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


    // Get the name of the current player
    public static String getCurrentPlayerName() {
        return playerNames.get(currentPlayerIndex);
    }

    public static void addScoreToCurrentPlayer(int score) {
        int currentScore = playerScores.get(currentPlayerIndex);
        playerScores.set(currentPlayerIndex, currentScore + score);
    }

    public static void initializePlayerScores() {
        playerScores.clear();
        for (int i = 0; i < playercount; i++) {
            playerScores.add(0);
        }
    }

    public static int getPlayerScore(int index) {
        return playerScores.get(index);
    }


    // Switch to the next player's turn
    public static void switchToNextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % playercount;
    }

    public static boolean isFirstMove() {
        return firstMove;
    }

    public static void setFirstMoveDone() {
        firstMove = false;
    }

    public static boolean isCenterPosition(int row, int col) {
        return row == MIDDLE_POSITION && col == MIDDLE_POSITION;
    }

    public static boolean isAdjacentToLetter(JButton[][] boardButtons, int row, int col) {
        if (row > 0 && !boardButtons[row - 1][col].getText().isEmpty()) return true;
        if (row < BOARD_SIZE - 1 && !boardButtons[row + 1][col].getText().isEmpty()) return true;
        if (col > 0 && !boardButtons[row][col - 1].getText().isEmpty()) return true;
        if (col < BOARD_SIZE - 1 && !boardButtons[row][col + 1].getText().isEmpty()) return true;

        return false;
    }

    public static void selectTile(JButton tileButton) {
        String newCharacter = tileButton.getText();

        if (selectedCharacter != null && selectedButton != null) {
            selectedButton.setText(selectedCharacter);
        }

        selectedCharacter = newCharacter;
        selectedButton = tileButton;
        tileButton.setText("");  // Clear the character from the button to "pick it up"
    }

    public static String getSelectedCharacter() {
        return selectedCharacter;
    }


    public static void clearSelectedCharacter() {
        selectedCharacter = null;
        selectedButton = null;
    }

    public static boolean areTilesInStraightLine(ArrayList<Point> placedTiles) {
        if (placedTiles.isEmpty()) {
            return false;
        }

        boolean isSameRow = true;
        boolean isSameColumn = true;

        int firstRow = placedTiles.get(0).x;
        int firstCol = placedTiles.get(0).y;

        for (Point tile : placedTiles) {
            if (tile.x != firstRow) {
                isSameRow = false;
            }
            if (tile.y != firstCol) {
                isSameColumn = false;
            }
        }

        return isSameRow || isSameColumn;
    }
}
