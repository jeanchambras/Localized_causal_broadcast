import java.net.DatagramSocket;
import java.util.ArrayList;

public class Beb implements Listener {
    private PerfectLink perfectLink;
    private Listener urb;
    private NetworkTopology networkTopology;
    private DatagramSocket socket;
    private ArrayList<Message> messages;
    private ArrayList<Message> messagesToSend;

    public Beb (DatagramSocket socket, NetworkTopology network, int numberOfMessages, int timeout, Listener urb){
        this.perfectLink = new PerfectLink(socket, timeout, this);
        this.networkTopology = network;
        this.socket = socket;
        this.urb = urb;
        this.messages = new ArrayList<>();

    }

    public void addMessages(ProcessDetails source, String payload){
//        System.out.println(networkTopology.getProcessFromPort(socket.getLocalPort()).getPort() + ": broadcast " + payload + " from "+ source.getPort());
        ProcessDetails sender = networkTopology.getProcessFromPort(socket.getLocalPort());
        ArrayList<Message> messages = new ArrayList<>();
        for (ProcessDetails destination : networkTopology.getProcessesInNetwork()) {
                Message m = new Message(destination, source, payload, sender);
                messages.add(m);
        }
        perfectLink.addMessagesToQueue(messages);
    }

    public void sendMessages(){
        perfectLink.sendMessages();
    }

    @Override
    public void callback(Message m) {
        deliver(m);

    }

    public void deliver(Message m){
        urb.callback(m);
    }

    public NetworkTopology getNetworkTopology(){
        return this.networkTopology;
    }

    public DatagramSocket getSocket(){
        return this.socket;
    }

    public ArrayList<Message> getMessages(){
        return  this.messages;
    }
}
