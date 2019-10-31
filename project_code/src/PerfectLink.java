import sun.plugin2.message.HeartbeatMessage;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class PerfectLink {
    private ArrayList<Message> messagesToAdd;
    private ArrayList<Message> receivedMessages;
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
        this.networkTopology = networkTopology;
        this.socket = socket;
        this.timeout = timeout;
        this.messagesToSend = new ArrayList<>();
        this.messagesToAdd = new ArrayList<>();
        this.beb = beb;
        this.server = new Thread(new Runnable() {
            public void run() {
                boolean running = true;
                byte[] buf = new byte[256];
                Message message = null;
                while (running) {
                    DatagramPacket packet
                            = new DatagramPacket(buf, buf.length);
                    try {
                        socket.receive(packet);
                        ObjectInputStream iStream;
                        iStream = new ObjectInputStream(new ByteArrayInputStream(buf));
                        message = (Message) iStream.readObject();
                        iStream.close();
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    if(!receivedMessages.contains(message)){
                        System.out.println(networkTopology.getProcessFromPort(packet.getPort()).getId() + " : " + message.getPayload());
                        deliver(networkTopology.getProcessFromPort(packet.getPort()),message);
                        receivedMessages.add(message);
                    }
                }
                socket.close();
            }
        });
        server.start();
//        initializeHeartbeats(networkTopology.getProcessesInNetwork());
    }


    public void sendMessages(){
        this.client = new Thread(new Runnable() {
            public void run() {
                boolean sending = true;
                while(sending){
                    messagesToSend.forEach((Message m) -> {
                        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                        ObjectOutput oo;
                        try {
                            oo = new ObjectOutputStream(bStream);
                            oo.writeObject(m);
                            oo.close();
                            byte[] buf = bStream.toByteArray();
                            DatagramPacket packet;
                            packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(m.getDestination().getAddress()), m.getDestination().getPort());
                            socket.send(packet);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                    if (!messagesToAdd.isEmpty()) {
                        messagesToSend.addAll(messagesToAdd);
                        messagesToAdd.clear();
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
