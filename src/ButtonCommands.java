package src;

import GUI.ScrabbleController;
import GUI.ScrabbleView;

import javax.swing.*;
import java.util.*;

import static GUI.ScrabbleController.clearPlacedTileCoordinates;
import static GUI.ScrabbleController.saveCurrentStateToUndoStack;

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
     * @param playerScoresLabels the array of JLabels displaying the scores of each player.
     * @param playerTileButtons  the array of JButton elements representing the player's tile rack.
     */
    public static void submit(ScrabbleView view, JTextArea wordHistoryArea, JLabel turnLabel, JLabel[] playerScoresLabels, JButton[] playerTileButtons) {
        saveCurrentStateToUndoStack();
        boolean isFirstTurn = ScrabbleController.isFirstTurn();
        ArrayList<JButton> placedButtons = ScrabbleController.getMasterPlacedButtons();

        if (!Helpers.isWordPlacementValid(isFirstTurn, true)) {
            ScrabbleController.clearMasterPlacedButtons();
            clear(playerTileButtons);
            return;
        }

        Set<String> uniqueWordsFormed = Helpers.getAllWordsFormed();

        // Validate the words
        if (!Helpers.areAllWordsValid(uniqueWordsFormed, true)) {
            resetInvalidPlacement(playerTileButtons, placedButtons);
            return;
        }

        updateScoresAndDisplayWords(uniqueWordsFormed, wordHistoryArea, playerScoresLabels);

        String currentPlayer = ScrabbleController.getCurrentPlayerName();
        List<Character> currentPlayerTiles = ScrabbleController.getPlayerTilesMap().get(currentPlayer);

        System.out.println("unique formed words: " + uniqueWordsFormed);

        for (String word : uniqueWordsFormed) {
            for (char letter : word.toCharArray()) { // Loop through each letter in the word
                if (currentPlayerTiles.remove((Character) letter)) {
                    System.out.println("Removed tile: " + letter);
                } else {
                    System.out.println("Warning: Tile '" + letter + "' not found in player tiles.");
                }
            }
        }

        System.out.println("After removing tiles, player tiles: " + currentPlayerTiles);


        List<Character> newTiles = ScrabbleController.tileBag.drawTiles(placedButtons.size());
        currentPlayerTiles.addAll(newTiles);
        System.out.println("New Tiles Drawn: " + newTiles);

        view.updatePlayerTiles();

        if (isFirstTurn) {
            ScrabbleController.setFirstTurnCompleted();
        }

        ScrabbleController.addPlacedTiles();
        ScrabbleController.clearMasterPlacedButtons();
        Helpers.updateOldTileCoordinates();
        clearPlacedTileCoordinates();
        view.updateBoardDisplay();

        ScrabbleController.switchToNextPlayer(playerScoresLabels);
        turnLabel.setText("Turn: " + ScrabbleController.getCurrentPlayerName());

        Helpers.updateScores(playerScoresLabels);
    }

    /**
     * Resets the game state after detecting an invalid tile placement.
     *
     * <p>This method performs the following actions:
     * <ul>
     *     <li>Clears the board positions and button text for the tiles placed in invalid positions.</li>
     *     <li>Re-enables the player's tile buttons to allow re-placement.</li>
     *     <li>Clears the master placed buttons list to reset tracking of placed tiles.</li>
     * </ul>
     *
     * @param playerTileButtons an array of JButton objects representing the player's tile rack.
     * @param placedButtons an ArrayList of JButton objects representing tiles that were placed on the board.
     */
    private static void resetInvalidPlacement(JButton[] playerTileButtons, ArrayList<JButton> placedButtons) {
        for (JButton button : placedButtons) {
            int row = (Integer) button.getClientProperty("row");
            int col = (Integer) button.getClientProperty("col");

            // Clear the board position and button text
            ScrabbleController.board[row][col] = '\0';
            button.setText("");
        }

        // Re-enable the player's tiles
        for (JButton button : playerTileButtons) {
            button.setEnabled(true);
        }

        // Clear the master placed buttons list
        ScrabbleController.clearMasterPlacedButtons();
        System.out.println("Invalid placement cleared. Tiles returned to the rack.");
    }

    /**
     * Passes the current player's turn, clears any tiles placed during the turn, and updates the display to show the next player's turn.
     *
     * @param playerTileButtons the array of JButton elements representing the player's tile rack.
     * @param turnLabel         the JLabel showing the current player's turn.
     */
    public static void pass(JButton[] playerTileButtons, JLabel turnLabel, JLabel[] playerScoresLabels) {
        saveCurrentStateToUndoStack();
        clear(playerTileButtons);
        ScrabbleController.switchToNextPlayer(playerScoresLabels);
        turnLabel.setText("Turn: " + ScrabbleController.getCurrentPlayerName());
    }

    /**
     * Clears all tiles placed on the board during the current turn, resets the player's rack to allow tile reuse, and empties the list of placed tiles.
     *
     * @param playerTileButtons the array of JButton elements representing the player's tile rack.
     */
    public static void clear(JButton[] playerTileButtons) {
        saveCurrentStateToUndoStack();
        ArrayList<JButton> placedButtons = ScrabbleController.getMasterPlacedButtons();
        for (JButton button : placedButtons) {
            int row = (Integer) button.getClientProperty("row");
            int col = (Integer) button.getClientProperty("col");

            button.setText("");
            ScrabbleController.board[row][col] = '\0';
        }

        clearPlacedTileCoordinates();

        for (JButton tileButton : playerTileButtons) {
            if (tileButton.getText() != null && !tileButton.getText().isEmpty()) {
                tileButton.setEnabled(true);
            }
        }
    }


    /**
     * Updates the word history and player scores after a valid turn.
//     *
     * @param wordHistoryArea    the JTextArea where the history is displayed.
     * @param playerScoresLabels the array of JLabels displaying players' scores.
     */
    public static void updateScoresAndDisplayWords(Set<String> formedWords, JTextArea wordHistoryArea, JLabel[] playerScoresLabels) {
        int currentPlayerIndex = ScrabbleController.getCurrentPlayerIndex();
        int turnScore = 0;

        // Track already scored words to avoid duplicates
        Set<String> scoredWords = new HashSet<>();

        for (String word : formedWords) {
            if (!scoredWords.contains(word)) {
                ScoreCalculation scoreCalc = new ScoreCalculation(word);
                int wordScore = scoreCalc.getTotalScore();
                turnScore += wordScore;

                wordHistoryArea.append(ScrabbleController.getCurrentPlayerName() + " placed: " + word + " (" + wordScore + " points)\n");
                scoredWords.add(word); // Mark word as scored
            }
        }

        // Add the score to the current player's total
        ScrabbleController.addScoreToPlayer(currentPlayerIndex, turnScore);
        Helpers.updateScores(playerScoresLabels);

        // Check for winning condition
        int currentPlayerScore = ScrabbleController.getPlayerScore(currentPlayerIndex);
        if (currentPlayerScore >= 100) {
            ScrabbleController.showWinningAnimation();
        }
    }
}
