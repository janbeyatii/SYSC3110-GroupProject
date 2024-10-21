package src;

public class WordPlacementLogic {

    // Place a word on the board
    public boolean placeWord(String word, int row, int col, char direction, char[][] board) {
        // Check if the word is valid by referencing the provided word list
        if (!WordValidity.isWordValid(word)) {
            System.out.println("Invalid word. " + word + " is not accepted in the dictionary.");
            return false;
        }

        // Check if word fits on the board and can be placed without conflicts
        if (direction == 'H' || direction == 'h') {
            if (col + word.length() > board.length) return false; // Out of bounds

            // Check for conflicts with existing letters and place the word
            for (int i = 0; i < word.length(); i++) {
                char boardChar = board[row][col + i];
                char wordChar = word.charAt(i);

                // Check if the board cell is empty (using '.' as empty) or matches the letter being placed
                if (boardChar != '.' && boardChar != wordChar) {
                    System.out.println("Word conflicts with existing letter at position (" + row + ", " + (col + i) + ")");
                    return false;
                }
            }

            // Now place the word since it's valid
            for (int i = 0; i < word.length(); i++) {
                board[row][col + i] = word.charAt(i);
            }

        } else if (direction == 'V' || direction == 'v') {
            if (row + word.length() > board.length) return false; // Out of bounds

            // Check for conflicts with existing letters and place the word
            for (int i = 0; i < word.length(); i++) {
                char boardChar = board[row + i][col];
                char wordChar = word.charAt(i);

                // Check if the board cell is empty (using '.' as empty) or matches the letter being placed
                if (boardChar != '.' && boardChar != wordChar) {
                    System.out.println("Word conflicts with existing letter at position (" + (row + i) + ", " + col + ")");
                    return false;
                }
            }

            // Now place the word since it's valid
            for (int i = 0; i < word.length(); i++) {
                board[row + i][col] = word.charAt(i);
            }

        } else {
            System.out.println("Invalid direction. Please use H for horizontal or V for vertical.");
            return false; // Invalid direction
        }

        return true; // Word placement successful
    }

    









}

