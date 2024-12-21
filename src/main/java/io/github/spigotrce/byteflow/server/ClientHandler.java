package io.github.spigotrce.byteflow.server;

import io.github.spigotrce.byteflow.common.IOUtils;
import io.github.spigotrce.byteflow.common.VersionConstants;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final FlowServer server;
    private final String token;
    private InputStream inputStream;
    private OutputStream outputStream;

    public ClientHandler(Socket socket, FlowServer server, String token) {
        this.socket = socket;
        this.server = server;
        this.token = token;
    }

    @Override
    public void run() {
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            int pvn = IOUtils.readInt(inputStream);
            String token = IOUtils.readUTF(inputStream);

            if (VersionConstants.PVN != pvn) {
                IOUtils.writeUTF(outputStream, "AUTH_FAIL");
                IOUtils.writeUTF(outputStream, "INVALID_PVN");
                IOUtils.writeInt(outputStream, VersionConstants.PVN);
                close();
                return;
            }

            if (!this.token.equals(token)) {
                IOUtils.writeUTF(outputStream, "AUTH_FAIL");
                IOUtils.writeUTF(outputStream, "INVALID_TOKEN");
                close();
            }

            IOUtils.writeUTF(outputStream, "AUTH_SUCCESS");
            server.registerClient(this);

            while (!socket.isClosed())
                server.broadcastMessage(IOUtils.readMessage(inputStream), this);
        } catch (IOException e) {
            // TODO: implement exceptionHandler
            server.deregisterClient(this);
        }
    }

    public void sendMessage(byte[] data) throws IOException {
        IOUtils.writeMessage(outputStream, data);
    }

    public void close() throws IOException {
        server.deregisterClient(this);
        socket.close();
    }
}
