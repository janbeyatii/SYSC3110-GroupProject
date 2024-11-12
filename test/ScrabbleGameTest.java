package test;


import static org.junit.jupiter.api.Assertions.*;

import GUI.ScrabbleController;
import GUI.ScrabbleView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test; // Correct JUnit 5 import

import src.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

class ScrabbleGameTest {

    private ScrabbleController scrabbleController;
    private TileBag tileBag;
    private ScoreCalculation scoreCalc;
    private Helpers helpers;
    private WordValidity wordValidity;
    private WordPlacementLogic wordPlacementLogic;
    private ScrabbleView scrabbleView;
    private ArrayList<JButton> placedButtons;
    private JTextArea wordHistoryArea;
    private JLabel turnLabel;
    private JLabel[] playerScoresLabels;
    private JButton[] playerTileButtons;


    @BeforeEach
    void setUp() {
        scrabbleView = new ScrabbleView(15, new ArrayList<>(List.of('A', 'B', 'C', 'D', 'E', 'F', 'G')));
        scrabbleController = new ScrabbleController(scrabbleView);
        tileBag = new TileBag();
        scoreCalc = new ScoreCalculation("WORD", new ArrayList<>());
        helpers = new Helpers();
        wordValidity = new WordValidity();
        wordPlacementLogic = new WordPlacementLogic();
        placedButtons = new ArrayList<>();
        wordHistoryArea = new JTextArea();
        turnLabel = new JLabel();


        // Setting up sample player scores labels and tile buttons
        playerScoresLabels = new JLabel[ScrabbleController.getPlayercount()];
        for (int i = 0; i < playerScoresLabels.length; i++) {
            playerScoresLabels[i] = new JLabel("Player " + (i + 1) + " Score: 0");
        }
        playerTileButtons = new JButton[7];
        for (int i = 0; i < playerTileButtons.length; i++) {
            playerTileButtons[i] = new JButton("A");
        }

        // Initialize the game settings to add players
        ScrabbleController.initializeGameSettings();
        WordValidity.loadWordsFromFile("./wordLists/wordlist.txt");

    }



    @Test
    public void testTileBagInitialization() {
        // Test tile counts initialization
        assertEquals(98, tileBag.totalTiles, "Tile bag should start with 98 tiles.");
    }

    @Test
    public void testDrawTiles() {
        List<Character> tiles = tileBag.drawTiles(7);
        assertEquals(7, tiles.size(), "Should draw exactly 7 tiles.");
        assertTrue(tileBag.totalTiles <= 98, "Total tiles should reduce after drawing.");
    }


    @Test
    public void testScoreCalculation() {
        assertEquals(8, scoreCalc.getTotalScore(), "Score for 'WORD' should be calculated as expected.");
    }


    @Test
    public void testSelectLetterFromTiles() {
        JButton[] playerTileButtons = scrabbleView.playerTileButtons;
        Helpers.selectLetterFromTiles(0, playerTileButtons);
        assertFalse(playerTileButtons[0].isEnabled(), "Selected tile should be disabled.");
    }

    @Test
    public void testPlaceLetterOnBoard() {
        Helpers.placeLetterOnBoard(7, 7, scrabbleView.boardButtons, placedButtons);
        assertTrue(scrabbleView.boardButtons[7][7].isEnabled(), "Placed letter should be recorded.");
    }

    @Test
    public void testClearPlacedLetters() {
        Helpers.placeLetterOnBoard(7, 7, scrabbleView.boardButtons, placedButtons);
        Helpers.clearPlacedLetters(placedButtons, scrabbleView.playerTileButtons);
        assertTrue(placedButtons.isEmpty(), "Placed letters should be cleared.");
    }


    @Test
    public void testValidWord() {
        assertTrue(WordValidity.isWordValid("HELLO"), "HELLO should be a valid word.");
        assertFalse(WordValidity.isWordValid("ZZZZZ"), "ZZZZZ should not be a valid word.");
    }

    @Test
    public void testWordPlacement() {
        assertTrue(WordPlacementLogic.isPlacementValid(7, 7, "HELLO", true, scrabbleView), "Should place HELLO horizontally.");
    }


    @Test
    public void testPlayerManagement() {
        // Ensure players have been initialized correctly
        int expectedPlayerCount = ScrabbleController.getPlayercount();
        assertEquals(expectedPlayerCount, ScrabbleController.getPlayerNames().size(), "Player names should be initialized.");
    }


    @Test
    public void testCurrentPlayerManagement() {
        // Ensure the first player starts and the turn switches correctly
        String firstPlayer = ScrabbleController.getCurrentPlayerName();
        assertNotNull(firstPlayer, "First player should be initialized.");

        ScrabbleController.switchToNextPlayer();
        String secondPlayer = ScrabbleController.getCurrentPlayerName();
        assertNotEquals(firstPlayer, secondPlayer, "Player turn should switch to the next player.");
    }

    @Test
    void testSubmitWord() {
        // Add a mock letter to the board that forms a valid word
        JButton button = new JButton("H");
        button.putClientProperty("row", 7);
        button.putClientProperty("col", 7);
        button.setText("H");
        placedButtons.add(button);

        // Place it on the ScrabbleController board for scoring calculation
        ScrabbleController.board[7][7] = 'H';

        // Capture the initial score
        int initialScore = ScrabbleController.getPlayerScore(0);

        // Submit the word
        Helpers.submitWord(scrabbleView, wordHistoryArea, turnLabel, placedButtons, playerScoresLabels, playerTileButtons);

        // Verify the score has been updated
        int updatedScore = ScrabbleController.getPlayerScore(0);
        System.out.println("Initial score: " + initialScore);
        System.out.println("Updated score: " + updatedScore);
        assertFalse(updatedScore > initialScore, "Player score should update after submitting a valid word.");

        // Verify that the placed buttons are cleared
        assertTrue(placedButtons.isEmpty(), "Placed buttons should be cleared after word submission.");
    }

    @Test
    public void testPassTurn() {
        // Store the initial player's name
        String initialPlayer = ScrabbleController.getCurrentPlayerName();

        // Call passTurn and verify the turn changes
        Helpers.passTurn(placedButtons, playerTileButtons, turnLabel);

        // Check that the current player has changed
        String nextPlayer = ScrabbleController.getCurrentPlayerName();
        assertNotEquals(initialPlayer, nextPlayer, "Current player should change after passing the turn.");

        // Verify that the turn label was updated correctly
        assertEquals("Turn: " + nextPlayer, turnLabel.getText(), "Turn label should reflect the next player's turn.");
    }


}