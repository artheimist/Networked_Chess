import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executors;

import javax.swing.*;

public class ChessController implements ChessInterface, ActionListener {

    private ChessModel chessModel = new ChessModel();

    private JFrame frame;
    private ChessView chessBoardPanel;
    private JButton resetBtn;
    private JButton serverBtn;
    private JButton clientBtn;

    private JLabel black,white,inTurn;
    private ServerSocket listener;
    private Socket socket;
    private PrintWriter printWriter;

    ChessController() {
        chessModel.reset();

        frame = new JFrame("Chess");
        frame.setSize(350, 430);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        chessBoardPanel = new ChessView(this);

        frame.add(chessBoardPanel, BorderLayout.CENTER);

        var buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        resetBtn = new JButton("Reset");
        resetBtn.addActionListener(this);
        buttonsPanel.add(resetBtn);

        serverBtn = new JButton("Create Game");
        buttonsPanel.add(serverBtn);
        serverBtn.addActionListener(this);

        clientBtn = new JButton("Join Game");
        buttonsPanel.add(clientBtn);
        clientBtn.addActionListener(this);

        frame.add(buttonsPanel, BorderLayout.PAGE_END);

        //label
        var blackPane= new JPanel(new FlowLayout(FlowLayout.CENTER));
        black= new JLabel("black :"+ chessModel.getBlack());
        inTurn= new JLabel("            WHITE TURN            ");
        white= new JLabel("white :"+chessModel.getWhite());
        blackPane.add(black);
        blackPane.add(inTurn);
        blackPane.add(white);


        //
        frame.add(blackPane, BorderLayout.PAGE_START);


        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                if (printWriter != null) printWriter.close();
                try {
                    if (listener != null) listener.close();
                    if (socket != null) socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    @Override
    public ChessPieces pieceAt(int col, int row) {
        return chessModel.pieceAt(col, row);
    }

    @Override
    public void movePiece(int fromCol, int fromRow, int toCol, int toRow) {
        chessModel.movePiece(fromCol, fromRow, toCol, toRow, frame);
        chessBoardPanel.repaint();
        if (printWriter != null) {
            printWriter.println(fromCol + "," + fromRow + "," + toCol + "," + toRow);
        }
        System.out.println("black :"+chessModel.getBlack()+" white :"+chessModel.getWhite());
        checkWin();
        changeTurn();
    }

    private void receiveMove(Scanner scanner) {
        while (scanner.hasNextLine()) {
            var moveStr = scanner.nextLine();
            System.out.println("chess move received: " + moveStr);
            var moveStrArr = moveStr.split(",");
            var fromCol = Integer.parseInt(moveStrArr[0]);
            var fromRow = Integer.parseInt(moveStrArr[1]);
            var toCol = Integer.parseInt(moveStrArr[2]);
            var toRow = Integer.parseInt(moveStrArr[3]);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    chessModel.movePiece(fromCol, fromRow, toCol, toRow,frame);
                    chessBoardPanel.repaint();
                }
            });
        }
    }

    private void runSocketServer(int port) {
        Executors.newFixedThreadPool(1).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    listener = new ServerSocket(port);
                    serverBtn.setEnabled(false);
                    clientBtn.setEnabled(false);
                    JOptionPane.showMessageDialog(frame, "listening on port " + port);
                    socket = listener.accept();
                    System.out.println("connected from " + socket.getInetAddress());
                    printWriter = new PrintWriter(socket.getOutputStream(), true);
                    var scanner = new Scanner(socket.getInputStream());
                    receiveMove(scanner);

                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(frame, "Failed " + port,"failed",JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }

    private void runSocketClient(String addres,int port) {
        try {
            socket = new Socket(addres, port);
            serverBtn.setEnabled(false);
            clientBtn.setEnabled(false);
            JOptionPane.showMessageDialog(frame, "connected to port " + port);
            var scanner = new Scanner(socket.getInputStream());
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            Executors.newFixedThreadPool(1).execute(new Runnable() {
                @Override
                public void run() {
                    receiveMove(scanner);
                }
            });
        } catch (IOException e1) {
           JOptionPane.showMessageDialog(frame,"Invalid Port", "failed", JOptionPane.WARNING_MESSAGE);
        }
    }
    public void checkWin(){
        black.setText("black :"+chessModel.getBlack());
        white.setText("white :"+chessModel.getWhite());
        if(chessModel.getWhite()==0){
            JOptionPane.showMessageDialog(frame," Black Wins !", " ! Game Over !",JOptionPane.OK_OPTION);
        }if(chessModel.getBlack()==0){
            JOptionPane.showMessageDialog(frame," White Wins !", " ! Game Over !",JOptionPane.OK_OPTION);
        }
    }

    public void changeTurn(){
        if(chessModel.getPlayerInTurn()==Player.WHITE){
            inTurn.setText("            WHITE TURN            ");
        }else{
            inTurn.setText("            BLACK TURN            ");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == resetBtn) {
            chessModel.reset();
            chessBoardPanel.repaint();
            try {
                if (listener != null) {
                    listener.close();
                }
                if (socket != null) {
                    socket.close();
                    JOptionPane.showMessageDialog(frame,"DISCONNECTED","Disconnected",JOptionPane.WARNING_MESSAGE);
                }
                serverBtn.setEnabled(true);
                clientBtn.setEnabled(true);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else if (e.getSource() == serverBtn) {
            int port= Integer.parseInt(JOptionPane.showInputDialog(frame, "Please Input Current Port to Create New Game","ex : 500"));
            frame.setTitle("Chess Server");
            runSocketServer(port);
        } else if (e.getSource() == clientBtn) {
            frame.setTitle("Chess Client");
            String address=(JOptionPane.showInputDialog(frame, "Please Input Destination Address","ex : localhost"));
            int port= Integer.parseInt(JOptionPane.showInputDialog(frame, "Please Input Destination Port","ex : 500"));
            runSocketClient(address,port);
        }
    }
}
