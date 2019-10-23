import java.io.IOException;
import java.net.*;

public class StubbornLinkServer implements Runnable {

    private DatagramSocket socket;
    private int receivePort;
    private boolean running;
    private byte[] buf = new byte[256];

    public StubbornLinkServer(int processReceivePort){
        this.receivePort = processReceivePort;
        try {
            socket = new DatagramSocket(processReceivePort);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        running = true;
            System.out.println("Server is up and running on port :" + receivePort);
                while (running) {
                    DatagramPacket packet
                            = new DatagramPacket(buf, buf.length);
                    try {
                        socket.receive(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String received = new String(packet.getData(), 0, packet.getLength());
                    System.out.println("packet from :" + received);

                    if (received.equals("end")) {
                        running = false;
                        continue;
                    }
                }
                socket.close();
    }
}
