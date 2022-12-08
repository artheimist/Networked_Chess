import java.awt.event.MouseEvent;

public interface ChessInterface {
    ChessPieces pieceAt(int col, int row);
    void movePiece(int fromCol, int fromRow, int toCol, int toRow);
}
