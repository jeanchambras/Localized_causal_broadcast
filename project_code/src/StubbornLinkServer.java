import java.io.IOException;
import java.net.*;

public class StubbornLinkServer implements Runnable {

    private DatagramSocket socket;
    private int receivePort;
    private boolean running;
    private byte[] buf = new byte[256];
    private NetworkTopology network;


    public int getReceivePort() {
        return receivePort;
    }

    public NetworkTopology getNetwork() {
        return network;
    }

    /**
     * Instantiate the UDP socket
     * @param processReceivePort port number to listen
     */
    public StubbornLinkServer(int processReceivePort, DatagramSocket socket, NetworkTopology network){
        this.receivePort = processReceivePort;
        this.socket = socket;
        this.network = network;
    }

    /**
     * Start the process UDP server
     */
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
                    deliver(received, packet.getPort());
                    if (received.equals("end")) {
                        running = false;
                        continue;
                    }
                }
                socket.close();
    }


    public void deliver(String received, int sourcePort) {
        System.out.println("On port : " + receivePort + ", packet received : " + received);
    }
}
