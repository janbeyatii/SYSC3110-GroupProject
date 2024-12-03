package src;

import GUI.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

import static GUI.ScrabbleController.view;

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
     */
    public static void placeLetterOnBoard(int row, int col, JButton[][] boardButtons) {
        if (selectedLetter != null && !selectedLetter.isEmpty() && boardButtons[row][col].getText().isEmpty()) {
            // Place the letter on the board visually and logically
            boardButtons[row][col].setText(selectedLetter);
            ScrabbleController.board[row][col] = selectedLetter.charAt(0);

            // Add the button to the masterPlacedButtons list
            JButton button = boardButtons[row][col];
            button.putClientProperty("row", row);
            button.putClientProperty("col", col);
            ScrabbleController.addToMasterPlacedButtons(button); // Update the master list

            // Reset the selected letter and previous button
            selectedLetter = null;
            previouslySelectedButton = null;

            // Track the placement coordinates
            ScrabbleController.setPlacedTileCoordinates(new Point(row, col));

            // Debugging: Logs to verify placement
            System.out.println("Letter placed: " + button.getText() + " at (" + row + ", " + col + ")");
            System.out.println("Master Placed Buttons: " + ScrabbleController.getMasterPlacedButtons());
            System.out.println("Placed Coordinates: " + ScrabbleController.getPlacedTileCoordinates());
        } else {
            // Show a message if placement conditions are not met
            JOptionPane.showMessageDialog(null, "Select a letter first or choose an empty tile.");
        }
    }


// Helper methods for bonus checks
static boolean isDoubleWord(int row, int col) {
    return ((row == 1) && (col == 1 || col == 13) ||
            (row == 2) && (col == 2 || col == 12) ||
            (row == 3) && (col == 3 || col == 11) ||
            (row == 4) && (col == 4 || col == 10) ||
            (row == 10) && (col == 4 || col == 10) ||
            (row == 11) && (col == 3 || col == 11) ||
            (row == 12) && (col == 2 || col == 12) ||
            (row == 13) && (col == 1 || col == 13));
}

static boolean isTripleWord(int row, int col) {
    return ((row == 0 || row == 7 || row == 14) && (col == 0 || col == 14) ||
            (row == 0 || row == 14) && (col == 7));
}

static boolean isDoubleLetter(int row, int col) {
    return ((row == 0 || row == 14) && (col == 3 || col == 11) ||
            (row == 2 || row == 12) && (col == 6 || col == 8) ||
            (row == 3 || row == 11) && (col == 0 || col == 14) ||
            (row == 6 || row == 8) && (col == 2 || col == 6 || col == 8 || col == 12) ||
            (row == 7) && (col == 3 || col == 11) ||
            (row == 3 || row == 11) && (col == 7));
}

