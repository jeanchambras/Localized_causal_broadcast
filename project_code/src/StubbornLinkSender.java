import org.w3c.dom.html.HTMLImageElement;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class StubbornLinkSender {
    private DatagramSocket socket;
    private byte[] buf;
    private boolean sending;
    private int timeout;

    /**
     * Instantiate a Stubborn link sender
     * @param timeout timeout between messages sent
     * @throws SocketException
     */
    public StubbornLinkSender(int timeout) throws SocketException {
        System.out.println("Sender is running");
        socket = new DatagramSocket();
        this.timeout = timeout;
    }

    /**
     * Stubborn link sender instance start to send messages
     * @param messagesToSend list of messages to send
     */
    public void sendMessage(Set<Tuple<Process, String>> messagesToSend) {
        sending = true;
        while(sending){
            messagesToSend.forEach((Tuple<Process, String> m) -> {
                buf = m.y.getBytes();
                DatagramPacket packet
                        = new DatagramPacket(buf, buf.length, m.x.getProcessIP(), m.x.getProcessReceivePort());
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(timeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            });
        }
    }
}