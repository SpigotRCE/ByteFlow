package io.github.spigotrce.byteflow.client;

import io.github.spigotrce.byteflow.common.MessageUtils;
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
    private Consumer<Throwable> exceptionConsumer;
    private Consumer<byte[]> globalListener;

    public FlowClient(String ip, int port, String token) throws IOException {
        this.ip = ip;
        this.port = port;
        this.token = token;
        connect();
    }

    private void connect() throws IOException {
        socket = new Socket(ip, port);
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
        authenticate();
        startListening();
    }

    private void authenticate() throws IOException {
        MessageUtils.writeInt(outputStream, VersionConstants.PVN);
        MessageUtils.writeUTF(outputStream, token);
        String response = MessageUtils.readUTF(inputStream);

        if ("AUTH_SUCCESS".equals(response)) return;
        if ("INVALID_PVN".equals(response))
            throw new IOException("Invalid PVN, server expecting: " + MessageUtils.readInt(inputStream));
        throw new IOException("Authentication failed");
    }

    private void startListening() {
        executor.submit(() -> {
            try {
                while (!socket.isClosed()) {
                    byte[] data = MessageUtils.readMessage(inputStream);

                    String channel = MessageUtils.extractChannel(data);

                    if (channelListeners.containsKey(channel))
                        channelListeners.get(channel).accept(MessageUtils.extractMessage(data));

                    if (globalListener != null)
                        globalListener.accept(data);
                }
            } catch (IOException e) {
                catchException(e);
            }
        });
    }

    private void catchException(Throwable exception) {
        exceptionConsumer.accept(exception);
    }

    public void setExceptionHandler(Consumer<Throwable> handler) {
        exceptionConsumer = handler;
    }

    public void onMessage(String channel, Consumer<byte[]> handler) {
        channelListeners.put(channel, handler);
    }

    public void onMessage(Consumer<byte[]> handler) {
        this.globalListener = handler;
    }

    public void sendMessage(String channel, byte[] message) throws IOException {
        byte[] data = MessageUtils.encodeMessage(channel, message);
        MessageUtils.writeMessage(outputStream, data);
    }

    public void close() throws IOException {
        socket.close();
        executor.shutdown();
    }
}
