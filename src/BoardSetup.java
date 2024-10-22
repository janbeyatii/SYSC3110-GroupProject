package src;

/**
 * Represents the setup of the Scrabble game board.
 * It initializes a 15x15 board for the game.
 */
public class BoardSetup {

    /**
     * The size of the Scrabble board, which is 15x15.
     */
    public static final int BOARD_SIZE = 15;

    /**
     * A 2D array representing the Scrabble board.
     * Each cell contains a character representing either a tile or an empty space.
     */
    public char[][] board;

    /**
     * Initializes the board by setting all positions to empty (represented by '.').
     */
    private void initializeBoard() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = '.';
            }
        }
    }

    /**
     * Constructs a new BoardSetup object and initializes the board.
     * The board is created as a 15x15 grid of empty spaces.
     */
    public BoardSetup() {
        board = new char[BOARD_SIZE][BOARD_SIZE];
        initializeBoard();
    }

    /**
     * Retrieves the current state of the Scrabble board.
     *
     * @return the 2D character array representing the Scrabble board.
     */
    public char[][] getBoard() {
        return board;
    }
}
