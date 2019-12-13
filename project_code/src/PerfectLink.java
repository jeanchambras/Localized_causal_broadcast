import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

public class PerfectLink {
    private HashSet<Message> messagesToSend;
    private HashSet<Message> messagesToAck;
    private HashSet<Message> receivedMessages;
    private HashSet<Message> messagesAcked;
    private DatagramSocket socket;
    private ConcurrentLinkedQueue<Message> messagesToDeliverQueue;
    private int timeout;
    private Listener beb;
    private Encoder encoder;
    private int SIZE;
    private ReentrantLock lockA = new ReentrantLock();
    private ReentrantLock lockB = new ReentrantLock();

    PerfectLink(NetworkTopology net, DatagramSocket socket, int timeout, Listener beb) {
        this.receivedMessages = new HashSet<>();
        this.messagesToAck = new HashSet<>(); // separated from the messages to send to be able to clear it easily
        this.messagesAcked = new HashSet<>(); // used not to resend acked messages
        this.messagesToSend = new HashSet<>();
        this.socket = socket;
        this.timeout = timeout;
        this.beb = beb;

        /*
         * Defined in the Encoder class, the packet sent have a 10 bit fix encoding
         * ( ack flag | source id | destination id | sender id | payload [message id]). The vector clock is encoded as
         * the byte array representation of an integer array.
         */

        this.SIZE = 10 + 4 * net.getNumberOfpeers();
        this.messagesToDeliverQueue = new ConcurrentLinkedQueue<>();
        this.encoder = new Encoder(net);

        // ############# threads initialization #############

        Server server = new Server();
        Sender sender = new Sender();
        Application application = new Application();
        new Thread(server).start();
        new Thread(sender).start();
        new Thread(application).start();
    }

    // #################### Sending ####################

    void addMessagesToQueue(ArrayList<Message> messagesToAdd) {
        lockA.lock();
        try {
            this.messagesToSend.addAll(messagesToAdd);
        } finally {
            lockA.unlock();
        }
    }

    private void sendPacket(byte[] buf, ProcessDetails destinationDetails) {
        try {
            DatagramPacket packet;
            packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(destinationDetails.getAddress()), destinationDetails.getPort());
            socket.send(packet);
        } catch (SocketException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * The thread that sends messages
     */

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
                            sendPacket(buf, m.getSender()); // sends the ack back to the sender
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
                            // We remove every acked message from the messagesToSend set
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

                System.gc(); // garbage collector

                try {
                    Thread.sleep(timeout); // sleep not to flood the network and flood the different threads of each process
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // #################### Receiving ####################

    void addToDeliver(Message m) {
        messagesToDeliverQueue.add(m);
    }

    private void deliver(Message message) {
        beb.callback(message);
    }

    /*
     * The thread that receives messages
     */

    public class Server implements Runnable {
        public void run() {
            byte[] buf = new byte[SIZE];
            while (true) {
                DatagramPacket UDPpacket = new DatagramPacket(buf, buf.length);

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

                /*
                 * handle a received acknowledgment message
                 */

                if (packet.getX() == 1) {
                    try {
                        lockB.lock();
                        messagesAcked.add(packet.getY());
                    } finally {
                        lockB.unlock();
                    }

                    /*
                     * handle a received message
                     */

                } else if (packet.getX() == 0) {
                    Message message = packet.getY();
                    try {
                        lockB.lock();
                        messagesToAck.add(message); // we acknowledge every received message
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

    /*
     * The thread that handle received messages
     */

    public class Application implements Runnable {
        public void run() {
            while (true) {
                if (!messagesToDeliverQueue.isEmpty()) {
                    Message message = messagesToDeliverQueue.poll();
                    deliver(message);
                }
            }
        }
    }
}
