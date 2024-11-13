package tests;

import org.junit.Before;
import org.junit.Test;
import src.ScoreCalculation;

import javax.swing.JButton;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class ScoreCalculationTests {

    private ArrayList<JButton> placedButtons;

    @Before
    public void setUp() {
        // Initialize an empty list of placed buttons for each test case
        placedButtons = new ArrayList<>();
    }

    @Test
    public void testScoreCalculationWithSingleLetter() {
        // Test a word with a single letter that scores 1 point
        ScoreCalculation scoreCalculation = new ScoreCalculation("A", placedButtons);
        assertEquals(1, scoreCalculation.getTotalScore());
    }

    @Test
    public void testScoreCalculationWithWord() {
        // Test a word "DOG" that scores 5 points (2+1+2)
        ScoreCalculation scoreCalculation = new ScoreCalculation("DOG", placedButtons);
        assertEquals(5, scoreCalculation.getTotalScore());
    }

    @Test
    public void testScoreCalculationWithHighScoringLetter() {
        // Test a word "QUIZ" that includes high-scoring letters (10+1+1+10 = 22)
        ScoreCalculation scoreCalculation = new ScoreCalculation("QUIZ", placedButtons);
        assertEquals(22, scoreCalculation.getTotalScore());
    }

    @Test
    public void testScoreCalculationWithMixedScores() {
        // Test a word "JAZZ" with mixed high scores (8+1+10+10 = 29)
        ScoreCalculation scoreCalculation = new ScoreCalculation("JAZZ", placedButtons);
        assertEquals(29, scoreCalculation.getTotalScore());
    }

    @Test
    public void testScoreCalculationWithLowerCaseLetters() {
        // Test that lowercase letters are scored correctly for "cat" (3+1+1 = 5)
        ScoreCalculation scoreCalculation = new ScoreCalculation("cat", placedButtons);
        assertEquals(5, scoreCalculation.getTotalScore());
    }

    @Test
    public void testScoreCalculationWithEmptyWord() {
        // Test an empty word, expecting a score of 0
        ScoreCalculation scoreCalculation = new ScoreCalculation("", placedButtons);
        assertEquals(0, scoreCalculation.getTotalScore());
    }

}
