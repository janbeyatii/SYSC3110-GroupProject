package src;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerTurns {
    private static final int numPlayerTiles = 7;
    private static final String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private List<Character> player1Rack;
    private List<Character> player2Rack;
    private Random random;

    // Constructor to initialize the player scores and letter racks
    public PlayerTurns() {
        player1Rack = new ArrayList<>();
        player2Rack = new ArrayList<>();
        random = new Random();
        generateNewLettersForPlayer(1);
        generateNewLettersForPlayer(2);
    }

    // Method to get the list of letters for a player (1 or 2)
    public List<Character> getLettersForPlayer(int player) {
        return (player == 1) ? player1Rack : player2Rack;
    }

    // Method to generate new letters for a player (1 or 2)
    public void generateNewLettersForPlayer(int player) {
        List<Character> rack = (player == 1) ? player1Rack : player2Rack;
        rack.clear();
        while (rack.size() < numPlayerTiles) {
            char randomLetter = letters.charAt(random.nextInt(letters.length()));
            rack.add(randomLetter);
        }
    }
}
