package Server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

class ClientThread extends Thread {

    private Socket clientSocket;
    private Index index = null;
    private BufferedReader clientReader;
    private BufferedWriter serverWriter;

    ClientThread(Socket socket, Index index) {
        this.clientSocket = socket;
        this.index = index;
        try {
            clientReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            serverWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        System.out.println("New client " + clientSocket);

        while (true) {
            try {
                String clientInput = clientReader.readLine();
                getAndSendResult(clientInput);
            } catch (IOException e) {
                disconnectClient();
                break;
            }
        }
    }

    void getAndSendResult(String word) {
        try {
            Collection<String> invertedIndex = index.findInvertedIndex(word);
            if (invertedIndex == null) {
                serverWriter.write("word '" + word + "' doesn't appear in source files\n"); // сообщение пользователю
                serverWriter.flush();
                return;
            }
            String wordInfo = invertedIndex.stream().map(o -> o.toString()).collect( Collectors.joining(","));
            serverWriter.write(wordInfo + "\n");
            serverWriter.flush();
            System.out.println("Send '" + word + "' info to client");
        } catch (IOException e) {
            e.printStackTrace();
            disconnectClient();
        }
    }

    private void disconnectClient() {
        try {
            if(!clientSocket.isClosed()) {
                clientSocket.close();
                clientReader.close();
                serverWriter.close();
            }
        } catch (IOException ignored) {}

        System.out.println("Disconnected " + clientSocket);
    }
}
