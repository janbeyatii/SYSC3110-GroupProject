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

                        if (selectedCharacter != null && boardTile.getText().isEmpty()) {
                            if (ScrabbleController.isFirstMove()) {
                                if (ScrabbleController.isCenterPosition(row, col)) {
                                    boardTile.setText(selectedCharacter);
                                    ScrabbleController.clearSelectedCharacter();
                                    ScrabbleController.setFirstMoveDone();
                                } else {
                                    JOptionPane.showMessageDialog(null, "The first letter must be placed in the center (8,8)!");
                                }
                            } else {
                                if (ScrabbleController.isAdjacentToLetter(boardButtons, row, col)) {
                                    boardTile.setText(selectedCharacter);
                                    ScrabbleController.clearSelectedCharacter();
                                } else {
                                    JOptionPane.showMessageDialog(null, "Letters must be placed next to existing letters.");
                                }
                            }
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

        playertileButtons = new JButton[5];
        for (int i = 0; i < 5; i++) {
            playertileButtons[i] = new JButton();
            if (i < tileCharacters.size()) {
                playertileButtons[i].setText(tileCharacters.get(i).toString());
            }

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
        setVisible(true);
    }

    public static void main(String[] args) {
        ArrayList<Character> player1tiles = new ArrayList<>();
        player1tiles.add('A');
        player1tiles.add('B');
        player1tiles.add('C');
        player1tiles.add('D');
        player1tiles.add('E');

        new ScrabbleView(15, player1tiles);
    }
}
