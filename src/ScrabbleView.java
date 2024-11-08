package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ScrabbleView extends JFrame {
    private JButton[][] boardButtons;
    private JButton tileButtons;
    private JLabel[] playerScores;
    private JPanel boardPanel, controlPanel, tilePanel;
    private JButton tileBagButton;
    int playercount = Integer.parseInt(JOptionPane.showInputDialog(this,"Number of players (2-4): "));
    public ScrabbleView(int boardSize, int numPlayers) {
        numPlayers = playercount;
        if (numPlayers > 4 || numPlayers < 2) {
                while (numPlayers > 4 || numPlayers < 2 ){
                    playercount = Integer.parseInt(JOptionPane.showInputDialog(this,"Number of players: "));
                    numPlayers = playercount;
                }
            }
        setTitle("Scrabble Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize board
        boardPanel = new JPanel(new GridLayout(boardSize, boardSize));
        boardButtons = new JButton[boardSize][boardSize];

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                boardButtons[i][j] = new JButton();
                boardPanel.add(boardButtons[i][j]);
            }
        }

        // Initialize control panel for score and tiles
        controlPanel = new JPanel(new GridLayout(2, 1));

        JPanel scorePanel = new JPanel(new GridLayout(numPlayers, 1));
        playerScores = new JLabel[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            playerScores[i] = new JLabel("Player " + (i + 1) + " Score: 0");
            scorePanel.add(playerScores[i]);
        }

        tileBagButton = new JButton("Draw Tile");

        controlPanel.add(scorePanel);
        controlPanel.add(tileBagButton);

        // Initialize player tiles
        tilePanel = new JPanel(new GridLayout(1, 5));
        for (int i = 0; i < 5; i++) {
            tileButtons = new JButton();
                tilePanel.add(tileButtons);
        }

        // Add panels to frame
        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);
        add(tilePanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    public void addBoardButtonListener(ActionListener listener) {
        for (int i = 0; i < boardButtons.length; i++) {
            for (int j = 0; j < boardButtons[i].length; j++) {
                boardButtons[i][j].addActionListener(listener);
            }
        }
    }

    public void addTileBagButtonListener(ActionListener listener) {
        tileBagButton.addActionListener(listener);
    }

    public JButton getBoardButton(int row, int col) {
        return boardButtons[row][col];
    }

    public void updateScore(int playerIndex, int score) {
        playerScores[playerIndex].setText("Player " + (playerIndex + 1) + " Score: " + score);
    }
    public static void main(String[] args) {
        new ScrabbleView(15, 0);
    }
}