package src;

public class PremiumSquare {
    private int row;
    private int col;
    private String type;

    public PremiumSquare(int row, int col, String type) {
        this.row = row;
        this.col = col;
        this.type = type;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public String getType() {
        return type;
    }
}
