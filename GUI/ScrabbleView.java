package GUI;

import src.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ScrabbleView extends JFrame {
    private JPanel boardPanel;
    public JButton[][] boardButtons;
    private JPanel controlPanel;
    private JLabel[] playerScoresLabels;
    private JLabel turnLabel;
    private JButton passButton, clearButton, submitButton;
    private JPanel tilePanel;
    private JButton[] playerTileButtons;
    private JTextArea wordHistoryArea;  // New JTextArea for word history

    private ArrayList<JButton> placedButtons = new ArrayList<>(); // Track placed buttons

    public ScrabbleView(int boardSize, ArrayList<Character> tileCharacters) {
        setTitle("SYSC3110 Group20 Scrabble Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize game settings (player count and names)
        ScrabbleController.initializeGameSettings();

        // Initialize board panel
        boardPanel = new JPanel(new GridBagLayout());
        boardButtons = new JButton[boardSize][boardSize];
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        int middle = boardSize / 2;
        Dimension buttonSize = new Dimension(40, 40);  // Fixed size for tiles

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                boardButtons[i][j] = new JButton();

                // Set the row and column properties
                boardButtons[i][j].putClientProperty("row", i);
                boardButtons[i][j].putClientProperty("col", j);

                boardButtons[i][j].setPreferredSize(buttonSize);
                boardButtons[i][j].setMinimumSize(buttonSize);
                boardButtons[i][j].setMaximumSize(buttonSize);
                boardButtons[i][j].setFont(new Font("Arial", Font.BOLD, 18)); // Bold, larger font
                boardButtons[i][j].setHorizontalAlignment(SwingConstants.CENTER); // Center text

                if (i == middle && j == middle) {
                    boardButtons[i][j].setBackground(new Color(173, 216, 230)); // Light cyan color for center
                } else {
                    boardButtons[i][j].setBackground(Color.LIGHT_GRAY); // Default background color
                }

                boardButtons[i][j].setOpaque(true);
                boardButtons[i][j].setBorder(BorderFactory.createLineBorder(Color.GRAY)); // Add a border for better look

                final int row = i;
                final int col = j;
                boardButtons[i][j].addActionListener(e -> Helpers.placeLetterOnBoard(row, col, boardButtons, placedButtons));

                gbc.gridx = j;
                gbc.gridy = i;
                boardPanel.add(boardButtons[i][j], gbc);
            }
        }

        // Initialize control panel
        controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints controlGbc = new GridBagConstraints();
        controlGbc.fill = GridBagConstraints.HORIZONTAL;
        controlGbc.insets = new Insets(5, 5, 5, 5);
        controlGbc.weightx = 1.0;

        // Place word history area at the top
        controlGbc.gridy = 0;
        wordHistoryArea = new JTextArea(10, 20);  // 10 rows, 20 columns
        wordHistoryArea.setEditable(false);  // Make it read-only
        wordHistoryArea.setLineWrap(true);
        wordHistoryArea.setWrapStyleWord(true);
        wordHistoryArea.setBorder(BorderFactory.createTitledBorder("Words Placed"));
        controlPanel.add(new JScrollPane(wordHistoryArea), controlGbc);  // Add with scroll pane

        // Player scores panel (position below word history area)
        JPanel scorePanel = new JPanel(new GridBagLayout());
        playerScoresLabels = new JLabel[ScrabbleController.getPlayercount()];
        for (int i = 0; i < ScrabbleController.getPlayercount(); i++) {
            playerScoresLabels[i] = new JLabel(ScrabbleController.getPlayerNames().get(i) + " Score: 0");
            playerScoresLabels[i].setFont(new Font("Arial", Font.PLAIN, 14)); // Consistent font for labels
            GridBagConstraints scoreGbc = new GridBagConstraints();
            scoreGbc.gridy = i;
            scorePanel.add(playerScoresLabels[i], scoreGbc);
        }
        controlGbc.gridy = 1;
        controlPanel.add(scorePanel, controlGbc);

        // Turn display
        turnLabel = new JLabel("Turn: " + ScrabbleController.getCurrentPlayerName());
        turnLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Slightly bold font for turn label
        controlGbc.gridy = 2;
        controlPanel.add(turnLabel, controlGbc);

        // Control buttons
        passButton = new JButton("Pass");
        passButton.setFont(new Font("Arial", Font.PLAIN, 14)); // Font for control buttons
        passButton.addActionListener(e -> {
            Helpers.passTurn(placedButtons, playerTileButtons, turnLabel);
            updatePlayerTiles(); // Refresh the tiles for the next player after passing the turn
        });
        controlGbc.gridy = 3;
        controlPanel.add(passButton, controlGbc);

        clearButton = new JButton("Clear");
        clearButton.setFont(new Font("Arial", Font.PLAIN, 14));
        clearButton.addActionListener(_ -> Helpers.clearPlacedLetters(placedButtons, playerTileButtons));
        controlGbc.gridy = 4;
        controlPanel.add(clearButton, controlGbc);

        submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Arial", Font.PLAIN, 14));
        submitButton.addActionListener(_ -> {
            Helpers.submitWord(this, wordHistoryArea, turnLabel, placedButtons, playerScoresLabels, playerTileButtons);
            updatePlayerTiles(); // Refresh the tiles for the current player after submitting a word
        });
        controlGbc.gridy = 5;
        controlPanel.add(submitButton, controlGbc);

        // Tile panel for player's tiles
        tilePanel = new JPanel(new GridBagLayout());
        GridBagConstraints tileGbc = new GridBagConstraints();
        tileGbc.fill = GridBagConstraints.BOTH;
        tileGbc.weightx = 1.0;
        tileGbc.insets = new Insets(2, 2, 2, 2);

        playerTileButtons = new JButton[7];
        for (int i = 0; i < 7; i++) {
            playerTileButtons[i] = new JButton(String.valueOf(tileCharacters.get(i)));
            playerTileButtons[i].setFont(new Font("Arial", Font.BOLD, 18)); // Bold font for player tiles
            playerTileButtons[i].setPreferredSize(new Dimension(40, 40)); // Fixed size for tile buttons
            int index = i;
            playerTileButtons[i].addActionListener(e -> Helpers.selectLetterFromTiles(index, playerTileButtons));
            tileGbc.gridx = i;
            tilePanel.add(playerTileButtons[i], tileGbc);
        }

        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);
        add(tilePanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    public void updatePlayerTiles() {
        List<Character> currentPlayerTiles = ScrabbleController.getCurrentPlayerTiles();
        if (currentPlayerTiles == null) {
            currentPlayerTiles = new ArrayList<>();
        }
        for (int i = 0; i < playerTileButtons.length; i++) {
            if (i < currentPlayerTiles.size()) {
                playerTileButtons[i].setText(String.valueOf(currentPlayerTiles.get(i)));
                playerTileButtons[i].setEnabled(true); // Enable buttons that have tiles
            } else {
                playerTileButtons[i].setText(""); // Clear buttons without tiles
                playerTileButtons[i].setEnabled(false);
            }
        }
    }

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
