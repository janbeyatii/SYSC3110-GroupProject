package GUI;

import src.*;
import src.PremiumSquare;
import src.BoardConfigLoader;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.Stack;

/**
 * The ScrabbleController class manages game settings, player data, and board state for a Scrabble game.
 * It initializes player names and scores, manages tile draws and placements, and controls the turn-based logic.
 */
public class ScrabbleController {

    // Game Settings and State Management
    private static int playercount;
    private static boolean isInitialized = false;
    private static boolean firstTurn = true;
    private static Stack<GameState> undoStack = new Stack<>();
    private static Stack<GameState> redoStack = new Stack<>();

    // Player Information
    private static ArrayList<String> playerNames = new ArrayList<>();
    private static ArrayList<Integer> playerScores = new ArrayList<>();
    private static Map<String, List<Character>> playerTilesMap = new HashMap<>();
    private static int currentPlayerIndex = 0;

    // Board and Tile Management
    private static ArrayList<JButton> masterPlacedButtons = new ArrayList<>();
    public static char[][] board = new char[15][15];
    public static TileBag tileBag = new TileBag();
    private static List<Point> placedTileCoordinates = new ArrayList<>();
    public static ScrabbleView view;
    /**
     * Constructor for the ScrabbleController class.
     *
     * @param view the ScrabbleView instance representing the GUI.
     */
    public ScrabbleController(ScrabbleView view) {
        // Default Constructor
    }

    /**
     * Retrieves the list of all placed buttons on the Scrabble board.
     *
     * @return an ArrayList of JButton objects representing the placed tiles.
     */
    public static ArrayList<JButton> getMasterPlacedButtons() {
        return masterPlacedButtons;
    }

    /**
     * Adds a JButton representing a tile to the list of placed buttons on the Scrabble board.
     *
     * @param button the JButton to be added to the list of placed tiles.
     */
    public static void addToMasterPlacedButtons(JButton button) {
        masterPlacedButtons.add(button);
    }

    /**
     * Clears all placed buttons from the Scrabble board, resetting the list of placed tiles.
     */
    public static void clearMasterPlacedButtons() {
        masterPlacedButtons.clear();
    }


    /**
     * Initializes game settings, such as player names, scores, and tiles, and sets up the game state.
     * Ensures that players are only initialized once.
     */
    public static void initializeGameSettings() {
        System.out.println("Initializing players: " + playerNames.size());

        if (!isInitialized) {
            //Initialize background music
            BackgroundMusic backgroundMusic = new BackgroundMusic();
            backgroundMusic.playMusic("resources/tunes.mp3");

            playerNames = setupPlayers();
            playercount = playerNames.size();
            initializePlayerScores();
            System.out.println("Adding blank tiles to the player tile pool");

            for (String playerName : playerNames) {
                System.out.println("Assigning tiles to: " + playerName);
                List<Character> initialTiles = tileBag.drawTiles(7);
                playerTilesMap.put(playerName, initialTiles);
            }

            ScrabbleController.initializeCustomBoard("boardConfig.json");
            isInitialized = true;
        }

    }

    /**
     * Load custom board configuration during game initialization
     */
    public static void initializeCustomBoard(String configFilePath) {
    List<PremiumSquare> premiumSquares = BoardConfigParser.loadFromJSON(configFilePath);
    for (PremiumSquare square : premiumSquares) {
        int row = square.getRow();
        int col = square.getCol();
        String type = square.getType();

        if (type.equals("Triple Word")) {
            ScrabbleView.boardButtons[row][col].setBackground(Color.RED);
        } else if (type.equals("Double Letter")) {
            ScrabbleView.boardButtons[row][col].setBackground(Color.BLUE);
        } else if (type.equals("Star")) {
            ScrabbleView.boardButtons[row][col].setBackground(Color.YELLOW);
        }
    }
}


    /**
     * Initializes player scores by setting all player scores to 0.
     * Clears any existing scores and resets the scores list based on the number of players.
     */
    public static void initializePlayerScores() {
        playerScores.clear();
        for (int i = 0; i < playercount; i++) {
            playerScores.add(0);
        }
    }

    /**
     * Retrieves the names of all players in the game.
     *
     * @return A list of player names.
     */
    public static List<String> getPlayerNames() {
        return playerNames;
    }

    public static Stack<GameState> getUndoStack() {
        return undoStack;
    }

    public static Stack<GameState> getRedoStack() {
        return redoStack;
    }

