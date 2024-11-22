package tests;

import GUI.ScrabbleController;
import GUI.ScrabbleView;
import org.junit.Before;
import org.junit.Test;
import src.ButtonCommands;
import src.Helpers;
import src.WordValidity;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class HelperTests {

    private ScrabbleView scrabbleView;
    private JTextArea wordHistoryArea;
    private JLabel turnLabel;
    private ArrayList<JButton> placedButtons;
    private JLabel[] playerScoresLabels;
    private JButton[] playerTileButtons;

    @Before
    public void setUp() {
        scrabbleView = new ScrabbleView(15, new ArrayList<>(List.of('A', 'B', 'C', 'D', 'E', 'F', 'G')));
        wordHistoryArea = new JTextArea();
        turnLabel = new JLabel();
        placedButtons = new ArrayList<>();

        playerScoresLabels = new JLabel[ScrabbleController.getPlayerNames().size()];
        for (int i = 0; i < playerScoresLabels.length; i++) {
            playerScoresLabels[i] = new JLabel("Player " + (i + 1) + " Score: 0");
        }

        playerTileButtons = new JButton[7];
        for (int i = 0; i < playerTileButtons.length; i++) {
            playerTileButtons[i] = new JButton("A");
        }

        ScrabbleController.initializeGameSettings();
        WordValidity.loadWordsFromFile("tests/test_wordlist.txt");  // Ensure this file is available for tests
    }

    @Test
    public void testSelectLetterFromTiles() {
        Helpers.selectLetterFromTiles(0, playerTileButtons);
        assertFalse("Selected tile should be disabled", playerTileButtons[0].isEnabled());
        assertEquals("Selected letter should match button text", "A", playerTileButtons[0].getText());
    }

    @Test
    public void testPlaceLetterOnBoard() {
        Helpers.selectLetterFromTiles(0, playerTileButtons);
        Helpers.placeLetterOnBoard(7, 7, scrabbleView.boardButtons, placedButtons);

        assertEquals("Placed letter should match selected letter", "A", scrabbleView.boardButtons[7][7].getText());
        assertEquals("Placed buttons list should include the board button", 1, placedButtons.size());
    }

    @Test
    public void testAreAllWordsValid() {
        Set<String> validWords = new HashSet<>();
        validWords.add("hello");

        Set<String> invalidWords = new HashSet<>();
        invalidWords.add("zzzz");

        assertTrue("Valid words set should be recognized as valid", Helpers.areAllWordsValid(validWords));
        assertFalse("Invalid words set should be recognized as invalid", Helpers.areAllWordsValid(invalidWords));
    }

    @Test
    public void testUpdateScoresAndDisplayWords() {
        Set<String> words = new HashSet<>();
        words.add("HELLO");

        ButtonCommands.updateScoresAndDisplayWords(words, wordHistoryArea, playerScoresLabels);

        String historyText = wordHistoryArea.getText();
        assertTrue("Word history should include 'HELLO'", historyText.contains("HELLO"));
    }

    @Test
    public void testClearPlacedLetters() {
        Helpers.selectLetterFromTiles(0, playerTileButtons);
        Helpers.placeLetterOnBoard(7, 7, scrabbleView.boardButtons, placedButtons);

        ButtonCommands.clear(placedButtons, playerTileButtons);
        assertTrue("Placed buttons should be cleared after clearing", placedButtons.isEmpty());
    }
}
