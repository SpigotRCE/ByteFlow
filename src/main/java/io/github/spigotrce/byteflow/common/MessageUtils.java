package io.github.spigotrce.byteflow.common;

import com.google.common.io.ByteStreams;
import java.io.*;

public class MessageUtils {
    public static void writeUTF(OutputStream outputStream, String value) throws IOException {
        byte[] utfBytes = value.getBytes();
        writeInt(outputStream, utfBytes.length);
        outputStream.write(utfBytes);
    }

    public static String readUTF(InputStream inputStream) throws IOException {
        int length = readInt(inputStream);
        byte[] utfBytes = ByteStreams.toByteArray(ByteStreams.limit(inputStream, length));
        return new String(utfBytes);
    }

    public static byte[] encodeMessage(String channel, byte[] message) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        writeUTF(byteStream, channel);
        writeBytes(byteStream, message);
        return byteStream.toByteArray();
    }

    public static byte[] readMessage(InputStream inputStream) throws IOException {
        int length = readInt(inputStream);
        return ByteStreams.toByteArray(ByteStreams.limit(inputStream, length));
    }

    public static void writeMessage(OutputStream outputStream, byte[] data) throws IOException {
        writeInt(outputStream, data.length);
        outputStream.write(data);
    }

    public static String extractChannel(byte[] data) throws IOException {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
        return readUTF(byteStream);
    }

    public static byte[] extractMessage(byte[] data) throws IOException {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
        readUTF(byteStream); // Skip the channel part
        return readBytes(byteStream);
    }

    private static byte[] readBytes(InputStream inputStream) throws IOException {
        int length = readInt(inputStream);
        return ByteStreams.toByteArray(ByteStreams.limit(inputStream, length));
    }

    private static void writeBytes(OutputStream outputStream, byte[] bytes) throws IOException {
        writeInt(outputStream, bytes.length);
        outputStream.write(bytes);
    }

    public static int readInt(InputStream inputStream) throws IOException {
        return ByteStreams.newDataInput(ByteStreams.toByteArray(ByteStreams.limit(inputStream, 4))).readInt();
    }

    public static void writeInt(OutputStream outputStream, int value) throws IOException {
        outputStream.write(new byte[]{
                (byte) ((value >> 24) & 0xFF),
                (byte) ((value >> 16) & 0xFF),
                (byte) ((value >> 8) & 0xFF),
                (byte) (value & 0xFF)
        });
    }
}
