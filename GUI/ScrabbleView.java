package GUI;

import src.*;
import src.PremiumSquare;
import src.BoardConfigLoader;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The ScrabbleView class represents the main GUI for the Scrabble game.
 * It includes the game board, controls, player tile rack, and score display.
 */

public class ScrabbleView extends JFrame {
    // Board and Tiles Display
    private JPanel boardPanel;
    public static JButton[][] boardButtons;

    // Control Panel Elements
    private JPanel controlPanel;
    public JButton passButton;
    public JButton clearButton;
    public JButton submitButton;

    // Player Information Display
    public JLabel[] playerScoresLabels;
    public JLabel turnLabel;

    // Tile Management Display
    private JPanel tilePanel;
    public JButton[] playerTileButtons;
    private JPanel aiTilePanel;
    private Map<String, JButton[]> aiTileButtonsMap;


    // Word History Display
    public JTextArea wordHistoryArea;
    private JMenuBar menuBar;
    private JMenu gameMenu;
    private JMenuItem saveMenuItem;
    private JMenuItem loadMenuItem;
    private JMenuItem undoMenuItem;
    private JMenuItem redoMenuItem;

    /**
     * Constructs the ScrabbleView GUI, initializing the game board, control panel, and tile panel.
     *
     * @param boardSize the size of the game board (e.g., 15 for a 15x15 board).
     * @param tileCharacters the initial list of characters for the player's tile rack.
     */
    public ScrabbleView(int boardSize, ArrayList<Character> tileCharacters) {
        setTitle("SYSC3110 Group20 Scrabble Game");
        setSize(950, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        ScrabbleController.setView(this);

        ScrabbleController.initializeGameSettings();

        initializeBoardPanel(boardSize);
        initializeControlPanel();
        initializeTilePanel(tileCharacters);
        initializeAITilePanel();
        initializeMenuBar();

        add(aiTilePanel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);
        add(tilePanel, BorderLayout.SOUTH);

        setVisible(true);
    }
    /**
     * Initializes the menu bar with Save and Load options.
     */
    private void initializeMenuBar() {
        menuBar = new JMenuBar();
        gameMenu = new JMenu("Game");

        // Save menu item
        saveMenuItem = new JMenuItem("Save Game");
        saveMenuItem.addActionListener(e -> saveGame());
        gameMenu.add(saveMenuItem);

        // Load menu item
        loadMenuItem = new JMenuItem("Load Game");
        loadMenuItem.addActionListener(e -> loadGame());
        gameMenu.add(loadMenuItem);

        // Undo menu item
        undoMenuItem = new JMenuItem("Undo");
        undoMenuItem.addActionListener(e -> ScrabbleController.undoLastMove());
        gameMenu.add(undoMenuItem);

        // Redo menu item
        redoMenuItem = new JMenuItem("Redo");
        redoMenuItem.addActionListener(e -> ScrabbleController.redoLastMove());
        gameMenu.add(redoMenuItem);

        menuBar.add(gameMenu);
        setJMenuBar(menuBar);
    }

    /**
     * Save the current game state to a file.
     */
    private void saveGame() {
        // Assume the user provides a filename, for example
        String filename = "game_save.dat";  // Use an appropriate file name
        ScrabbleController.saveGame(filename);
    }

    /**
     * Retrieves the text area displaying the word history in the game.
     *
     * @return the JTextArea object that contains the word history.
     */
    public JTextArea getWordHistory() {
        return wordHistoryArea;
    }

    /**
     * Load a previously saved game state from a file.
     */
    private void loadGame() {
        // Assume the user provides a filename, for example
        String filename = "game_save.dat";  // Use an appropriate file name
        ScrabbleController.loadGame(filename);
    }

    /**
     * Initializes the game board panel with buttons for each tile.
     *
     * @param boardSize the size of the board (e.g., 15 for a 15x15 grid).
     */

    private void initializeBoardPanel(int boardSize) {

        boardPanel = new JPanel(new GridBagLayout());
        boardButtons = new JButton[boardSize][boardSize];
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        int middle = boardSize / 2;
        Dimension buttonSize = new Dimension(40, 40);
        
        List<PremiumSquare> premiumSquares = BoardConfigLoader.loadFromJSON("boardConfig.json");

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {

                boardButtons[i][j] = new JButton();
                boardButtons[i][j].putClientProperty("row", i);
                boardButtons[i][j].putClientProperty("col", j);

                // Set fixed size for each button
                boardButtons[i][j].setPreferredSize(buttonSize);
                boardButtons[i][j].setMinimumSize(buttonSize);
                boardButtons[i][j].setMaximumSize(buttonSize);
                boardButtons[i][j].setFont(new Font("Arial", Font.BOLD, 18));
                boardButtons[i][j].setHorizontalAlignment(SwingConstants.CENTER);
                boardButtons[i][j].setBackground(Color.LIGHT_GRAY);

                /**for (PremiumSquare square : premiumSquares) {
                if (square.getRow() == i && square.getCol() == j) {
                    if (square.getType().equals("Triple Word")) {
                        boardButtons[i][j].setBackground(Color.RED);
                    } else if (square.getType().equals("Double Letter")) {
                        boardButtons[i][j].setBackground(Color.BLUE);
                    } else if (square.getType().equals("Star")) {
                        boardButtons[i][j].setBackground(Color.YELLOW);
                    }
                    break;
                }
            }
                 * 
                 */

                

                ScrabbleController.doubleword(i,j);
                ScrabbleController.tripleword(i,j);
                ScrabbleController.doubleletter(i,j);
                ScrabbleController.tripleletter(i,j);

                if (i == middle && j == middle) {
                    boardButtons[i][j].setBackground(new Color(255, 255, 0)); // Starting tile
                }

                int finalI = i;
                int finalJ = j;
                boardButtons[i][j].addActionListener(e -> Helpers.placeLetterOnBoard(finalI, finalJ, boardButtons)); // Use field directly
                boardButtons[i][j].setOpaque(true);
                boardButtons[i][j].setBorder(BorderFactory.createLineBorder(Color.GRAY));

                gbc.gridx = j;
                gbc.gridy = i;
                boardPanel.add(boardButtons[i][j], gbc);
            }
        }
    }

