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
        if (selectedLetter != null && !selectedLetter.isEmpty() && boardButtons[row][col].getText().isEmpty()) {
            // Place the letter on the button in the GUI
            boardButtons[row][col].setText(selectedLetter);

            // Store the letter in the underlying board array to keep it persistent
            ScrabbleController.board[row][col] = selectedLetter.charAt(0);

            // Track the placed button to allow clearing for the current turn
            placedButtons.add(boardButtons[row][col]);

            // Reset selected letter and previously selected button
            selectedLetter = null;
            if (previouslySelectedButton != null) {
                previouslySelectedButton.setEnabled(true);
                previouslySelectedButton = null;
            }
        } else {
            JOptionPane.showMessageDialog(null, "Select a letter first or choose an empty tile.");
        }
    }


    public static void submitWord(ScrabbleView view, JTextArea wordHistoryArea, JLabel turnLabel, ArrayList<JButton> placedButtons, JLabel[] playerScoresLabels, JButton[] playerTileButtons) {
        StringBuilder wordBuilder = new StringBuilder();
        int startRow = -1;
        int startCol = -1;
        boolean isHorizontal = true;

        // Build the word from placed buttons and determine orientation
        for (int i = 0; i < placedButtons.size(); i++) {
            JButton button = placedButtons.get(i);
            int row = (Integer) button.getClientProperty("row");
            int col = (Integer) button.getClientProperty("col");

            // Set the starting position for the word
            if (i == 0) {
                startRow = row;
                startCol = col;
            } else if (row != startRow) {
                isHorizontal = false;
            }

            wordBuilder.append(button.getText());
        }
        String word = wordBuilder.toString();

        // Prepare a list of coordinates for each placed tile
        List<int[]> placedTiles = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {
            int row = startRow + (isHorizontal ? 0 : i);
            int col = startCol + (isHorizontal ? i : 0);
            placedTiles.add(new int[]{row, col});
        }

        // Step 1: Validate the entire placement
        if (WordPlacementLogic.isPlacementValid(startRow, startCol, word, isHorizontal, view)) {
            // Step 2: Check if all words formed are valid
            if (WordPlacementLogic.validateNewWords(placedTiles, view)) {
                // Main word validation
                if (WordValidity.isWordValid(word)) {
                    // Calculate score for the main word
                    ScoreCalculation scoreCalculation = new ScoreCalculation(word, placedButtons);
                    int score = scoreCalculation.getTotalScore();

                    // Add score to current player
                    ScrabbleController.addScoreToCurrentPlayer(score);
                    updateScores(playerScoresLabels);

                    // Record the word in history
                    String playerName = ScrabbleController.getCurrentPlayerName();
                    wordHistoryArea.append(playerName + ": " + word + " (" + score + " points)\n");

                    // Step 3: Check for additional words formed by this placement
                    List<String> newWords = WordPlacementLogic.checkNewWords(placedTiles, view);
                    for (String newWord : newWords) {
                        if (!newWord.equals(word) && WordValidity.isWordValid(newWord)) {  // Only display if newWord is distinct
                            int newWordScore = new ScoreCalculation(newWord, placedButtons).getTotalScore();
                            ScrabbleController.addScoreToCurrentPlayer(newWordScore);
                            wordHistoryArea.append(playerName + ": " + newWord + " (" + newWordScore + " points)\n");
                        }
                    }

                    // Remove used tiles from the player’s set and refill
                    TileBag.removeUsedTiles(word);
                    ScrabbleController.switchToNextPlayer();
                    turnLabel.setText("Turn: " + ScrabbleController.getCurrentPlayerName());

                    // Confirm placement on the board
                    confirmedButtons.addAll(placedButtons);
                    placedButtons.clear();  // Clear the temporary list for the next turn

                    view.updateBoardDisplay();
                    view.updatePlayerTiles();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid word. Try again.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Invalid placement. One or more words formed are not valid.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Invalid placement. Please follow Scrabble rules.");
        }

        // Clear placed letters in case of invalid move or at the end of submission
        clearPlacedLetters(placedButtons, playerTileButtons);
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
            tileButton.setEnabled(true);
        }
    }


    public static void updateScores(JLabel[] playerScoresLabels) {
        for (int i = 0; i < ScrabbleController.getPlayercount(); i++) {
            playerScoresLabels[i].setText(ScrabbleController.getPlayerNames().get(i) + " Score: " + ScrabbleController.getPlayerScore(i));
        }
    }
}