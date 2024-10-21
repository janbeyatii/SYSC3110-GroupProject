package src;

import java.util.ArrayList;
import java.util.List;

public class WordPlacementLogic {

    // Method to place a word on the board and calculate the score
    public int placeWord(String word, int row, int col, char direction, char[][] board, List<Character> playerTiles, TileBag tileBag) {
        // Check if the player has the necessary tiles to place the word
        if (!canPlaceWord(word, playerTiles)) {
            System.out.println("You don't have the necessary tiles to place this word.");
            return -1; // Return -1 to indicate invalid move due to lack of tiles
        }

        // Check if the word is valid by referencing the provided word list
        if (!WordValidity.isWordValid(word)) {
            System.out.println("Invalid word. " + word + " is not accepted in the dictionary.");
            return -1; // Return -1 to indicate an invalid word
        }

        String combinedWord = word;

        // Check for horizontal placement
        if (direction == 'H' || direction == 'h') {
            if (col + word.length() > board.length) return -1; // Out of bounds

            // Extend the word to the left if there are existing letters
            int leftCol = col - 1;
            while (leftCol >= 0 && board[row][leftCol] != '.') {
                combinedWord = board[row][leftCol] + combinedWord; // Add to the beginning
                leftCol--;
            }

            // Extend the word to the right if there are existing letters
            int rightCol = col + word.length();
            while (rightCol < board.length && board[row][rightCol] != '.') {
                combinedWord = combinedWord + board[row][rightCol]; // Add to the end
                rightCol++;
            }

            // Check if the combined word is valid
            if (!WordValidity.isWordValid(combinedWord)) {
                System.out.println("Invalid combined word: " + combinedWord + " is not accepted in the dictionary.");
                return -1; // Return -1 if the combined word is invalid
            }

            // Now place the word since it's valid
            for (int i = 0; i < word.length(); i++) {
                board[row][col + i] = word.charAt(i);
            }

        } else if (direction == 'V' || direction == 'v') {
            if (row + word.length() > board.length) return -1; // Out of bounds

            // Extend the word upwards if there are existing letters
            int upRow = row - 1;
            while (upRow >= 0 && board[upRow][col] != '.') {
                combinedWord = board[upRow][col] + combinedWord; // Add to the beginning
                upRow--;
            }

            // Extend the word downwards if there are existing letters
            int downRow = row + word.length();
            while (downRow < board.length && board[downRow][col] != '.') {
                combinedWord = combinedWord + board[downRow][col]; // Add to the end
                downRow++;
            }

            // Check if the combined word is valid
            if (!WordValidity.isWordValid(combinedWord)) {
                System.out.println("Invalid combined word: " + combinedWord + " is not accepted in the dictionary.");
                return -1; // Return -1 if the combined word is invalid
            }

            // Now place the word since it's valid
            for (int i = 0; i < word.length(); i++) {
                board[row + i][col] = word.charAt(i);
            }

        } else {
            System.out.println("Invalid direction. Please use H for horizontal or V for vertical.");
            return -1; // Return -1 to indicate invalid direction
        }

        // Update player's tiles after the word is placed
        updatePlayerTilesAfterMove(word, playerTiles, tileBag);

        // Now that the combined word is successfully placed, calculate the score
        ScoreCalculation scoreCalculation = new ScoreCalculation(combinedWord);
        int combinedWordScore = scoreCalculation.getTotalScore();
        System.out.println("Word placed: " + combinedWord + " | Score: " + combinedWordScore);

        return combinedWordScore; // Return the calculated score for the combined word
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

    // Update player's tiles after successfully placing a word
    public void updatePlayerTilesAfterMove(String word, List<Character> playerTiles, TileBag tileBag) {
        for (char c : word.toCharArray()) {
            playerTiles.remove((Character) c); // Remove the used tile
        }
        // Draw new tiles to replace the used ones
        playerTiles.addAll(tileBag.drawTiles(word.length()));
    }

}
