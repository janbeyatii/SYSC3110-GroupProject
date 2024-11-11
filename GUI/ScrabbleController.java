package GUI;

import src.TileBag;
import src.WordPlacementLogic;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;


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

    public static void initializeGameSettings() {
        // Check if already initialized
        if (!isInitialized) {
            // Initialize player count and names at the start of the game
            playercount = getPlayercount();
            getPlayerNames();
            initializePlayerScores();
            for (String playerName : playerNames) {
                List<Character> initialTiles = tileBag.drawTiles(7);
                playerTilesMap.put(playerName, initialTiles);
            }
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

    public static Map<String, List<Character>> getPlayerTilesMap() {
        return playerTilesMap;
    }

    public static List<Character> getCurrentPlayerTiles() {
        String currentPlayer = getCurrentPlayerName();
        return playerTilesMap.getOrDefault(currentPlayer, new ArrayList<>());
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

}