    /**
     * Retrieves the index of the current player in the turn rotation.
     *
     * @return The index of the current player.
     */
    public static int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }


    /**
     * Retrieves the ScrabbleView instance representing the game's GUI.
     *
     * @return The ScrabbleView instance.
     */
    public static ScrabbleView getView() {
        return view;
    }

    /**
     * Sets the ScrabbleView instance to be used by the controller.
     *
     * @param scrabbleView The ScrabbleView instance to set.
     */
    public static void setView(ScrabbleView scrabbleView) {
        view = scrabbleView;
    }

    /**
     * Retrieves the mapping of player names to their current tiles.
     *
     * @return A Map where the keys are player names and the values are lists of characters (tiles) assigned to each player.
     */
    public static Map<String, List<Character>> getPlayerTilesMap() {
        return playerTilesMap;
    }

    /**
     * Retrieves the tiles of the current player.
     *
     * @return A list of characters representing the current player's tiles.
     */
    public static List<Character> getCurrentPlayerTiles() {
        String currentPlayer = playerNames.get(currentPlayerIndex);
        System.out.println("Fetching tiles for player: " + currentPlayer);
        return playerTilesMap.get(currentPlayer);
    }

    /**
     * Retrieves the name of the current player.
     *
     * @return The name of the current player.
     */
    public static String getCurrentPlayerName() {
        return playerNames.get(currentPlayerIndex);
    }

    /**
     * Retrieves the list of tile coordinates placed during the current turn.
     *
     * @return A list of Points representing the placed tile coordinates.
     */
    public static List<Point> getPlacedTileCoordinates() {
        return placedTileCoordinates;
    }

    public static void setPlacedTileCoordinates(Point p) {placedTileCoordinates.add(p);}
    
    public static void clearPlacedTileCoordinates() {placedTileCoordinates.clear();}
    /**
     * Retrieves the score of a player by their index.
     *
     * @param index The index of the player.
     * @return The player's score.
     */
    public static int getPlayerScore(int index) {
        return playerScores.get(index);
    }

    /**
     * Prompts the user to set up the game with one human player and up to three AI players.
     *
     * @return A list of player names (1 human and up to 3 AI).
     */
    public static ArrayList<String> setupPlayers() {
        System.out.println("Players setup: " + playerNames);

        ArrayList<String> playerNames = new ArrayList<>();
        int totalAIPlayers = 0;

        while (true) {
            String input = JOptionPane.showInputDialog("How many AIs do you want to play against? (1-3) ");
            try {
                int count = Integer.parseInt(input);
                if (count >= 1 && count <= 3) {
                    totalAIPlayers = count;
                    break;
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a number between 1 and 3.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Please enter a valid integer.");
            }
        }

        while (true) {
            String humanName = JOptionPane.showInputDialog("Enter your name:");
            if (humanName != null && !humanName.trim().isEmpty()) {
                playerNames.add(humanName.trim());
                break;
            } else {
                JOptionPane.showMessageDialog(null, "Player name cannot be empty. Please enter a valid name.");
            }
        }

        // Add AI players
        for (int i = 1; i <= totalAIPlayers; i++) {
            playerNames.add("AI Player " + i);
        }

        return playerNames;
    }

    /**
     * Marks the first turn of the game as completed.
     */
    public static void setFirstTurnCompleted() {
        firstTurn = false;
    }

    /**
     * Adds a score to the specified player.
     *
     * @param playerIndex The index of the player.
     * @param score       The score to add.
     */
    public static void addScoreToPlayer(int playerIndex, int score) {
        int currentScore = playerScores.get(playerIndex);
        playerScores.set(playerIndex, currentScore + score);
    }

    /**
     * Adds the coordinates of placed tiles to the game state.
     */
    public static void addPlacedTiles() {
        ArrayList<JButton> placedButtons = ScrabbleController.getMasterPlacedButtons();
        for (JButton button : placedButtons) {
            int row = (Integer) button.getClientProperty("row");
            int col = (Integer) button.getClientProperty("col");
            placedTileCoordinates.add(new Point(row, col));
        }
    }
    // Get the board state
    public static char[][] getBoard() {
        return board;  // Access the current board state
    }
    /**
     * Handles the AI player's turn, including word placement, scoring, and tile updates.
     */
    private static void handleAITurn(JLabel[] playerScoresLabels) {
        String aiPlayerName = getCurrentPlayerName();
        List<Character> aiTiles = playerTilesMap.get(aiPlayerName);


        Set<String> formedWords = AIPlayer.makeMove(board, aiTiles,playerScoresLabels);

        if (!formedWords.isEmpty()) {
            for (String word : formedWords) {

                for (int i = 0; i < word.length(); i++) {
                    JButton dummyButton = new JButton(String.valueOf(word.charAt(i)));
                    dummyButton.putClientProperty("row", 0); // Replace with the actual row if available
                    dummyButton.putClientProperty("col", i); // Replace with the actual column if available
                }

                assert view != null;
                view.updateAITiles();

                System.out.println(aiPlayerName + " placed the word: " + word);
            }

            Helpers.updateOldTileCoordinates();
        }

        if (formedWords.isEmpty()) {
            System.out.println(aiPlayerName + " passed their turn.");
        }

        if (view != null) {
            view.updateBoardDisplay();
            view.updatePlayerTiles();
            view.turnLabel.setText("Turn: " + getCurrentPlayerName());
        }

        switchToNextPlayer(playerScoresLabels);
    }

    /**
     * Checks if it is the first turn of the game.
     *
     * @return True if it is the first turn; otherwise, false.
     */
    public static boolean isFirstTurn() {
        return firstTurn;
    }

    /**
     * Switches to the next player's turn, including handling AI turns.
     */
    public static void switchToNextPlayer(JLabel[] playerScoresLabels) {
        currentPlayerIndex = (currentPlayerIndex + 1) % playercount;

        if (getCurrentPlayerName().startsWith("AI Player")) {
            handleAITurn(playerScoresLabels);
        }
    }

    /**
     * Sets the list of player names in the game.
     *
     * @param names an ArrayList of player names to be used in the game.
     */
    public static void setPlayerNames(ArrayList<String> names) {
        playerNames = names;  // Set the list of player names in the controller
    }

    /**
     * Sets the map of player tiles, associating each player's name with their respective list of tiles.
     *
     * @param tilesMap a map where the key is the player's name and the value is a list of tiles (characters) they possess.
     */
    public static void setPlayerTilesMap(Map<String, List<Character>> tilesMap) {
        playerTilesMap = tilesMap;  // Set the map of player tiles
    }

    /**
     * Updates the current state of the Scrabble board.
     *
     * @param newBoard a 2D character array representing the updated state of the board.
     */
    public static void setBoard(char[][] newBoard) {
        board = newBoard;  // Set the current board state
    }


    /**
     * Sets the triple word score tiles on the board.
     *
     * @param i The row index of the tile.
     * @param j The column index of the tile.
     */
    public static void tripleword(int i, int j) {
        if ((i == 0 || i == 7 || i == 14) && (j == 0 || j == 14) ||
                (i == 0 || i == 14) && (j == 7)) {
            ScrabbleView.boardButtons[i][j].setBackground(new Color(220, 50, 50));
        }
    }

    /**
     * Sets the triple letter score tiles on the board.
     *
     * @param i The row index of the tile.
     * @param j The column index of the tile.
     */
    public static void tripleletter(int i, int j) {
        if ((i == 1 || i == 5 || i == 9 || i == 13) && (j == 5 || j == 9) ||
                (i == 5 || i == 9) && (j == 1 || j == 13)) {
            ScrabbleView.boardButtons[i][j].setBackground(new Color(65, 105, 225));
        }
    }

    /**
     * Sets the double word score tiles on the board.
     *
     * @param i The row index of the tile.
     * @param j The column index of the tile.
     */
    public static void doubleword(int i, int j) {
        if ((i == 1) && (j == 1 || j == 13) || (i == 2) && (j == 2 || j == 12)
                || (i == 3) && (j == 3 || j == 11) || (i == 4) && (j == 4 || j == 10)
                || (i == 10) && (j == 4 || j == 10) || (i == 11) && (j == 3 || j == 11)
                || (i == 12) && (j == 2 || j == 12) || (i == 13) && (j == 1 || j == 13)) {
            ScrabbleView.boardButtons[i][j].setBackground(new Color(230, 100, 100));
        }
    }

    /**
     * Sets the double letter score tiles on the board.
     *
     * @param i The row index of the tile.
     * @param j The column index of the tile.
     */
    public static void doubleletter(int i, int j) {
        if ((i == 0 || i == 14) && (j == 3 || j == 11) ||
                (i == 2 || i == 12) && (j == 6 || j == 8) ||
                (i == 3 || i == 11) && (j == 0 || j == 14) ||
                (i == 6 || i == 8) && (j == 2 || j == 6 || j == 8 || j == 12) ||
                (i == 7) && (j == 3 || j == 11) ||
                (i == 3 || i == 11) && (j == 7)) {
            ScrabbleView.boardButtons[i][j].setBackground(new Color(173, 216, 230));
        }
    }

    /**
     * Displays a winning animation with confetti and a "YOU WIN" message.
     * The animation lasts for 6 seconds before closing the application.
     */
    public static void showWinningAnimation() {
        JFrame confettiFrame = new JFrame("Congratulations!");
        confettiFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        confettiFrame.setSize(800, 600);
        confettiFrame.setLocationRelativeTo(null);

        JPanel animationPanel = new JPanel() {
            private final Random random = new Random();
            private final List<Point> confetti = new ArrayList<>();

            {
                // Timer to continuously add and animate confetti
                Timer timer = new Timer(20, e -> {
                    confetti.add(new Point(random.nextInt(getWidth()), 0)); // Add new confetti pieces
                    repaint();
                });
                timer.start();
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Draw confetti
                for (Point point : confetti) {
                    g.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
                    g.fillOval(point.x, point.y += 5, 10, 10); // Move confetti pieces down
                }

                // Remove confetti that go out of bounds
                confetti.removeIf(p -> p.y > getHeight());

                // Draw the "YOU WIN" message
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 72)); // Bold and large font
                FontMetrics metrics = g.getFontMetrics();
                String message = "YOU WIN!";
                int x = (getWidth() - metrics.stringWidth(message)) / 2;
                int y = getHeight() / 2;
                g.drawString(message, x, y);
            }
        };

        animationPanel.setBackground(Color.BLACK);
        confettiFrame.add(animationPanel);
        confettiFrame.setVisible(true);

        // Timer to close the window after 6 seconds
        new Timer(6000, e -> {
            confettiFrame.dispose(); // Close the window
            System.exit(0); // End the program
        }).start();
    }

    /**
     * Saves the current game state to a file.
     *
     * @param filename the name of the file where the game state will be saved.
     */
    public static void saveGame(String filename) {
        try {
            // Fetch game state using existing methods
            List<String> playerNames = getPlayerNames();
            Map<String, List<Character>> playerTilesMap = getPlayerTilesMap();
            char[][] boardState = getBoard();
            Set<String> oldTileCoordinates = Helpers.getOldTileCoordinates(); // Fetch old tiles.
            Set<String> placedTileCoordinatesSet = new HashSet<>();
            for (Point point : placedTileCoordinates) {
                placedTileCoordinatesSet.add(point.x + "," + point.y); // Store as a string representation
            }
            ArrayList<Integer> playerScores = new ArrayList<>(ScrabbleController.playerScores);

            // Retrieve word history as text
            ScrabbleView view = getView();
            String wordHistory = view.getWordHistory().getText();

            // Create GameState object
            GameState gameState = new GameState(playerNames, playerTilesMap, playerScores,
                    boardState, firstTurn, oldTileCoordinates, placedTileCoordinatesSet, wordHistory);

            // Save the game state
            GameState.saveGameState(gameState, filename);
            System.out.println("Game state saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving game state: " + e.getMessage());
        }
    }

    /**
     * Loads the game state from a file and restores it.
     *
     * @param filename the name of the file from which the game state will be loaded.
     */
    public static void loadGame(String filename) {
        try {
            // Load the game state from the file
            GameState gameState = GameState.loadGameState(filename);

            // Restore the game state using the loaded data
            setPlayerNames(gameState.getPlayerNames());
            setPlayerTilesMap(gameState.getPlayerTilesMap());
            setBoard(gameState.getBoardState());
            firstTurn = gameState.isFirstTurn();
            Helpers.setOldTileCoordinates(gameState.getOldTileCoordinates()); // Restore old tiles.
            Set<String> placedTileCoordinatesSet = gameState.getPlacedTileCoordinates();
            placedTileCoordinates.clear(); // Clear current placed tiles
            for (String coord : placedTileCoordinatesSet) {
                String[] parts = coord.split(",");
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                placedTileCoordinates.add(new Point(x, y));
            }
            playerScores = new ArrayList<>(gameState.getPlayerScores());

            // Update the view
            ScrabbleView view = getView();
            view.updatePlayerTiles();
            view.updateBoardDisplay();
            view.updateAITiles();
            view.getWordHistory().setText(gameState.getWordHistory()); // Set word history text

            System.out.println("Game state loaded successfully.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading game state: " + e.getMessage());
        }
    }


    /**
     * Reverts the game to the previous turn by undoing the last move.
     * This method uses the undo stack to restore the previous game state.
     */
    public static void undoLastMove() {
        // Check if there is a previous state to undo
        if (undoStack.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No moves to undo.");
            return;
        }

        try {
            // Ensure there's a valid state to save for redo before popping the undo stack
            if (!undoStack.isEmpty()) {
                redoStack.push(getCurrentGameState()); // Save current state to redo stack
            }

            // Pop the last state from the undo stack and restore it
            GameState previousState = undoStack.pop();
            restoreGameState(previousState);

            // Update the GUI after restoring the state
            if (view != null) {
                view.updateBoardDisplay();
                view.updatePlayerTiles();
                view.turnLabel.setText("Turn: " + getCurrentPlayerName());
            }

            System.out.println("Undo successful. Reverted to the previous turn.");
        } catch (Exception e) {
            System.err.println("Error during undo operation: " + e.getMessage());
        }
    }
    /**
     * Redoes the last undone move by restoring the game state from the redo stack.
     * This method uses the redo stack to reapply a previously undone game state.
     */
    public static void redoLastMove() {
        // Check if there is a state to redo
        if (redoStack.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No moves to redo.");
            return;
        }

        try {
            // Ensure there's a valid state to save for undo before popping the redo stack
            if (!redoStack.isEmpty()) {
                undoStack.push(getCurrentGameState()); // Save current state to undo stack
            }

            // Pop the last state from the redo stack and restore it
            GameState nextState = redoStack.pop();
            restoreGameState(nextState);

            // Update the GUI after restoring the state
            if (view != null) {
                view.updateBoardDisplay();
                view.updatePlayerTiles();
                view.turnLabel.setText("Turn: " + getCurrentPlayerName());
            }

            System.out.println("Redo successful. Reapplied the undone move.");
        } catch (Exception e) {
            System.err.println("Error during redo operation: " + e.getMessage());
        }
    }

    /**
     * Saves the current game state to the undo stack.
     * Should be called before making a move.
     */
    public static void saveCurrentStateToUndoStack() {
        try {
            undoStack.push(getCurrentGameState());
            System.out.println("Saved current state to undo stack.");
        } catch (Exception e) {
            System.err.println("Error saving current state to undo stack: " + e.getMessage());
        }
    }

    /**
     * Gets the current game state.
     *
     * @return The current game state as a GameState object.
     */
    private static GameState getCurrentGameState() {
        // Retrieve the current game state using existing data
        return new GameState(
                new ArrayList<>(playerNames),
                new HashMap<>(playerTilesMap),
                new ArrayList<>(playerScores),
                copyBoard(board),
                firstTurn,
                Helpers.getOldTileCoordinates(),
                new HashSet<>(getPlacedTileCoordinatesAsStringSet()),
                view.getWordHistory().getText()
        );
    }

    /**
     * Restores the game state from the provided GameState object.
     *
     * @param gameState The GameState object to restore.
     */
    private static void restoreGameState(GameState gameState) {
        setPlayerNames(gameState.getPlayerNames());
        setPlayerTilesMap(gameState.getPlayerTilesMap());
        setBoard(gameState.getBoardState());
        firstTurn = gameState.isFirstTurn();
        Helpers.setOldTileCoordinates(gameState.getOldTileCoordinates());

        placedTileCoordinates.clear();
        for (String coord : gameState.getPlacedTileCoordinates()) {
            String[] parts = coord.split(",");
            placedTileCoordinates.add(new Point(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])));
        }

        playerScores = new ArrayList<>(gameState.getPlayerScores());
        view.getWordHistory().setText(gameState.getWordHistory());
    }

    /**
     * Copies the board state to prevent accidental modification of the original.
     *
     * @param originalBoard The original board to copy.
     * @return A copy of the board.
     */
    private static char[][] copyBoard(char[][] originalBoard) {
        char[][] boardCopy = new char[originalBoard.length][];
        for (int i = 0; i < originalBoard.length; i++) {
            boardCopy[i] = Arrays.copyOf(originalBoard[i], originalBoard[i].length);
        }
        return boardCopy;
    }

    /**
     * Converts the placed tile coordinates to a set of string representations.
     *
     * @return A set of string representations of placed tile coordinates.
     */
    private static Set<String> getPlacedTileCoordinatesAsStringSet() {
        Set<String> coordinatesSet = new HashSet<>();
        for (Point point : placedTileCoordinates) {
            coordinatesSet.add(point.x + "," + point.y);
        }
        return coordinatesSet;
    }

}
