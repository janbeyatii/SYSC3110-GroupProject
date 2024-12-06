package tests;

import GUI.ScrabbleController;
import org.junit.Before;
import org.junit.Test;
import src.ScoreCalculation;

import javax.swing.JButton;
import java.awt.*;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class ScoreCalculationTests {

    @Before
    public void setUp() {
        // Clear any previous data from ScrabbleController
        ScrabbleController.getMasterPlacedButtons().clear();
        ScrabbleController.getPlacedTileCoordinates().clear();
    }

    @Test
    public void testScoreCalculationWithSingleLetter() {
        // Mock data for the letter "A" placed at (0, 0)
        mockTilePlacement("A", new Point(0, 0));

        // Create ScoreCalculation and verify score
        ScoreCalculation scoreCalculation = new ScoreCalculation("A");
        assertEquals(1, scoreCalculation.getTotalScore());
    }

    @Test
    public void testScoreCalculationWithWord() {
        // Mock data for the word "DOG" placed horizontally
        mockTilePlacement("DOG", new Point(0, 0), new Point(1, 0), new Point(2, 0));

        // Create ScoreCalculation and verify score
        ScoreCalculation scoreCalculation = new ScoreCalculation("DOG");
        assertEquals(5, scoreCalculation.getTotalScore()); // D(2) + O(1) + G(2) = 5
    }

    @Test
    public void testScoreCalculationWithHighScoringLetter() {
        // Mock data for the word "QUIZ" placed horizontally
        mockTilePlacement("QUIZ", new Point(0, 0), new Point(1, 0), new Point(2, 0), new Point(3, 0));

        // Create ScoreCalculation and verify score
        ScoreCalculation scoreCalculation = new ScoreCalculation("QUIZ");
        assertEquals(22, scoreCalculation.getTotalScore()); // Q(10) + U(1) + I(1) + Z(10) = 22
    }

    @Test
    public void testScoreCalculationWithMixedScores() {
        // Mock data for the word "JAZZ" placed horizontally
        mockTilePlacement("JAZZ", new Point(0, 0), new Point(1, 0), new Point(2, 0), new Point(3, 0));

        // Create ScoreCalculation and verify score
        ScoreCalculation scoreCalculation = new ScoreCalculation("JAZZ");
        assertEquals(29, scoreCalculation.getTotalScore()); // J(8) + A(1) + Z(10) + Z(10) = 29
    }

    @Test
    public void testScoreCalculationWithLowerCaseLetters() {
        // Mock data for the word "cat" placed horizontally
        mockTilePlacement("cat", new Point(0, 0), new Point(1, 0), new Point(2, 0));

        // Create ScoreCalculation and verify score
        ScoreCalculation scoreCalculation = new ScoreCalculation("cat");
        assertEquals(5, scoreCalculation.getTotalScore()); // c(3) + a(1) + t(1) = 5
    }

    @Test
    public void testScoreCalculationWithEmptyWord() {
        // No tiles placed for an empty word
        // Create ScoreCalculation and verify score
        ScoreCalculation scoreCalculation = new ScoreCalculation("");
        assertEquals(0, scoreCalculation.getTotalScore());
    }

    // Helper method to mock tile placements
    private void mockTilePlacement(String word, Point... coordinates) {
        ArrayList<JButton> buttons = ScrabbleController.getMasterPlacedButtons();
        ArrayList<Point> points = (ArrayList<Point>) ScrabbleController.getPlacedTileCoordinates();

        for (int i = 0; i < word.length(); i++) {
            JButton button = new JButton(String.valueOf(word.charAt(i)));
            buttons.add(button);
            points.add(coordinates[i]);
        }
    }
}
