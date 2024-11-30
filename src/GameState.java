package src;

import java.io.*;
import java.util.*;

public class GameState implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> playerNames;
    private Map<String, List<Character>> playerTilesMap;
    private ArrayList<Integer> playerScores;
    private String currentPlayerName;
    private char[][] boardState;


    public GameState(List<String> playerNames, Map<String, List<Character>> playerTilesMap,
                     ArrayList<Integer> playerScores, String currentPlayerName,
                     char[][] boardState) {
        this.playerNames = playerNames;
        this.playerTilesMap = playerTilesMap;
        this.playerScores = playerScores;
        this.currentPlayerName = currentPlayerName;
        this.boardState = boardState;

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
