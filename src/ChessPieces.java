import java.awt.image.*;

enum Player {
    WHITE,
    BLACK
}

enum Rank {
    KING,
    QUEEN,
    KNIGHT,
    BISHOP,
    ROOK,
    PAWN
}

public class ChessPieces {

    private  int col;
    private  int row;
    private  Player player;
    private  Rank rank;
    private  String name;

    public ChessPieces(int col, int row, Player player, Rank rank, String name) {

        this.col = col;
        this.row = row;
        this.player = player;
        this.rank = rank;
        this.name = name;
    }
    public int getCol() {
        return col;
    }

    public String getName() {
        return name;
    }

    public int getRow() {
        return row;
    }

    public Rank getRank() {
        return rank;
    }

    public Player getPlayer() {
        return player;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRow(int row) {
        this.row = row;
    }
}
