import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
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

    public PerfectLink(DatagramSocket socket, int timeout, Listener beb){
        this.receivedMessages = new ArrayList<>();
        this.messagesToAck = new ArrayList<>();
        this.nextMessagesToAck = new ArrayList<>();
        this.messagesAcked = new ArrayList<>();
        this.messagesToSend = new ArrayList<>();
        this.nextMessagesToSend = new ArrayList<>();
        this.socket = socket;
        this.timeout = timeout;
        this.beb = beb;
        // start a thread that listen for incoming packets
        this.server = new Server();
        this.sender = new Sender();
        new Thread(server).start();
    }
    // TODO start a thread that starts to send messages -> we should handle USR2 signal before starting to send
    public void sendMessages(){
        new Thread(sender).start();
    }

    public void addMessagesToQueue(ArrayList<Message> messagesToAdd) {
        this.nextMessagesToSend.addAll(messagesToAdd);
    }

    public void sendPacket(Packet p, ProcessDetails destination){
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        ObjectOutput oo;
        try {
            oo = new ObjectOutputStream(bStream);
            oo.writeObject(p);
            oo.close();
            byte[] buf = bStream.toByteArray();
            DatagramPacket packet;
            packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(destination.getAddress()), destination.getPort());
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deliver(Message received) {
        beb.callback(received);
    }

    public void stop(){
        server.stopServer();
        sender.stopSender();
    }

    public class Server implements Runnable {
        private AtomicBoolean running = new AtomicBoolean(true);
        public void run() {
            byte[] buf = new byte[512];
            Packet packet;
            while (running.get()) {
                DatagramPacket UDPpacket
                        = new DatagramPacket(buf, buf.length);
                try {
                    socket.receive(UDPpacket);
                } catch (SocketTimeoutException e) {
                    continue;
                } catch (IOException e){
                    e.printStackTrace();
                }
                try {
                    ObjectInputStream iStream;
                    iStream = new ObjectInputStream(new ByteArrayInputStream(buf));
                    packet = (Packet) iStream.readObject();
                    iStream.close();

                if (packet.message == null && packet.ack != null){
                    messagesAcked.add(packet.ack.getMessage());
                } else if (packet.ack == null && packet.message != null) {
                    Message message = packet.message;
                    nextMessagesToAck.add(message);
                    if (!receivedMessages.contains(message)) {
                        deliver(message);
                        receivedMessages.add(message);
                    }
                }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        public void stopServer(){
            running.set(false);
        }
    }

    public class Sender implements Runnable {
            private  AtomicBoolean running = new AtomicBoolean(true);
            public void run() {
                while(running.get()){
                    if(!messagesToSend.isEmpty()){
                        synchronized(messagesToSend)
                        {
                            messagesToSend.forEach((Message m) -> {
                                Packet p = new Packet(m);
                                sendPacket(p, m.getDestination());
                            });
                        }

                    }

                    if(!messagesToAck.isEmpty()){
                        synchronized(messagesToAck)
                        {
                            messagesToAck.forEach((Message m)->{
                                Ack a = new Ack(m);
                                Packet p = new Packet(a);
                                sendPacket(p, m.getSender());
                            });
                            messagesToAck.clear();
                        }
                    }

                    if(!nextMessagesToAck.isEmpty()){
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
            public void stopSender(){
                running.set(false);
            }

    }
}
