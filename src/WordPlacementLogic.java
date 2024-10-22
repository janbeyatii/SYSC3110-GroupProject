package src;

import java.util.List;
import src.Helpers;

public class WordPlacementLogic {
    private boolean isFirstMove = true;  // flag to check if it is the first move
    private Helpers helpers = new Helpers();  // Create an instance of Helpers

    // place a word on the board and calculate the score
    public int placeWord(String word, int row, int col, char direction, char[][] board, List<Character> playerTiles, TileBag tileBag) {

        // Check if the player has the necessary tiles to place the word
        if (!helpers.canPlaceWord(word, playerTiles)) {
            System.out.println("You don't have the necessary tiles to place this word.");
            return -1;
        }

        // Check if word is valid
        if (!WordValidity.isWordValid(word)) {
            System.out.println("Invalid word. " + word + " was not found in the word list.");
            return -1;
        }

        // first word must pass through the center
        if (isFirstMove) {
            if (!helpers.passesThroughCenter(row, col, direction, word.length())) {
                System.out.println("The first word must be placed on the center of the board. (8 x 8)");
                return -1;
            }
            isFirstMove = false; //update flag
        } else {
            // check if next words are connected to original
            if (!helpers.isWordConnected(row, col, direction, word, board)) {
                System.out.println("The word must connect to existing words on the board.");
                return -1;
            }
        }

        String combinedWord = word;

        // horizontal placement
        if (direction == 'H' || direction == 'h') {
            if (col + word.length() > board.length) return -1; // out of bounds

            for (int i = 0; i < word.length(); i++) {
                char boardChar = board[row][col + i];
                char wordChar = word.charAt(i);

                // check available board location
                if (boardChar != '.' && boardChar != wordChar) {
                    System.out.println("You cannot place a tile on an occupied space.");
                    return -1;
                }
            }

            // Extend word to left if there are existing letters
            int leftCol = col - 1;
            while (leftCol >= 0 && board[row][leftCol] != '.') {
                combinedWord = board[row][leftCol] + combinedWord;
                leftCol--;
            }

            // Extend word to right if there are existing letters
            int rightCol = col + word.length();
            while (rightCol < board.length && board[row][rightCol] != '.') {
                combinedWord = combinedWord + board[row][rightCol];
                rightCol++;
            }

            // Check if combined word is found in word list
            if (!WordValidity.isWordValid(combinedWord)) {
                System.out.println("Invalid combined word: " + combinedWord + " was not found in the word list.");
                return -1;
            }

            // all checks pass, place word
            for (int i = 0; i < word.length(); i++) {
                board[row][col + i] = word.charAt(i);
            }

        //repeat above steps for vertical positions
        } else if (direction == 'V' || direction == 'v') {
            if (row + word.length() > board.length) return -1;

            for (int i = 0; i < word.length(); i++) {
                char boardChar = board[row][col + i];
                char wordChar = word.charAt(i);

                if (boardChar != '.' && boardChar != wordChar) {
                    System.out.println("You cannot place a tile on an occupied space.");
                    return -1;
                }
            }
            int upRow = row - 1;
            while (upRow >= 0 && board[upRow][col] != '.') {
                combinedWord = board[upRow][col] + combinedWord;
                upRow--;
            }

            int downRow = row + word.length();
            while (downRow < board.length && board[downRow][col] != '.') {
                combinedWord = combinedWord + board[downRow][col];
                downRow++;
            }

            if (!WordValidity.isWordValid(combinedWord)) {
                System.out.println("Invalid combined word: " + combinedWord + " is not accepted in the dictionary.");
                return -1;
            }

            for (int i = 0; i < word.length(); i++) {
                board[row + i][col] = word.charAt(i);
            }

        } else {
            System.out.println("Invalid direction. Please use H for horizontal or V for vertical.");
            return -1;
        }

        // Update the players tiles after a word is placed
        helpers.updatePlayerTilesAfterMove(word, playerTiles, tileBag);

        // calculate the score
        ScoreCalculation scoreCalculation = new ScoreCalculation(combinedWord);
        int combinedWordScore = scoreCalculation.getTotalScore();
        System.out.println("Word placed: " + combinedWord + " | Score: " + combinedWordScore);

        return combinedWordScore;
    }
}
