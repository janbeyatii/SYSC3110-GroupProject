package src;

import GUI.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

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
        // Ensure selected letter is valid
        if (selectedLetter != null && !selectedLetter.isEmpty()) {
            // Check if the tile is already occupied
            if (!boardButtons[row][col].getText().isEmpty()) {
                boolean isTileReplaced = placedButtons.contains(boardButtons[row][col]);

                // If it's occupied and not the same tile, show error
                if (!isTileReplaced) {
                    JOptionPane.showMessageDialog(null, "Tile is already occupied. Choose another tile.");
                    // Re-enable the previously selected button and reset the selection
                    if (previouslySelectedButton != null) {
                        previouslySelectedButton.setEnabled(true); // Re-enable the tile
                    }
                    selectedLetter = null; // Clear selected letter
                    previouslySelectedButton = null; // Clear previously selected button
                    return; // Exit early if tile is occupied
                }
            }

            // Place the letter on the board button in the GUI
            boardButtons[row][col].setText(selectedLetter);

            // Update the board state (stored in the ScrabbleController)
            ScrabbleController.board[row][col] = selectedLetter.charAt(0);

            // Track placed buttons for clearing on next turn
            placedButtons.add(boardButtons[row][col]);

            // Reset selected letter and previously selected button
            selectedLetter = null;
            previouslySelectedButton = null;

            System.out.println("Letter placed: " + selectedLetter + " at position [" + row + "," + col + "]");
        } else {
            JOptionPane.showMessageDialog(null, "Select a letter first or choose an empty tile.");
        }
    }

    public static void submitWord(ScrabbleView view, JTextArea wordHistoryArea, JLabel turnLabel, ArrayList<JButton> placedButtons, JLabel[] playerScoresLabels, JButton[] playerTileButtons) {
        List<String> allWordsFormed = new ArrayList<>();
        boolean isFirstTurn = ScrabbleController.getPlayerScore(0) == 0; // Detect if it's the first turn
        boolean allWordsValid = true;

        // Sort the placed buttons based on row and column to help detect alignment and gaps
        placedButtons.sort((button1, button2) -> {
            int row1 = (Integer) button1.getClientProperty("row");
            int col1 = (Integer) button1.getClientProperty("col");
            int row2 = (Integer) button2.getClientProperty("row");
            int col2 = (Integer) button2.getClientProperty("col");

            // Sort by row first, then by column
            if (row1 == row2) {
                return Integer.compare(col1, col2);
            }
            return Integer.compare(row1, row2);
        });

        // Get the main word by detecting alignment and including gaps (including newly placed letters)
        String mainWord = getFullWordFromBoardIncludingNewLetters(placedButtons);

        // First turn logic: Ensure the main word includes the center tile
        if (isFirstTurn) {
            int boardCenterRow = ScrabbleController.board.length / 2;
            int boardCenterCol = ScrabbleController.board[0].length / 2;
            boolean isCenterTileUsed = placedButtons.stream().anyMatch(button ->
                    (Integer) button.getClientProperty("row") == boardCenterRow &&
                            (Integer) button.getClientProperty("col") == boardCenterCol
            );

            if (!isCenterTileUsed) {
                JOptionPane.showMessageDialog(null, "The first word must use the center tile.");
                clearPlacedLetters(placedButtons, playerTileButtons);
                return;
            }
        }

        if (mainWord.isEmpty()) {
            JOptionPane.showMessageDialog(null, "The word must be placed in a single row or column.");
            clearPlacedLetters(placedButtons, playerTileButtons);
            return;
        }
        allWordsFormed.add(mainWord);

        // Collect any additional crosswords formed (including new letters)
        for (JButton button : placedButtons) {
            int row = (Integer) button.getClientProperty("row");
            int col = (Integer) button.getClientProperty("col");

            if (!isFirstTurn) {
                // Collect any crosswords formed by this tile placement (including new letters)
                String verticalWord = collectWordIncludingNewLetters(row, col, false);
                String horizontalWord = collectWordIncludingNewLetters(row, col, true);

                if (!verticalWord.isEmpty() && !verticalWord.equals(mainWord)) {
                    allWordsFormed.add(verticalWord);
                }
                if (!horizontalWord.isEmpty() && !horizontalWord.equals(mainWord)) {
                    allWordsFormed.add(horizontalWord);
                }
            }
        }

        // Validate all words and display each, whether valid or invalid
        for (String word : allWordsFormed) {
            if (!WordValidity.isWordValid(word)) {
                allWordsValid = false;
                JOptionPane.showMessageDialog(null, "Invalid word formed: " + word);
                clearPlacedLetters(placedButtons, playerTileButtons);
                return;
            }
        }

        // If valid, calculate score, update board, and add to history
        if (allWordsValid) {
            int totalScore = 0;
            for (String word : allWordsFormed) {
                ScoreCalculation wordScore = new ScoreCalculation(word, placedButtons);
                totalScore += wordScore.getTotalScore();
                String playerName = ScrabbleController.getCurrentPlayerName();

                // Display the word and score in wordHistoryArea
                wordHistoryArea.append(playerName + ": " + word + " (" + wordScore.getTotalScore() + " points)\n");
            }

            ScrabbleController.addScoreToCurrentPlayer(totalScore);
            updateScores(playerScoresLabels);

            TileBag.removeUsedTiles(mainWord);
            ScrabbleController.switchToNextPlayer();
            turnLabel.setText("Turn: " + ScrabbleController.getCurrentPlayerName());

            placedButtons.clear();
            clearPlacedLetters(placedButtons, playerTileButtons);
            view.updateBoardDisplay();
            view.updatePlayerTiles();
        }
    }




    // Helper function to get the full word, including the newly placed letter
    private static String getFullWordFromBoardIncludingNewLetters(ArrayList<JButton> placedButtons) {
        boolean isHorizontal = true; // Assume horizontal placement unless otherwise determined
        int startRow = (Integer) placedButtons.get(0).getClientProperty("row");
        int startCol = (Integer) placedButtons.get(0).getClientProperty("col");

        // Determine alignment (horizontal or vertical)
        for (JButton button : placedButtons) {
            int row = (Integer) button.getClientProperty("row");
            int col = (Integer) button.getClientProperty("col");
            if (row != startRow) {
                isHorizontal = false;
                break;
            }
        }

        // Collect all letters in the full word along the determined alignment, including the newly placed letter
        return isHorizontal ? collectWordIncludingNewLetters(startRow, startCol, true) : collectWordIncludingNewLetters(startRow, startCol, false);
    }


    // Helper function to collect a full word in a specified direction, including newly placed letters
    private static String collectWordIncludingNewLetters(int row, int col, boolean horizontal) {
        StringBuilder word = new StringBuilder();

        // Move backward to find the start of the word
        int startRow = row;
        int startCol = col;
        while ((horizontal ? startCol : startRow) > 0 && ScrabbleController.board[startRow][startCol] != '\0') {
            if (horizontal) {
                startCol--;
            } else {
                startRow--;
            }
        }

        // Adjust to the first letter of the word
        if (horizontal && ScrabbleController.board[startRow][startCol] == '\0') startCol++;
        if (!horizontal && ScrabbleController.board[startRow][startCol] == '\0') startRow++;

        // Move forward to build the word from start to end
        int r = startRow;
        int c = startCol;
        boolean hasMultipleLetters = false; // Flag to confirm it's a multi-letter word
        while (r < ScrabbleController.board.length && c < ScrabbleController.board[0].length && ScrabbleController.board[r][c] != '\0') {
            word.append(ScrabbleController.board[r][c]);

            // Check if we have more than one letter for valid word formation
            if (word.length() > 1) {
                hasMultipleLetters = true;
            }

            if (horizontal) {
                c++;
            } else {
                r++;
            }
        }

        // Return only if we have a valid word (not a single letter)
        return hasMultipleLetters ? word.toString() : "";
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