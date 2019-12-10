import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The PerfectLink class defines the perfect link algorithm as well as handling the network reading and writing.
 */

public class PerfectLink {
    private HashSet<Message> messagesToSend;
    private HashSet<Message> messagesToAck;
    private HashSet<Message> receivedMessages;
    private HashSet<Message> messagesAcked;
    private DatagramSocket socket;
    private ConcurrentLinkedQueue<Message> toDeliver;
    private Server server;
    private Sender sender;
    private Application application;
    private int timeout;
    private Listener beb;
    private Encoder encoder;
    private int SIZE;
    private ReentrantLock lockA = new ReentrantLock();
    private ReentrantLock lockB = new ReentrantLock();

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
        this.toDeliver = new ConcurrentLinkedQueue<>();
        this.application = new Application();
        new Thread(server).start();
        new Thread(sender).start();
        new Thread(application).start();
    }

    public void sendMessages() {
    }

    public void addMessagesToQueue(ArrayList<Message> messagesToAdd) {
        lockA.lock();
        try {
            this.messagesToSend.addAll(messagesToAdd);
        } finally {
            lockA.unlock();
        }
    }

    public void addToDeliver(Message m) {
        toDeliver.add(m);
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
                    try {
                        lockB.lock();
                        messagesAcked.add(packet.getY());
                    } finally {
                        lockB.unlock();
                    }
                } else if (packet.getX() == 0) {
                    Message message = packet.getY();
                    try {
                        lockB.lock();
                        messagesToAck.add(message);
                    } finally {
                        lockB.unlock();
                    }
                    if (!receivedMessages.contains(message)) {
                        addToDeliver(message);
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
                    try {
                        lockA.lock();
                        messagesToSend.forEach((Message m) -> {
                            byte[] buf = encoder.encode(false, m);
                            sendPacket(buf, m.getDestination());
                        });
                    } finally {
                        lockA.unlock();
                    }
                }

                if (!messagesToAck.isEmpty()) {
                    try {
                        lockB.lock();
                        messagesToAck.forEach((Message m) -> {
                            byte[] buf = encoder.encode(true, m);
                            sendPacket(buf, m.getSender());
                        });
                        messagesToAck.clear();
                    } finally {
                        lockB.unlock();
                    }
                }
                try {
                    lockB.lock();
                    if (!messagesAcked.isEmpty()) {
                        try {
                            lockA.lock();
                            messagesToSend.removeAll(messagesAcked);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            lockA.unlock();
                        }
                        messagesAcked.clear();
                    }
                } finally {
                    lockB.unlock();
                }
                System.gc();
                try {
                    Thread.sleep(timeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class Application implements Runnable {
        public void run() {
            while (true) {
                if (!toDeliver.isEmpty()) {
                    Message message = toDeliver.poll();
                    deliver(message);
                }
            }
        }
    }
}
