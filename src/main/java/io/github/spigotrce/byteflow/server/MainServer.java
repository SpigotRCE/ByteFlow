package io.github.spigotrce.byteflow.server;

import java.io.IOException;

public class MainServer {
    public static void main(String[] args) throws IOException {
        FlowServer server = new FlowServer(Integer.parseInt(args[0]), args[1]);
        server.start();
    }
}
