package src;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;

import static src.Helpers.*;

public class ScoreCalculation {
    private int totalScore;

    /**
     * Constructs a ScoreCalculation object and calculates the total score of the given word.
     * The score is computed based on the positions of placed tiles on the GUI board.
     *
     * @param word the word to be scored.
     * @param placedButtons an ArrayList of JButtons representing the placed tiles on the board.
     */
    public ScoreCalculation(String word, ArrayList<JButton> placedButtons) {
        totalScore = 0; // Initialize total score to 0

        boolean isDoubleWord = false;
        boolean isTripleWord = false;

        // Process only tiles placed by the player
        for (int k = 0; k < placedButtons.size(); k++) {
            JButton button = placedButtons.get(k); // Get the placed button
            char letter = Character.toUpperCase(word.charAt(k)); // Get the corresponding letter

            boolean isDoubleLetter = false;
            boolean isTripleLetter = false;

            // Get the row and column of the placed button
            int row = getRow(button);
            int col = getCol(button);

            // Check for multipliers at the button's position
            if (isDoubleLetter(row, col)) {
                isDoubleLetter = true;
            }
            if (isTripleLetter(row, col)) {
                isTripleLetter = true;
            }
            if (isDoubleWord(row, col)) {
                isDoubleWord = true;
            }
            if (isTripleWord(row, col)) {
                isTripleWord = true;
            }

            int letterScore = getLetterValue(letter);

            // Apply letter multipliers
            if (isDoubleLetter) {
                totalScore += letterScore * 2;
                System.out.println("DL "+totalScore);
            } else if (isTripleLetter) {
                totalScore += letterScore * 3;
                System.out.println("DW "+totalScore);

            } else {
                totalScore += letterScore;
                System.out.println("N "+totalScore);

            }
        }

        // Apply word multipliers (only once for the whole word)
        if (isDoubleWord) {
            totalScore *= 2;
            System.out.println("DW "+totalScore);

        }
        if (isTripleWord) {
            totalScore *= 3;
            System.out.println("TW "+totalScore);

        }
    }

    /**
     * Returns the Scrabble score value for a given letter.
     *
     * @param letter the letter to get the score for.
     * @return the score value of the letter.
     */
    public static int getLetterValue(char letter) {
        ArrayList<Character> score1 = new ArrayList<>(Arrays.asList('A', 'E', 'I', 'O', 'U', 'L', 'N', 'S', 'T', 'R'));
        ArrayList<Character> score2 = new ArrayList<>(Arrays.asList('G', 'D'));
        ArrayList<Character> score3 = new ArrayList<>(Arrays.asList('B', 'C', 'M', 'P'));
        ArrayList<Character> score4 = new ArrayList<>(Arrays.asList('F', 'H', 'V', 'W', 'Y'));
        ArrayList<Character> score5 = new ArrayList<>(Arrays.asList('K'));
        ArrayList<Character> score8 = new ArrayList<>(Arrays.asList('J', 'X'));
        ArrayList<Character> score10 = new ArrayList<>(Arrays.asList('Q', 'Z'));

        // Convert letter to uppercase to make sure it matches the key in the lists
        letter = Character.toUpperCase(letter);

        if (score1.contains(letter)) {
            return 1;
        } else if (score2.contains(letter)) {
            return 2;
        } else if (score3.contains(letter)) {
            return 3;
        } else if (score4.contains(letter)) {
            return 4;
        } else if (score5.contains(letter)) {
            return 5;
        } else if (score8.contains(letter)) {
            return 8;
        } else if (score10.contains(letter)) {
            return 10;
        }

        return 0; // Return 0 if the letter is not found (shouldn't happen with valid letters)
    }

    /**
     * Returns the total score of the word.
     *
     * @return the total score of the word.
     */
    public int getTotalScore() {
        return totalScore;
    }

    /**
     * Helper method to get the row of a JButton.
     *
     * @param button the JButton on the board.
     * @return the row index of the button.
     */
    private int getRow(JButton button) {
        // Implement logic to calculate row based on the button's position
        return Integer.parseInt(button.getClientProperty("row").toString());
    }

    /**
     * Helper method to get the column of a JButton.
     *
     * @param button the JButton on the board.
     * @return the column index of the button.
     */
    private int getCol(JButton button) {
        // Implement logic to calculate column based on the button's position
        return Integer.parseInt(button.getClientProperty("col").toString());
    }
}
