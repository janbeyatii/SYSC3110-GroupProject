import src.BoardSetup;
import src.DisplayBoard;
import src.WordPlacementLogic;
import src.WordValidity;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        BoardSetup boardSetup = new BoardSetup();
        DisplayBoard display = new DisplayBoard();
        WordPlacementLogic wordLogic = new WordPlacementLogic();

        // Load the word list
        String wordListFilePath = "wordLists/wordlist.txt";
        WordValidity.loadWordsFromFile(wordListFilePath);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            display.displayBoard(boardSetup.getBoard());
            System.out.println("Enter a word, row, column, and direction (H/V), or 'pass' to skip turn:");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("pass")) {
                System.out.println("Turn passed.");
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

            if (!wordLogic.placeWord(word, row, col, direction, boardSetup.getBoard())) {
                System.out.println("Invalid word placement. Try again.");
            }
        }
    }
}
