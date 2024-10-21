package src;
import java.util.*;
import java.util.List;
import java.util.ArrayList;

public class TileBag {
    private final Map<Character, Integer> tileCounts = new HashMap<>();
    private int totalTiles = 98; // as were not using blank tiles

    public TileBag() {
        initTileCounts();
    }

    //initialize a hash map with the number of each letter present in the bag of total tiles

    private void initTileCounts() {
        tileCounts.put('E', 12); tileCounts.put('A', 9); tileCounts.put('I', 9); tileCounts.put('O', 8);
        tileCounts.put('N', 6); tileCounts.put('R', 6); tileCounts.put('T', 6); tileCounts.put('L', 4);
        tileCounts.put('S', 4); tileCounts.put('U', 4); tileCounts.put('D', 4); tileCounts.put('G', 3);
        tileCounts.put('B', 2); tileCounts.put('C', 2); tileCounts.put('M', 2); tileCounts.put('P', 2);
        tileCounts.put('F', 2); tileCounts.put('H', 2); tileCounts.put('V', 2); tileCounts.put('W', 2);
        tileCounts.put('Y', 2); tileCounts.put('K', 1); tileCounts.put('J', 1); tileCounts.put('X', 1);
        tileCounts.put('Q', 1); tileCounts.put('Z', 1);
        //tileCounts.put(' ', 2); Blank tiles - milestone 3
    }

    //method to be able to randomly draw 7 tiles to start the game off
    public List<Character> drawTiles(int numTiles) {
        List<Character> drawnTile = new ArrayList<>();
        Random random = new Random();
    


        for (int i = 0; i < numTiles; i++) {
            if (totalTiles == 0) break; // No more tiles to draw

            // Get a random tile
            char tile = getRandomTile(random);

            // Add tile to player's hand and update tile count
            drawnTile.add(tile);
            updateTileCount(tile);
        }

        return drawnTile;
    }

    // Helper function to randomly pick a tile from the available tiles
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

    // Update the count of a tile after it's drawn
    private void updateTileCount(char tile) {
        tileCounts.put(tile, tileCounts.get(tile) - 1);
        totalTiles--;
    }

    // Method to check how many tiles are left
    public int getTotalTiles() {
        return totalTiles;
    }

}