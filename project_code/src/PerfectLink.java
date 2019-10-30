import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.*;

public class PerfectLink {
    private ArrayList<Tuple<ProcessDetails, String>> perfectLinkDeliveredMessages;
    private ArrayList<Tuple<ProcessDetails, String>> messagesToAdd;
    private ArrayList<String> receivedMessages;
    private ArrayList<Tuple<ProcessDetails, String>> messagesToSend;
    private NetworkTopology networkTopology;
    private DatagramSocket socket;
    private int timeout;


    public ArrayList<Tuple<ProcessDetails, String>> getPerfectLinkDeliveredMessages() {
        return perfectLinkDeliveredMessages;
    }

    public PerfectLink(DatagramSocket socket, NetworkTopology networkTopology){
        this.receivedMessages = new ArrayList<>();
        this.networkTopology = networkTopology;
        this.socket = socket;
        this.timeout = 300;
        this.perfectLinkDeliveredMessages = new ArrayList<>();
        this.messagesToSend = new ArrayList<>();
        this.messagesToAdd = new ArrayList<>();

        new Thread(new Runnable() {
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
                    if (!receivedMessages.contains(received)){
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
        }).start();
    }


    public void sendMessages(){
        new Thread(new Runnable() {
            public void run() {
                boolean sending = true;
                while(sending){
                    messagesToSend.forEach((Tuple<ProcessDetails, String> m) -> {
                        byte[] buf = m.y.getBytes();
                        DatagramPacket packet
                                = new DatagramPacket(buf, buf.length, m.x.getAddress(), m.x.getPort());
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
        }).start();
    }

    /**
     * Add messages to send by perfect link sender
     * @param messagesToAdd
     */
    public void addMessagesToQueue(ArrayList<Tuple<ProcessDetails, String>> messagesToAdd) {
        this.messagesToAdd.addAll(messagesToAdd);
    }

    public void deliver(String received, int sourcePort) {
        ProcessDetails source = networkTopology.getProcessFromPort(sourcePort);
        System.out.println(received + " from " + source.getId());
        perfectLinkDeliveredMessages.add(new Tuple<>(source, received));
        addMessagesToQueue(new ArrayList<>(Arrays.asList(new Tuple<>(source, received))));
    }

}
