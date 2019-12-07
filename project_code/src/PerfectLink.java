import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * The PerfectLink class defines the perfect link algorithm as well as handling the network reading and writing.
 */

public class PerfectLink {
    private HashSet<Message> messagesToSend;
    private HashSet<Message> messagesToAck;
    private HashSet<Message> receivedMessages;
    private HashSet<Message> messagesAcked;
    private DatagramSocket socket;
    private Server server;
    private Sender sender;
    private int timeout;
    private Listener beb;
    private Encoder encoder;
    private int SIZE;

    public PerfectLink(NetworkTopology net, DatagramSocket socket, int timeout, Listener beb) {
        this.receivedMessages = new HashSet<>();
        this.messagesToAck = new HashSet<>();
        this.messagesAcked = new HashSet<>();
        this.messagesToSend = new HashSet<>();
        this.socket = socket;
        this.timeout = timeout;
        this.beb = beb;
        this.SIZE = 10 + 4 * net.getNumberOfpeers();
        this.encoder = new Encoder(net);
        this.server = new Server();
        this.sender = new Sender();
        new Thread(server).start();
        new Thread(sender).start();
    }

    public void sendMessages() {
    }

    public void addMessagesToQueue(ArrayList<Message> messagesToAdd) {
        synchronized (messagesToSend) {
            this.messagesToSend.addAll(messagesToAdd);
        }
    }


    public void sendPacket(byte[] buf, ProcessDetails destination) {
        try {
            DatagramPacket packet;
            packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(destination.getAddress()), destination.getPort());
            socket.send(packet);
        } catch (SocketException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deliver(Message received) {
        beb.callback(received);
    }

    public class Server implements Runnable {
        public void run() {
            byte[] buf = new byte[SIZE];
            while (true) {
                DatagramPacket UDPpacket
                        = new DatagramPacket(buf, buf.length);
                try {
                    socket.receive(UDPpacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Tuple<Integer, Message> packet = null;
                try {
                    packet = encoder.decode(buf);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (packet.getX() == 1) {
                    synchronized (messagesAcked) {

                        messagesAcked.add(packet.getY());
                    }
                } else if (packet.getX() == 0) {
                    Message message = packet.getY();
                    synchronized (messagesToAck) {
                        messagesToAck.add(message);

                    }
                    if (!receivedMessages.contains(message)) {
                        deliver(message);
                        receivedMessages.add(message);
                    }
                }
            }
        }
    }

    public class Sender implements Runnable {
        public void run() {
            while (true) {
                if (!messagesToSend.isEmpty()) {
                    synchronized (messagesToSend) {
                        messagesToSend.forEach((Message m) -> {
                            byte[] buf = encoder.encode(false, m);
                            sendPacket(buf, m.getDestination());
                        });
                    }

                }

                if (!messagesToAck.isEmpty()) {
                    synchronized (messagesToAck) {
                        messagesToAck.forEach((Message m) -> {
                            byte[] buf = encoder.encode(true, m);

                            sendPacket(buf, m.getSender());
                        });
                        messagesToAck.clear();
                    }
                }

                if (!messagesAcked.isEmpty()) {
                    synchronized (messagesAcked) {
                        messagesToSend.removeAll(messagesAcked);
                        messagesAcked.clear();

                    }
                }
                // TODO optimiser timeout
                try {
                    Thread.sleep(timeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
