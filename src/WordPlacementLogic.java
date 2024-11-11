package src;

import GUI.ScrabbleController;
import GUI.ScrabbleView;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import static GUI.ScrabbleController.board;

public class WordPlacementLogic {

    private static final int BOARD_SIZE = 15;
    private static boolean isFirstWordPlaced = false;

    public static boolean isPlacementValid(int startRow, int startCol, String word, boolean isHorizontal, ScrabbleView view) {
        if (!isFirstWordPlaced) {
            // Ensure the first word crosses the center tile (7,7 on a 15x15 board)
            if (isHorizontal) {
                if (!(startRow == 7 && startCol <= 7 && startCol + word.length() > 7)) {
                    JOptionPane.showMessageDialog(null, "The first word must cross the center tile.");
                    return false;
                }
            } else {
                if (!(startCol == 7 && startRow <= 7 && startRow + word.length() > 7)) {
                    JOptionPane.showMessageDialog(null, "The first word must cross the center tile.");
                    return false;
                }
            }
            isFirstWordPlaced = true;  // Mark the first word as placed
        } else {
            // For subsequent words, ensure that at least one tile in the new word is adjacent to an existing tile
            boolean isConnected = false;

            for (int i = 0; i < word.length(); i++) {
                int row = startRow + (isHorizontal ? 0 : i);
                int col = startCol + (isHorizontal ? i : 0);

                char boardChar = view.boardButtons[row][col].getText().isEmpty() ? '\0' : view.boardButtons[row][col].getText().charAt(0);
                char wordChar = word.charAt(i);

                // Allow placing on an occupied space only if the characters match
                if (boardChar != '\0' && boardChar != wordChar) {
                    JOptionPane.showMessageDialog(null, "Cannot place a tile on an occupied space with a different letter.");
                    return false;
                }

                // Check if this tile is adjacent to an existing tile
                if (isTileAdjacentToExistingTile(row, col, view)) {
                    isConnected = true;
                }
            }

            // If no tile in the new word is adjacent to an existing tile, it's an invalid placement
            if (!isConnected) {
                JOptionPane.showMessageDialog(null, "Each word must connect to an existing word.");
                return false;
            }
        }

        return true;
    }





    private static boolean isTileAdjacentToExistingTile(int row, int col, ScrabbleView view) {
        // Check adjacent tiles (up, down, left, right) for existing letters
        return (row > 0 && !view.boardButtons[row - 1][col].getText().isEmpty()) ||  // Above
                (row < 14 && !view.boardButtons[row + 1][col].getText().isEmpty()) ||  // Below
                (col > 0 && !view.boardButtons[row][col - 1].getText().isEmpty()) ||   // Left
                (col < 14 && !view.boardButtons[row][col + 1].getText().isEmpty());    // Right
    }

    public static List<String> checkNewWords(List<int[]> placedTiles, ScrabbleView view) {
        List<String> newWords = new ArrayList<>();

        for (int[] tile : placedTiles) {
            int row = tile[0];
            int col = tile[1];

            // Check horizontal word in the row
            String horizontalWord = getWordInRow(row, col, view);
            if (horizontalWord.length() > 1 && WordValidity.isWordValid(horizontalWord) && !newWords.contains(horizontalWord)) {
                newWords.add(horizontalWord);
            }

            // Check vertical word in the column
            String verticalWord = getWordInColumn(row, col, view);
            if (verticalWord.length() > 1 && WordValidity.isWordValid(verticalWord) && !newWords.contains(verticalWord)) {
                newWords.add(verticalWord);
            }
        }

        return newWords;
    }

    private static String getWordInRow(int row, int col, ScrabbleView view) {
        StringBuilder word = new StringBuilder();

        // Move left to find the start of the word
        int leftCol = col;
        while (leftCol >= 0 && !view.boardButtons[row][leftCol].getText().isEmpty()) {
            leftCol--;
        }
        leftCol++; // Move to the first letter of the word

        // Move right to build the word
        while (leftCol < view.boardButtons[row].length && !view.boardButtons[row][leftCol].getText().isEmpty()) {
            word.append(view.boardButtons[row][leftCol].getText());
            leftCol++;
        }

        return word.toString();
    }

    private static String getWordInColumn(int row, int col, ScrabbleView view) {
        StringBuilder word = new StringBuilder();

        // Move up to find the start of the word
        int upRow = row;
        while (upRow >= 0 && !view.boardButtons[upRow][col].getText().isEmpty()) {
            upRow--;
        }
        upRow++; // Move to the first letter of the word

        // Move down to build the word
        while (upRow < view.boardButtons.length && !view.boardButtons[upRow][col].getText().isEmpty()) {
            word.append(view.boardButtons[upRow][col].getText());
            upRow++;
        }

        return word.toString();
    }

    public static boolean validateNewWords(List<int[]> placedTiles, ScrabbleView view) {
        for (int[] tile : placedTiles) {
            int row = tile[0];
            int col = tile[1];

            // Check horizontal word in the row
            String horizontalWord = getWordInRow(row, col, view);
            if (horizontalWord.length() > 1 && !WordValidity.isWordValid(horizontalWord)) {
                JOptionPane.showMessageDialog(null, "Invalid word formed: " + horizontalWord);
                return false;
            }

            // Check vertical word in the column
            String verticalWord = getWordInColumn(row, col, view);
            if (verticalWord.length() > 1 && !WordValidity.isWordValid(verticalWord)) {
                JOptionPane.showMessageDialog(null, "Invalid word formed: " + verticalWord);
                return false;
            }
        }

        // If all words formed are valid, return true
        return true;
    }

}
