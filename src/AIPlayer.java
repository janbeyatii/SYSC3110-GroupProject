package src;

import java.util.*;
import GUI.ScrabbleController;

public class AIPlayer {
    /**
     * Simulates an AI making a move on the board.
     * The AI forms a valid word using its available tiles.
     *
     * @param board      The current Scrabble board.
     * @param aiTiles    The AI's available tiles.
     * @return A set of words formed by the AI.
     */
    public static Set<String> makeMove(char[][] board, List<Character> aiTiles) {
        Set<String> formedWords = new HashSet<>();

        if (aiTiles == null || aiTiles.isEmpty()) {
            // If the AI has no tiles, it passes its turn
            System.out.println("AI has no tiles and will pass its turn.");
            return formedWords; // No words formed
        }

        // Form the best possible word using the AI's tiles
        String word = formWord(aiTiles);

        if (!word.isEmpty()) {
            formedWords.add(word);

            // Place the word on the board
            placeWordOnBoard(board, word);

            // Remove used tiles and refill AI's tile set
            TileBag.removeUsedTiles(word);
        }

        return formedWords;
    }

    /**
     * Forms a word using the AI's available tiles.
     * This version forms a simple word but can be extended with smarter logic.
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


    /**
     * Places a word on the board. The word is placed starting at a random position
     * and aligned either horizontally or vertically.
     *
     * @param board The Scrabble board.
     * @param word  The word to place on the board.
     */
    private static void placeWordOnBoard(char[][] board, String word) {
        Random random = new Random();
        int boardSize = board.length;

        // Randomly decide alignment (horizontal or vertical)
        boolean isHorizontal = random.nextBoolean();

        // Pick a random starting position on the board
        int row, col;
        if (isHorizontal) {
            row = random.nextInt(boardSize);
            col = random.nextInt(boardSize - word.length() + 1); // Ensure word fits
        } else {
            row = random.nextInt(boardSize - word.length() + 1); // Ensure word fits
            col = random.nextInt(boardSize);
        }

        // Place the word on the board
        for (int i = 0; i < word.length(); i++) {
            if (isHorizontal) {
                board[row][col + i] = word.charAt(i);
            } else {
                board[row + i][col] = word.charAt(i);
            }
        }

        System.out.println("AI placed word '" + word + "' at " +
                (isHorizontal ? "row " + row + ", starting at column " + col : "column " + col + ", starting at row " + row));
    }
}
