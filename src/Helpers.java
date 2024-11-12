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
        StringBuilder wordBuilder = new StringBuilder();
        int startRow = -1;
        int startCol = -1;
        boolean isHorizontal = true;

        // Build the word from placed buttons and determine orientation
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

        // Validate adjacency for all placed tiles, including already placed tiles (e.g., "E" from previous turn)
        System.out.println("Validating adjacency for word: " + word);

        for (int i = 0; i < placedButtons.size(); i++) {
            JButton button = placedButtons.get(i);
            int row = (Integer) button.getClientProperty("row");
            int col = (Integer) button.getClientProperty("col");

            boolean isTileAdjacent = false;

            // Check if the newly placed tile is adjacent to any already placed tiles on the board
            for (int r = 0; r < ScrabbleController.board.length; r++) {
                for (int c = 0; c < ScrabbleController.board[r].length; c++) {
                    if (ScrabbleController.board[r][c] != '\0') {  // Only check non-empty spots (tiles already placed)
                        if ((row == r - 1 && col == c) || // Above
                                (row == r + 1 && col == c) || // Below
                                (row == r && col == c - 1) || // Left
                                (row == r && col == c + 1)) { // Right
                            isTileAdjacent = true;
                            break;
                        }
                    }
                }
            }

            // If none of the tiles are adjacent to already placed tiles, mark it as invalid
            if (!isTileAdjacent) {
                JOptionPane.showMessageDialog(null, "All tiles must be adjacent to each other.");
                System.out.println("Adjacency check failed.");
                // Restore the tiles in the player's hand and re-enable them
                for (JButton tileButton : playerTileButtons) {
                    tileButton.setEnabled(true);
                }
                // Clear placed letters
                clearPlacedLetters(placedButtons, playerTileButtons);
                return;  // Exit early if adjacency is invalid
            }
        }
        System.out.println("Adjacency check passed.");

        List<int[]> placedTiles = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {
            int row = startRow + (isHorizontal ? 0 : i);
            int col = startCol + (isHorizontal ? i : 0);
            placedTiles.add(new int[]{row, col});
        }

        // Validate placement
        if (WordPlacementLogic.isPlacementValid(startRow, startCol, word, isHorizontal, view)) {
            // Validate new words formed by the placement
            if (WordPlacementLogic.validateNewWords(placedTiles, view)) {
                if (WordValidity.isWordValid(word)) {
                    int totalScore = 0;

                    // Calculate the score for the main word
                    ScoreCalculation mainWordScore = new ScoreCalculation(word, placedButtons);
                    totalScore += mainWordScore.getTotalScore();
                    String playerName = ScrabbleController.getCurrentPlayerName();
                    wordHistoryArea.append(playerName + ": " + word + " (" + mainWordScore.getTotalScore() + " points)\n");

                    // Calculate scores for additional words formed
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

                    // Update the tile bag after a valid word
                    TileBag.removeUsedTiles(word);

                    // Switch to the next player
                    ScrabbleController.switchToNextPlayer();
                    turnLabel.setText("Turn: " + ScrabbleController.getCurrentPlayerName());

                    placedButtons.clear();
                    clearPlacedLetters(placedButtons, playerTileButtons);
                    view.updateBoardDisplay();
                    view.updatePlayerTiles();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid word. Try again.");
                    System.out.println("Word is not valid.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Invalid placement. One or more words formed are not valid.");
                System.out.println("Placement is invalid.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Invalid placement. Please follow Scrabble rules.");
            System.out.println("Placement is invalid.");
        }

        // Clear placed letters after the word is placed and validated
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