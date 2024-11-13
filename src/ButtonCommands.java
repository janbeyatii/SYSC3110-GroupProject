package src;

import GUI.ScrabbleController;
import GUI.ScrabbleView;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Set;

import static src.Helpers.*;

public class ButtonCommands {

    public static void submit(ScrabbleView view, JTextArea wordHistoryArea, JLabel turnLabel, ArrayList<JButton> placedButtons, JLabel[] playerScoresLabels, JButton[] playerTileButtons) {
        boolean isFirstTurn = ScrabbleController.getPlayerScore(0) == 0;

        if (!isWordPlacementValid(placedButtons, isFirstTurn)) {
            clear(placedButtons, playerTileButtons);
            return;
        }

        Set<String> uniqueWordsFormed = getAllWordsFormed(placedButtons);

        if (!areAllWordsValid(uniqueWordsFormed)) {
            clear(placedButtons, playerTileButtons);
            return;
        }

        updateScoresAndDisplayWords(uniqueWordsFormed, wordHistoryArea, playerScoresLabels);

        ScrabbleController.addPlacedTiles(placedButtons);
        placedButtons.clear();
        clear(placedButtons, playerTileButtons);
        view.updateBoardDisplay();
        view.updatePlayerTiles();

        ScrabbleController.switchToNextPlayer();
        turnLabel.setText("Turn: " + ScrabbleController.getCurrentPlayerName());
    }

    public static void pass(ArrayList<JButton> placedButtons, JButton[] playerTileButtons, JLabel turnLabel) {
        clear(placedButtons, playerTileButtons);
        ScrabbleController.switchToNextPlayer();
        turnLabel.setText("Turn: " + ScrabbleController.getCurrentPlayerName());
    }

    public static void clear(ArrayList<JButton> placedButtons, JButton[] playerTileButtons) {
        for (JButton button : placedButtons) {
            int row = (Integer) button.getClientProperty("row");
            int col = (Integer) button.getClientProperty("col");

            button.setText("");
            ScrabbleController.board[row][col] = '\0';
        }

        placedButtons.clear();

        for (JButton tileButton : playerTileButtons) {
            if (tileButton.getText() != null && !tileButton.getText().isEmpty()) {
                tileButton.setEnabled(true);
            }
        }
    }

}
