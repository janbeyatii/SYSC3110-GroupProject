package src;

import GUI.ScrabbleController;
import GUI.ScrabbleView;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static GUI.ScrabbleController.clearPlacedTileCoordinates;
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

        if (!Helpers.isWordPlacementValid(placedButtons, isFirstTurn, true)) {
            clear(placedButtons, playerTileButtons);
            return;
        }

        Set<String> uniqueWordsFormed = Helpers.getAllWordsFormed(placedButtons);

        if (!Helpers.areAllWordsValid(uniqueWordsFormed)) {
            clear(placedButtons, playerTileButtons);
            return;
        }

        updateScoresAndDisplayWords(uniqueWordsFormed, wordHistoryArea, playerScoresLabels);

        String currentPlayer = ScrabbleController.getCurrentPlayerName();
        List<Character> currentPlayerTiles = ScrabbleController.getPlayerTilesMap().get(currentPlayer);

        for (JButton button : placedButtons) {
            String letter = button.getText();
            currentPlayerTiles.remove((Character) letter.charAt(0));
        }

        List<Character> newTiles = ScrabbleController.tileBag.drawTiles(placedButtons.size());
        currentPlayerTiles.addAll(newTiles);
        System.out.println("New Tiles Drawn: " + newTiles);

        view.updatePlayerTiles();

        if (isFirstTurn) {
            ScrabbleController.setFirstTurnCompleted();
        }

        ScrabbleController.addPlacedTiles(placedButtons);
        Helpers.updateOldTileCoordinates();

        placedButtons.clear();
        clear(placedButtons, playerTileButtons);

        view.updateBoardDisplay();
        ScrabbleController.switchToNextPlayer();
        turnLabel.setText("Turn: " + ScrabbleController.getCurrentPlayerName());

        Helpers.updateScores(playerScoresLabels);
    }


    /**
     * Passes the current player's turn, clears any tiles placed during the turn, and updates the display to show the next player's turn.
     *
     * @param placedButtons     the list of JButtons representing tiles placed by the player during the current turn.
     * @param playerTileButtons the array of JButton elements representing the player's tile rack.
     * @param turnLabel         the JLabel showing the current player's turn.
     */
    public static void pass(ArrayList<JButton> placedButtons, JButton[] playerTileButtons, JLabel turnLabel) {
        clear(placedButtons, playerTileButtons);
        ScrabbleController.switchToNextPlayer();
        turnLabel.setText("Turn: " + ScrabbleController.getCurrentPlayerName());
    }

    /**
     * Clears all tiles placed on the board during the current turn, resets the player's rack to allow tile reuse, and empties the list of placed tiles.
     *
     * @param placedButtons     the list of JButtons representing tiles placed by the player during the current turn.
     * @param playerTileButtons the array of JButton elements representing the player's tile rack.
     */
    public static void clear(ArrayList<JButton> placedButtons, JButton[] playerTileButtons) {
        for (JButton button : placedButtons) {
            int row = (Integer) button.getClientProperty("row");
            int col = (Integer) button.getClientProperty("col");

            button.setText("");
            ScrabbleController.board[row][col] = '\0';
        }

        placedButtons.clear();
        clearPlacedTileCoordinates();
        for (JButton tileButton : playerTileButtons) {
            if (tileButton.getText() != null && !tileButton.getText().isEmpty()) {
                tileButton.setEnabled(true);
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
        int currentPlayerIndex = ScrabbleController.getCurrentPlayerIndex();
        int turnScore = 0;

        for (String word : uniqueWordsFormed) {
            ScoreCalculation scoreCalc = new ScoreCalculation(word, ScrabbleController.getPlacedButtons());
            int wordScore = scoreCalc.getTotalScore();
            turnScore += wordScore;

            // Display word and its total score in the history area
            wordHistoryArea.append(word + ": " + wordScore + " points\n");

            // Display in terminal: word, letter scores, and total score
            System.out.println("Word: " + word);
            System.out.print("Letter Scores: ");
            ArrayList<Integer> letterScores = scoreCalc.getLetterScores();

            for (int i = 0; i < word.length(); i++) {
                char letter = word.charAt(i);
                System.out.print(letter + "(" + letterScores.get(i) + ") ");
            }
            System.out.println("\nTotal Score: " + wordScore + " points");
        }

        // Add turn score to the player's total score
        ScrabbleController.addScoreToPlayer(currentPlayerIndex, turnScore);

        // Update player score labels
        for (int i = 0; i < playerScoresLabels.length; i++) {
            playerScoresLabels[i].setText("Score: " + ScrabbleController.getPlayerScore(i));
        }
    }
}
