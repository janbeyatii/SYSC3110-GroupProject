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
            previouslySelectedButton = null;

        } else {
            JOptionPane.showMessageDialog(null, "Select a letter first or choose an empty tile.");
        }
    }


    public static void submitWord(ScrabbleView view, JTextArea wordHistoryArea, JLabel turnLabel, ArrayList<JButton> placedButtons, JLabel[] playerScoresLabels, JButton[] playerTileButtons) {
        StringBuilder wordBuilder = new StringBuilder();
        int startRow = -1;
        int startCol = -1;
        boolean isHorizontal = true;

        // Build the main word from placed buttons and determine orientation
        for (int i = 0; i < placedButtons.size(); i++) {
            JButton button = placedButtons.get(i);
            int row = (Integer) button.getClientProperty("row");
            int col = (Integer) button.getClientProperty("col");

            if (i == 0) {
                startRow = row;
                startCol = col;
            } else if (row != startRow) {
                isHorizontal = false;
            }

            wordBuilder.append(button.getText());
        }
        String word = wordBuilder.toString();

        List<int[]> placedTiles = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {
            int row = startRow + (isHorizontal ? 0 : i);
            int col = startCol + (isHorizontal ? i : 0);
            placedTiles.add(new int[]{row, col});
        }

        if (WordPlacementLogic.isPlacementValid(startRow, startCol, word, isHorizontal, view)) {
            if (WordPlacementLogic.validateNewWords(placedTiles, view)) {
                if (WordValidity.isWordValid(word)) {
                    int totalScore = 0;

                    // Calculate and add the score for the main word
                    ScoreCalculation mainWordScore = new ScoreCalculation(word, placedButtons);
                    totalScore += mainWordScore.getTotalScore();
                    String playerName = ScrabbleController.getCurrentPlayerName();
                    wordHistoryArea.append(playerName + ": " + word + " (" + mainWordScore.getTotalScore() + " points)\n");

                    // Calculate and add scores for additional words formed
                    List<String> newWords = WordPlacementLogic.checkNewWords(placedTiles, view);
                    for (String newWord : newWords) {
                        if (!newWord.equals(word) && WordValidity.isWordValid(newWord)) {
                            ScoreCalculation newWordScore = new ScoreCalculation(newWord, placedButtons);
                            totalScore += newWordScore.getTotalScore();
                            wordHistoryArea.append(playerName + ": " + newWord + " (" + newWordScore.getTotalScore() + " points)\n");
                        }
                    }

                    // Add total score to the player's score
                    ScrabbleController.addScoreToCurrentPlayer(totalScore);
                    updateScores(playerScoresLabels);

                    TileBag.removeUsedTiles(word);
                    ScrabbleController.switchToNextPlayer();
                    turnLabel.setText("Turn: " + ScrabbleController.getCurrentPlayerName());

                    confirmedButtons.addAll(placedButtons);
                    placedButtons.clear();
                    if (WordPlacementLogic.isPlacementValid(startRow, startCol, word, isHorizontal, view)) {
                        clearPlacedLetters(placedButtons, playerTileButtons);
                    }
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