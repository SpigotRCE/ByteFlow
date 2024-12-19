import io.github.spigotrce.byteflow.client.FlowClient;

import java.io.IOException;

public class MainClient2 {
    public static void main(String[] args) throws IOException, IllegalStateException {
        FlowClient client = new FlowClient("localhost", 8080, "myToken", exception -> System.out.println("Error occured: " + exception.getMessage()));

        client.setChannelHandler("channel:test", message -> System.out.println("Received: " + new String(message)));
        client.sendChannelMessage("channel:test", "Client 2".getBytes());
        client.sendChannelMessage("channel:test", "Client 2".getBytes());
        client.sendChannelMessage("channel:test", "Client 2".getBytes());
        client.sendChannelMessage("channel:test", "Client 2".getBytes());
        client.sendChannelMessage("channel:test", "Client 2".getBytes());
        client.sendChannelMessage("channel:test", "Client 2".getBytes());
        client.sendChannelMessage("channel:test", "Client 2".getBytes());
        client.sendChannelMessage("channel:test", "Client 2".getBytes());
        client.sendChannelMessage("channel:test", "Client 2".getBytes());
        client.sendChannelMessage("channel:test", "Client 2".getBytes());
    }
}
