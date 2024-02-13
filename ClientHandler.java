import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

// ClientHandler class manages communication with an individual client
class ClientHandler implements Runnable {
    private Socket socket;
    private ChatServer server;
    private PrintWriter out;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String clientAddress = socket.getInetAddress().getHostName();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String msg;
            while ((msg = in.readLine()) != null) {
                System.out.println(clientAddress + ": " + msg);
                server.broadcastMessage(clientAddress + ": " + msg, this);
            }
        } catch (IOException e) {
            System.out.println(clientAddress + " disconnected.");
        } finally {
            server.removeClient(this);
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}