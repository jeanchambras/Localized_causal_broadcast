import java.net.DatagramSocket;
import java.util.ArrayList;

public class Beb implements Listener {
    private PerfectLink perfectLink;
    private Listener urb;
    private NetworkTopology networkTopology;
    private DatagramSocket socket;

    public Beb(DatagramSocket socket, NetworkTopology network, int timeout, Listener urb) {
        this.perfectLink = new PerfectLink(socket, timeout, this);
        this.networkTopology = network;
        this.socket = socket;
        this.urb = urb;
    }

    public void addMessage(ProcessDetails source, String payload) {
        ProcessDetails sender = networkTopology.getProcessFromPort(socket.getLocalPort());
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

    public void stop() {
        perfectLink.stop();
    }
}
