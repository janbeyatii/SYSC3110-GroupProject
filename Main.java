import GUI.*;
import src.WordValidity;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The Main class is the entry point for the Scrabble game.
 * It sets up players, manages the game loop, handles word placement, and checks for winners.
 */
public class Main {

    /**s
     * Default constructor for the Main class.
     * Initializes the game without any specific setup.
     */
    public Main() {
        // Default constructor
    }

    /**
     * The main method that runs the Scrabble game.
     * It initializes the board, players, word list, and manages the game flow.
     *
     * @param args command-line arguments (not used in this game).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null,"Welcome to Scrabble Game\n");

            String wordListFilePath = "resources/wordlist.txt";
            WordValidity.loadWordsFromFile(wordListFilePath);

            // Initialize game settings and players
            ScrabbleController.initializeGameSettings();

            // Fetch the first player's tiles from playerTilesMap
            List<Character> playerTiles = ScrabbleController.getCurrentPlayerTiles();

            // Initialize the view with the first player's tiles
            ScrabbleView view = new ScrabbleView(15, new ArrayList<>(playerTiles));
            view.setVisible(true);
        });
    }
}