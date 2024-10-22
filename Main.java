import src.BoardSetup;
import src.DisplayBoard;
import src.WordPlacementLogic;
import src.WordValidity;
import src.Helpers;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import src.TileBag;

public class Main {
    public static void main(String[] args) {

        System.out.println("Welcome to our Scrabble game!!");
        System.out.println("Made by Kaif Ali - 101180909");
        System.out.println("Made by Milad Zazai - 101185228");
        System.out.println("Made by Jan Beyati - 101186335");

        Scanner scanner = new Scanner(System.in);
        List<String> playerNames = new ArrayList<>();
        List<List<Character>> playerTiles = new ArrayList<>();
        TileBag tileBag = new TileBag();

        // player setup
        Helpers.setupPlayers(scanner, playerNames, playerTiles, tileBag);

        BoardSetup boardSetup = new BoardSetup();
        DisplayBoard display = new DisplayBoard();
        WordPlacementLogic wordLogic = new WordPlacementLogic();
        Helpers helpers = new Helpers();

        // Load the word list
        String wordListFilePath = "wordLists/wordlist.txt";
        WordValidity.loadWordsFromFile(wordListFilePath);

        int[] playerScores = new int[playerNames.size()];
        int currentPlayer = 0;

        while (true) {
            display.displayBoard(boardSetup.getBoard());

            // Show only the current player's tiles
            List<Character> currentPlayerTiles = playerTiles.get(currentPlayer);
            System.out.println(playerNames.get(currentPlayer) + "'s tiles: " + currentPlayerTiles);

            System.out.println(playerNames.get(currentPlayer) + ": Enter a word, row, column, and direction (H/V), or 'pass' to skip turn:");
            String input = scanner.nextLine().toUpperCase();

            if (input.equalsIgnoreCase("pass")) {
                System.out.println(playerNames.get(currentPlayer) + " passed the turn.");
                currentPlayer = (currentPlayer + 1) % playerNames.size(); // Move to the next player
                continue;
            }

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

            int score = wordLogic.placeWord(word, row, col, direction, boardSetup.getBoard(), currentPlayerTiles, tileBag);
            if (score != -1) {
                playerScores[currentPlayer] += score;
                System.out.println(playerNames.get(currentPlayer) + "'s total score: " + playerScores[currentPlayer]);

                // Update the current player's tiles after word placement
                helpers.updatePlayerTilesAfterMove(word, currentPlayerTiles, tileBag);

                if (playerScores[currentPlayer] >= 150) {
                    System.out.println(playerNames.get(currentPlayer) + " HAS WON! YAY!");
                    break;
                }

                currentPlayer = (currentPlayer + 1) % playerNames.size();
            } else {
                System.out.println("Invalid word placement. Try again.");
            }
        }
    }
}
