package src;

import GUI.*;
import javax.swing.*;
import java.util.*;

/**
 * Utility class for managing tile selection, word placement, word validation, and score updates in the Scrabble game.
 */
public class Helpers {

    private static JButton previouslySelectedButton = null;
    private static String selectedLetter = null;
    private static Set<String> oldTileCoordinates = new HashSet<>();

    /**
     * Default constructor for the Helpers class.
     * Initializes the Helpers object without any specific setup.
     */
    public Helpers() {
        // Default constructor
    }


    /**
     * Selects a letter from the player's tile rack, disables it in the rack to indicate it is in use, and tracks it as the currently selected letter.
     *
     * @param index             the index of the selected letter tile in the player's tile rack.
     * @param playerTileButtons the array of JButton elements representing the player's tile rack.
     */
    public static void selectLetterFromTiles(int index, JButton[] playerTileButtons) {
        // Only disable the button if a letter is selected
        if (previouslySelectedButton != null) {
            previouslySelectedButton.setEnabled(true); // Re-enable previously selected button
        }

        // Select the new letter
        selectedLetter = playerTileButtons[index].getText();
        playerTileButtons[index].setEnabled(false);
        previouslySelectedButton = playerTileButtons[index];

        // Log to track changes to player tiles
        System.out.println("Selected Letter: " + selectedLetter);
        System.out.println("Previously Selected Button: " + previouslySelectedButton.getText());
    }

    /**
     * Places a selected letter on the game board at the specified row and column.
     * Ensures the letter is only placed on empty tiles and tracks the placed button.
     *
     * @param row           the row on the board where the letter should be placed.
     * @param col           the column on the board where the letter should be placed.
     * @param boardButtons  the 2D array of JButtons representing the game board.
     * @param placedButtons the list of JButtons where letters were placed in the current turn.
     */
    public static void placeLetterOnBoard(int row, int col, JButton[][] boardButtons, ArrayList<JButton> placedButtons) {
        if (selectedLetter != null && !selectedLetter.isEmpty() && boardButtons[row][col].getText().isEmpty()) {
            boardButtons[row][col].setText(selectedLetter);
            ScrabbleController.board[row][col] = selectedLetter.charAt(0);
            placedButtons.add(boardButtons[row][col]);
            selectedLetter = null;
            previouslySelectedButton = null;

            System.out.println("Letter placed: " + selectedLetter + " at (" + row + ", " + col + ")");
        } else {
            JOptionPane.showMessageDialog(null, "Select a letter first or choose an empty tile.");
        }
    }

    /**
     * Updates the list of coordinates for tiles that are already placed on the Scrabble board.
     * This method clears the existing list and repopulates it with the coordinates of all non-empty cells.
     * Each coordinate is stored as a string in the format "row,column".
     */
    public static void updateOldTileCoordinates() {
        oldTileCoordinates.clear();
        for (int row = 0; row < ScrabbleController.board.length; row++) {
            for (int col = 0; col < ScrabbleController.board[row].length; col++) {
                if (ScrabbleController.board[row][col] != '\0') {
                    oldTileCoordinates.add(row + "," + col);
                }
            }
        }
    }

    /**
     * Collects a full word from the board starting at a specific position in a specified direction (horizontal or vertical).
     *
     * @param row         the starting row of the word.
     * @param col         the starting column of the word.
     * @param isHorizontal true if the word is to be collected horizontally, false if vertically.
     * @return the full word as a String, or an empty string if the word is a single letter.
     */
    private static String collectFullWord(int row, int col, boolean isHorizontal) {
        StringBuilder word = new StringBuilder();

        int startRow = row;
        int startCol = col;

        while (isValidPosition(startRow, startCol) && ScrabbleController.board[startRow][startCol] != '\0') {
            if (isHorizontal) {
                if (startCol == 0) break;
                startCol--;
            } else {
                if (startRow == 0) break;
                startRow--;
            }
        }

        if (isHorizontal && ScrabbleController.board[startRow][startCol] == '\0' && startCol < ScrabbleController.board[0].length - 1) {
            startCol++;
        }
        if (!isHorizontal && ScrabbleController.board[startRow][startCol] == '\0' && startRow < ScrabbleController.board.length - 1) {
            startRow++;
        }

        int r = startRow;
        int c = startCol;
        while (isValidPosition(r, c) && ScrabbleController.board[r][c] != '\0') {
            word.append(ScrabbleController.board[r][c]);
            if (isHorizontal) {
                if (c == ScrabbleController.board[0].length - 1) break;
                c++;
            } else {
                if (r == ScrabbleController.board.length - 1) break;
                r++;
            }
        }

        return word.length() > 1 ? word.toString() : "";
    }

