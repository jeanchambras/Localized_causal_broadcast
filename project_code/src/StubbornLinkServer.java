import java.io.IOException;
import java.net.*;
import java.util.*;

public class StubbornLinkServer implements Runnable {

    private DatagramSocket socket;
    private int receivePort;
    private boolean running;
    private byte[] buf = new byte[256];


    public int getReceivePort() {
        return receivePort;
    }
    /**
     * Instantiate the UDP socket
     * @param processReceivePort port number to listen
     */
    public StubbornLinkServer(int processReceivePort){
        this.receivePort = processReceivePort;
        try {
            socket = new DatagramSocket(processReceivePort);
        } catch (SocketException e) {
            e.printStackTrace();
        }
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
                    deliver(received);
                    if (received.equals("end")) {
                        running = false;
                        continue;
                    }
                }
                socket.close();
    }


    public void deliver(String received) {
        System.out.println("On port : " + receivePort + ", packet received : " + received);
    }
}
