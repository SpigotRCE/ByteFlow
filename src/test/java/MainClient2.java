import io.github.spigotrce.byteflow.client.FlowClient;

import java.io.IOException;

public class MainClient2 {
    public static void main(String[] args) throws IOException {
        FlowClient client = new FlowClient("localhost", 8080, "myToken");

        client.onMessage("channel:test", message -> System.out.println("Received: " + new String(message)));
        client.onExceptionThrown(exception -> System.out.println("Error occured: " + exception.getMessage()));
        client.sendMessage("channel:test", "Client 2".getBytes());
        client.sendMessage("channel:test", "Client 2".getBytes());
        client.sendMessage("channel:test", "Client 2".getBytes());
        client.sendMessage("channel:test", "Client 2".getBytes());
        client.sendMessage("channel:test", "Client 2".getBytes());
        client.sendMessage("channel:test", "Client 2".getBytes());
        client.sendMessage("channel:test", "Client 2".getBytes());
        client.sendMessage("channel:test", "Client 2".getBytes());
        client.sendMessage("channel:test", "Client 2".getBytes());
        client.sendMessage("channel:test", "Client 2".getBytes());
    }
}