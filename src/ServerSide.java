import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerSide extends Thread {
    public static final int port =5000;
    private Socket socket;
    private ServerSocket listener;
    private BufferedReader input;
    private PrintWriter output;
    private Scanner scanner;

    public void startServer() {
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try {
            listener = new ServerSocket(5000);
            System.out.println("server is listening on port " + 5000);
            socket = listener.accept();
            System.out.println("connected from " + socket.getInetAddress());
            while(socket.isConnected()) {
                output = new PrintWriter(socket.getOutputStream(), true);
                scanner = new Scanner(socket.getInputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static  void main(String[] args){
        ServerSide s= new ServerSide();
        s.startServer();
    }
}
