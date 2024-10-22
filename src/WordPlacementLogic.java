package src;

import java.util.List;

/**
 * The WordPlacementLogic class handles the logic of placing words on the Scrabble board.
 * It verifies whether the word can be placed, checks word validity and connections to existing words,
 * updates the board, and calculates the score for the word placed.
 */
public class WordPlacementLogic {
    private boolean isFirstMove = true;  // Flag to check if it is the first move
    private Helpers helpers = new Helpers();  // Create instance of Helpers

    /**
     * Default constructor for the WordPlacementLogic class.
     * Initializes the WordPlacementLogic object without any specific setup.
     */
    public WordPlacementLogic() {
        // Default constructor
    }

    /**
     * Places a word on the Scrabble board if it meets the game's placement rules.
     * It checks if the player has the necessary tiles, validates the word, checks if it's connected to other words,
     * and calculates the score of the placed word.
     *
     * @param word the word to be placed on the board.
     * @param row the starting row for the word.
     * @param col the starting column for the word.
     * @param direction the direction of the word ('H' for horizontal, 'V' for vertical).
     * @param board the current state of the Scrabble board.
     * @param playerTiles the tiles the player has.
     * @param tileBag the TileBag object to draw additional tiles.
     * @return the score of the placed word, or -1 if the placement is invalid.
     */
    public int placeWord(String word, int row, int col, char direction, char[][] board, List<Character> playerTiles, TileBag tileBag) {

        // Check if the player has the necessary tiles to place the word
        if (!helpers.canPlaceWord(word, playerTiles)) {
            System.out.println("You don't have the necessary tiles to place this word.");
            return -1;
        }

        // Check if the word is valid
        if (!WordValidity.isWordValid(word)) {
            System.out.println("Invalid word. " + word + " was not found in the word list.");
            return -1;
        }

        // First word must pass through the center
        if (isFirstMove) {
            if (!helpers.passesThroughCenter(row, col, direction, word.length())) {
                System.out.println("The first word must be placed on the center of the board. (8 x 8)");
                return -1;
            }
            isFirstMove = false; // Update flag
        } else {
            // Check if the word is connected to existing words
            if (!helpers.isWordConnected(row, col, direction, word, board)) {
                System.out.println("The word must connect to existing words on the board.");
                return -1;
            }
        }

        String combinedWord = word;

        // Horizontal placement
        if (direction == 'H' || direction == 'h') {
            if (col + word.length() > board.length) return -1; // Out of bounds

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

            // Vertical placement
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

            // Extend the word upwards
            int upRow = row - 1;
            while (upRow >= 0 && board[upRow][col] != '.') {
                combinedWord = board[upRow][col] + combinedWord;
                upRow--;
            }

            // Extend the word downwards
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
