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
        for (int i = 0; i < numPlayers; i++) {
            System.out.print("Enter the name of player " + (i + 1) + ": ");
            String playerName = scanner.nextLine();
            playerNames.add(playerName);
        }

        System.out.println("Players in the game: " + playerNames);

        TileBag tileBag = new TileBag();

        //drawing for each player

        for (String playerName : playerNames) {
            List<Character> playerTiles = tileBag.drawTiles(7);
            System.out.println(playerName + "'s tiles: " + playerTiles);
        }

        BoardSetup boardSetup = new BoardSetup();
        DisplayBoard display = new DisplayBoard();
        WordPlacementLogic wordLogic = new WordPlacementLogic();

        // Load the word list
        String wordListFilePath = "wordLists/wordlist.txt";
        WordValidity.loadWordsFromFile(wordListFilePath);

        // Track the total scores for each player
        int[] playerScores = new int[numPlayers];
        int currentPlayer = 0;

        while (true) {
            display.displayBoard(boardSetup.getBoard());

            // Show the prompt for the current player
            String currentPlayerName = playerNames.get(currentPlayer);
            System.out.println(currentPlayerName + ": Enter a word, row, column, and direction (H/V), or 'pass' to skip turn:");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("pass")) {
                System.out.println(currentPlayerName + " passed the turn.");
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

            // Call placeWord and get the score for the word placed
            int score = wordLogic.placeWord(word, row, col, direction, boardSetup.getBoard());
            if (score != -1) {
                playerScores[currentPlayer] += score; // Add to the current player's score
                System.out.println(currentPlayerName + "'s total score: " + playerScores[currentPlayer]);

                // Check if the player has reached the score limit
                if (playerScores[currentPlayer] >= 150) {
                    System.out.println(currentPlayerName + " has won the game with a total score of " + playerScores[currentPlayer] + "!");
                    break;
                }

                // Move to the next player
                currentPlayer = (currentPlayer + 1) % numPlayers;

            } else {
                System.out.println("Invalid word placement. Try again.");
            }
        }
    }
}
