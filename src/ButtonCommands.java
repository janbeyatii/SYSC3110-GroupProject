package src;

import GUI.ScrabbleController;
import GUI.ScrabbleView;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Set;

import static src.Helpers.*;

/**
 * This class contains commands for handling button actions such as submitting, passing, and clearing moves in the Scrabble game.
 */
public class ButtonCommands {

    /**
     * Default constructor for the ButtonCommands class.
     * Initializes the ButtonCommands object without any specific setup.
     */
    public ButtonCommands() {
        // Default constructor
    }

    /**
     * Submits the word(s) formed by the player, validates the placement and legality of words, updates scores,
     * switches the turn to the next player, and refreshes the board and player tiles.
     *
     * @param view               the ScrabbleView instance used to update the display.
     * @param wordHistoryArea    the JTextArea displaying the history of words formed by players.
     * @param turnLabel          the JLabel showing the current player's turn.
     * @param placedButtons      the list of JButtons representing tiles placed by the player during the current turn.
     * @param playerScoresLabels the array of JLabels displaying the scores of each player.
     * @param playerTileButtons  the array of JButton elements representing the player's tile rack.
     */
    public static void submit(ScrabbleView view, JTextArea wordHistoryArea, JLabel turnLabel, ArrayList<JButton> placedButtons, JLabel[] playerScoresLabels, JButton[] playerTileButtons) {
        boolean isFirstTurn = ScrabbleController.isFirstTurn();

        // Validate word placement, including adjacency check
        if (!isWordPlacementValid(placedButtons, isFirstTurn)) {
            clear(placedButtons, playerTileButtons);
            return;
        }

        Set<String> uniqueWordsFormed = getAllWordsFormed(placedButtons);

        // Check if all words are valid
        if (!areAllWordsValid(uniqueWordsFormed)) {
            clear(placedButtons, playerTileButtons);
            return;
        }

        // Update scores and display words in history separately
        updateScoresAndDisplayWords(uniqueWordsFormed, wordHistoryArea, playerScoresLabels);

        // Check if any player has reached 150 points or more
        int currentPlayerScore = ScrabbleController.getPlayerScore(ScrabbleController.getCurrentPlayerIndex());
        if (currentPlayerScore >= 150) {
            JOptionPane.showMessageDialog(view, "YOU WIN YAY!!", "Congratulations!", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Mark the first turn as completed
        if (isFirstTurn) {
            ScrabbleController.setFirstTurnCompleted();
        }

        // Add placed tiles to the board
        ScrabbleController.addPlacedTiles(placedButtons);

        // Update the old tile coordinates after this turn
        updateOldTileCoordinates();

        placedButtons.clear();
        clear(placedButtons, playerTileButtons);

        // Refresh the display
        view.updateBoardDisplay();
        view.updatePlayerTiles();

        // Switch to the next player
        ScrabbleController.switchToNextPlayer();
        turnLabel.setText("Turn: " + ScrabbleController.getCurrentPlayerName());

        // Update the score labels
        updateScores(playerScoresLabels);
    }

    /**
     * Passes the current player's turn, clears any tiles placed during the turn, and updates the display to show the next player's turn.
     *
     * @param placedButtons     the list of JButtons representing tiles placed by the player during the current turn.
     * @param playerTileButtons the array of JButton elements representing the player's tile rack.
     * @param turnLabel         the JLabel showing the current player's turn.
     */
    public static void pass(ArrayList<JButton> placedButtons, JButton[] playerTileButtons, JLabel turnLabel) {
        clear(placedButtons, playerTileButtons);  // Clear the placed tiles and reset the tile rack
        ScrabbleController.switchToNextPlayer();  // Switch to the next player
        turnLabel.setText("Turn: " + ScrabbleController.getCurrentPlayerName());  // Update the turn label
    }

    /**
     * Clears all tiles placed on the board during the current turn, resets the player's rack to allow tile reuse, and empties the list of placed tiles.
     *
     * @param placedButtons     the list of JButtons representing tiles placed by the player during the current turn.
     * @param playerTileButtons the array of JButton elements representing the player's tile rack.
     */
    public static void clear(ArrayList<JButton> placedButtons, JButton[] playerTileButtons) {
        // Clear the placed tiles from the board
        for (JButton button : placedButtons) {
            int row = (Integer) button.getClientProperty("row");
            int col = (Integer) button.getClientProperty("col");

            button.setText("");  // Remove the text (letter) from the button
            ScrabbleController.board[row][col] = '\0';  // Reset the board position to empty
        }

        placedButtons.clear();  // Clear the list of placed buttons

        // Re-enable the tile buttons in the player's tile rack
        for (JButton tileButton : playerTileButtons) {
            if (tileButton.getText() != null && !tileButton.getText().isEmpty()) {
                tileButton.setEnabled(true);  // Re-enable the button if it is not empty
            }
        }
    }


    /**
     * Updates the word history and player scores after a valid turn.
     *
     * @param uniqueWordsFormed  the set of unique words formed in this turn.
     * @param wordHistoryArea    the JTextArea where the history is displayed.
     * @param playerScoresLabels the array of JLabels displaying players' scores.
     */
    public static void updateScoresAndDisplayWords(Set<String> uniqueWordsFormed, JTextArea wordHistoryArea, JLabel[] playerScoresLabels) {
        // Add each word formed in the current turn to the word history
        int currentPlayerIndex = ScrabbleController.getCurrentPlayerIndex();
        int turnScore = 0;

        // Process each word formed
        for (String word : uniqueWordsFormed) {
            ScoreCalculation scoreCalc = new ScoreCalculation(word, ScrabbleController.getPlacedButtons());
            int wordScore = scoreCalc.getTotalScore(); // Calculate the score for the word
            turnScore += wordScore; // Add to the total score for the turn

            // Display the word and its score in the word history
            wordHistoryArea.append(word + ": " + wordScore + " points\n");
        }

        // Update the player's total score
        ScrabbleController.addScoreToPlayer(currentPlayerIndex, turnScore);

        // Update the score labels
        for (int i = 0; i < playerScoresLabels.length; i++) {
            playerScoresLabels[i].setText("Score: " + ScrabbleController.getPlayerScore(i));
        }
    }

}
