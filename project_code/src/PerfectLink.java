import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

public class PerfectLink {
    private ArrayList<Message> messagesToAdd;
    private ArrayList<Message> addAck;
    private ArrayList<Message> messagesToAck;
    private ArrayList<Message> receivedMessages;
    private ArrayList<Message> messagesToDelete;
    private ArrayList<Message> messagesToSend;
    private NetworkTopology networkTopology;
    private DatagramSocket socket;
    private Thread server;
    private Thread client;
    private int timeout;
    private Listener beb;

    public ArrayList<Message> getMessagesToSend() {
        return messagesToSend;
    }


    public PerfectLink(DatagramSocket socket, NetworkTopology networkTopology, PerfectFailureDetector failureDetector, int timeout, Listener beb){
        this.receivedMessages = new ArrayList<>();
        this.addAck = new ArrayList<>();
        this.messagesToAck = new ArrayList<>();
        this.networkTopology = networkTopology;
        this.socket = socket;
        this.timeout = timeout;
        this.messagesToSend = new ArrayList<>();
        this.messagesToAdd = new ArrayList<>();
        this.messagesToDelete = new ArrayList<>();
        this.beb = beb;
        this.server = new Thread(new Runnable() {
            public void run() {
                boolean running = true;
                byte[] buf = new byte[512];
                Packet packet = null;
                while (running) {
                    DatagramPacket UDPpacket
                            = new DatagramPacket(buf, buf.length);
                    try {
                        socket.receive(UDPpacket);
                        ObjectInputStream iStream;
                        iStream = new ObjectInputStream(new ByteArrayInputStream(buf));
                        packet = (Packet) iStream.readObject();
                        iStream.close();
                    } catch (IOException | ClassNotFoundException e) {
                        System.err.println(e);
                    }
                   if (packet.message == null && packet.ack != null){
                         messagesToDelete.add(packet.ack.getMessage());
                   } else if (packet.ack == null && packet.message != null) {
                        Message message = packet.message;
                        addAck.add(message);
                       if (!receivedMessages.contains(message)) {
                           System.out.println(networkTopology.getProcessFromPort(UDPpacket.getPort()).getId() + " : " + message.getPayload());
                           deliver(networkTopology.getProcessFromPort(UDPpacket.getPort()), message);
                           receivedMessages.add(message);
                       }
                   }
                }
                socket.close();
            }
        });
        server.start();
    }


    public void sendMessages(){
        this.client = new Thread(new Runnable() {
            public void run() {
                boolean sending = true;
                while(sending){
                    messagesToSend.forEach((Message m) -> {
                        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                        ObjectOutput oo;
                        Packet p = new Packet(m);
                        try {
                            oo = new ObjectOutputStream(bStream);
                            oo.writeObject(p);
                            oo.close();
                            byte[] buf = bStream.toByteArray();
                            DatagramPacket packet;
                            packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(m.getDestination().getAddress()), m.getDestination().getPort());
                            socket.send(packet);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    messagesToAck.forEach((Message m) -> {
                        Ack a = new Ack(m);
                        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                        ObjectOutput oo;
                        Packet p = new Packet(a);
                        try {
                            oo = new ObjectOutputStream(bStream);
                            oo.writeObject(p);
                            oo.close();
                            byte[] buf = bStream.toByteArray();
                            DatagramPacket packet;
                            packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(m.getSource().getAddress()), m.getSource().getPort());
                            socket.send(packet);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                    messagesToAck.clear();
                    if(!addAck.isEmpty()){
                        messagesToAck.addAll(addAck);
                        addAck.clear();
                    }
                    if (!messagesToAdd.isEmpty()) {
                        messagesToSend.addAll(messagesToAdd);
                        messagesToAdd.clear();
                    }
                    if (!messagesToDelete.isEmpty()) {
                        messagesToSend.removeAll(messagesToDelete);
                        messagesToDelete.clear();
                    }
                    try {
                        Thread.sleep(timeout);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        client.start();
    }

    /**
     * Add messages to send by perfect link sender
     * @param messagesToAdd
     */
    public void addMessagesToQueue(ArrayList<Message> messagesToAdd) {
        this.messagesToAdd.addAll(messagesToAdd);
    }
    public void addMessagesToQueue(Message messageToAdd) {
        this.messagesToAdd.add(messageToAdd);
    }

    public void initializeHeartbeats(ArrayList<ProcessDetails> alive){
        for (ProcessDetails p : alive) {
//            Message heartbeat = new Message(p, "heartbeat");
//            addMessagesToQueue(heartbeat);
        }
    }

    public void deliver(ProcessDetails source, Message received) {
        beb.callback();
        // TODO remove -> just for debugging


    }

}