    /**
     * Checks if a specified position is within the bounds of the board.
     *
     * @param row the row position to check.
     * @param col the column position to check.
     * @return true if the position is within the board's boundaries, false otherwise.
     */
    private static boolean isValidPosition(int row, int col) {
        return row >= 0 && row < ScrabbleController.board.length && col >= 0 && col < ScrabbleController.board[0].length;
    }


    /**
     * Checks if the placed tiles form a valid word placement on the board.
     * This includes verifying alignment, ensuring the use of the center tile for the first move,
     * adjacency to existing words for non-first moves, and ensuring there are no isolated letters.
     *
     * @param placedButtons the list of JButtons where letters were placed in the current turn.
     * @param isFirstTurn   true if it is the first turn of the game, false otherwise.
     * @return true if the word placement is valid, false otherwise.
     */
    public static boolean isWordPlacementValid(ArrayList<JButton> placedButtons, boolean isFirstTurn, boolean showMessages) {
        if (isFirstTurn) {
            Set<String> wordsFormed = getAllWordsFormed(placedButtons);
            if (!areAllWordsValid(wordsFormed, showMessages)) {
                if (showMessages) {
                    JOptionPane.showMessageDialog(null, "Invalid first word.");
                }
                return false;
            }

            // Ensure center tile is used and adjacent
            if (!isUsingCenterTile(placedButtons)) {
                if (showMessages) {
                    JOptionPane.showMessageDialog(null, "The first word must use the center tile.");
                }
                return false;
            }
            if (!isCenterTileAdjacentToAnotherTile(placedButtons)) {
                if (showMessages) {
                    JOptionPane.showMessageDialog(null, "On the first turn, the center tile must be adjacent to another newly placed tile.");
                }
                return false;
            }
        }

        // Normal validation for non-first turn
        if (!isFirstTurn && !isAdjacentToExistingWords(placedButtons)) {
            if (showMessages) {
                JOptionPane.showMessageDialog(null, "Placed letters must be adjacent to existing words.");
            }
            return false;
        }

        // Check word alignment (row/column)
        if (!isAlignedInSingleRowOrColumn(placedButtons)) {
            if (showMessages) {
                JOptionPane.showMessageDialog(null, "Placed letters must be in the same row or column.");
            }
            return false;
        }

        return true;
    }



    /**
     * Collects all unique words formed by the placement of tiles in the current turn.
     *
     * @param placedButtons the list of JButtons where letters were placed in the current turn.
     * @return a Set of Strings containing all unique words formed.
     */
    public static Set<String> getAllWordsFormed(ArrayList<JButton> placedButtons) {
        Set<String> uniqueWordsFormed = new HashSet<>();

        for (JButton button : placedButtons) {
            int row = (Integer) button.getClientProperty("row");
            int col = (Integer) button.getClientProperty("col");

            String verticalWord = collectFullWord(row, col, false);
            String horizontalWord = collectFullWord(row, col, true);

            if (!verticalWord.isEmpty()) {
                uniqueWordsFormed.add(verticalWord);
            }
            if (!horizontalWord.isEmpty()) {
                uniqueWordsFormed.add(horizontalWord);
            }
        }
        return uniqueWordsFormed;
    }

    /**
     * Validates all words formed by checking each word against the dictionary.
     *
     * @param words the Set of words to be validated.
     * @return true if all words are valid, false if any word is invalid.
     */
    public static boolean areAllWordsValid(Set<String> words, boolean showMessage) {
        for (String word : words) {
            if (!WordValidity.isWordValid(word)) {
                if (showMessage) {
                    JOptionPane.showMessageDialog(null, "Invalid word formed: " + word);
                }
                return false;
            }
        }
        return true;
    }
    /**
     * Checks if all placed letters are in a single row or column.
     *
     * @param placedButtons the list of JButtons where letters were placed in the current turn.
     * @return true if letters are in a single row or column, false otherwise.
     */
    private static boolean isAlignedInSingleRowOrColumn(ArrayList<JButton> placedButtons) {
        boolean isSingleRow = placedButtons.stream().map(b -> (Integer) b.getClientProperty("row")).distinct().count() == 1;
        boolean isSingleColumn = placedButtons.stream().map(b -> (Integer) b.getClientProperty("col")).distinct().count() == 1;
        return isSingleRow || isSingleColumn;
    }

