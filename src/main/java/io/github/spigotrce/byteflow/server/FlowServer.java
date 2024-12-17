package io.github.spigotrce.byteflow.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.*;

public class FlowServer {
    private final int port;
    private final String authenticationToken;
    private final ExecutorService clientPool = Executors.newCachedThreadPool();
    private final ArrayList<ClientHandler> clients = new ArrayList<>();

    public FlowServer(int port, String authenticationToken) {
        this.port = port;
        this.authenticationToken = authenticationToken;
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket, this, authenticationToken);
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

    public void broadcastMessage(byte[] data) throws IOException {
        for (ClientHandler handler : clients)
            handler.sendMessage(data);
    }
}
