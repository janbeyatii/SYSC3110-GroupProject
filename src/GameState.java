package src;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class GameState implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> playerNames;
    private Map<String, List<Character>> playerTilesMap;
    private ArrayList<Integer> playerScores;
    private String currentPlayerName;
    private char[][] boardState;
    private boolean firstTurn;
    private Set<String> oldTileCoordinates;
    private Set<String> placedTileCoordinates;

    public GameState(List<String> playerNames, Map<String, List<Character>> playerTilesMap,
                     ArrayList<Integer> playerScores, String currentPlayerName,
                     char[][] boardState, boolean firstTurn, Set<String> oldTileCoordinates,Set<String> placedTileCoordinates) {
        this.playerNames = playerNames;
        this.playerTilesMap = playerTilesMap;
        this.playerScores = playerScores;
        this.currentPlayerName = currentPlayerName;
        this.boardState = boardState;
        this.firstTurn = firstTurn;
        this.oldTileCoordinates = oldTileCoordinates;
        this.placedTileCoordinates = placedTileCoordinates;

    }

    public Set<String> getPlacedTileCoordinates() {
        return placedTileCoordinates;
    }

    // Add getter and setter for oldTileCoordinates
    public Set<String> getOldTileCoordinates() {
        return oldTileCoordinates;
    }

    // Getters for the GameState fields
    public ArrayList<String> getPlayerNames() {
        return (ArrayList<String>) playerNames;
    }

    public Map<String, List<Character>> getPlayerTilesMap() {
        return playerTilesMap;
    }

    public ArrayList<Integer>  getPlayerScores() {
        return playerScores;
    }

    public String getCurrentPlayerName() {
        return currentPlayerName;
    }

    public char[][] getBoardState() {
        return boardState;
    }

    public boolean isFirstTurn() {
        return firstTurn;
    }
    public void setFirstTurn(boolean firstTurn) {
        this.firstTurn = firstTurn;
    }
    // Save the game state to a file
    public static void saveGameState(GameState gameState, String filename) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(gameState);
        }
    }

    // Load the game state from a file
    public static GameState loadGameState(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (GameState) in.readObject();
        }
    }
}
