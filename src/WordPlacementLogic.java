package src;

import java.util.List;

public class WordPlacementLogic {
    private boolean isFirstMove = true;  // flag to check if it is the first move
    private Helpers helpers = new Helpers();  // Create instance of Helpers

    public int placeWord(String word, int row, int col, char direction, char[][] board, List<Character> playerTiles, TileBag tileBag) {

        if (!helpers.canPlaceWord(word, playerTiles)) {
            System.out.println("You don't have the necessary tiles to place this word.");
            return -1;
        }

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
            isFirstMove = false; // update flag
        } else {
            // check if word is connected to existing words
            if (!helpers.isWordConnected(row, col, direction, word, board)) {
                System.out.println("The word must connect to existing words on the board.");
                return -1;
            }
        }

        String combinedWord = word;

        // horizontal placement
        if (direction == 'H' || direction == 'h') {
            if (col + word.length() > board.length) return -1; // out of bounds

            // Check the board for conflicts and build the combined word
            for (int i = 0; i < word.length(); i++) {
                char boardChar = board[row][col + i];
                char wordChar = word.charAt(i);

                if (boardChar != '.' && boardChar != wordChar) {
                    System.out.println("You cannot place a tile on an occupied space.");
                    return -1;
                }
            }

            // Extend the word to the left
            int leftCol = col - 1;
            while (leftCol >= 0 && board[row][leftCol] != '.') {
                combinedWord = board[row][leftCol] + combinedWord;
                leftCol--;
            }

            // Extend the word to the right
            int rightCol = col + word.length();
            while (rightCol < board.length && board[row][rightCol] != '.') {
                combinedWord = combinedWord + board[row][rightCol];
                rightCol++;
            }

        //repeat for vertical
        } else if (direction == 'V' || direction == 'v') {
            if (row + word.length() > board.length) return -1;

            for (int i = 0; i < word.length(); i++) {
                char boardChar = board[row + i][col];
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

        } else {
            System.out.println("Invalid direction. Please use H for horizontal or V for vertical.");
            return -1;
        }

        // Check if the combined word is valid
        if (!WordValidity.isWordValid(combinedWord)) {
            System.out.println("Invalid combined word: " + combinedWord + " was not found in the word list.");
            return -1;
        }

        // Place the word on the board
        if (direction == 'H' || direction == 'h') {
            for (int i = 0; i < word.length(); i++) {
                if (board[row][col + i] == '.') {  // Only place in empty spaces
                    board[row][col + i] = word.charAt(i);
                }
            }
        } else if (direction == 'V' || direction == 'v') {
            for (int i = 0; i < word.length(); i++) {
                if (board[row + i][col] == '.') {  // Only place in empty spaces
                    board[row + i][col] = word.charAt(i);
                }
            }
        }

        // Update the player's tiles after the word is placed
        helpers.updatePlayerTilesAfterMove(word, playerTiles, tileBag);

        // Calculate the score
        ScoreCalculation scoreCalculation = new ScoreCalculation(combinedWord);
        int combinedWordScore = scoreCalculation.getTotalScore();
        System.out.println("Word placed: " + combinedWord + " | Score: " + combinedWordScore);

        return combinedWordScore;
    }
}
