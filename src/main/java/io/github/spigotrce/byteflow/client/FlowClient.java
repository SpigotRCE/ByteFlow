package io.github.spigotrce.byteflow.client;

import io.github.spigotrce.byteflow.common.IOUtils;
import io.github.spigotrce.byteflow.common.VersionConstants;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.function.*;

public class FlowClient {
    private final String ip;
    private final int port;
    private final String token;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    private final ConcurrentHashMap<String, Consumer<byte[]>> channelListeners = new ConcurrentHashMap<>();
    private final Consumer<Throwable> exceptionHandler;
    public FlowClient(String ip, int port, String token, Consumer<Throwable> exceptionHandler) throws IOException, IllegalStateException {
        this.ip = ip;
        this.port = port;
        this.token = token;
        this.exceptionHandler = exceptionHandler;
        connect();
    }

    private void connect() throws IOException, IllegalStateException {
        socket = new Socket(ip, port);
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
        authenticate();
        startListening();
    }

    private void authenticate() throws IOException, IllegalStateException {
        IOUtils.writeInt(outputStream, VersionConstants.PVN);
        IOUtils.writeUTF(outputStream, token);

        String result = IOUtils.readUTF(inputStream);
        if ("AUTH_SUCCESS".equals(result)) return;

        String response = IOUtils.readUTF(inputStream);
        if ("INVALID_PVN".equals(response)) {
            int pvn = IOUtils.readInt(inputStream);
            throw new IOException("Invalid PVN, server expecting: " + pvn);
        }

        if ("INVALID_TOKEN".equals(response))
            throw new IOException("Invalid token");

        throw new IllegalStateException("Invalid response: " + response);
    }

    private void startListening() {
        executor.submit(() -> {
            try {
                while (!socket.isClosed()) {
                    byte[] data = IOUtils.readMessage(inputStream);

                    String channel = IOUtils.extractChannel(data);

                    if (channelListeners.containsKey(channel))
                        channelListeners.get(channel).accept(IOUtils.extractMessage(data));
                }
            } catch (IOException e) {
                exceptionHandler.accept(e);
            }
        });
    }

    public void setChannelHandler(String channel, Consumer<byte[]> handler) {
        channelListeners.put(channel, handler);
    }

    public void sendChannelMessage(String channel, byte[] message) throws IOException {
        byte[] data = IOUtils.encodeMessage(channel, message);
        IOUtils.writeMessage(outputStream, data);
    }

    public void close() throws IOException {
        socket.close();
        executor.shutdown();
    }
}
