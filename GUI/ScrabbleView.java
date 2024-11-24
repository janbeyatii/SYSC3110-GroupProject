package GUI;

import src.*;

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
    private ArrayList<JButton> placedButtons = new ArrayList<>();

    // Control Panel Elements
    private JPanel controlPanel;
    public JButton passButton;
    public JButton clearButton;
    public JButton submitButton;

    // Player Information Display
    private JLabel[] playerScoresLabels;
    public JLabel turnLabel;

    // Tile Management Display
    private JPanel tilePanel;
    public JButton[] playerTileButtons;
    private JPanel aiTilePanel;
    public JButton[] aiTileButtons;
    private Map<String, JButton[]> aiTileButtonsMap;


    // Word History Display
    public JTextArea wordHistoryArea;

    /**
     * Constructs the ScrabbleView GUI, initializing the game board, control panel, and tile panel.
     *
     * @param boardSize the size of the game board (e.g., 15 for a 15x15 board).
     * @param tileCharacters the initial list of characters for the player's tile rack.
     */
    public ScrabbleView(int boardSize, ArrayList<Character> tileCharacters) {
        setTitle("SYSC3110 Group20 Scrabble Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        ScrabbleController.setView(this);

        ScrabbleController.initializeGameSettings();

        initializeBoardPanel(boardSize);
        initializeControlPanel();
        initializeTilePanel(tileCharacters);
        initializeAITilePanel();

        add(aiTilePanel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);
        add(tilePanel, BorderLayout.SOUTH);

        setVisible(true);
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

                ScrabbleController.doubleword(i,j);
                ScrabbleController.tripleword(i,j);
                ScrabbleController.doubleletter(i,j);
                ScrabbleController.tripleletter(i,j);

                if (i == middle && j == middle) {
                    boardButtons[i][j].setBackground(new Color(255, 255, 0)); // Starting tile
                    }

                boardButtons[i][j].setOpaque(true);
                boardButtons[i][j].setBorder(BorderFactory.createLineBorder(Color.GRAY));

                final int row = i;
                final int col = j;
                boardButtons[i][j].addActionListener(e -> Helpers.placeLetterOnBoard(row, col, boardButtons, placedButtons));

                gbc.gridx = j;
                gbc.gridy = i;
                boardPanel.add(boardButtons[i][j], gbc);
            }
        }
    }
    /**
     * Initializes the control panel containing word history, player scores, turn display, and control buttons.
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
        passButton = createButton("Pass", e -> {
            ButtonCommands.pass(placedButtons, playerTileButtons, turnLabel);
            updatePlayerTiles();
        });
        controlGbc.gridy = 3;
        controlPanel.add(passButton, controlGbc);

        clearButton = createButton("Clear", e -> ButtonCommands.clear(placedButtons, playerTileButtons));
        controlGbc.gridy = 4;
        controlPanel.add(clearButton, controlGbc);

        submitButton = createButton("Submit", e -> {
            ButtonCommands.submit(this, wordHistoryArea, turnLabel, placedButtons, playerScoresLabels, playerTileButtons);
            updatePlayerTiles();
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
                System.out.println("reached inside loop");
                playerTileButtons[i] = new JButton(" ");
                playerTileButtons[i].addActionListener(e -> {
                    System.out.println("Tile button clicked at index " + index);
                    Helpers.assignBlankTile(index, playerTileButtons);
                });
            } else {
                playerTileButtons[i] = new JButton(String.valueOf(tileCharacters.get(i)));
                playerTileButtons[i].addActionListener(e -> Helpers.selectLetterFromTiles(index, playerTileButtons));
            }

            // Set fixed size for each tile button
            playerTileButtons[i].setFont(new Font("Arial", Font.BOLD, 18));
            playerTileButtons[i].setPreferredSize(tileButtonSize);
            playerTileButtons[i].setMinimumSize(tileButtonSize);
            playerTileButtons[i].setMaximumSize(tileButtonSize);

            tileGbc.gridx = i;
            tilePanel.add(playerTileButtons[i], tileGbc);
        }
         repaint();
        revalidate();
    }

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
        final int index = i; // Declare a new final variable for use in the lambda

        if (i < currentPlayerTiles.size()) {
            char tile = currentPlayerTiles.get(i);

            // Check for blank tile and render as "_"
            if (tile == ' ') {
                
                playerTileButtons[index].setText(" ");
                playerTileButtons[index].addActionListener(e -> Helpers.assignBlankTile(index, playerTileButtons));
            } else {
                playerTileButtons[index].setText(String.valueOf(tile));
                playerTileButtons[index].addActionListener(e -> Helpers.selectLetterFromTiles(index, playerTileButtons));
            }

            playerTileButtons[index].setEnabled(true);
        } else {
            playerTileButtons[index].setText("");
            playerTileButtons[index].setEnabled(false);
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