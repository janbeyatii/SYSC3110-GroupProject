package tests;

import GUI.ScrabbleController;
import org.junit.Before;
import org.junit.Test;
import src.TileBag;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ScrabbleControllerTests {

    @Before
    public void setUp() {
        // Set up the game settings with automated values
        ScrabbleController.getPlayercount(); // Set default player count for testing
        ScrabbleController.initializeGameSettings();
    }

    @Test
    public void testPlayerCountInitialization() {
        assertEquals("Player count should be initialized to 2", 2, ScrabbleController.getPlayercount());
    }

    @Test
    public void testPlayerNamesInitialization() {
        List<String> playerNames = ScrabbleController.getPlayerNames();
        assertEquals("There should be two players", 2, playerNames.size());
        assertEquals("First player name should be 'Player 1'", "Player 1", playerNames.get(0));
        assertEquals("Second player name should be 'Player 2'", "Player 2", playerNames.get(1));
    }

    @Test
    public void testPlayerScoresInitialization() {
        ScrabbleController.initializePlayerScores();
        assertEquals("First player score should be 0", 0, ScrabbleController.getPlayerScore(0));
        assertEquals("Second player score should be 0", 0, ScrabbleController.getPlayerScore(1));
    }

    @Test
    public void testAddScoreToCurrentPlayer() {
        ScrabbleController.addScoreToCurrentPlayer(10);
        assertEquals("Current player (Player 1) score should be 10", 10, ScrabbleController.getPlayerScore(0));

        ScrabbleController.switchToNextPlayer();
        ScrabbleController.addScoreToCurrentPlayer(20);
        assertEquals("Next player (Player 2) score should be 20", 20, ScrabbleController.getPlayerScore(1));
    }

    @Test
    public void testSwitchToNextPlayer() {
        String firstPlayer = ScrabbleController.getCurrentPlayerName();
        ScrabbleController.switchToNextPlayer();
        String secondPlayer = ScrabbleController.getCurrentPlayerName();

        assertNotEquals("Current player should change after switching", firstPlayer, secondPlayer);
        assertEquals("Second player name should be 'Player 2'", "Player 2", secondPlayer);

        ScrabbleController.switchToNextPlayer();
        assertEquals("Should return to the first player", firstPlayer, ScrabbleController.getCurrentPlayerName());
    }

    @Test
    public void testTileDrawing() {
        TileBag tileBag = new TileBag();
        List<Character> playerTiles = tileBag.drawTiles(7);
        assertEquals("Player should draw exactly 7 tiles", 7, playerTiles.size());
    }

    @Test
    public void testAddPlacedTiles() {
        JButton button = new JButton();
        button.putClientProperty("row", 7);
        button.putClientProperty("col", 7);
        ScrabbleController.addPlacedTiles(List.of(button));

        List<Point> placedTileCoordinates = ScrabbleController.getPlacedTileCoordinates();
        assertEquals("There should be one placed tile", 1, placedTileCoordinates.size());
        assertEquals("Placed tile should be at (7, 7)", new Point(7, 7), placedTileCoordinates.get(0));
    }

    @Test
    public void testPlayerTilesInitialization() {
        Map<String, List<Character>> playerTilesMap = ScrabbleController.getPlayerTilesMap();
        assertEquals("Two players should have tiles assigned", 2, playerTilesMap.size());
        assertTrue("Each player should have 7 tiles initially", playerTilesMap.values().stream().allMatch(tiles -> tiles.size() == 7));
    }
}
