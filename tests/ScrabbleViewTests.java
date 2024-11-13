package tests;

import GUI.ScrabbleController;
import GUI.ScrabbleView;
import javax.swing.*;
import org.junit.Before;
import org.junit.Test;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class ScrabbleViewTests {
    private ScrabbleView scrabbleView;
    private ArrayList<Character> tileCharacters;

    @Before
    public void setUp() {
        tileCharacters = new ArrayList<>(List.of('A', 'B', 'C', 'D', 'E', 'F', 'G'));
        scrabbleView = new ScrabbleView(15, tileCharacters);
    }

    @Test
    public void testBoardInitialization() {
        assertNotNull("Board should be initialized", scrabbleView.boardButtons);
        assertEquals("Board should have 15 rows", 15, scrabbleView.boardButtons.length);
        assertEquals("Each row should have 15 columns", 15, scrabbleView.boardButtons[0].length);

        JButton centerButton = scrabbleView.boardButtons[7][7];
        assertEquals("Center tile should have light cyan color", new Color(173, 216, 230), centerButton.getBackground());

        Dimension expectedSize = new Dimension(40, 40);
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                assertEquals("Board button should have preferred size of 40x40", expectedSize, scrabbleView.boardButtons[i][j].getPreferredSize());
            }
        }
    }

    @Test
    public void testTilePanelInitialization() {
        assertEquals("Tile panel should contain 7 buttons", 7, scrabbleView.playerTileButtons.length);

        for (int i = 0; i < 7; i++) {
            assertEquals("Tile button should display correct character", String.valueOf(tileCharacters.get(i)), scrabbleView.playerTileButtons[i].getText());
            assertEquals("Tile button should have preferred size", new Dimension(40, 40), scrabbleView.playerTileButtons[i].getPreferredSize());
        }
    }

    @Test
    public void testControlButtonsInitialization() {
        // Verify control buttons are created and have the correct labels
        assertNotNull("Pass button should be initialized", scrabbleView.passButton);
        assertEquals("Pass button should have correct label", "Pass", scrabbleView.passButton.getText());

        assertNotNull("Clear button should be initialized", scrabbleView.clearButton);
        assertEquals("Clear button should have correct label", "Clear", scrabbleView.clearButton.getText());

        assertNotNull("Submit button should be initialized", scrabbleView.submitButton);
        assertEquals("Submit button should have correct label", "Submit", scrabbleView.submitButton.getText());
    }

    @Test
    public void testTurnLabelInitialization() {
        assertNotNull("Turn label should be initialized", scrabbleView.turnLabel);
        assertTrue("Turn label should start with 'Turn:'", scrabbleView.turnLabel.getText().startsWith("Turn:"));
    }

    @Test
    public void testUpdatePlayerTiles() {
        List<Character> newTiles = List.of('H', 'I', 'J', 'K', 'L', 'M', 'N');
        ScrabbleController.getPlayerTilesMap().put(ScrabbleController.getCurrentPlayerName(), new ArrayList<>(newTiles));
        scrabbleView.updatePlayerTiles();
        for (int i = 0; i < newTiles.size(); i++) {
            assertEquals("Player tile button should display updated tile", String.valueOf(newTiles.get(i)), scrabbleView.playerTileButtons[i].getText());
        }
    }

    @Test
    public void testUpdateBoardDisplay() {
        ScrabbleController.board[7][7] = 'A';
        ScrabbleController.board[7][8] = 'B';

        scrabbleView.updateBoardDisplay();

        assertEquals("Board button should display letter 'A'", "A", scrabbleView.boardButtons[7][7].getText());
        assertEquals("Board button should display letter 'B'", "B", scrabbleView.boardButtons[7][8].getText());
    }
}
