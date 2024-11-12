package src;

import GUI.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Helpers {

    private static JButton previouslySelectedButton = null; // Track the previously selected button
    private static String selectedLetter = null; // Track the currently selected letter
    private static ArrayList<JButton> confirmedButtons = new ArrayList<>();

    public static void selectLetterFromTiles(int index, JButton[] playerTileButtons) {
        if (previouslySelectedButton != null) {
            previouslySelectedButton.setEnabled(true); // Re-enable previously selected button
        }

        selectedLetter = playerTileButtons[index].getText();
        playerTileButtons[index].setEnabled(false); // Disable the newly selected button to show it's in use
        previouslySelectedButton = playerTileButtons[index]; // Update the reference to the currently selected button
    }

    public static void placeLetterOnBoard(int row, int col, JButton[][] boardButtons, ArrayList<JButton> placedButtons) {
        if (selectedLetter != null && !selectedLetter.isEmpty() && boardButtons[row][col].getText().isEmpty()) {
            // Place the letter on the button in the GUI
            boardButtons[row][col].setText(selectedLetter);

            // Store the letter in the underlying board array to keep it persistent
            ScrabbleController.board[row][col] = selectedLetter.charAt(0);

            // Track the placed button to allow clearing for the current turn
            placedButtons.add(boardButtons[row][col]);

            // Reset selected letter and previously selected button
            selectedLetter = null;
            previouslySelectedButton = null;

        } else {
            JOptionPane.showMessageDialog(null, "Select a letter first or choose an empty tile.");
        }
    }

    private static String collectFullWord(int row, int col, boolean isHorizontal) {
        StringBuilder word = new StringBuilder();

        // Traverse backwards to find the start of the word
        int startRow = row;
        int startCol = col;
        while (isValidPosition(startRow, startCol) && ScrabbleController.board[startRow][startCol] != '\0') {
            if (isHorizontal) {
                startCol--;
            } else {
                startRow--;
            }
        }

        // Adjust start position if necessary
        if (isHorizontal && ScrabbleController.board[startRow][startCol] == '\0') startCol++;
        if (!isHorizontal && ScrabbleController.board[startRow][startCol] == '\0') startRow++;

        // Traverse forwards to collect the full word
        while (isValidPosition(startRow, startCol) && ScrabbleController.board[startRow][startCol] != '\0') {
            word.append(ScrabbleController.board[startRow][startCol]);
            if (isHorizontal) {
                startCol++;
            } else {
                startRow++;
            }
        }

        return word.length() > 1 ? word.toString() : ""; // Only return valid words longer than 1 letter
    }

    private static boolean isValidPosition(int row, int col) {
        return row >= 0 && row < ScrabbleController.board.length && col >= 0 && col < ScrabbleController.board[0].length;
    }

    public static void submitWord(ScrabbleView view, JTextArea wordHistoryArea, JLabel turnLabel, ArrayList<JButton> placedButtons, JLabel[] playerScoresLabels, JButton[] playerTileButtons) {
        List<String> allWordsFormed = new ArrayList<>();
        boolean allWordsValid = true;
        boolean isFirstTurn = ScrabbleController.getPlayerScore(0) == 0;

        // Validate alignment of placed tiles in a single row or column
        if (!isAlignedInSingleRowOrColumn(placedButtons)) {
            JOptionPane.showMessageDialog(null, "Placed letters must be in the same row or column.");
            clearPlacedLetters(placedButtons, playerTileButtons);
            return;
        }

        // Check for adjacency to existing words, unless it's the first turn
        if (isFirstTurn && !isUsingCenterTile(placedButtons)) {
            JOptionPane.showMessageDialog(null, "First word must be adjacent to existing words.");
            clearPlacedLetters(placedButtons, playerTileButtons);
            return;
        }

        // Check for adjacency to existing words, unless it's the first turn
        if (!isFirstTurn && !isAdjacentToExistingWords(placedButtons)) {
            JOptionPane.showMessageDialog(null, "Placed letters must be adjacent to existing words.");
            clearPlacedLetters(placedButtons, playerTileButtons);
            return;
        }

        // Collect words formed by placed tiles without duplicates
        Set<String> uniqueWordsFormed = new HashSet<>();
        for (JButton button : placedButtons) {
            int row = (Integer) button.getClientProperty("row");
            int col = (Integer) button.getClientProperty("col");

            // Check horizontal and vertical word formation
            String verticalWord = collectFullWord(row, col, false);
            String horizontalWord = collectFullWord(row, col, true);

            if (!verticalWord.isEmpty()) {
                uniqueWordsFormed.add(verticalWord);
            }
            if (!horizontalWord.isEmpty()) {
                uniqueWordsFormed.add(horizontalWord);
            }
        }
        allWordsFormed = new ArrayList<>(uniqueWordsFormed);


        // Validate and score words only once
        for (String word : uniqueWordsFormed) {
            if (!WordValidity.isWordValid(word)) {
                allWordsValid = false;
                JOptionPane.showMessageDialog(null, "Invalid word formed: " + word);
                clearPlacedLetters(placedButtons, playerTileButtons);
                return;
            }
        }

        if (allWordsValid) {
            int totalScore = 0;
            for (String word : uniqueWordsFormed) {
                ScoreCalculation wordScore = new ScoreCalculation(word, placedButtons);
                totalScore += wordScore.getTotalScore();
                String playerName = ScrabbleController.getCurrentPlayerName();
                wordHistoryArea.append(playerName + ": " + word + " (" + wordScore.getTotalScore() + " points)\n");
            }

            ScrabbleController.addScoreToCurrentPlayer(totalScore);
            updateScores(playerScoresLabels);
            TileBag.removeUsedTiles(String.valueOf(allWordsFormed));
            ScrabbleController.switchToNextPlayer();
            turnLabel.setText("Turn: " + ScrabbleController.getCurrentPlayerName());

            // Update the board and tiles
            ScrabbleController.addPlacedTiles(placedButtons);
            placedButtons.clear();
            clearPlacedLetters(placedButtons, playerTileButtons);
            view.updateBoardDisplay();
            view.updatePlayerTiles();
        }
    }

    // Check if placed buttons are aligned in a single row or column
    private static boolean isAlignedInSingleRowOrColumn(ArrayList<JButton> placedButtons) {
        boolean isSingleRow = placedButtons.stream().map(b -> (Integer) b.getClientProperty("row")).distinct().count() == 1;
        boolean isSingleColumn = placedButtons.stream().map(b -> (Integer) b.getClientProperty("col")).distinct().count() == 1;
        return isSingleRow || isSingleColumn;
    }

    // Check if the placed tiles include the center tile on the first turn
    private static boolean isUsingCenterTile(ArrayList<JButton> placedButtons) {
        int boardCenterRow = ScrabbleController.board.length / 2;
        int boardCenterCol = ScrabbleController.board[0].length / 2;
        return placedButtons.stream().anyMatch(button ->
                (Integer) button.getClientProperty("row") == boardCenterRow &&
                        (Integer) button.getClientProperty("col") == boardCenterCol
        );
    }

    private static boolean isAdjacentToExistingWords(ArrayList<JButton> placedButtons) {
        // Assume horizontal or vertical alignment
        boolean isHorizontal = isAlignedInSingleRowOrColumn(placedButtons);
        int minRow = Integer.MAX_VALUE, maxRow = Integer.MIN_VALUE;
        int minCol = Integer.MAX_VALUE, maxCol = Integer.MIN_VALUE;

        for (JButton button : placedButtons) {
            int row = (Integer) button.getClientProperty("row");
            int col = (Integer) button.getClientProperty("col");
            minRow = Math.min(minRow, row);
            maxRow = Math.max(maxRow, row);
            minCol = Math.min(minCol, col);
            maxCol = Math.max(maxCol, col);
        }

        // Traverse the aligned row or column to check continuity and adjacency
        if (isHorizontal) {
            for (int col = minCol; col <= maxCol; col++) {
                if (ScrabbleController.board[minRow][col] == '\0' && !isPlacedHere(placedButtons, minRow, col)) {
                    return false; // Gap in the word
                }
            }
        } else {
            for (int row = minRow; row <= maxRow; row++) {
                if (ScrabbleController.board[row][minCol] == '\0' && !isPlacedHere(placedButtons, row, minCol)) {
                    return false; // Gap in the word
                }
            }
        }

        // Check if any placed button is adjacent to an existing tile
        for (JButton button : placedButtons) {
            int row = (Integer) button.getClientProperty("row");
            int col = (Integer) button.getClientProperty("col");

            if ((row > 0 && ScrabbleController.board[row - 1][col] != '\0') || // above
                    (row < ScrabbleController.board.length - 1 && ScrabbleController.board[row + 1][col] != '\0') || // below
                    (col > 0 && ScrabbleController.board[row][col - 1] != '\0') || // left
                    (col < ScrabbleController.board[0].length - 1 && ScrabbleController.board[row][col + 1] != '\0')) { // right
                return true; // Adjacent to an existing word
            }
        }

        // If no adjacent tile is found, return false
        return false;
    }

    // Helper function to check if the button at (row, col) is in placedButtons
    private static boolean isPlacedHere(ArrayList<JButton> placedButtons, int row, int col) {
        return placedButtons.stream().anyMatch(button ->
                (Integer) button.getClientProperty("row") == row &&
                        (Integer) button.getClientProperty("col") == col);
    }


    public static void passTurn(ArrayList<JButton> placedButtons, JButton[] playerTileButtons, JLabel turnLabel) {
        clearPlacedLetters(placedButtons, playerTileButtons);
        ScrabbleController.switchToNextPlayer();
        turnLabel.setText("Turn: " + ScrabbleController.getCurrentPlayerName());
    }

    public static void clearPlacedLetters(ArrayList<JButton> placedButtons, JButton[] playerTileButtons) {
        for (JButton button : placedButtons) {
            int row = (Integer) button.getClientProperty("row");
            int col = (Integer) button.getClientProperty("col");

            button.setText("");
            ScrabbleController.board[row][col] = '\0';
        }

        placedButtons.clear();

        for (JButton tileButton : playerTileButtons) {
            if (tileButton.getText() != null && !tileButton.getText().isEmpty()) {
                tileButton.setEnabled(true);
            }
        }
    }

    public static void updateScores(JLabel[] playerScoresLabels) {
        for (int i = 0; i < ScrabbleController.getPlayercount(); i++) {
            playerScoresLabels[i].setText(ScrabbleController.getPlayerNames().get(i) + " Score: " + ScrabbleController.getPlayerScore(i));
        }
    }
}
