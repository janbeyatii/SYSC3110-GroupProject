package src;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ScrabbleView extends JFrame {
    private JPanel boardPanel;
    private JButton[][] boardButtons;
    private JPanel controlPanel;
    private JLabel[] playerScores;
    private JButton passButton, clearButton, submitButton;
    private JPanel tilePanel;
    private JButton[] playertileButtons;

    public ScrabbleView(int boardSize, ArrayList<Character> tileCharacters) {
        setTitle("Scrabble Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize board panel with GridBagLayout
        boardPanel = new JPanel(new GridBagLayout());
        boardButtons = new JButton[boardSize][boardSize];
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        int middle = boardSize / 2;

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                boardButtons[i][j] = new JButton();
                if (i == middle && j == middle) {
                    boardButtons[i][j].setBackground(Color.BLUE);
                    boardButtons[i][j].setOpaque(true);
                }

                int row = i;
                int col = j;
                boardButtons[i][j].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JButton boardTile = (JButton) e.getSource();
                        String selectedCharacter = ScrabbleController.getSelectedCharacter();

                        // Don't check for valid move on individual button click
                        if (selectedCharacter != null && boardTile.getText().isEmpty()) {
                            boardTile.setText(selectedCharacter);
                            ScrabbleController.clearSelectedCharacter();
                        }
                    }
                });

                gbc.gridx = j;
                gbc.gridy = i;
                boardPanel.add(boardButtons[i][j], gbc);
            }
        }

        // Control panel for player scores and buttons
        controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints controlGbc = new GridBagConstraints();
        controlGbc.fill = GridBagConstraints.HORIZONTAL;
        controlGbc.insets = new Insets(5, 5, 5, 5);
        controlGbc.weightx = 1.0;

        // Score panel with player scores
        JPanel scorePanel = new JPanel(new GridBagLayout());
        GridBagConstraints scoreGbc = new GridBagConstraints();
        scoreGbc.fill = GridBagConstraints.HORIZONTAL;
        scoreGbc.insets = new Insets(2, 2, 2, 2);
        scoreGbc.weightx = 1.0;

        playerScores = new JLabel[ScrabbleController.getPlayercount()];
        for (int i = 0; i < ScrabbleController.getPlayercount(); i++) {
            playerScores[i] = new JLabel(ScrabbleController.getPlayerNames().get(i) + " Score: 0");
            scoreGbc.gridy = i;
            scorePanel.add(playerScores[i], scoreGbc);
        }

        controlGbc.gridx = 0;
        controlGbc.gridy = 0;
        controlPanel.add(scorePanel, controlGbc);

        controlGbc.gridy = 1;
        passButton = new JButton("Pass");
        controlPanel.add(passButton, controlGbc);

        controlGbc.gridy = 2;
        clearButton = new JButton("Clear");
        controlPanel.add(clearButton, controlGbc);

        controlGbc.gridy = 3;
        submitButton = new JButton("Submit");
        controlPanel.add(submitButton, controlGbc);

        // Initialize tile panel for player's tiles
        tilePanel = new JPanel(new GridBagLayout());
        GridBagConstraints tileGbc = new GridBagConstraints();
        tileGbc.fill = GridBagConstraints.BOTH;
        tileGbc.weightx = 1.0;
        tileGbc.insets = new Insets(2, 2, 2, 2);

        playertileButtons = new JButton[7];
        for (int i = 0; i < 7; i++) {
            playertileButtons[i] = new JButton();
            playertileButtons[i].setText(ScrabbleController.playerTiles.get(i).toString());

            int index = i;
            playertileButtons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ScrabbleController.selectTile(playertileButtons[index]);
                }
            });

            tileGbc.gridx = i;
            tilePanel.add(playertileButtons[i], tileGbc);
        }

        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);
        add(tilePanel, BorderLayout.SOUTH);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Point> placedTiles = new ArrayList<>();

                // Collect the positions of all placed letters
                for (int i = 0; i < boardSize; i++) {
                    for (int j = 0; j < boardSize; j++) {
                        JButton boardTile = boardButtons[i][j];
                        if (!boardTile.getText().isEmpty()) {
                            placedTiles.add(new Point(i, j));  // Store the coordinates of the placed tile
                        }
                    }
                }

                if (placedTiles.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No letters have been placed.");
                    return;
                }

                // Now, check if the move is valid only when submit is clicked
                for (int i = 0; i < boardSize; i++) {
                    for (int j = 0; j < boardSize; j++) {
                        JButton boardTile = boardButtons[i][j];
                        if (!boardTile.getText().isEmpty()) {
                            String selectedCharacter = ScrabbleController.getSelectedCharacter();

                            // Adjacent letter check
                            if (!ScrabbleController.isFirstMove() && !ScrabbleController.isAdjacentToLetter(boardButtons, i, j)) {
                                JOptionPane.showMessageDialog(null, "Letters must be placed next to existing letters.");
                                return; // exit early if the move is invalid
                            }

                            // First move check
                            if (ScrabbleController.isFirstMove() && !ScrabbleController.isCenterPosition(i, j)) {
                                JOptionPane.showMessageDialog(null, "The first letter must be placed in the center (8,8)!");
                                return; // exit early if the move is invalid
                            }

                            // Call the controller method to check if the placed tiles are in a straight line
                            if (!ScrabbleController.areTilesInStraightLine(placedTiles)) {
                                JOptionPane.showMessageDialog(null, "The letters must be placed in a straight line: either horizontally or vertically.");
                                return;  // Exit early if the move is invalid
                            }

                            ScrabbleController.clearSelectedCharacter();
                            ScrabbleController.setFirstMoveDone();
                        }
                    }
                }
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        ArrayList<Character> playerTiles = ScrabbleController.getPlayerTiles();
        new ScrabbleView(15, playerTiles);
    }
}
