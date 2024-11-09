package src;


import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;


public class ScrabbleController {

    static int playercount = Integer.parseInt(JOptionPane.showInputDialog("Number of players (2-4): "));
    static ArrayList<String> playerNames = new ArrayList<>();
    private static boolean firstMove = true;
    private static final int BOARD_SIZE = 15;
    private static final int MIDDLE_POSITION = BOARD_SIZE / 2;
    private static String selectedCharacter = null;
    private static JButton selectedButton = null;
    private static TileBag tileBag = new TileBag();
    private static ArrayList<Character> playerTiles;

    public static ArrayList<Character> getPlayerTiles() {

        playerTiles = new ArrayList<>(tileBag.drawTiles(7));
        return playerTiles;
    }

    public static int getPlayercount() {
        if (playercount > 4 || playercount < 2) {
                while (playercount > 4 || playercount < 2 ){
                    playercount = Integer.parseInt(JOptionPane.showInputDialog("Number of players (2-4): "));
                }
            }
        return playercount;
    }
    public static ArrayList<String> getPlayerNames() {
        String player = JOptionPane.showInputDialog("Player Name: ");
        playerNames.add(player);
        return playerNames;
    }

        public static boolean isFirstMove() {
            return firstMove;
        }

        public static void setFirstMoveDone() {
            firstMove = false;
        }

        public static boolean isCenterPosition(int row, int col) {
            return row == MIDDLE_POSITION && col == MIDDLE_POSITION;
        }

        public static boolean isAdjacentToLetter(JButton[][] boardButtons, int row, int col) {
            // Check adjacent positions for a placed letter
            if (row > 0 && !boardButtons[row - 1][col].getText().isEmpty()) return true;
            if (row < BOARD_SIZE - 1 && !boardButtons[row + 1][col].getText().isEmpty()) return true;
            if (col > 0 && !boardButtons[row][col - 1].getText().isEmpty()) return true;
            if (col < BOARD_SIZE - 1 && !boardButtons[row][col + 1].getText().isEmpty()) return true;

            return false;
        }

        // Selects a new character and places the previous one back if necessary
        public static void selectTile(JButton tileButton) {
            String newCharacter = tileButton.getText();

            // Check if there is already a selected tile
            if (selectedCharacter != null && selectedButton != null) {
                // Place the previous character back to its button
                selectedButton.setText(selectedCharacter);
            }

            // Pick up the new character and clear it from the button
            selectedCharacter = newCharacter;
            selectedButton = tileButton;
            tileButton.setText("");  // Clear the character from the button to "pick it up"
        }

        // Get the currently selected character
        public static String getSelectedCharacter() {
            return selectedCharacter;
        }

        // Clear the selected character after placing it
        public static void clearSelectedCharacter() {
            selectedCharacter = null;
            selectedButton = null;
        }

    }


