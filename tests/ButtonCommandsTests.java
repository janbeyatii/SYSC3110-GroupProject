package tests;

import GUI.ScrabbleController;
import GUI.ScrabbleView;
import src.ButtonCommands;
import javax.swing.*;
import org.junit.Before;
import org.junit.Test;
import src.Helpers;
import src.WordValidity;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ButtonCommandsTests {

    private ScrabbleView scrabbleView;
    private ArrayList<JButton> placedButtons;
    private JTextArea wordHistoryArea;
    private JLabel turnLabel;
    private JLabel[] playerScoresLabels;
    private JButton[] playerTileButtons;

    @Before
    public void setUp() {

        scrabbleView = new ScrabbleView(15, new ArrayList<>(List.of('A', 'B', 'C', 'D', 'E', 'F', 'G')));
        placedButtons = new ArrayList<>();
        wordHistoryArea = new JTextArea();
        turnLabel = new JLabel();

        int playerCount = 2; // Set player count for testing
        ScrabbleController.getPlayerNames();

        playerScoresLabels = new JLabel[playerCount];
        for (int i = 0; i < playerCount; i++) {
            playerScoresLabels[i] = new JLabel("Player " + (i + 1) + " Score: 0");
        }

        playerTileButtons = new JButton[7];
        for (int i = 0; i < 7; i++) {
            playerTileButtons[i] = new JButton("A");
        }

        ScrabbleController.initializeGameSettings();
    }


    @Test
    public void testClearPlacedTiles() {
        // Place a letter on the board
        JButton button = new JButton("A");
        button.putClientProperty("row", 7);
        button.putClientProperty("col", 7);
        button.setText("A");
        placedButtons.add(button);

        ScrabbleController.board[7][7] = 'A';

        ButtonCommands.clear(playerTileButtons);

        assertEquals("Board position should be cleared after using clear", "", scrabbleView.boardButtons[7][7].getText());

        assertTrue("Placed buttons list should be cleared", placedButtons.isEmpty());

        for (JButton tileButton : playerTileButtons) {
            assertTrue("Tile buttons should be re-enabled after clear", tileButton.isEnabled());
        }
    }
}