    /**
     * Initializes the control panel containing word history, player scores, turn display, control buttons, and the legend.
     */
    private void initializeControlPanel() {
        controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints controlGbc = new GridBagConstraints();
        controlGbc.fill = GridBagConstraints.HORIZONTAL;
        controlGbc.insets = new Insets(5, 5, 5, 5);
        controlGbc.weightx = 1.0;

        initializeWordHistoryArea(controlGbc);
        initializeScorePanel(controlGbc);
        initializeTurnLabel(controlGbc);
        initializeControlButtons(controlGbc);
        initializeLegend(controlGbc);
    }

    /**
     * Initializes the legend to display the colors used on the board and their meanings.
     *
     * @param controlGbc the layout constraints for positioning within the control panel.
     */
    private void initializeLegend(GridBagConstraints controlGbc) {
        JPanel legendPanel = new JPanel();
        legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
        legendPanel.setBorder(BorderFactory.createTitledBorder("Board Legend"));

        // Add legend entries for different colors and descriptions
        legendPanel.add(createLegendEntry(new Color(255, 255, 0), "Starting Tile"));
        legendPanel.add(createLegendEntry(new Color(230, 100, 100), "Double Word Score"));
        legendPanel.add(createLegendEntry(new Color(220, 50, 50), "Triple Word Score"));
        legendPanel.add(createLegendEntry(new Color(173, 216, 230), "Double Letter Score"));
        legendPanel.add(createLegendEntry(new Color(65, 105, 225), "Triple Letter Score"));

        // Add the legend panel to the control panel
        controlGbc.gridy = 6;
        controlPanel.add(legendPanel, controlGbc);
    }

    /**
     * Creates a JPanel entry for the color legend.
     *
     * @param color the color block to be displayed.
     * @param description the description of the color.
     * @return the created JPanel entry.
     */
    private JPanel createLegendEntry(Color color, String description) {
        JPanel legendEntry = new JPanel();
        legendEntry.setLayout(new FlowLayout(FlowLayout.LEFT));

        // Create a color block
        JPanel colorBlock = new JPanel();
        colorBlock.setBackground(color);
        colorBlock.setPreferredSize(new Dimension(20, 20));

        // Add the color block and description text
        legendEntry.add(colorBlock);
        legendEntry.add(new JLabel(description));

        return legendEntry;
    }


