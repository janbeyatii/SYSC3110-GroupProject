package src;

public class WordPlacementLogic {

    // Place a word on the board
    public boolean placeWord(String word, int row, int col, char direction, char[][] board) {
        // Check if the word is valid by referencing the prof provided list
        if (!WordValidity.isWordValid(word)) {
            System.out.println("Invalid word. " + word + " is not accepted in the dictionary");
            return false;
        }

        // Give the player the option to select the horizontal or vertical position of the word
        if (direction == 'H' || direction == 'h') {
            if (col + word.length() > board.length) return false; // Out of bounds
            for (int i = 0; i < word.length(); i++) {
                board[row][col + i] = word.charAt(i);
            }
        } else if (direction == 'V' || direction == 'v') {
            if (row + word.length() > board.length) return false;
            for (int i = 0; i < word.length(); i++) {
                board[row + i][col] = word.charAt(i);
            }
        } else {
            return false; // Invalid direction
        }

        return true; // Word placement successful
    }
}
