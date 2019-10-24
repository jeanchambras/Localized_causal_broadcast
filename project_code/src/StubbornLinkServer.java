import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class StubbornLinkServer implements Runnable {

    private DatagramSocket socket;
    private int receivePort;
    private boolean running;
    private byte[] buf = new byte[256];
    private ArrayList<String> receivedMessages;

    public StubbornLinkServer(int processReceivePort,ArrayList<String> receivedMessages ){
        this.receivePort = processReceivePort;
        this.receivedMessages = receivedMessages;
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

                    if(!receivedMessages.contains(received)) {
                        System.out.println("packet from :" + received);
                        receivedMessages.add(received);
                    }
                    if (received.equals("end")) {
                        running = false;
                        continue;
                    }
                }
                socket.close();
    }
}
