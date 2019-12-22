import java.net.DatagramSocket;
import java.util.ArrayList;

public class Beb implements Listener {
    private PerfectLink perfectLink;
    private Listener urb;
    private NetworkTopology networkTopology;
    private ProcessDetails processDetails;

    Beb(ProcessDetails processDetails, DatagramSocket socket, NetworkTopology network, int timeout, Listener urb) {
        this.perfectLink = new PerfectLink(network, socket, timeout, this);
        this.networkTopology = network;
        this.urb = urb;
        this.processDetails = processDetails;
    }

    /*
     * Create point to point messages to every processes in the network (including self), and send them to the perfect
     * link abstraction
     */

    void bebBroadcast(ProcessDetails source, Integer payload, int[] vc) {
        ArrayList<Message> pointToPointMessages = new ArrayList<>();
        for (ProcessDetails destination : networkTopology.getProcessesInNetwork()) {
            Message pointToPointMessage = new Message(destination, source, payload, processDetails, vc);

            /* If the destination process is self, we pass it to the delivering queue of perfect link to be handled by the
             * application thread (the thread that handle received messages). Otherwise we pass the message to the
             * sending thread.
             */

            if (pointToPointMessage.getDestination().equals(processDetails)) {
                perfectLink.addToDeliver(pointToPointMessage);
            } else {
                pointToPointMessages.add(pointToPointMessage);
            }
        }
        perfectLink.addMessagesToQueue(pointToPointMessages);
    }

    @Override
    public void callback(Message perfectLinkDeliveredMessage) { deliver(perfectLinkDeliveredMessage); }

    public void deliver(Message message) { urb.callback(message); }

    @Override
    public void callback(Triple t) {}
}
