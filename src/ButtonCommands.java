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
        boolean isFirstTurn = ScrabbleController.getPlayerScore(0) == 0;

        if (!isWordPlacementValid(placedButtons, isFirstTurn)) {
            clear(placedButtons, playerTileButtons);
            return;
        }

        Set<String> uniqueWordsFormed = getAllWordsFormed(placedButtons);

        if (!areAllWordsValid(uniqueWordsFormed)) {
            clear(placedButtons, playerTileButtons);
            return;
        }

        updateScoresAndDisplayWords(uniqueWordsFormed, wordHistoryArea, playerScoresLabels);

        int currentPlayerScore = ScrabbleController.getPlayerScore(ScrabbleController.getCurrentPlayerIndex());
        if (currentPlayerScore >= 150) {
            JOptionPane.showMessageDialog(view, "YOU WIN YAY!!", "Congratulations!", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        ScrabbleController.addPlacedTiles(placedButtons);
        placedButtons.clear();
        clear(placedButtons, playerTileButtons);
        view.updateBoardDisplay();
        view.updatePlayerTiles();

        ScrabbleController.switchToNextPlayer();
        turnLabel.setText("Turn: " + ScrabbleController.getCurrentPlayerName());
    }

    /**
     * Passes the current player's turn, clears any tiles placed during the turn, and updates the display to show the next player's turn.
     *
     * @param placedButtons      the list of JButtons representing tiles placed by the player during the current turn.
     * @param playerTileButtons  the array of JButton elements representing the player's tile rack.
     * @param turnLabel          the JLabel showing the current player's turn.
     */
    public static void pass(ArrayList<JButton> placedButtons, JButton[] playerTileButtons, JLabel turnLabel) {
        clear(placedButtons, playerTileButtons);
        ScrabbleController.switchToNextPlayer();
        turnLabel.setText("Turn: " + ScrabbleController.getCurrentPlayerName());
    }

    /**
     * Clears all tiles placed on the board during the current turn, resets the player's rack to allow tile reuse, and empties the list of placed tiles.
     *
     * @param placedButtons      the list of JButtons representing tiles placed by the player during the current turn.
     * @param playerTileButtons  the array of JButton elements representing the player's tile rack.
     */
    public static void clear(ArrayList<JButton> placedButtons, JButton[] playerTileButtons) {
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

}