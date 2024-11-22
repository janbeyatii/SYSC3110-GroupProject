package src;

import GUI.ScrabbleController;

import javax.swing.*;
import java.util.*;

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
            // If the AI has no tiles, it passes its turn
            System.out.println("AI has no tiles and will pass its turn.");
            return formedWords; // No words formed
        }

        // Try forming and placing a word
        for (int attempt = 0; attempt < 100; attempt++) { // Limit attempts to avoid infinite loops
            String word = formWord(aiTiles); // Form a word using AI's tiles
            if (word.isEmpty()) {
                System.out.println("AI could not form a valid word.");
                return formedWords; // No valid word formed
            }

            // Attempt to place the word on the board
            boolean isPlaced = tryPlaceWordOnBoard(board, word, aiTiles);

            if (isPlaced) {
                formedWords.add(word);
                System.out.println("AI placed the word: " + word);
                break; // Exit the loop after successfully placing a word
            }
        }

        if (formedWords.isEmpty()) {
            System.out.println("AI failed to place any word after multiple attempts.");
        }

        return formedWords;
    }

    private static boolean tryPlaceWordOnBoard(char[][] board, String word, List<Character> aiTiles) {
        Random random = new Random();
        int boardSize = board.length;

        // Attempt to place the word on the board in valid positions
        for (int attempt = 0; attempt < 50; attempt++) {
            boolean isHorizontal = random.nextBoolean(); // Randomly decide alignment
            int row = random.nextInt(boardSize);
            int col = random.nextInt(boardSize);

            // Create a dummy placement to validate
            ArrayList<JButton> placedButtonsDummy = new ArrayList<>();
            for (int i = 0; i < word.length(); i++) {
                JButton dummyButton = new JButton();
                int currentRow = isHorizontal ? row : row + i;
                int currentCol = isHorizontal ? col + i : col;

                // Ensure placement is within bounds
                if (currentRow >= boardSize || currentCol >= boardSize) {
                    break;
                }

                dummyButton.putClientProperty("row", currentRow);
                dummyButton.putClientProperty("col", currentCol);
                placedButtonsDummy.add(dummyButton);
            }

            // Validate placement
            if (Helpers.isWordPlacementValid(placedButtonsDummy, ScrabbleController.isFirstTurn(), false)) {
                // Place the word on the actual board
                for (int i = 0; i < word.length(); i++) {
                    int currentRow = isHorizontal ? row : row + i;
                    int currentCol = isHorizontal ? col + i : col;

                    board[currentRow][currentCol] = word.charAt(i);
                }

                // Remove used tiles from AI's tiles
                for (char c : word.toCharArray()) {
                    aiTiles.remove((Character) c);
                }

                // Draw new tiles for the AI
                List<Character> newTiles = ScrabbleController.tileBag.drawTiles(word.length());
                aiTiles.addAll(newTiles);
                System.out.println("Drawing tiles for AI: " + newTiles);

                return true; // Successfully placed the word
            }
        }

        return false; // Failed to place the word
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

        // Attempt to form a valid word using AI's tiles
        for (int attempt = 0; attempt < 100; attempt++) { // Limit attempts to avoid infinite loops
            word.setLength(0); // Clear the previous word
            int wordLength = Math.min(aiTiles.size(), random.nextInt(6) + 2); // Random word length (2 to 7)

            for (int i = 0; i < wordLength; i++) {
                int tileIndex = random.nextInt(aiTiles.size());
                word.append(aiTiles.get(tileIndex));
            }

            // Validate the word
            if (WordValidity.isWordValid(word.toString())) {
                return word.toString(); // Return the valid word
            }
        }

        // If no valid word is formed after multiple attempts, return an empty string
        return "";
    }


}
