package io.github.spigotrce.byteflow.server;

import io.github.spigotrce.byteflow.common.MessageUtils;
import io.github.spigotrce.byteflow.common.VersionConstants;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final FlowServer server;
    private final String authenticationToken;
    private InputStream inputStream;
    private OutputStream outputStream;

    public ClientHandler(Socket socket, FlowServer server, String authenticationToken) {
        this.socket = socket;
        this.server = server;
        this.authenticationToken = authenticationToken;
    }

    @Override
    public void run() {
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            if (VersionConstants.PVN != MessageUtils.readInt(inputStream)) {
                MessageUtils.writeUTF(outputStream, "INVALID_PVN");
                MessageUtils.writeInt(outputStream, VersionConstants.PVN);
                socket.close();
            }

            if (isValidToken(MessageUtils.readUTF(inputStream))) {
                MessageUtils.writeUTF(outputStream, "AUTH_SUCCESS");
                server.registerClient(this);

                while (!socket.isClosed())
                    server.broadcastMessage(MessageUtils.readMessage(inputStream), this);
            } else {
                MessageUtils.writeUTF(outputStream, "AUTH_FAIL");
                socket.close();
            }
        } catch (IOException e) {
            server.deregisterClient(this);
        }
    }

    private boolean isValidToken(String token) {
        return token.equals(authenticationToken);
    }

    public void sendMessage(byte[] data) throws IOException {
        MessageUtils.writeMessage(outputStream, data);
    }
}
