import java.net.DatagramSocket;
import java.util.ArrayList;

public class Beb implements Listener {
    private PerfectLink perfectLink;
    private Listener urb;
    private NetworkTopology networkTopology;
    private ProcessDetails sender;
    public Beb(ProcessDetails sender, DatagramSocket socket, NetworkTopology network, int timeout, Listener urb) {
        this.perfectLink = new PerfectLink(network, socket, timeout, this);
        this.networkTopology = network;
        this.urb = urb;
        this.sender = sender;
    }

    public void addMessage(ProcessDetails source, String payload) {
        ArrayList<Message> messages = new ArrayList<>();
        for (ProcessDetails destination : networkTopology.getProcessesInNetwork()) {
            Message m = new Message(destination, source, payload, sender);
            messages.add(m);
        }
        perfectLink.addMessagesToQueue(messages);
    }

    public void sendMessages() {
        perfectLink.sendMessages();
    }

    @Override
    public void callback(Message m) {
        deliver(m);
    }

    @Override
    public void callback(Tuple t) {
    }

    public void deliver(Message m) {
        urb.callback(m);
    }
}