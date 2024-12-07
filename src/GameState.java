package src;

import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Represents the state of the Scrabble game, including player information, board state,
 * and other relevant game details. Provides methods to save and load the game state.
 */
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

    /**
     * Constructs a new GameState object with the specified parameters.
     *
     * @param playerNames a list of player names.
     * @param playerTilesMap a map associating player names with their respective tiles.
     * @param playerScores a list of scores for each player.
     * @param boardState the current state of the game board as a 2D character array.
     * @param firstTurn a boolean indicating if it's the first turn of the game.
     * @param oldTileCoordinates a set of coordinates for old tiles on the board.
     * @param placedTileCoordinates a set of coordinates for tiles placed during the current turn.
     * @param wordHistory a string representing the history of played words.
     */
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

    /**
     * Creates a deep copy of a 2D character array representing the game board.
     *
     * @param original the original 2D character array.
     * @return a deep copy of the 2D character array.
     */
    private char[][] deepCopyBoard(char[][] original) {
        char[][] copy = new char[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
        }
        return copy;
    }

    /**
     * Creates a deep copy of the player tiles map.
     *
     * @param original the original map associating player names with their tiles.
     * @return a deep copy of the player tiles map.
     */
    private Map<String, List<Character>> deepCopyPlayerTiles(Map<String, List<Character>> original) {
        Map<String, List<Character>> copy = new HashMap<>();
        for (Map.Entry<String, List<Character>> entry : original.entrySet()) {
            copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copy;
    }

    /**
     * Retrieves the list of player names.
     *
     * @return a new ArrayList containing the player names.
     */
    public ArrayList<String> getPlayerNames() {
        return new ArrayList<>(playerNames);
    }

    /**
     * Retrieves the map of player tiles.
     *
     * @return a deep copy of the player tiles map.
     */
    public Map<String, List<Character>> getPlayerTilesMap() {
        return deepCopyPlayerTiles(playerTilesMap);
    }

    /**
     * Retrieves the list of player scores.
     *
     * @return a new ArrayList containing the player scores.
     */
    public ArrayList<Integer> getPlayerScores() {
        return new ArrayList<>(playerScores);
    }

    /**
     * Retrieves the current state of the game board.
     *
     * @return a deep copy of the 2D character array representing the board state.
     */
    public char[][] getBoardState() {
        return deepCopyBoard(boardState);
    }

    /**
     * Retrieves the history of played words.
     *
     * @return a string representing the word history.
     */
    public String getWordHistory() {
        return wordHistory;
    }

    /**
     * Checks if it is the first turn of the game.
     *
     * @return {@code true} if it is the first turn, otherwise {@code false}.
     */
    public boolean isFirstTurn() {
        return firstTurn;
    }

    /**
     * Retrieves the set of coordinates for old tiles on the board.
     *
     * @return a new HashSet containing the old tile coordinates.
     */
    public Set<String> getOldTileCoordinates() {
        return new HashSet<>(oldTileCoordinates);
    }

    /**
     * Retrieves the set of coordinates for tiles placed during the current turn.
     *
     * @return a new HashSet containing the placed tile coordinates.
     */
    public Set<String> getPlacedTileCoordinates() {
        return new HashSet<>(placedTileCoordinates);
    }

    /**
     * Saves the current game state to a file.
     *
     * @param gameState the GameState object to save.
     * @param filename the name of the file to save the game state to.
     * @throws IOException if an I/O error occurs while saving the file.
     */
    public static void saveGameState(GameState gameState, String filename) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(gameState);
        }
    }

    /**
     * Loads a saved game state from a file.
     *
     * @param filename the name of the file to load the game state from.
     * @return the loaded GameState object.
     * @throws IOException if an I/O error occurs while loading the file.
     * @throws ClassNotFoundException if the class of the serialized object cannot be found.
     */
    public static GameState loadGameState(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (GameState) in.readObject();
        }
    }
}
