import io.github.spigotrce.byteflow.client.FlowClient;

import java.io.IOException;

public class MainClient {
    public static void main(String[] args) throws IOException {
        FlowClient client = new FlowClient("localhost", 8080, "myToken", exception -> System.out.println("Error occured: " + exception.getMessage()));

        client.setChannelHandler("channel:test", message -> System.out.println("Received: " + new String(message)));
        client.sendMessage("channel:test", "Client 1".getBytes());
        client.sendMessage("channel:test", "Client 1".getBytes());
        client.sendMessage("channel:test", "Client 1".getBytes());
        client.sendMessage("channel:test", "Client 1".getBytes());
        client.sendMessage("channel:test", "Client 1".getBytes());
        client.sendMessage("channel:test", "Client 1".getBytes());
        client.sendMessage("channel:test", "Client 1".getBytes());
    }
}
