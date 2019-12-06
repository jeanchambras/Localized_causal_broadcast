import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

/**
 * The PerfectLink class defines the perfect link algorithm as well as handling the network reading and writing.
 */

public class PerfectLink {
    private ArrayList<Message> messagesToSend;
    private ArrayList<Message> nextMessagesToSend;
    private ArrayList<Message> messagesToAck;
    private ArrayList<Message> nextMessagesToAck;
    private ArrayList<Message> receivedMessages;
    private ArrayList<Message> messagesAcked;
    private DatagramSocket socket;
    private Server server;
    private Sender sender;
    private int timeout;
    private Listener beb;
    private Encoder encoder;
    private int SIZE;

    public PerfectLink(NetworkTopology net, DatagramSocket socket, int timeout, Listener beb) {
        this.receivedMessages = new ArrayList<>();
        this.messagesToAck = new ArrayList<>();
        this.nextMessagesToAck = new ArrayList<>();
        this.messagesAcked = new ArrayList<>();
        this.messagesToSend = new ArrayList<>();
        this.nextMessagesToSend = new ArrayList<>();
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
        this.nextMessagesToSend.addAll(messagesToAdd);
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
                    messagesAcked.add(packet.getY());
                } else if (packet.getX() == 0) {
                    Message message = packet.getY();
                    nextMessagesToAck.add(message);
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

                if (!nextMessagesToAck.isEmpty()) {
                    messagesToAck.addAll(nextMessagesToAck);
                    nextMessagesToAck.clear();
                }
                if (!nextMessagesToSend.isEmpty()) {
                    messagesToSend.addAll(nextMessagesToSend);
                    nextMessagesToSend.clear();
                }
                if (!messagesAcked.isEmpty()) {
                    messagesToSend.removeAll(messagesAcked);
                    messagesAcked.clear();
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
