package src;

import GUI.ScrabbleController;
import GUI.ScrabbleView;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class AIPlayer {

    /**
     * Simulates an AI making a move on the board.
     * The AI forms a valid word using its available tiles and ensures the placement adheres to Scrabble rules.
     *
     * @param board   The current Scrabble board.
     * @param aiTiles The AI's available tiles.
     * @return A set of words formed by the AI.
     */
    public static Set<String> makeMove(char[][] board, List<Character> aiTiles) {
        Set<String> formedWords = new HashSet<>();

        if (aiTiles == null || aiTiles.isEmpty()) {
            System.out.println("AI has no tiles and will pass its turn.");
            return formedWords;
        }

        for (int attempt = 0; attempt < 100; attempt++) {
            String word = formWord(aiTiles);
            if (word.isEmpty()) {
                System.out.println("AI could not form a valid word.");
                return formedWords;
            }

            boolean isPlaced = tryPlaceWordOnBoard(board, word, aiTiles);

            if (isPlaced) {
                // Collect all words formed by this placement
                formedWords = Helpers.getAllWordsFormed(ScrabbleController.getPlacedButtons());

                // Validate all words formed
                if (!Helpers.areAllWordsValid(formedWords, false)) {
                    // Undo the move if any word is invalid
                    System.out.println("AI formed invalid words. Undoing move.");
                    undoAIMove(board, ScrabbleController.getPlacedButtons());
                    formedWords.clear();
                    continue;
                }

                System.out.println("AI placed the word: " + word);
                break;
            }
        }

        if (formedWords.isEmpty()) {
            System.out.println("AI failed to place any word after multiple attempts.");
        }

        return formedWords;
    }

    /**
     * Undoes an AI's move by removing the tiles it placed on the board.
     * This method resets the affected board cells to empty and clears the text on the corresponding buttons
     * in the GUI to reflect the undone move.
     *
     * @param board          A 2D character array representing the Scrabble board.
     *                       Each cell holds a character representing a tile or '\0' if the cell is empty.
     * @param placedButtons  A list of buttons representing the tiles placed during the AI's move.
     *                       These buttons are cleared visually and logically.
     */
    private static void undoAIMove(char[][] board, ArrayList<JButton> placedButtons) {
        for (JButton button : placedButtons) {
            int row = (Integer) button.getClientProperty("row");
            int col = (Integer) button.getClientProperty("col");
            board[row][col] = '\0';
            button.setText("");
        }

        placedButtons.clear();
    }

    /**
     * Attempts to place a word on the Scrabble board for the AI player.
     * This method randomly selects a position and orientation (horizontal/vertical) for the word
     * and validates its placement according to the game's rules. If the placement is valid,
     * the word is added to the board, and the AI's tiles are updated accordingly.
     *
     * @param board    A 2D character array representing the Scrabble board.
     *                 Each cell holds a character representing a tile or '\0' if the cell is empty.
     * @param word     The word the AI is attempting to place on the board.
     * @param aiTiles  A list of characters representing the AI player's available tiles.
     *                 Tiles used for the word are removed, and new tiles are drawn to replace them.
     * @return True if the word was successfully placed on the board; false otherwise.
     */
    private static boolean tryPlaceWordOnBoard(char[][] board, String word, List<Character> aiTiles) {
        Random random = new Random();
        int boardSize = board.length;

        for (int attempt = 0; attempt < 50; attempt++) {
            boolean isHorizontal = random.nextBoolean();
            int row = random.nextInt(boardSize);
            int col = random.nextInt(boardSize);

            ArrayList<JButton> placedButtonsDummy = new ArrayList<>();
            boolean isValidPlacement = true;

            // Clear previous placements
            ScrabbleController.clearPlacedTileCoordinates();

            for (int i = 0; i < word.length(); i++) {
                int currentRow = isHorizontal ? row : row + i;
                int currentCol = isHorizontal ? col + i : col;

                if (currentRow >= boardSize || currentCol >= boardSize || board[currentRow][currentCol] != '\0') {
                    isValidPlacement = false;
                    break;
                }

                JButton dummyButton = new JButton();
                dummyButton.putClientProperty("row", currentRow);
                dummyButton.putClientProperty("col", currentCol);
                dummyButton.setText(String.valueOf(word.charAt(i)));
                placedButtonsDummy.add(dummyButton);
            }

            if (!isValidPlacement) continue;

            if (!Helpers.isWordPlacementValid(placedButtonsDummy, ScrabbleController.isFirstTurn(), false)) {
                continue;
            }

            // Place the word on the board and capture coordinates
            for (int i = 0; i < word.length(); i++) {
                int currentRow = isHorizontal ? row : row + i;
                int currentCol = isHorizontal ? col + i : col;

                board[currentRow][currentCol] = word.charAt(i);
                ScrabbleController.addPlacedTileCoordinates(new Point(currentRow, currentCol));
            }

            // Retrieve the coordinates of placed tiles
            List<Point> placedCoordinates = ScrabbleController.getPlacedTileCoordinates();
            System.out.println("Placed coordinates: " + placedCoordinates);

            // Validate words formed and calculate score (existing logic)
            Set<String> formedWords = Helpers.getAllWordsFormed(placedButtonsDummy);
            if (!Helpers.areAllWordsValid(formedWords, false)) {
                System.out.println("Invalid words formed: " + formedWords);
                undoAIMove(board, placedButtonsDummy);
                continue;
            }

// Scoring and updating logic (adjusted to show score for each word individually)
            int totalScore = 0;
            for (String formedWord : formedWords) {
                // Calculate the score for each word
                ScoreCalculation scoreCalculation = new ScoreCalculation(formedWord, placedButtonsDummy);
                int wordScore = scoreCalculation.getTotalScore();

                // Append each word's score separately in the history
                ScrabbleController.getView().wordHistoryArea.append("AI placed: " + formedWord + " (" + wordScore + " points)\n");

                // Add the word's score to the total score
                totalScore += wordScore;
            }

            ScrabbleController.addScoreToPlayer(ScrabbleController.getCurrentPlayerIndex(), totalScore);

            System.out.println("AI formed words: " + formedWords);
            System.out.println("AI scored: " + totalScore + " points.");

            // Update AI tiles
            for (char c : word.toCharArray()) {
                aiTiles.remove((Character) c);
            }
            List<Character> newTiles = ScrabbleController.tileBag.drawTiles(word.length());
            aiTiles.addAll(newTiles);

            // Update the view
            ScrabbleView view = ScrabbleController.getView();
            view.updateBoardDisplay();
            view.updateAITiles();
            view.repaint();
            view.revalidate();

            return true;
        }

        return false;
    }


    /**
     * Forms a word using the AI's available tiles.
     *
     * @param aiTiles The AI's available tiles.
     * @return A valid word formed using the tiles.
     */
    private static String formWord(List<Character> aiTiles) {
        StringBuilder word = new StringBuilder();
        Random random = new Random();

        for (int attempt = 0; attempt < 100; attempt++) {
            word.setLength(0);
            int wordLength = Math.min(aiTiles.size(), random.nextInt(6) + 2);

            for (int i = 0; i < wordLength; i++) {
                int tileIndex = random.nextInt(aiTiles.size());
                word.append(aiTiles.get(tileIndex));
            }

            if (WordValidity.isWordValid(word.toString())) {
                return word.toString();
            }
        }

        return "";
    }


}
