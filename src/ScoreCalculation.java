package src;

import GUI.ScrabbleController;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.Point;
import java.util.List;

import static src.Helpers.isDoubleLetter;
import static src.Helpers.isTripleLetter;
import static src.Helpers.isDoubleWord;
import static src.Helpers.isTripleWord;

public class ScoreCalculation {
    private int totalScore;
    private ArrayList<Integer> letterScores;
    private ArrayList<Point> letterCoordinates;  // To store coordinates of placed letters
    private int wordMultiplier;  // Keeps track of word score multiplier (for double/triple word)

    /**
     * Constructs a ScoreCalculation object and calculates the total score of the given word.
     * The score is computed based on the positions of placed tiles on the GUI board.
     *
     * @param word           the word to be scored.
     */
    public ScoreCalculation(String word) {
        ArrayList<JButton> placedButtons = ScrabbleController.getMasterPlacedButtons();
        List<Point> letterCoordinates = ScrabbleController.getPlacedTileCoordinates();

        if (placedButtons.isEmpty() || letterCoordinates.isEmpty()) {
            throw new IllegalArgumentException("No letter coordinates or placed buttons provided for scoring.");
        }

        letterScores = new ArrayList<>();
        wordMultiplier = 1;  // Default word multiplier is 1 (no bonus)

        ArrayList<Character> score1 = new ArrayList<>(Arrays.asList('A', 'E', 'I', 'O', 'U', 'L', 'N', 'S', 'T', 'R'));
        ArrayList<Character> score2 = new ArrayList<>(Arrays.asList('G', 'D'));
        ArrayList<Character> score3 = new ArrayList<>(Arrays.asList('B', 'C', 'M', 'P'));
        ArrayList<Character> score4 = new ArrayList<>(Arrays.asList('F', 'H', 'V', 'W', 'Y'));
        ArrayList<Character> score5 = new ArrayList<>(Arrays.asList('K'));
        ArrayList<Character> score8 = new ArrayList<>(Arrays.asList('J', 'X'));
        ArrayList<Character> score10 = new ArrayList<>(Arrays.asList('Q', 'Z'));

        // Calculate the score based on the word and placed buttons
        for (int k = 0; k < word.length(); k++) {
            char letter = Character.toUpperCase(word.charAt(k)); // Convert to uppercase
            int letterScore = 0;

            if (letter == ' ') {
                continue;
            } else if (score1.contains(letter)) {
                letterScore = 1;
            } else if (score2.contains(letter)) {
                letterScore = 2;
            } else if (score3.contains(letter)) {
                letterScore = 3;
            } else if (score4.contains(letter)) {
                letterScore = 4;
            } else if (score5.contains(letter)) {
                letterScore = 5;
            } else if (score8.contains(letter)) {
                letterScore = 8;
            } else if (score10.contains(letter)) {
                letterScore = 10;
            }

            if (letterCoordinates.isEmpty()) {
                throw new IllegalArgumentException("No letter coordinates provided for scoring.");
            }
            if (placedButtons.isEmpty()) {
                throw new IllegalArgumentException("No placed buttons provided for scoring.");
            }

            int originalLetterScore = letterScore; // Store original letter score before applying bonuses
            Point coordinates = letterCoordinates.get(k);  // Get the coordinates of the current letter

            // Debugging: Check size of letterCoordinates and the current index
            System.out.println("letterCoordinates size: " + letterCoordinates.size());
            System.out.println("Current index (k): " + k);

            // Apply double/triple letter bonuses ONLY for the current letter
            if (isDoubleLetter(coordinates.x, coordinates.y)) {
                letterScore *= 2;
            } else if (isTripleLetter(coordinates.x, coordinates.y)) {
                letterScore *= 3;
            }  else if (isDoubleWord(coordinates.x, coordinates.y)) {
                letterScore *= 1;
            }

            // Add the letter score (after applying bonuses) for this letter
            totalScore += letterScore;
            letterScores.add(letterScore);

            // Reset the letter score back to its original value
            letterScore = originalLetterScore;

            // Update the corresponding JButton with the score (for debugging)
            if (k < placedButtons.size()) {
                placedButtons.get(k).setText(String.valueOf(letterScore));
            }

            // Check for word bonuses (double/triple word)
            // Apply these only once for the current word
            if (isDoubleWord(coordinates.x, coordinates.y)) {
                wordMultiplier *= 2;
            }

            if (isTripleWord(coordinates.x, coordinates.y)) {
                wordMultiplier *= 3;
            }
        }

        // Apply the word multiplier to the total score (if there was a word bonus)
        totalScore *= wordMultiplier;

        // Optionally print out the coordinates and scores for debugging
        System.out.println("Letter Coordinates: " + letterCoordinates);
        System.out.println("Total Score after applying word bonuses: " + totalScore);
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
     * Returns the individual scores of the letters in the word.
     *
     * @return an ArrayList of scores for each letter in the word.
     */
    public ArrayList<Integer> getLetterScores() {
        return letterScores;
    }

    /**
     * Returns the coordinates of the placed letters.
     *
     * @return an ArrayList of Points representing the coordinates of placed letters.
     */
    public ArrayList<Point> getLetterCoordinates() {
        return letterCoordinates;
    }
}
