
import java.util.HashSet;
import java.util.Set;

public class ChessModel {
    private Set<ChessPieces> piecesBox = new HashSet<ChessPieces>();
    private Player playerInTurn = Player.WHITE;

    private int black=0,white=0;

    void reset() {
        piecesBox.removeAll(piecesBox);

        for (int i = 0; i < 2; i++) {
            piecesBox.add(new ChessPieces(0 + i * 7, 7, Player.BLACK, Rank.ROOK, ChessName.bRook));
            piecesBox.add(new ChessPieces(0 + i * 7, 0, Player.WHITE, Rank.ROOK, ChessName.wRook));

            piecesBox.add(new ChessPieces(1 + i * 5, 7, Player.BLACK, Rank.KNIGHT, ChessName.bKnight));
            piecesBox.add(new ChessPieces(1 + i * 5, 0, Player.WHITE, Rank.KNIGHT, ChessName.wKnight));

            piecesBox.add(new ChessPieces(2 + i * 3, 7, Player.BLACK, Rank.BISHOP, ChessName.bBishop));
            piecesBox.add(new ChessPieces(2 + i * 3, 0, Player.WHITE, Rank.BISHOP, ChessName.wBishop));
        }

        for (int i = 0; i < 8; i++) {
            piecesBox.add(new ChessPieces(i, 6, Player.BLACK, Rank.PAWN, ChessName.bPawn));
            piecesBox.add(new ChessPieces(i, 1, Player.WHITE, Rank.PAWN, ChessName.wPawn));
        }

        piecesBox.add(new ChessPieces(3, 7, Player.BLACK, Rank.QUEEN, ChessName.bQueen));
        piecesBox.add(new ChessPieces(3, 0, Player.WHITE, Rank.QUEEN, ChessName.wQueen));
        piecesBox.add(new ChessPieces(4, 7, Player.BLACK, Rank.KING, ChessName.bKing));
        piecesBox.add(new ChessPieces(4, 0, Player.WHITE, Rank.KING, ChessName.wKing));
        countPieces();
        playerInTurn = Player.WHITE;
    }

    void movePiece(int fromCol, int fromRow, int toCol, int toRow) {
        ChessPieces movingPiece = pieceAt(fromCol, fromRow);
        if (movingPiece == null || fromCol == toCol && fromRow == toRow) {
            return;
        }

        ChessPieces target = pieceAt(toCol, toRow);
        if (target != null) {
            if (target.getPlayer() == movingPiece.getPlayer()) {
                return;
            } else {
                if(target.getPlayer()==Player.WHITE){
                    white--;
                }else{
                    black--;
                }
                piecesBox.remove(target);
            }
        }

        piecesBox.remove(movingPiece);
        piecesBox.add(new ChessPieces(toCol, toRow, movingPiece.getPlayer(), movingPiece.getRank(), movingPiece.getName()));
        playerInTurn = playerInTurn == Player.WHITE ? Player.BLACK : Player.WHITE;
    }

    ChessPieces pieceAt(int col, int row) {
        for (ChessPieces chessPiece : piecesBox) {
            if (chessPiece.getCol() == col && chessPiece.getRow() == row) {
                return chessPiece;
            }
        }
        return null;
    }
    public void countPieces(){
        for (ChessPieces pieces: piecesBox){
            if(pieces.getPlayer()==Player.WHITE){
                white++;
            }else {
                black++;
            }
        }
    }
    public void rules(){

    }
    public int getBlack() {
        return black;
    }

    public int getWhite() {
        return white;
    }

    @Override
    public String toString() {
        String desc = "";

        for (int row = 7; row >= 0; row--) {
            desc += "" + row;
            for (int col = 0; col < 8; col++) {
                ChessPieces p = pieceAt(col, row);
                if (p == null) {
                    desc += " .";
                } else {
                    desc += " ";
                    switch (p.getRank()) {
                        case KING:
                            desc += p.getPlayer() == Player.WHITE ? "k" : "K";
                            break;
                        case QUEEN:
                            desc += p.getPlayer() == Player.WHITE ? "q" : "Q";
                            break;
                        case BISHOP:
                            desc += p.getPlayer() == Player.WHITE ? "b" : "B";
                            break;
                        case ROOK:
                            desc += p.getPlayer() == Player.WHITE ? "r" : "R";
                            break;
                        case KNIGHT:
                            desc += p.getPlayer() == Player.WHITE ? "n" : "N";
                            break;
                        case PAWN:
                            desc += p.getPlayer() == Player.WHITE ? "p" : "P";
                            break;
                    }
                }
            }
            desc += "\n";
        }
        desc += "  0 1 2 3 4 5 6 7";

        return desc;
    }
}
