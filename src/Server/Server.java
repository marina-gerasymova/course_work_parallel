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
            int CLA_threadsAmount = Integer.parseInt(args[0]);
            THREADS_AMOUNT = CLA_threadsAmount > 1 ? CLA_threadsAmount : THREADS_AMOUNT;
        }
        System.out.println("Number of threads: " + THREADS_AMOUNT);
        IndexBuilder builder = new IndexBuilder("/datasets", THREADS_AMOUNT);
        try {
            long startTime = System.nanoTime();
            index = builder.buildIndex();
            long elapsedTime = System.nanoTime() - startTime;
            System.out.println("Total execution time in millis: "
                    + elapsedTime/1000000);
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
