package src;

public class DisplayBoard {

    // Method to display the current state of the board with row and column numbers
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
