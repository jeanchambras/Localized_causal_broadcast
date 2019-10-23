import java.io.IOException;
import java.net.*;

public class StubbornLinkSend {
    private DatagramSocket socket;
    private InetAddress address;
    private int port;
    private byte[] buf;
    private boolean sending;

    public StubbornLinkSend(InetAddress address, int port) throws SocketException {
        System.out.println("Sender is running");
        socket = new DatagramSocket();
        this.port = port;
        this.address = address;
    }

    public void sendMessage(String msg) throws IOException {
        sending = true;
        while(sending){
        buf = msg.getBytes();
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        socket.close();
    }
}