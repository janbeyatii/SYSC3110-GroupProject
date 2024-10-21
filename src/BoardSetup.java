package src;

public class BoardSetup {
    public static final int BOARD_SIZE = 15;
    public char[][] board;

    // Constructor to initialize the 15x15 board
    public BoardSetup() {
        board = new char[BOARD_SIZE][BOARD_SIZE];
        initializeBoard();
    }

    // Initialize the board with empty spaces (or any placeholder, like '.')
    private void initializeBoard() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = '.';
            }
        }
    }

    // Display the current state of the board with row and column numbers
    public void displayBoard() {
        System.out.print("   "); // Leading spaces for row numbers

        // Print column numbers (1-15)
        for (int col = 0; col < BOARD_SIZE; col++) {
            System.out.printf("%2d ", col + 1);
        }
        System.out.println();

        // Print each row with row numbers (1-15) and board content
        for (int row = 0; row < BOARD_SIZE; row++) {
            System.out.printf("%2d ", row + 1); // Print row number
            for (int col = 0; col < BOARD_SIZE; col++) {
                System.out.print(board[row][col] + "  ");
            }
            System.out.println();
        }
    }

    public boolean placeWord(String word, int row, int col, char direction) {
        if (direction == 'H' || direction == 'h') {
            if (col + word.length() > BOARD_SIZE) return false; // Out of bounds check
            for (int i = 0; i < word.length(); i++) {
                board[row][col + i] = word.charAt(i);
            }
        } else if (direction == 'V' || direction == 'v') {
            if (row + word.length() > BOARD_SIZE) return false; // Out of bounds check
            for (int i = 0; i < word.length(); i++) {
                board[row + i][col] = word.charAt(i);
            }
        } else {
            return false; // Invalid direction
        }
        return true; // Word successfully placed
    }
}
