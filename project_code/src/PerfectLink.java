import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;

public class PerfectLink {

    private ArrayList<Message> messagesToSend;
    private ArrayList<Message> nextMessagesToSend;
    private ArrayList<Message> messagesToAck;
    private ArrayList<Message> nextMessagesToAck;
    private ArrayList<Message> receivedMessages;
    private ArrayList<Message> messagesAcked;
    private DatagramSocket socket;
    private Thread server;
    private Thread client;
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
        this.server = new Thread(new Runnable() {
            public void run() {
                byte[] buf = new byte[512];
                Packet packet = null;
                while (true) {
                    DatagramPacket UDPpacket
                            = new DatagramPacket(buf, buf.length);
                    try {
                        socket.receive(UDPpacket);
                        ObjectInputStream iStream;
                        iStream = new ObjectInputStream(new ByteArrayInputStream(buf));
                        packet = (Packet) iStream.readObject();
                        iStream.close();
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
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
                }
            }
        });
        server.start();
    }
    // TODO start a thread that starts to send messages -> we should handle USR2 signal before starting to send
    public void sendMessages(){
        this.client = new Thread(() -> {
            while(true){

                synchronized(messagesToSend)
                {
                    Iterator<Message> it = messagesToSend.iterator();
                    while (it.hasNext()){
                        Message m = it.next();
                        Packet p = new Packet(m);
                        sendPacket(p, m.getDestination());
                    }
                }
                synchronized(messagesToAck)
                {
                    Iterator<Message> it = messagesToAck.iterator();
                    while (it.hasNext()){
                        Message m = it.next();
                        Ack a = new Ack(m);
                        Packet p = new Packet(a);
                        sendPacket(p, m.getSender());
                    }
                    messagesToAck.clear();
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
        });
        client.start();
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

    public ArrayList<Message> getMessagesToSend() {
        return messagesToSend;
    }

    public void setMessagesToSend(ArrayList<Message> messagesToSend) {
        this.messagesToSend = messagesToSend;
    }

}