static boolean isTripleLetter(int row, int col) {
    return ((row == 1 || row == 5 || row == 9 || row == 13) && (col == 5 || col == 9) ||
            (row == 5 || row == 9) && (col == 1 || col == 13));
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

    public static Set<String> getOldTileCoordinates() {
        return new HashSet<>(oldTileCoordinates); // Return a copy to avoid external modification.
    }

    public static void setOldTileCoordinates(Set<String> coordinates) {
        oldTileCoordinates.clear();
        oldTileCoordinates.addAll(coordinates);
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

        // Move to the beginning of the word
        while (isValidPosition(startRow, startCol) && ScrabbleController.board[startRow][startCol] != '\0') {
            if (isHorizontal) {
                if (startCol == 0) break;
                startCol--;
            } else {
                if (startRow == 0) break;
                startRow--;
            }
        }

        if (isHorizontal) startCol++;
        else startRow++;

        // Collect the word
        while (isValidPosition(startRow, startCol) && ScrabbleController.board[startRow][startCol] != '\0') {
            word.append(ScrabbleController.board[startRow][startCol]);
            if (isHorizontal) startCol++;
            else startRow++;
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
     * @param isFirstTurn   true if it is the first turn of the game, false otherwise.
     * @return true if the word placement is valid, false otherwise.
     */
    public static boolean isWordPlacementValid(boolean isFirstTurn, boolean showMessages) {

        if (isFirstTurn) {
            // Get all words formed and validate them
            Set<String> wordsFormed = getAllWordsFormed();
            if (!areAllWordsValid(wordsFormed, showMessages)) {
                if (showMessages) {
                    JOptionPane.showMessageDialog(null, "Invalid first word.");
                }
                return false;
            }

            // Ensure the center tile is used and properly placed
            if (!isUsingCenterTile()) {
                if (showMessages) {
                    JOptionPane.showMessageDialog(null, "The first word must use the center tile.");
                }
                return false;
            }

            if (!isCenterTileAdjacentToAnotherTile()) {
                if (showMessages) {
                    JOptionPane.showMessageDialog(null, "On the first turn, the center tile must be adjacent to another newly placed tile.");
                }
                return false;
            }
        }

        // Validation for non-first turn
        if (!isFirstTurn && !isAdjacentToExistingWords()) {
            if (showMessages) {
                JOptionPane.showMessageDialog(null, "Placed letters must be adjacent to existing words.");
            }
            return false;
        }

        // Check alignment (must be in a single row or column)
        if (!isAlignedInSingleRowOrColumn()) {
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
     * @return a Set of Strings containing all unique words formed.
     */
    public static Set<String> getAllWordsFormed() {
        ArrayList<JButton> placedButtons = ScrabbleController.getMasterPlacedButtons();
        Set<String> uniqueWordsFormed = new HashSet<>();

        for (JButton button : placedButtons) {
            // Ensure valid button properties
            int row = (Integer) button.getClientProperty("row");
            int col = (Integer) button.getClientProperty("col");

            // Collect words formed in both directions
            String verticalWord = collectFullWord(row, col, false);
            String horizontalWord = collectFullWord(row, col, true);

            // Only add valid and new words
            if (!verticalWord.isEmpty() && isWordNew(verticalWord, placedButtons)) {
                uniqueWordsFormed.add(verticalWord);
            }
            if (!horizontalWord.isEmpty() && isWordNew(horizontalWord, placedButtons)) {
                uniqueWordsFormed.add(horizontalWord);
            }
        }

        return uniqueWordsFormed;
    }


    private static boolean isWordNew(String word, ArrayList<JButton> placedButtons) {
        // Ensure the word is not null or empty
        if (word == null || word.isEmpty()) {
            return false;
        }

        // Check that all tiles in the word are from the current placement
        for (JButton button : placedButtons) {
            String tileText = button.getText();
            if (tileText == null || tileText.isEmpty()) {
                continue; // Skip empty tiles
            }
            char tile = tileText.charAt(0);
            if (!word.contains(String.valueOf(tile))) {
                return false; // The word contains tiles not placed in the current move
            }
        }

        return true; // All tiles in the word are newly placed
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
     */
    private static boolean isAlignedInSingleRowOrColumn() {
        ArrayList<JButton> placedButtons = ScrabbleController.getMasterPlacedButtons();

        // Check alignment of newly placed tiles
        boolean isSingleRow = placedButtons.stream().map(b -> (Integer) b.getClientProperty("row")).distinct().count() == 1;
        boolean isSingleColumn = placedButtons.stream().map(b -> (Integer) b.getClientProperty("col")).distinct().count() == 1;

        // Allow valid adjacency to existing tiles
        if (isSingleRow || isSingleColumn) {
            return true;
        }

        // If not aligned, check if they connect to existing words
        return isAdjacentToExistingWords();
    }


    /**
     * Checks if the placed letters use the center tile of the board, required for the first move.
     *
     * @return true if any placed tile uses the center tile, false otherwise.
     */
    private static boolean isUsingCenterTile() {
        ArrayList<JButton> placedButtons = ScrabbleController.getMasterPlacedButtons();

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
     * @return true if at least one placed letter is adjacent to an existing letter, false otherwise.
     */
    private static boolean isAdjacentToExistingWords() {
        ArrayList<JButton> placedButtons = ScrabbleController.getMasterPlacedButtons();

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
     * @return true if the center tile is adjacent to another placed tile, false otherwise.
     */
    private static boolean isCenterTileAdjacentToAnotherTile() {
        ArrayList<JButton> placedButtons = ScrabbleController.getMasterPlacedButtons();

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
