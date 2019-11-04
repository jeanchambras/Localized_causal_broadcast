import java.net.DatagramSocket;
import java.util.ArrayList;

public class Beb implements Listener {
    private PerfectLink perfectLink;
    private Listener urb;
    private NetworkTopology networkTopology;
    private DatagramSocket socket;
    private ArrayList<Message> messages;

    public Beb (DatagramSocket socket, NetworkTopology network, int numberOfMessages, int timeout, Listener urb){
        this.perfectLink = new PerfectLink(socket, timeout, this);
        this.networkTopology = network;
        this.socket = socket;
        this.urb = urb;
        this.messages = new ArrayList<>();
        for (ProcessDetails processDetails : network.getProcessesInNetwork()) {
            for (int i = 1; i <= numberOfMessages; ++i){
                ProcessDetails localDetails = networkTopology.getProcessFromPort(socket.getLocalPort());
                this.messages.add(new Message(processDetails,network.getProcessFromPort(socket.getLocalPort()), Integer.toString(i)));
            }
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
        //System.out.println("Process " + m.getSource().getId() + " beb-delivered message : "+ m.getPayload() + " from process "+ m.getSource().getId());
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
