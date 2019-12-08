import java.net.DatagramSocket;
import java.util.ArrayList;



/**
 * BEB class defines the BEB algorithm. Like every algorithm in the stack it has the sendMessages and deliver functions which corresponds to the Broadcast and Deliver functions of the algorithms.
 * The deliver function always calls the corresponding function above in the abstraction stack.
 *
 */


public class Beb implements Listener {
    private PerfectLink perfectLink;
    private Listener urb;
    private NetworkTopology networkTopology;
    private ProcessDetails sender;
    public Beb(ProcessDetails sender, DatagramSocket socket, NetworkTopology network, int timeout, Listener urb) {
        this.perfectLink = new PerfectLink(network,socket, timeout, this);
        this.networkTopology = network;
        this.urb = urb;
        this.sender = sender;
    }

    public void addMessage(ProcessDetails source, String payload, int[] vc) {
        ArrayList<Message> messages = new ArrayList<>();
        for (ProcessDetails destination : networkTopology.getProcessesInNetwork()) {
            Message m = new Message(destination, source, payload, sender, vc);
            if (m.getDestination().equals(sender)){
                perfectLink.addToDeliver(m);
            } else {
                messages.add(m);
            }
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
    public void callback(Triple t) {
    }

    public void deliver(Message m) {
        urb.callback(m);
    }
}
