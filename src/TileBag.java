package src;

import GUI.ScrabbleController;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import static GUI.ScrabbleController.*;

/**
 * The TileBag class represents the collection of tiles used in a Scrabble game.
 * It manages the drawing of tiles, keeps track of tile counts, and refills players' tile sets.
 */
public class TileBag {
    public final Map<Character, Integer> tileCounts = new HashMap<>();
    public int totalTiles = 98; // Total number of tiles, excluding blank tiles.

    /**
     * Constructs a TileBag object and initializes the tile counts for each letter.
     */
    public TileBag() {
        initTileCounts();
    }

    /**
     * Initializes the tile counts for each letter based on Scrabble's standard distribution.
     * The counts represent the number of available tiles for each letter in the game.
     */
    private void initTileCounts() {
        tileCounts.put('E', 12); tileCounts.put('A', 9); tileCounts.put('I', 9); tileCounts.put('O', 8);
        tileCounts.put('N', 6); tileCounts.put('R', 6); tileCounts.put('T', 6); tileCounts.put('L', 4);
        tileCounts.put('S', 4); tileCounts.put('U', 4); tileCounts.put('D', 4); tileCounts.put('G', 3);
        tileCounts.put('B', 2); tileCounts.put('C', 2); tileCounts.put('M', 2); tileCounts.put('P', 2);
        tileCounts.put('F', 2); tileCounts.put('H', 2); tileCounts.put('V', 2); tileCounts.put('W', 2);
        tileCounts.put('Y', 2); tileCounts.put('K', 1); tileCounts.put('J', 1); tileCounts.put('X', 1);
        tileCounts.put('Q', 1); tileCounts.put('Z', 1);
    }

    /**
     * Draws a specified number of random tiles from the bag for a player.
     * Each tile is chosen randomly, and the tile counts are updated to reflect the tiles drawn.
     *
     * @param numTiles the number of tiles to draw.
     * @return a list of characters representing the drawn tiles.
     */
    public List<Character> drawTiles(int numTiles) {
        List<Character> drawnTile = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < numTiles; i++) {
            if (totalTiles == 0) break; // Stop if no more tiles are available

            // Get a random tile
            char tile = getRandomTile(random);

            // Add tile to player's hand and update tile count
            drawnTile.add(tile);
            updateTileCount(tile);
        }
        System.out.println("Drawing tiles: " + drawnTile);
        return drawnTile;
    }

    /**
     * Randomly selects a tile from the available tiles.
     *
     * @param random a Random object used to select a tile randomly.
     * @return a character representing the randomly selected tile.
     */
    private char getRandomTile(Random random) {
        List<Character> availableTiles = new ArrayList<>();

        // Add each tile to the list based on the number of times it is still available
        for (Map.Entry<Character, Integer> entry : tileCounts.entrySet()) {
            char tile = entry.getKey();
            int count = entry.getValue();
            for (int i = 0; i < count; i++) {
                availableTiles.add(tile);
            }
        }

        return availableTiles.get(random.nextInt(availableTiles.size()));
    }

    /**
     * Updates the count of a tile after it has been drawn from the tile bag.
     * Reduces the count of the specific tile and decreases the total number of available tiles.
     *
     * @param tile the character representing the tile that was drawn.
     */
    private void updateTileCount(char tile) {
        tileCounts.put(tile, tileCounts.get(tile) - 1);
        totalTiles--;
    }

    public static void removeUsedTiles(String word) {
        String currentPlayer = getCurrentPlayerName();
        List<Character> playerTiles = ScrabbleController.getPlayerTilesMap().get(currentPlayer);

        for (char c : word.toCharArray()) {
            playerTiles.remove((Character) c); // Remove each used tile
        }

        // Refill tiles to ensure the player has 7
        int tilesNeeded = 7 - playerTiles.size();
        playerTiles.addAll(tileBag.drawTiles(tilesNeeded));
    }
}
