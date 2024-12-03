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
    private char[][] boardState;
    private boolean firstTurn;
    private Set<String> oldTileCoordinates;
    private Set<String> placedTileCoordinates;
    private String wordHistory;

    public GameState(List<String> playerNames, Map<String, List<Character>> playerTilesMap,
                     ArrayList<Integer> playerScores, char[][] boardState, boolean firstTurn,
                     Set<String> oldTileCoordinates, Set<String> placedTileCoordinates, String wordHistory) {
        this.playerNames = new ArrayList<>(playerNames);
        this.playerTilesMap = deepCopyPlayerTiles(playerTilesMap);
        this.playerScores = new ArrayList<>(playerScores);
        this.boardState = deepCopyBoard(boardState);
        this.firstTurn = firstTurn;
        this.oldTileCoordinates = new HashSet<>(oldTileCoordinates);
        this.placedTileCoordinates = new HashSet<>(placedTileCoordinates);
        this.wordHistory = wordHistory;

    }

    // Deep copy for board state
    private char[][] deepCopyBoard(char[][] original) {
        char[][] copy = new char[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
        }
        return copy;
    }

    // Deep copy for player tiles map
    private Map<String, List<Character>> deepCopyPlayerTiles(Map<String, List<Character>> original) {
        Map<String, List<Character>> copy = new HashMap<>();
        for (Map.Entry<String, List<Character>> entry : original.entrySet()) {
            copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copy;
    }

    // Getters for the GameState fields
    public ArrayList<String> getPlayerNames() {
        return new ArrayList<>(playerNames);
    }

    public Map<String, List<Character>> getPlayerTilesMap() {
        return deepCopyPlayerTiles(playerTilesMap);
    }

    public ArrayList<Integer> getPlayerScores() {
        return new ArrayList<>(playerScores);
    }

    public char[][] getBoardState() {
        return deepCopyBoard(boardState);
    }

    public String getWordHistory() {
        return wordHistory;
    }

    public void setWordHistory(String wordHistory) {
        this.wordHistory = wordHistory;
    }

    public boolean isFirstTurn() {
        return firstTurn;
    }

    public void setFirstTurn(boolean firstTurn) {
        this.firstTurn = firstTurn;
    }

    public Set<String> getOldTileCoordinates() {
        return new HashSet<>(oldTileCoordinates);
    }

    public Set<String> getPlacedTileCoordinates() {
        return new HashSet<>(placedTileCoordinates);
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

    // Method to create a deep copy of GameState for undo/redo functionality
    public static GameState copy(GameState original) {
        return new GameState(
                original.getPlayerNames(),
                original.getPlayerTilesMap(),
                original.getPlayerScores(),
                original.getBoardState(),
                original.isFirstTurn(),
                original.getOldTileCoordinates(),
                original.getPlacedTileCoordinates(),
                original.getWordHistory()
        );
    }
}
