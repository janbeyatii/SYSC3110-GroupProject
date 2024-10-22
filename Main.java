import src.BoardSetup;
import src.DisplayBoard;
import src.WordPlacementLogic;
import src.WordValidity;
import src.Helpers;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import src.TileBag;

/**
 * The Main class is the entry point for the Scrabble game.
 * It sets up players, manages the game loop, handles word placement, and checks for winners.
 */
public class Main {

    /**
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

        System.out.println("Welcome to our Scrabble game!!");
        System.out.println("Made by Kaif Ali - 101180909");
        System.out.println("Made by Milad Zazai - 101185228");
        System.out.println("Made by Jan Beyati - 101186335");

        Scanner scanner = new Scanner(System.in);
        List<String> playerNames = new ArrayList<>();
        List<List<Character>> playerTiles = new ArrayList<>();
        TileBag tileBag = new TileBag();

        // Player setup
        Helpers.setupPlayers(scanner, playerNames, playerTiles, tileBag);

        BoardSetup boardSetup = new BoardSetup();
        DisplayBoard display = new DisplayBoard();
        WordPlacementLogic wordLogic = new WordPlacementLogic();
        Helpers helpers = new Helpers();

        // Load the word list from a file
        String wordListFilePath = "wordLists/wordlist.txt";
        WordValidity.loadWordsFromFile(wordListFilePath);

        int[] playerScores = new int[playerNames.size()];
        int currentPlayer = 0;

        // Main game loop
        while (true) {
            display.displayBoard(boardSetup.getBoard());

            // Show only the current player's tiles
            List<Character> currentPlayerTiles = playerTiles.get(currentPlayer);
            System.out.println(playerNames.get(currentPlayer) + "'s tiles: " + currentPlayerTiles);

            System.out.println(playerNames.get(currentPlayer) + ": Enter a word, row, column, and direction (H/V), or 'pass' to skip turn:");
            String input = scanner.nextLine().toUpperCase();

            // Player chooses to pass their turn
            if (input.equalsIgnoreCase("pass")) {
                System.out.println(playerNames.get(currentPlayer) + " passed the turn.");
                currentPlayer = (currentPlayer + 1) % playerNames.size(); // Move to the next player
                continue;
            }

            // Parse the input
            String[] inputs = input.split(" ");
            if (inputs.length != 4) {
                System.out.println("Invalid input. Please enter in the format: WORD ROW# COL# DIR(H/V)");
                continue;
            }

            String word = inputs[0];
            int row = Integer.parseInt(inputs[1]) - 1;
            int col = Integer.parseInt(inputs[2]) - 1;
            char direction = inputs[3].charAt(0);

            // Check if the current player has the necessary tiles to place the word
            if (!helpers.canPlaceWord(word, currentPlayerTiles)) {
                System.out.println("You don't have the tiles to place this word.");
                continue;
            }

            // Place the word on the board and calculate the score
            int score = wordLogic.placeWord(word, row, col, direction, boardSetup.getBoard(), currentPlayerTiles, tileBag);
            if (score != -1) { // Valid word placement
                playerScores[currentPlayer] += score;
                System.out.println(playerNames.get(currentPlayer) + "'s total score: " + playerScores[currentPlayer]);

                // Update the player's tiles after the word is placed
                helpers.updatePlayerTilesAfterMove(word, currentPlayerTiles, tileBag);

                // Check if the player has won by reaching 150 points
                if (playerScores[currentPlayer] >= 150) {
                    System.out.println(playerNames.get(currentPlayer) + " HAS WON! YAY!");
                    break;
                }

                // Move to the next player
                currentPlayer = (currentPlayer + 1) % playerNames.size();
            } else {
                System.out.println("Invalid word placement. Try again.");
            }
        }
    }
}
