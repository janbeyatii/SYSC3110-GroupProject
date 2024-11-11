package src;

import GUI.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Helpers {

    private static JButton previouslySelectedButton = null; // Track the previously selected button
    private static String selectedLetter = null; // Track the currently selected letter
    private static ArrayList<JButton> confirmedButtons = new ArrayList<>();
    private static ArrayList<JButton> placedButtons = new ArrayList<>();  // Tiles placed during the current turn

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

        for (JButton button : placedButtons) {
            wordBuilder.append(button.getText());
        }
        String word = wordBuilder.toString();

        // First, check if the word is valid (exists in the dictionary)
        if (WordValidity.isWordValid(word)) {
            // Then, check if the placement is valid according to Scrabble rules
                // Calculate score
                ScoreCalculation scoreCalculation = new ScoreCalculation(word, placedButtons);
                int score = scoreCalculation.getTotalScore();

                // Add score to current player
                ScrabbleController.addScoreToCurrentPlayer(score);
                updateScores(playerScoresLabels);

                // Record the word in history
                String playerName = ScrabbleController.getCurrentPlayerName();
                wordHistoryArea.append(playerName + ": " + word + " (" + score + " points)\n");

                // Remove used tiles from the player’s set and refill
                TileBag.removeUsedTiles(word);
                ScrabbleController.switchToNextPlayer();
                turnLabel.setText("Turn: " + ScrabbleController.getCurrentPlayerName());

                confirmedButtons.addAll(placedButtons);
                placedButtons.clear();  // Clear the temporary list for the next turn

                view.updateBoardDisplay();
                view.updatePlayerTiles();
        } else {
            // Inform the user about the invalid word
            JOptionPane.showMessageDialog(null, "Invalid word. Try again.");
        }
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