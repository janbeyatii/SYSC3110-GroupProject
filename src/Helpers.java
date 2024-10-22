package src;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
public class Helpers {

    // check if the word is connected to existing words on the board
    public boolean isWordConnected(int row, int col, char direction, String word, char[][] board) {
        for (int i = 0; i < word.length(); i++) {

            if (direction == 'H' || direction == 'h') {
                if (isAdjacentToExistingWord(row, col + i, board)) {
                    return true;
                }
            }

            else if (direction == 'V' || direction == 'v') {
                if (isAdjacentToExistingWord(row + i, col, board)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Check if a letter is adjacent to any existing word
    public boolean isAdjacentToExistingWord(int row, int col, char[][] board) {
        return (row > 0 && board[row - 1][col] != '.') ||
                (row < board.length - 1 && board[row + 1][col] != '.') ||
                (col > 0 && board[row][col - 1] != '.') ||
                (col < board[0].length - 1 && board[row][col + 1] != '.');
    }

    // Check if the first word passes through the center of the board
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


    // Check if the player has the necessary tiles to place the word
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

        // update the players tile list
        playerTiles.clear();
        playerTiles.addAll(remainingTiles);

        // refill players tiles back to 7
        tileBag.refillTiles(playerTiles, word.length());
    }

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
