import src.BoardSetup;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        BoardSetup board = new BoardSetup();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            board.displayBoard();
            System.out.println("Enter a word, row, column, and direction (H/V), or 'pass' to skip turn:");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("pass")) {
                System.out.println("Turn passed.");
                continue;
            }

            String[] inputs = input.split(" ");
            if (inputs.length != 4) {
                System.out.println("Invalid input. Please enter in the format: word row col direction (H/V)");
                continue;
            }

            String word = inputs[0];
            int row = Integer.parseInt(inputs[1]) - 1; // Adjust to zero-based index
            int col = Integer.parseInt(inputs[2]) - 1; // Adjust to zero-based index
            char direction = inputs[3].charAt(0);

            if (!board.placeWord(word, row, col, direction)) {
                System.out.println("Invalid word placement. Try again.");
            }
        }
    }
}
