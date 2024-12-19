package io.github.spigotrce.byteflow.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.*;

public class FlowServer {
    private final int port;
    private final String token;
    private final ExecutorService clientPool = Executors.newCachedThreadPool();
    private final ArrayList<ClientHandler> clients = new ArrayList<>();

    public FlowServer(int port, String token) {
        this.port = port;
        this.token = token;
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket, this, token);
                clientPool.submit(handler);
            }
        }
    }

    public void registerClient(ClientHandler handler) {
        clients.add(handler);
    }

    public void deregisterClient(ClientHandler handler) {
        clients.remove(handler);
    }

    public void broadcastMessage(byte[] data, ClientHandler excludeClient) throws IOException {
        for (ClientHandler handler : clients)
            if (handler != excludeClient)
                handler.sendMessage(data);
    }

    public void broadcastMessage(byte[] data) throws IOException {
        broadcastMessage(data, null);
    }
}
