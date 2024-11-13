package src;

import GUI.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Helpers {

    private static JButton previouslySelectedButton = null;
    private static String selectedLetter = null;

    /**
     * Selects a letter from the player's tile rack, disables it in the rack to indicate it is in use, and tracks it as the currently selected letter.
     *
     * @param index             the index of the selected letter tile in the player's tile rack.
     * @param playerTileButtons the array of JButton elements representing the player's tile rack.
     */
    public static void selectLetterFromTiles(int index, JButton[] playerTileButtons) {
        if (previouslySelectedButton != null) {
            previouslySelectedButton.setEnabled(true);
        }

        selectedLetter = playerTileButtons[index].getText();
        playerTileButtons[index].setEnabled(false);
        previouslySelectedButton = playerTileButtons[index];
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
        } else {
            JOptionPane.showMessageDialog(null, "Select a letter first or choose an empty tile.");
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

        // Move backward to find the start of the word, with boundary check
        while (isValidPosition(startRow, startCol) && ScrabbleController.board[startRow][startCol] != '\0') {
            if (isHorizontal) {
                if (startCol == 0) break;  // Stop if we reach the left boundary
                startCol--;
            } else {
                if (startRow == 0) break;  // Stop if we reach the top boundary
                startRow--;
            }
        }

        // Adjust to the first letter of the word if we went one step too far
        if (isHorizontal && ScrabbleController.board[startRow][startCol] == '\0' && startCol < ScrabbleController.board[0].length - 1) {
            startCol++;
        }
        if (!isHorizontal && ScrabbleController.board[startRow][startCol] == '\0' && startRow < ScrabbleController.board.length - 1) {
            startRow++;
        }

        // Move forward to build the word from start to end, with boundary check
        int r = startRow;
        int c = startCol;
        while (isValidPosition(r, c) && ScrabbleController.board[r][c] != '\0') {
            word.append(ScrabbleController.board[r][c]);
            if (isHorizontal) {
                if (c == ScrabbleController.board[0].length - 1) break;  // Stop if we reach the right boundary
                c++;
            } else {
                if (r == ScrabbleController.board.length - 1) break;  // Stop if we reach the bottom boundary
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
     */
    static boolean isWordPlacementValid(ArrayList<JButton> placedButtons, boolean isFirstTurn) {
        if (!isAlignedInSingleRowOrColumn(placedButtons)) {
            JOptionPane.showMessageDialog(null, "Placed letters must be in the same row or column.");
            return false;
        }

        if (isFirstTurn && !isUsingCenterTile(placedButtons)) {
            JOptionPane.showMessageDialog(null, "The first word must use the center tile.");
            return false;
        }

        if (!isFirstTurn && !isAdjacentToExistingWords(placedButtons)) {
            JOptionPane.showMessageDialog(null, "Placed letters must be adjacent to existing words.");
            return false;
        }

        return true;
    }

    static Set<String> getAllWordsFormed(ArrayList<JButton> placedButtons) {
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

    static boolean areAllWordsValid(Set<String> words) {
        for (String word : words) {
            if (!WordValidity.isWordValid(word)) {
                JOptionPane.showMessageDialog(null, "Invalid word formed: " + word);
                return false;
            }
        }
        return true;
    }

    static void updateScoresAndDisplayWords(Set<String> words, JTextArea wordHistoryArea, JLabel[] playerScoresLabels) {
        int totalScore = 0;
        for (String word : words) {
            ScoreCalculation wordScore = new ScoreCalculation(word, new ArrayList<>());
            totalScore += wordScore.getTotalScore();
            String playerName = ScrabbleController.getCurrentPlayerName();
            wordHistoryArea.append(playerName + ": " + word + " (" + wordScore.getTotalScore() + " points)\n");
        }

        ScrabbleController.addScoreToCurrentPlayer(totalScore);
        updateScores(playerScoresLabels);
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
     *
     * @param placedButtons the list of JButtons where letters were placed in the current turn.
     * @return true if at least one placed letter is adjacent to an existing letter, false otherwise.
     */
    private static boolean isAdjacentToExistingWords(ArrayList<JButton> placedButtons) {
        for (JButton button : placedButtons) {
            int row = (Integer) button.getClientProperty("row");
            int col = (Integer) button.getClientProperty("col");

            // Check for existing adjacent tiles with letters above, below, left, and right
            if ((row > 0 && ScrabbleController.board[row - 1][col] != '\0') || // Above
                    (row < ScrabbleController.board.length - 1 && ScrabbleController.board[row + 1][col] != '\0') || // Below
                    (col > 0 && ScrabbleController.board[row][col - 1] != '\0') || // Left
                    (col < ScrabbleController.board[0].length - 1 && ScrabbleController.board[row][col + 1] != '\0')) { // Right
                return true; // At least one tile is adjacent to an existing letter
            }
        }

        // If no adjacent tiles with letters are found, return false
        return false;
    }
    /**
     * Updates the player scores displayed on the screen.
     *
     * @param playerScoresLabels the array of JLabels that display players' scores.
     */
    public static void updateScores(JLabel[] playerScoresLabels) {
        for (int i = 0; i < ScrabbleController.getPlayercount(); i++) {
            playerScoresLabels[i].setText(ScrabbleController.getPlayerNames().get(i) + " Score: " + ScrabbleController.getPlayerScore(i));
        }
    }
}
