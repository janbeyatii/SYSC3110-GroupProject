package src;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * The Helpers class provides utility methods for managing Scrabble game logic,
 * including checking word placement, player tile management, and player setup.
 */
public class Helpers {

    /**
     * Default constructor for the Helpers class.
     * Initializes the Helpers object without any specific setup.
     */
    public Helpers() {
        // Default constructor
    }

    /**
     * Checks if the word is connected to existing words on the board.
     * This ensures that all new words placed on the board are adjacent to other words.
     *
     * @param row the starting row for the word.
     * @param col the starting column for the word.
     * @param direction the direction of the word ('H' for horizontal, 'V' for vertical).
     * @param word the word to be placed.
     * @param board the current Scrabble board.
     * @return true if the word is adjacent to an existing word, false otherwise.
     */
    public boolean isWordConnected(int row, int col, char direction, String word, char[][] board) {
        for (int i = 0; i < word.length(); i++) {
            if (direction == 'H' || direction == 'h') {
                if (isAdjacentToExistingWord(row, col + i, board)) {
                    return true;
                }
            } else if (direction == 'V' || direction == 'v') {
                if (isAdjacentToExistingWord(row + i, col, board)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if a letter on the board is adjacent to any existing word.
     *
     * @param row the row position of the letter.
     * @param col the column position of the letter.
     * @param board the current Scrabble board.
     * @return true if the letter is adjacent to any existing word, false otherwise.
     */
    public boolean isAdjacentToExistingWord(int row, int col, char[][] board) {
        return (row > 0 && board[row - 1][col] != '.') ||
                (row < board.length - 1 && board[row + 1][col] != '.') ||
                (col > 0 && board[row][col - 1] != '.') ||
                (col < board[0].length - 1 && board[row][col + 1] != '.');
    }

    /**
     * Checks if the first word placed on the board passes through the center.
     *
     * @param row the starting row of the word.
     * @param col the starting column of the word.
     * @param direction the direction of the word ('H' for horizontal, 'V' for vertical).
     * @param wordLength the length of the word to be placed.
     * @return true if the word passes through the center, false otherwise.
     */
    public boolean passesThroughCenter(int row, int col, char direction, int wordLength) {
        int centerRow = 7;
        int centerCol = 7;

        if (direction == 'H' || direction == 'h') {
            return row == centerRow && col <= centerCol && (col + wordLength - 1) >= centerCol;
        } else if (direction == 'V' || direction == 'v') {
            return col == centerCol && row <= centerRow && (row + wordLength - 1) >= centerRow;
        }
        return false;
    }

    /**
     * Checks if the player has the necessary tiles to place the word.
     *
     * @param word the word the player wants to place.
     * @param playerTiles the list of tiles the player currently holds.
     * @return true if the player has the necessary tiles, false otherwise.
     */
    public boolean canPlaceWord(String word, List<Character> playerTiles) {
        List<Character> tempTiles = new ArrayList<>(playerTiles);
        for (char c : word.toCharArray()) {
            if (tempTiles.contains(c)) {
                tempTiles.remove((Character) c); // Remove the tile if the player has it
            } else {
                return false; // Player doesn't have the necessary tiles
            }
        }
        return true;
    }

    /**
     * Updates the player's tiles after they successfully place a word.
     * Removes the tiles used for the word and refills the player's tiles from the tile bag.
     *
     * @param word the word that was placed.
     * @param playerTiles the player's current tiles.
     * @param tileBag the tile bag used to refill the player's tiles.
     */
    public void updatePlayerTilesAfterMove(String word, List<Character> playerTiles, TileBag tileBag) {
        // Convert the word to a list of characters to track which letters need to be removed
        List<Character> wordLetters = new ArrayList<>();
        for (char letter : word.toCharArray()) {
            wordLetters.add(letter);
        }

        // Create a new list to store the player's remaining tiles
        List<Character> remainingTiles = new ArrayList<>();

        // Iterate over the player's tiles and only remove one instance of each letter in the word
        for (char tile : playerTiles) {
            if (wordLetters.contains(tile)) {
                wordLetters.remove((Character) tile); // Remove the first occurrence of the tile from wordLetters
            } else {
                remainingTiles.add(tile); // Keep the tile if it's not part of the word
            }
        }

        // Update the player's tile list
        playerTiles.clear();
        playerTiles.addAll(remainingTiles);

        // Refill player's tiles back to 7
        tileBag.refillTiles(playerTiles, word.length());
    }

    /**
     * Sets up players by asking for their names and drawing 7 tiles for each player from the tile bag.
     *
     * @param scanner the scanner object to read user input.
     * @param playerNames a list to store the names of the players.
     * @param playerTiles a list of lists to store each player's tiles.
     * @param tileBag the tile bag to draw tiles from.
     */
    public static void setupPlayers(Scanner scanner, List<String> playerNames, List<List<Character>> playerTiles, TileBag tileBag) {
        int numPlayers = 0;
        while (numPlayers < 2 || numPlayers > 4) {
            try {
                System.out.print("Enter the number of players (2-4): ");
                numPlayers = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character after entering the number
                if (numPlayers < 2 || numPlayers > 4) {
                    System.out.println("Invalid number of players. Please enter a number between 2 and 4 inclusive.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number between 2 and 4.");
                scanner.nextLine(); // Clear the invalid input from the scanner
            }
        }

        for (int i = 0; i < numPlayers; i++) {
            System.out.print("Enter the name of player " + (i + 1) + ": ");
            String playerName = scanner.nextLine();
            playerNames.add(playerName);
            playerTiles.add(tileBag.drawTiles(7)); // Draws 7 tiles for each player
        }

        System.out.println("Players in the game: " + playerNames);
    }
}
