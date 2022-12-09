import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ChessView extends JPanel implements MouseListener, MouseMotionListener {

    private ChessInterface chessInterface;

    private double scaleFactor = 1;
    private int originX = -1;
    private int originY = -1;
    private int cellSide = -1;

    private Map<String, Image> keyNameValueImage = new HashMap<String, Image>();
    private int fromCol = -1;
    private int fromRow = -1;
    private ChessPieces movingPiece;
    private Point movingPiecePoint;

    ChessView(ChessInterface chessInterface) {
        this.chessInterface = chessInterface;

        String[] imageNames = {
                ChessName.bBishop,
                ChessName.wBishop,
                ChessName.bKing,
                ChessName.wKing,
                ChessName.bKnight,
                ChessName.wKnight,
                ChessName.bPawn,
                ChessName.wPawn,
                ChessName.bQueen,
                ChessName.wQueen,
                ChessName.bRook,
                ChessName.wRook,
        };

        try {
            for (String imgNm : imageNames) {
                Image img = loadImage(imgNm + ".png");
                keyNameValueImage.put(imgNm, img);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
     public void paint(Graphics g) {
        int smaller = Math.min(getSize().width, getSize().height);
        cellSide = (int) (((double)smaller) * scaleFactor / 8);
        originX = (getSize().width - 8 * cellSide) / 2;
        originY = (getSize().height - 8 * cellSide) / 2;

        Graphics2D g2 = (Graphics2D)g;

        drawBoard(g2);
        drawPieces(g2);
    }

    private void drawPieces(Graphics2D g2) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPieces p = chessInterface.pieceAt(col, row);
                if (p != null && p != movingPiece) {
                    drawImage(g2, col, row, p.getName());
                }
            }
        }

        if (movingPiece != null && movingPiecePoint != null) {
            Image img = keyNameValueImage.get(movingPiece.getName());
            g2.drawImage(img, movingPiecePoint.x - cellSide/2, movingPiecePoint.y - cellSide/2, cellSide, cellSide, null);
        }
    }

    private void drawImage(Graphics2D g2, int col, int row, String imgName) {
        Image img = keyNameValueImage.get(imgName);
        g2.drawImage(img, originX + col * cellSide, originY + (7 - row) * cellSide, cellSide, cellSide, null);
    }

    private Image loadImage(String imgFileName) throws Exception  {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resURL = classLoader.getResource(imgFileName);
        if (resURL == null) {
            return null;
        } else {
            File imgFile = new File(resURL.toURI());
            return ImageIO.read(imgFile);
        }
    }

    private void drawBoard(Graphics2D g2) {
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 4; i++) {
                drawSquare(g2, 0 + 2 * i, 0 + 2 * j, true);
                drawSquare(g2, 1 + 2 * i, 1 + 2 * j, true);

                drawSquare(g2, 1 + 2 * i, 0 + 2 * j, false);
                drawSquare(g2, 0 + 2 * i, 1 + 2 * j, false);
            }
        }
    }

    private void drawSquare(Graphics2D g2, int col, int row, boolean light) {
        g2.setColor(light ? Color.white : Color.gray);
        g2.fillRect(originX + col * cellSide, originY + row * cellSide, cellSide, cellSide);
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        fromCol = (e.getPoint().x - originX) / cellSide;
        fromRow = 7 - (e.getPoint().y - originY) / cellSide;
        movingPiece = chessInterface.pieceAt(fromCol, fromRow);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int col = (e.getPoint().x - originX) / cellSide;
        int row = 7 - (e.getPoint().y - originY) / cellSide;

        if (fromCol != col || fromRow != row) {
            chessInterface.movePiece(fromCol, fromRow, col, row);
        }

        movingPiece = null;
        movingPiecePoint = null;
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {
        movingPiecePoint = e.getPoint();
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {}
}
