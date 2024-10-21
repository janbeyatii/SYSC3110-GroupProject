package src;

public class BoardSetup {
    public static final int BOARD_SIZE = 15;
    public char[][] board;

    // Initialize the board
    private void initializeBoard() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = '.';
            }
        }
    }

    // Constructor
    public BoardSetup() {
        board = new char[BOARD_SIZE][BOARD_SIZE];
        initializeBoard();
    }

    public char[][] getBoard() {
        return board;
    }
}
