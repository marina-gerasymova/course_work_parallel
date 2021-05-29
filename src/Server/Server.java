package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static ServerSocket serverSocket;
    private static int port = 8000;
    private static Index index;

    public static void main(String[] args) throws IOException {
        int THREADS_AMOUNT = 2;
        if (args.length > 0) {
            THREADS_AMOUNT = Integer.parseInt(args[0]);
        }
        System.out.println(System.getProperty("user.dir"));
        IndexBuilder builder = new IndexBuilder("/datasets", THREADS_AMOUNT);
        try {
            index = builder.buildIndex();
        } catch (Exception e) {
            e.printStackTrace();
        }
        serverSocket = new ServerSocket(port);
        System.out.println("Server is running on port " + port);

        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientThread clientThread = new ClientThread(clientSocket, index);
                clientThread.start();
            }
        } finally {
            kill();
        }
    }

    public static void kill() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
