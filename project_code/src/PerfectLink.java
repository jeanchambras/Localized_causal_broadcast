import sun.plugin2.message.HeartbeatMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class PerfectLink {
    private ArrayList<Tuple<ProcessDetails, String>> perfectLinkDeliveredMessages;
    private ArrayList<Tuple<ProcessDetails, String>> messagesToAdd;
    private ArrayList<String> receivedMessages;
    private ArrayList<Tuple<ProcessDetails, String>> messagesToSend;
    private NetworkTopology networkTopology;
    private DatagramSocket socket;
    private Thread server;
    private Thread client;
    private int timeout;


    public ArrayList<Tuple<ProcessDetails, String>> getPerfectLinkDeliveredMessages() {
        return perfectLinkDeliveredMessages;
    }

    public ArrayList<Tuple<ProcessDetails, String>> getMessagesToSend() {
        return messagesToSend;
    }

    public PerfectLink(DatagramSocket socket, NetworkTopology networkTopology, PerfectFailureDetector failureDetector, int timeout){
        this.receivedMessages = new ArrayList<>();
        this.networkTopology = networkTopology;
        this.socket = socket;
        this.timeout = timeout;
        this.perfectLinkDeliveredMessages = new ArrayList<>();
        this.messagesToSend = new ArrayList<>();
        this.messagesToAdd = new ArrayList<>();
        this.server = new Thread(new Runnable() {
            public void run() {
                boolean running = true;
                byte[] buf = new byte[256];
                while (running) {
                    DatagramPacket packet
                            = new DatagramPacket(buf, buf.length);
                    try {
                        socket.receive(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String received = new String(packet.getData(), 0, packet.getLength());
                    if (received.equals("heartbeat")){
                        ProcessDetails source = networkTopology.getProcessFromPort(packet.getPort());
                        failureDetector.getAlive().add(source);
                    } else if(!receivedMessages.contains(received)){
                        receivedMessages.add(received);
                        deliver(received, packet.getPort());
                    }

                    if (received.equals("end")) {
                        running = false;
                        continue;
                    }
                }
                socket.close();
            }
        });
        server.start();
        initializeHeartbeats(networkTopology.getProcessesInNetwork());
    }


    public void sendMessages(){
        this.client = new Thread(new Runnable() {
            public void run() {
                boolean sending = true;
                while(sending){
                    messagesToSend.forEach((Tuple<ProcessDetails, String> m) -> {
                        byte[] buf = m.y.getBytes();
                        DatagramPacket packet
                                = null;
                        try {
                            packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(m.x.getAddress()), m.x.getPort());
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                        try {
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
    public void addMessagesToQueue(ArrayList<Tuple<ProcessDetails, String>> messagesToAdd) {
        this.messagesToAdd.addAll(messagesToAdd);
    }
    public void addMessagesToQueue(Tuple<ProcessDetails, String> messagesToAdd) {
        this.messagesToAdd.add(messagesToAdd);
    }

    public void initializeHeartbeats(ArrayList<ProcessDetails> alive){
        for (ProcessDetails p : alive) {
            Tuple<ProcessDetails, String> heartbeat = new Tuple<>(p, "heartbeat");
            addMessagesToQueue(heartbeat);
        }
    }

    public void deliver(String received, int sourcePort) {
        ProcessDetails source = networkTopology.getProcessFromPort(sourcePort);
        perfectLinkDeliveredMessages.add(new Tuple<>(source, received));

        // TODO remove -> just for debugging
        ProcessDetails destination = networkTopology.getProcessFromPort(socket.getLocalPort());
        System.out.println("from process " + source.getId() + " to process " + destination.getId() + " delivered message "+ received );
    }

}