    /**
     * Initializes the word history area, where placed words are displayed.
     *
     * @param controlGbc the layout constraints for positioning within the control panel.
     */
    private void initializeWordHistoryArea(GridBagConstraints controlGbc) {
        controlGbc.gridy = 0;
        wordHistoryArea = new JTextArea(10, 20);
        wordHistoryArea.setEditable(false);
        wordHistoryArea.setLineWrap(true);
        wordHistoryArea.setWrapStyleWord(true);
        wordHistoryArea.setBorder(BorderFactory.createTitledBorder("Words Placed"));
        controlPanel.add(new JScrollPane(wordHistoryArea), controlGbc);
    }

    /**
     * Initializes the score panel, which displays the scores of all players.
     *
     * @param controlGbc the layout constraints for positioning within the control panel.
     */
    private void initializeScorePanel(GridBagConstraints controlGbc) {
        JPanel scorePanel = new JPanel(new GridBagLayout());
        List<String> playerNames = ScrabbleController.getPlayerNames(); // Get the correct player names
        int playerCount = playerNames.size();
        playerScoresLabels = new JLabel[playerCount]; // Adjust size to match actual player count

        for (int i = 0; i < playerCount; i++) {
            // Use the correct player name for the label
            String playerName = playerNames.get(i);
            playerScoresLabels[i] = new JLabel(playerName + " Score: 0");
            playerScoresLabels[i].setFont(new Font("Arial", Font.PLAIN, 14));

            // Add label to the score panel
            GridBagConstraints scoreGbc = new GridBagConstraints();
            scoreGbc.gridy = i;
            scorePanel.add(playerScoresLabels[i], scoreGbc);
        }

        controlGbc.gridy = 1;
        controlPanel.add(scorePanel, controlGbc);
    }

    /**
     * Initializes the label that displays the current player's turn.
     *
     * @param controlGbc the layout constraints for positioning within the control panel.
     */
    private void initializeTurnLabel(GridBagConstraints controlGbc) {
        turnLabel = new JLabel("Turn: " + ScrabbleController.getCurrentPlayerName());
        turnLabel.setFont(new Font("Arial", Font.BOLD, 14));
        controlGbc.gridy = 2;
        controlPanel.add(turnLabel, controlGbc);
    }

    /**
     * Initializes the control buttons (Pass, Clear, Submit) and adds them to the control panel.
     *
     * @param controlGbc the layout constraints for positioning within the control panel.
     */
    private void initializeControlButtons(GridBagConstraints controlGbc) {
        ArrayList<JButton> buttonsToSubmit = ScrabbleController.getMasterPlacedButtons();

        passButton = createButton("Pass", e -> {
            ButtonCommands.pass(playerTileButtons, turnLabel, playerScoresLabels);
            updatePlayerTiles();
        });
        controlGbc.gridy = 3;
        controlPanel.add(passButton, controlGbc);

        System.out.println("Placed Buttons in initializeControlButtons: " + buttonsToSubmit);

        clearButton = createButton("Clear", e -> ButtonCommands.clear(playerTileButtons));
        controlGbc.gridy = 4;
        controlPanel.add(clearButton, controlGbc);

        submitButton = createButton("Submit", e -> {
            if (!buttonsToSubmit.isEmpty()) {
                ButtonCommands.submit(this, wordHistoryArea, turnLabel, playerScoresLabels, playerTileButtons);
                updatePlayerTiles();
            } else {
                System.out.println("No buttons placed to submit!");
            }
        });
        controlGbc.gridy = 5;
        controlPanel.add(submitButton, controlGbc);
    }

