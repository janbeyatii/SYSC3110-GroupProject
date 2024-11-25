package tests;

import src.TileBag;
import GUI.ScrabbleController;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TileBagTests {

    private TileBag tileBag;

    @Before
    public void setUp() {
        tileBag = new TileBag();
        ScrabbleController.getPlayerNames(); // Ensure player setup for removeUsedTiles
        ScrabbleController.initializeGameSettings(); // Initialize players for removeUsedTiles test
    }

    @Test
    public void testTileInitialization() {
        // Check the total tile count after initialization
        int total = tileBag.totalTiles;
        assertEquals("Tile bag should start with 100 tiles.", 100, total);

        // Verify specific tile counts
        Map<Character, Integer> tileCounts = tileBag.tileCounts;
        assertEquals("Tile count for 'E' should be 12.", (Integer) 12, tileCounts.get('E'));
        assertEquals("Tile count for 'A' should be 9.", (Integer) 9, tileCounts.get('A'));
        assertEquals("Tile count for 'Z' should be 1.", (Integer) 1, tileCounts.get('Z'));
    }

    @Test
    public void testDrawTiles() {
        // Draw 7 tiles and verify count reduction
        List<Character> drawnTiles = tileBag.drawTiles(7);
        assertEquals("Should draw exactly 7 tiles.", 7, drawnTiles.size());
        assertTrue("Total tiles should reduce after drawing.", tileBag.totalTiles < 98);
    }

    @Test
    public void testDrawRandomTiles() {
        // Draw 10 tiles and check total reduction in tile bag
        List<Character> drawnTiles = tileBag.drawTiles(10);
        assertEquals("Should draw exactly 10 tiles.", 10, drawnTiles.size());

        // Ensure total tiles reduced by 10
        int expectedTotal = 100 - 10;
        assertEquals("Total tile count should decrease by 10.", expectedTotal, tileBag.totalTiles);
    }
}
