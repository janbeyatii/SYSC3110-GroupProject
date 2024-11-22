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
            System.out.println("AI has no tiles and will pass its turn.");
            return formedWords;
        }

        // Try forming and placing a word
        for (int attempt = 0; attempt < 100; attempt++) {
            String word = formWord(aiTiles);
            if (word.isEmpty()) {
                System.out.println("AI could not form a valid word.");
                return formedWords;
            }

            boolean isPlaced = tryPlaceWordOnBoard(board, word, aiTiles);

            if (isPlaced) {
                formedWords.add(word);
                System.out.println("AI placed the word: " + word);
                break;
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

        for (int attempt = 0; attempt < 50; attempt++) {
            boolean isHorizontal = random.nextBoolean();
            int row = random.nextInt(boardSize);
            int col = random.nextInt(boardSize);

            ArrayList<JButton> placedButtonsDummy = new ArrayList<>();
            boolean isValidPlacement = true;

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

            if (Helpers.isWordPlacementValid(placedButtonsDummy, ScrabbleController.isFirstTurn(), false)) {
                Set<String> formedWords = Helpers.getAllWordsFormed(placedButtonsDummy);
                if (!Helpers.areAllWordsValid(formedWords)) {
                    continue;
                }

                for (int i = 0; i < word.length(); i++) {
                    int currentRow = isHorizontal ? row : row + i;
                    int currentCol = isHorizontal ? col + i : col;
                    board[currentRow][currentCol] = word.charAt(i);
                }

                for (char c : word.toCharArray()) {
                    aiTiles.remove((Character) c);
                }

                List<Character> newTiles = ScrabbleController.tileBag.drawTiles(word.length());
                aiTiles.addAll(newTiles);
                System.out.println("Drawing tiles for AI: " + newTiles);

                return true;
            }
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
