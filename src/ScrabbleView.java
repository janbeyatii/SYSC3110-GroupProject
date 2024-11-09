package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ScrabbleView extends JFrame {
    private JButton[][] boardButtons;
    private JLabel[] playerScores;
    private JPanel boardPanel, controlPanel, tilePanel;
    private JButton passButton, submitButton, clearButton, playertileButtons;

    public ScrabbleView(int boardSize) {
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
                    boardButtons[i][j].setBackground(Color.BLUE);  // Set middle button to blue
                    boardButtons[i][j].setOpaque(true);
                }
                gbc.gridx = j;
                gbc.gridy = i;
                boardPanel.add(boardButtons[i][j], gbc);
            }
        }
        // Initialize control panel for score and tiles with GridBagLayout
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

        playerScores = new JLabel[src.ScrabbleController.getPlayercount()];
        for (int i = 0; i < src.ScrabbleController.getPlayercount(); i++) {
            playerScores[i] = new JLabel(src.ScrabbleController.getPlayerNames().get(i) + " Score: 0");
            scoreGbc.gridy = i;
            scorePanel.add(playerScores[i], scoreGbc);
        }

        // Add score panel to control panel
        controlGbc.gridx = 0;
        controlGbc.gridy = 0;
        controlPanel.add(scorePanel, controlGbc);

        // Add buttons to control panel
        controlGbc.gridy = 1;
        passButton = new JButton("Pass");
        controlPanel.add(passButton, controlGbc);

        controlGbc.gridy = 2;
        clearButton = new JButton("Clear");
        controlPanel.add(clearButton, controlGbc);

        controlGbc.gridy = 3;
        submitButton = new JButton("Submit");
        controlPanel.add(submitButton, controlGbc);

        // Initialize player tiles panel with GridBagLayout
        tilePanel = new JPanel(new GridBagLayout());
        GridBagConstraints tileGbc = new GridBagConstraints();
        tileGbc.fill = GridBagConstraints.BOTH;
        tileGbc.weightx = 1.0;
        tileGbc.insets = new Insets(2, 2, 2, 2);

        for (int i = 0; i < 5; i++) {
            playertileButtons = new JButton();
            tileGbc.gridx = i;
            tilePanel.add(playertileButtons, tileGbc);
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

    public void addpassButtonListener(ActionListener listener) {
        passButton.addActionListener(listener);
    }
    public void addsubmitButtonListener(ActionListener listener) {
        submitButton.addActionListener(listener);
    }
    public JButton getBoardButton(int row, int col) {
        return boardButtons[row][col];
    }

    public void updateScore(int playerIndex, int score) {
        playerScores[playerIndex].setText(ScrabbleController.getPlayerNames().get(playerIndex + 1)+ " Score: " + score);
    }
    public static void main(String[] args) {
        new ScrabbleView(15);
    }
}