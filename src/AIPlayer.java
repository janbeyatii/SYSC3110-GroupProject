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
    public static Set<String> makeMove(char[][] board, List<Character> aiTiles, JLabel[] playerScoresLabels) {
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

            boolean isPlaced = tryPlaceWordOnBoard(board, word, aiTiles, playerScoresLabels);

            if (isPlaced) {
                // Collect all words formed by this placement
                formedWords = Helpers.getAllWordsFormed();

                // Validate all words formed
                if (Helpers.areAllWordsValid(formedWords, false)) {
                    System.out.println("AI placed the word: " + word);
                    break;
                } else {
                    System.out.println("AI formed invalid words. Undoing move.");
                    undoAIMove(board);
                    formedWords.clear();
                }
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
     */
    private static void undoAIMove(char[][] board) {
        ArrayList<JButton> masterPlacedButtons = ScrabbleController.getMasterPlacedButtons();
        for (JButton button : masterPlacedButtons) {
            int row = (Integer) button.getClientProperty("row");
            int col = (Integer) button.getClientProperty("col");
            board[row][col] = '\0';
            button.setText("");
        }

        ScrabbleController.clearMasterPlacedButtons(); // Clear the centralized list
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
    private static boolean tryPlaceWordOnBoard(char[][] board, String word, List<Character> aiTiles, JLabel[] playerScoresLabels) {
        Random random = new Random();
        int boardSize = board.length;

        for (int attempt = 0; attempt < 150; attempt++) {
            boolean isHorizontal = random.nextBoolean();
            int row = random.nextInt(boardSize);
            int col = random.nextInt(boardSize);

            // Clear previously placed tiles
            ScrabbleController.clearMasterPlacedButtons();
            ScrabbleController.clearPlacedTileCoordinates();

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

                JButton button = new JButton(String.valueOf(word.charAt(i)));
                button.putClientProperty("row", currentRow);
                button.putClientProperty("col", currentCol);
                ScrabbleController.addToMasterPlacedButtons(button);
                ScrabbleController.setPlacedTileCoordinates(new Point(currentRow, currentCol));
            }

            if (!isValidPlacement) continue;

            if (!Helpers.isWordPlacementValid(ScrabbleController.isFirstTurn(), false)) {
                continue;
            }

            // Place the word on the board
            for (int i = 0; i < word.length(); i++) {
                int currentRow = isHorizontal ? row : row + i;
                int currentCol = isHorizontal ? col + i : col;
                board[currentRow][currentCol] = word.charAt(i);
            }

            // Validate words formed
            Set<String> formedWords = Helpers.getAllWordsFormed();
            if (!Helpers.areAllWordsValid(formedWords, false)) {
                System.out.println("Invalid words formed: " + formedWords);
                undoAIMove(board); // Undo placement
                continue;
            }

            ButtonCommands.updateScoresAndDisplayWords(formedWords, ScrabbleController.getView().wordHistoryArea, playerScoresLabels);
            ScrabbleController.clearMasterPlacedButtons(); // Clear only after a valid move
            Helpers.updateOldTileCoordinates(); // Update old tile coordinates after AI places its tiles

            System.out.println("AI formed words: " + formedWords);

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
