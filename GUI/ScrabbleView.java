package GUI;

import src.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ScrabbleView extends JFrame {
    private JPanel boardPanel;
    private JButton[][] boardButtons;
    private JPanel controlPanel;
    private JLabel[] playerScoresLabels;
    private JLabel turnLabel;
    private JButton passButton, clearButton, submitButton;
    private JPanel tilePanel;
    private JButton[] playerTileButtons;
    private JTextArea wordHistoryArea;  // New JTextArea for word history

    private String selectedLetter = null;  // Track selected letter
    private ArrayList<JButton> placedButtons = new ArrayList<>(); // Track placed buttons

    public ScrabbleView(int boardSize, ArrayList<Character> tileCharacters) {
        setTitle("Scrabble Game");
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
                boardButtons[i][j].addActionListener(e -> placeLetterOnBoard(row, col));
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
        passButton.addActionListener(e -> passTurn());
        controlGbc.gridy = 3;
        controlPanel.add(passButton, controlGbc);

        clearButton = new JButton("Clear");
        clearButton.setFont(new Font("Arial", Font.PLAIN, 14));
        clearButton.addActionListener(e -> clearPlacedLetters());
        controlGbc.gridy = 4;
        controlPanel.add(clearButton, controlGbc);

        submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Arial", Font.PLAIN, 14));
        submitButton.addActionListener(e -> submitWord());
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
            playerTileButtons[i].addActionListener(e -> selectLetterFromTiles(index));
            tileGbc.gridx = i;
            tilePanel.add(playerTileButtons[i], tileGbc);
        }

        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);
        add(tilePanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void selectLetterFromTiles(int index) {
        selectedLetter = playerTileButtons[index].getText();
        playerTileButtons[index].setEnabled(false);  // Disable the tile to show it's in use
    }

    private void placeLetterOnBoard(int row, int col) {
        if (selectedLetter != null && boardButtons[row][col].getText().isEmpty()) {
            boardButtons[row][col].setText(selectedLetter);
            placedButtons.add(boardButtons[row][col]);  // Track the button for clearing
            selectedLetter = null;
        } else {
            JOptionPane.showMessageDialog(this, "Select a letter first or choose an empty tile.");
        }
    }

    private void submitWord() {
        StringBuilder wordBuilder = new StringBuilder();
        for (JButton button : placedButtons) {
            wordBuilder.append(button.getText());
        }
        String word = wordBuilder.toString();

        if (WordValidity.isWordValid(word)) {
            // Use ScoreCalculation to calculate the score based on placed letters
            ScoreCalculation scoreCalculation = new ScoreCalculation(word, placedButtons);
            int score = scoreCalculation.getTotalScore();  // Calculate score using letter values

            // Update current player's score
            ScrabbleController.addScoreToCurrentPlayer(score);
            updateScores();

            // Add word to the history
            String playerName = ScrabbleController.getCurrentPlayerName();
            wordHistoryArea.append(playerName + ": " + word + " (" + score + " points)\n");

            // Switch to the next player
            ScrabbleController.switchToNextPlayer();
            turnLabel.setText("Turn: " + ScrabbleController.getCurrentPlayerName());
        } else {
            JOptionPane.showMessageDialog(this, "Invalid word. Try again.");
        }

        clearPlacedLetters();
    }

    private void passTurn() {
        clearPlacedLetters();  // Ensure no letters remain on the board
        ScrabbleController.switchToNextPlayer();
        turnLabel.setText("Turn: " + ScrabbleController.getCurrentPlayerName());
    }

    private void clearPlacedLetters() {
        // Clear the letters placed on the board during this turn
        for (JButton button : placedButtons) {
            button.setText("");
        }
        placedButtons.clear();

        // Re-enable the player's tile buttons
        resetTileButtons();
    }

    private void resetTileButtons() {
        for (JButton button : playerTileButtons) {
            button.setEnabled(true);
        }
    }

    private void updateScores() {
        for (int i = 0; i < ScrabbleController.getPlayercount(); i++) {
            playerScoresLabels[i].setText(ScrabbleController.getPlayerNames().get(i) + " Score: " + ScrabbleController.getPlayerScore(i));
        }
    }
}
