import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

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

    public PerfectLink(NetworkTopology networkTopology, DatagramSocket socket, int timeout, Listener beb) {
        this.receivedMessages = new ArrayList<>();
        this.messagesToAck = new ArrayList<>();
        this.nextMessagesToAck = new ArrayList<>();
        this.messagesAcked = new ArrayList<>();
        this.messagesToSend = new ArrayList<>();
        this.nextMessagesToSend = new ArrayList<>();
        this.socket = socket;
        this.timeout = timeout;
        this.beb = beb;
        this.server = new Server();
        this.sender = new Sender();
        this.encoder = new Encoder(networkTopology);
        new Thread(server).start();
    }

    public void sendMessages() {
        new Thread(sender).start();
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

    public void stop() {
        server.stopServer();
        sender.stopSender();
    }

    public class Server implements Runnable {
        private AtomicBoolean running = new AtomicBoolean(true);

        public void run() {
            byte[] buf = new byte[9];
            while (running.get()) {
                DatagramPacket UDPpacket
                        = new DatagramPacket(buf, buf.length);
                try {
                    socket.receive(UDPpacket);
                } catch (SocketTimeoutException | SocketException e) {
                    continue;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Tuple<Integer,Message> packet = encoder.decode(buf);

                if (packet.x == 1) {
                    messagesAcked.add(packet.y);
                } else if (packet.x == 0) {
                    Message message = packet.y;
                    nextMessagesToAck.add(message);
                    if (!receivedMessages.contains(message)) {
                        deliver(message);
                        receivedMessages.add(message);
                    }
                }
            }
        }

        public void stopServer() {
            running.set(false);
        }
    }

    public class Sender implements Runnable {
        private AtomicBoolean running = new AtomicBoolean(true);

        public void run() {
            while (running.get()) {
                if (!messagesToSend.isEmpty()) {
                    synchronized (messagesToSend) {
                        messagesToSend.forEach((Message m) -> {
                            if (!(m == null)) {
                                byte[] buf = encoder.encode(m);
                                sendPacket(buf, m.getDestination());
                            } else {
                                System.out.println("Null");
                            }
                        });
                    }

                }

                if (!messagesToAck.isEmpty()) {
                    synchronized (messagesToAck) {
                        messagesToAck.forEach((Message m) -> {
                            Ack a = new Ack(m);
                            byte[] buf = encoder.encode(a);
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
                try {
                    Thread.sleep(timeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void stopSender() {
            running.set(false);
        }

    }
}
