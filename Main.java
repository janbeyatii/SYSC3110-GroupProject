import src.BoardSetup;
import src.DisplayBoard;
import src.WordPlacementLogic;
import src.WordValidity;
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

        int numPlayers = 0;
        while (numPlayers < 2 || numPlayers > 4) {
            try {
                System.out.print("Enter the number of players (2-4): ");
                numPlayers = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character after entering the number
                if (numPlayers < 2 || numPlayers > 4) {
                    System.out.println("Invalid number of players. Please enter a number between 2 and 4 inclusive.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number between 2 and 4.");
                scanner.nextLine(); // Clear the invalid input from the scanner
            }
        }

        List<String> playerNames = new ArrayList<>();
        List<List<Character>> playerTiles = new ArrayList<>(); // Store each player's tiles
        TileBag tileBag = new TileBag();

        for (int i = 0; i < numPlayers; i++) {
            System.out.print("Enter the name of player " + (i + 1) + ": ");
            String playerName = scanner.nextLine();
            playerNames.add(playerName);
            playerTiles.add(tileBag.drawTiles(7)); // Draw 7 tiles for each player
        }

        System.out.println("Players in the game: " + playerNames);

        BoardSetup boardSetup = new BoardSetup();
        DisplayBoard display = new DisplayBoard();
        WordPlacementLogic wordLogic = new WordPlacementLogic();

        // Load the word list
        String wordListFilePath = "wordLists/wordlist.txt";
        WordValidity.loadWordsFromFile(wordListFilePath);

        int[] playerScores = new int[numPlayers];
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
                currentPlayer = (currentPlayer + 1) % numPlayers; // Move to the next player
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
            if (!wordLogic.canPlaceWord(word, currentPlayerTiles)) {
                System.out.println("You don't have the tiles to place this word.");
                continue;
            }

            int score = wordLogic.placeWord(word, row, col, direction, boardSetup.getBoard(), currentPlayerTiles, tileBag);
            if (score != -1) {
                playerScores[currentPlayer] += score;
                System.out.println(playerNames.get(currentPlayer) + "'s total score: " + playerScores[currentPlayer]);

                // Update the current player's tiles after word placement
                wordLogic.updatePlayerTilesAfterMove(word, currentPlayerTiles, tileBag);

                if (playerScores[currentPlayer] >= 150) {
                    System.out.println(playerNames.get(currentPlayer) + " HAS WON! YAY!");
                    break;
                }

                currentPlayer = (currentPlayer + 1) % numPlayers;
            } else {
                System.out.println("Invalid word placement. Try again.");
            }
        }
    }
}
