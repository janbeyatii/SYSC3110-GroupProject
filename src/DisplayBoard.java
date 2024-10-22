package src;

/**
 * Represents the functionality to display the current state of the Scrabble game board.
 * This class provides a method to print the board along with row and column numbers.
 */
public class DisplayBoard {

    /**
     * Default constructor for the DisplayBoard class.
     * Initializes the DisplayBoard object without any specific setup.
     */
    public DisplayBoard() {
        // Default constructor
    }

    /**
     * Displays the current state of the Scrabble board with row and column numbers.
     * The board is displayed as a grid where each cell is represented by a character.
     *
     * @param board a 2D character array representing the current Scrabble board.
     */
    public void displayBoard(char[][] board) {
        System.out.print("   "); // Leading spaces for row numbers

        // Print column numbers (1-15)
        for (int col = 0; col < board.length; col++) {
            System.out.printf("%2d ", col + 1);
        }
        System.out.println();

        // Print each row with row numbers (1-15) and board content
        for (int row = 0; row < board.length; row++) {
            System.out.printf("%2d ", row + 1);
            for (int col = 0; col < board[row].length; col++) {
                System.out.print(board[row][col] + "  ");
            }
            System.out.println();
        }
    }
}
