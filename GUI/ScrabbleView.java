package GUI;

import src.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The ScrabbleView class represents the main GUI for the Scrabble game.
 * It includes the game board, controls, player tile rack, and score display.
 */

public class ScrabbleView extends JFrame {
    private JPanel boardPanel;
    public JButton[][] boardButtons;
    private JPanel controlPanel;
    private JLabel[] playerScoresLabels;
    public JLabel turnLabel;
    public JButton passButton;
    public JButton clearButton;
    public JButton submitButton;
    private JPanel tilePanel;
    public JButton[] playerTileButtons;
    private JTextArea wordHistoryArea;

    private ArrayList<JButton> placedButtons = new ArrayList<>();

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

        ScrabbleController.initializeGameSettings();

        initializeBoardPanel(boardSize);
        initializeControlPanel();
        initializeTilePanel(tileCharacters);

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

                if (i == middle && j == middle) {
                    boardButtons[i][j].setBackground(new Color(173, 216, 230));
                } else {
                    boardButtons[i][j].setBackground(Color.LIGHT_GRAY);
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
        playerScoresLabels = new JLabel[ScrabbleController.getPlayercount()];
        for (int i = 0; i < ScrabbleController.getPlayercount(); i++) {
            playerScoresLabels[i] = new JLabel(ScrabbleController.getPlayerNames().get(i) + " Score: 0");
            playerScoresLabels[i].setFont(new Font("Arial", Font.PLAIN, 14));
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

        clearButton = createButton("Clear", _ -> ButtonCommands.clear(placedButtons, playerTileButtons));
        controlGbc.gridy = 4;
        controlPanel.add(clearButton, controlGbc);

        submitButton = createButton("Submit", _ -> {
            ButtonCommands.submit(this, wordHistoryArea, turnLabel, placedButtons, playerScoresLabels, playerTileButtons);
            updatePlayerTiles();
        });
        controlGbc.gridy = 5;
        controlPanel.add(submitButton, controlGbc);
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

            // Set fixed size for each tile button
            playerTileButtons[i].setPreferredSize(tileButtonSize);
            playerTileButtons[i].setMinimumSize(tileButtonSize);
            playerTileButtons[i].setMaximumSize(tileButtonSize);

            int index = i;
            playerTileButtons[i].addActionListener(e -> Helpers.selectLetterFromTiles(index, playerTileButtons));
            tileGbc.gridx = i;
            tilePanel.add(playerTileButtons[i], tileGbc);
        }
    }
    /**
     * Updates the player's tile rack with new tiles and refreshes the display.
     */
    public void updatePlayerTiles() {
        List<Character> currentPlayerTiles = ScrabbleController.getCurrentPlayerTiles();
        if (currentPlayerTiles == null) {
            currentPlayerTiles = new ArrayList<>();
        }
        for (int i = 0; i < playerTileButtons.length; i++) {
            if (i < currentPlayerTiles.size()) {
                playerTileButtons[i].setText(String.valueOf(currentPlayerTiles.get(i)));
                playerTileButtons[i].setEnabled(true);
            } else {
                playerTileButtons[i].setText("");
                playerTileButtons[i].setEnabled(false);
            }
        }
    }
    /**
     * Updates the display of the board based on the current state of the game.
     */
    public void updateBoardDisplay() {
        for (int row = 0; row < boardButtons.length; row++) {
            for (int col = 0; col < boardButtons[row].length; col++) {
                char letter = ScrabbleController.board[row][col];
                if (letter != '\0') {
                    // Set the button's text to the letter from the board array if it exists
                    boardButtons[row][col].setText(String.valueOf(letter));
                } else {
                    // Clear the button's text if there's no letter in that position
                    boardButtons[row][col].setText("");
                }
            }
        }
    }
}