    /**
     * Initializes the tile panel displaying the player's current rack of tiles.
     *
     * @param tileCharacters the list of characters representing the player's tile rack.
     */
    private void initializeTilePanel(ArrayList<Character> tileCharacters) {
        tilePanel = new JPanel(new GridBagLayout());
        GridBagConstraints tileGbc = new GridBagConstraints();
        tileGbc.fill = GridBagConstraints.BOTH;
        tileGbc.weightx = 1.0;
        tileGbc.insets = new Insets(2, 2, 2, 2);

        Dimension tileButtonSize = new Dimension(40, 40);  // Fixed size for player tiles

        playerTileButtons = new JButton[7];
        for (int i = 0; i < 7; i++) {
            playerTileButtons[i] = new JButton(String.valueOf(tileCharacters.get(i)));
            playerTileButtons[i].setFont(new Font("Arial", Font.BOLD, 18));

            int index = i;

            //logic for blank tiles
            if (tileCharacters.get(i) == ' ') {
                playerTileButtons[i].addActionListener(e -> Helpers.assignBlankTile(index, playerTileButtons));
            } else {
                playerTileButtons[i].addActionListener(e -> Helpers.selectLetterFromTiles(index, playerTileButtons));
            }

            // Set fixed size for each tile button
            playerTileButtons[i].setPreferredSize(tileButtonSize);
            playerTileButtons[i].setMinimumSize(tileButtonSize);
            playerTileButtons[i].setMaximumSize(tileButtonSize);

            tileGbc.gridx = i;
            tilePanel.add(playerTileButtons[i], tileGbc);
        }
    }
    /**
     * Initializes the AI tile panel, which displays the tiles for each AI player in the game.
     * The tiles are displayed as disabled buttons to prevent user interaction and provide a visual representation
     * of the AI players' tiles. The panel organizes the AI players' names and their tiles in a vertical layout.
     */
    private void initializeAITilePanel() {
        aiTilePanel = new JPanel();
        aiTilePanel.setLayout(new BoxLayout(aiTilePanel, BoxLayout.Y_AXIS));
        aiTileButtonsMap = new HashMap<>();

        List<String> playerNames = ScrabbleController.getPlayerNames();
        for (String playerName : playerNames) {
            if (!playerName.startsWith("AI")) continue;

            JPanel aiPlayerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel playerLabel = new JLabel(playerName + ": ");
            playerLabel.setFont(new Font("Arial", Font.BOLD, 16));
            aiPlayerPanel.add(playerLabel);

            JButton[] aiTileButtons = new JButton[7];
            for (int i = 0; i < 7; i++) {
                aiTileButtons[i] = new JButton("...");
                aiTileButtons[i].setPreferredSize(new Dimension(50, 50));
                aiTileButtons[i].setFont(new Font("Arial", Font.BOLD, 20));
                aiTileButtons[i].setEnabled(false);
                aiPlayerPanel.add(aiTileButtons[i]);
            }

            aiTilePanel.add(aiPlayerPanel);
            aiTileButtonsMap.put(playerName, aiTileButtons);
        }

        add(aiTilePanel, BorderLayout.NORTH);
    }
    /**
     * Creates a JButton with the specified text and action listener.
     *
     * @param text the text to display on the button.
     * @param action the action listener to handle button clicks.
     * @return the created JButton.
     */
    private JButton createButton(String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.addActionListener(action);
        return button;
    }
    /**
     * Updates the player's tile rack with new tiles and refreshes the display.
     */
    public void updatePlayerTiles() {
        List<Character> currentPlayerTiles = ScrabbleController.getCurrentPlayerTiles();

        if (currentPlayerTiles == null) {
            currentPlayerTiles = new ArrayList<>();
        }

        System.out.println("Updating player tiles: " + currentPlayerTiles); // Debug log

        for (int i = 0; i < playerTileButtons.length; i++) {
            if (i < currentPlayerTiles.size()) {
                playerTileButtons[i].setText(String.valueOf(currentPlayerTiles.get(i)));
                playerTileButtons[i].setEnabled(true);
            } else {
                playerTileButtons[i].setText("");
                playerTileButtons[i].setEnabled(false);
            }
        }

        repaint();
        revalidate();
    }
    /**
     * Updates the display of the board based on the current state of the game.
     */
    public void updateBoardDisplay() {
        for (int row = 0; row < boardButtons.length; row++) {
            for (int col = 0; col < boardButtons[row].length; col++) {
                char letter = ScrabbleController.board[row][col];
                if (letter != '\0') {
                    boardButtons[row][col].setText(String.valueOf(letter));
                } else {
                    boardButtons[row][col].setText("");
                }
            }
        }
    }
    /**
     * Updates the tiles displayed for each AI player in the AI tile panel.
     * Retrieves the current tiles for each AI player from the ScrabbleController and updates the corresponding buttons.
     * If an AI player has fewer than 7 tiles, the remaining buttons are cleared.
     */
    public void updateAITiles() {
        Map<String, List<Character>> playerTilesMap = ScrabbleController.getPlayerTilesMap();

        for (Map.Entry<String, JButton[]> entry : aiTileButtonsMap.entrySet()) {
            String playerName = entry.getKey();
            JButton[] aiTileButtons = entry.getValue();

            List<Character> aiTiles = playerTilesMap.get(playerName);
            if (aiTiles != null) {
                for (int i = 0; i < aiTileButtons.length; i++) {
                    if (i < aiTiles.size()) {
                        aiTileButtons[i].setText(String.valueOf(aiTiles.get(i)));
                    } else {
                        aiTileButtons[i].setText("");
                    }
                }
            }
        }
    }
}