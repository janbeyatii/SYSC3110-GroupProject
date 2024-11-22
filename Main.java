import GUI.*;
import src.WordValidity;
import javax.swing.*;
import java.util.ArrayList;

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
            JOptionPane.showMessageDialog(null,"Welcome to Scrabble Game\n" + "Created by:\n" +"Jan Beyati - 101186335\n" +
                    "Milad Zazai - 101185228\n" +
                    "Kaif Ali - 101180909\n");

            String wordListFilePath = "wordLists/wordlist.txt";
            WordValidity.loadWordsFromFile(wordListFilePath);
            System.out.println(ScrabbleController.getPlayerNames());

            ScrabbleController.initializeGameSettings();

            ArrayList<Character> playerTiles = ScrabbleController.getPlayerTiles();
            ScrabbleView view = new ScrabbleView(15, playerTiles);
            view.setVisible(true);
        });
    }
}