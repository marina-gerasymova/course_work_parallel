package Client;

import java.io.*;
import java.net.Socket;

public class Client {

    static String ip = "localhost";
    static int port = 8000;
    static Socket clientSocket;
    private static BufferedWriter clientWriter;
    private static BufferedReader serverReader;
    private static BufferedReader in; // поток чтения из сокета
    private static BufferedWriter out; // поток чтения в сокет
    private static BufferedReader inputUser; // поток чтения с консоли

    public static void main(String[] args) {
        try {
            connectClient(ip, port);
            inputUser = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            activateClient();
        } catch (Exception e) {
            e.printStackTrace();
            disconnectClient();
        }
    }

    private static void connectClient(String ip, int port) throws Exception {
        System.out.println("Connecting to the server...");
        try {
            clientSocket = new Socket(ip, port);
        } catch (Exception e) {
            throw new Exception(e);
        }
        try {
            clientWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            serverReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println("Connected!");
        } catch (IOException ignored) {
            disconnectClient();
        }
    }

    public static void activateClient() throws Exception {
        BufferedReader clientReader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("Enter the word you want to find: ");
            String nickname = inputUser.readLine();
            if (nickname.equals("")) {
                continue;
            }
            out.write(nickname + "\n");
            out.flush();
            String response = in.readLine();
            System.out.println(response);
        }
    }

    private static void disconnectClient() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
                clientWriter.close();
                serverReader.close();
            }
        } catch (IOException ignored) {}

        System.out.println("Disconnected");
    }
}