    /**
     * Checks if the placed letters use the center tile of the board, required for the first move.
     *
     * @param placedButtons the list of JButtons where letters were placed in the current turn.
     * @return true if any placed tile uses the center tile, false otherwise.
     */
    private static boolean isUsingCenterTile(ArrayList<JButton> placedButtons) {
        int boardCenterRow = ScrabbleController.board.length / 2;
        int boardCenterCol = ScrabbleController.board[0].length / 2;

        for (JButton button : placedButtons) {
            int row = (Integer) button.getClientProperty("row");
            int col = (Integer) button.getClientProperty("col");
            if (row == boardCenterRow && col == boardCenterCol) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if placed letters are adjacent to any existing tiles, required for non-first moves.
     * Only checks the newly placed tiles and compares them with old tile coordinates.
     *
     * @param placedButtons the list of JButtons where letters were placed in the current turn.
     * @return true if at least one placed letter is adjacent to an existing letter, false otherwise.
     */
    private static boolean isAdjacentToExistingWords(ArrayList<JButton> placedButtons) {
        for (JButton button : placedButtons) {
            int row = (Integer) button.getClientProperty("row");
            int col = (Integer) button.getClientProperty("col");

            if (oldTileCoordinates.contains((row - 1) + "," + col) ||  // Check above
                    oldTileCoordinates.contains((row + 1) + "," + col) ||  // Check below
                    oldTileCoordinates.contains(row + "," + (col - 1)) ||  // Check left
                    oldTileCoordinates.contains(row + "," + (col + 1))) {  // Check right
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the center tile (middle of the board) is adjacent to another newly placed tile
     * during the first turn.
     *
     * @param placedButtons the list of JButtons where letters were placed in the current turn.
     * @return true if the center tile is adjacent to another placed tile, false otherwise.
     */
    private static boolean isCenterTileAdjacentToAnotherTile(ArrayList<JButton> placedButtons) {
        int boardCenterRow = ScrabbleController.board.length / 2;
        int boardCenterCol = ScrabbleController.board[0].length / 2;

        for (JButton button : placedButtons) {
            int row = (Integer) button.getClientProperty("row");
            int col = (Integer) button.getClientProperty("col");

            if ((Math.abs(row - boardCenterRow) == 1 && col == boardCenterCol) ||
                    (Math.abs(col - boardCenterCol) == 1 && row == boardCenterRow)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Updates the player scores displayed on the screen.
     *
     * @param playerScoresLabels the array of JLabels that display players' scores.
     */
    public static void updateScores(JLabel[] playerScoresLabels) {
        for (int i = 0; i < ScrabbleController.getPlayerNames().size(); i++) {
            String playerName = ScrabbleController.getPlayerNames().get(i);
            int playerScore = ScrabbleController.getPlayerScore(i);

            playerScoresLabels[i].setText(playerName + " Score: " + playerScore);
        }
    }
    /**
     * Assigns a value to a blank tile. Once value is set, it cannot be changed
     *
     * @param playerTileButtons An index to a players blank tile that will be assigned a letter
     * @return none
     */
    public static void assignBlankTile(int index, JButton[] playerTileButtons) {
    System.out.println("Blank tile clicked at index " + index);
    String input = JOptionPane.showInputDialog("Enter a letter for the blank tile:");

    // Validate the input
    if (input != null && input.matches("[a-zA-Z]") && input.length() == 1) {
        String letter = input.toUpperCase();
        playerTileButtons[index].setText(letter);
        playerTileButtons[index].setEnabled(false); // Disable further changes
        System.out.println("Assigned blank tile at index " + index + " to letter: " + letter);
    } else {
        JOptionPane.showMessageDialog(null, "Invalid input. Please enter a single letter.");
    }
}


}
