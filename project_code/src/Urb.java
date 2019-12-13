import java.net.DatagramSocket;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Urb implements Listener {
    private HashSet<Triple<Integer, int[], ProcessDetails>> deliveredMessages;
    private HashMap<Triple<Integer, int[], ProcessDetails>, Set<ProcessDetails>> ackedMessages;
    private Beb beb;
    private NetworkTopology network;
    private Listener lcb;
    private ReentrantLock lock = new ReentrantLock();

    Urb(ProcessDetails sender, DatagramSocket socket, NetworkTopology network, int timeout, Listener lcb) {
        this.beb = new Beb(sender, socket, network, timeout, this);
        this.network = network;
        this.ackedMessages = new HashMap<>();
        this.deliveredMessages = new HashSet<>();
        this.lcb = lcb;
    }

    // ######################## Broadcasting #######################

    void urbBroadcast(ProcessDetails source, Integer payload, int[] vc) {
        lock.lock();
        try {
            ackedMessages.put(new Triple<>(payload, vc, source), new HashSet<>(Collections.singletonList(source)));
        } finally {
            lock.unlock();
        }
        beb.bebBroadcast(source, payload, vc);
    }

    // ######################## Delivering #########################

    private void deliver(Triple<Integer, int[], ProcessDetails> urbDeliveredTriple) {
        lcb.callback(urbDeliveredTriple);
    }

    /*
     * We check if we have pending messages that can be delivered. This method is called each time we receive a new
     * beb delivered message.
     */

    protected void checkToDeliver() {
        Triple<Integer, int[], ProcessDetails> tripleToUrbDeliver;
        try {

            /*
             * While there is some messages to URB deliver, we deliver them
             */

            do {
                lock.lock();
                try {
                    tripleToUrbDeliver = null;
                    for (Map.Entry<Triple<Integer, int[], ProcessDetails>, Set<ProcessDetails>> entry : ackedMessages.entrySet()) {
                        if (canDeliver(entry.getKey()) && !deliveredMessages.contains(entry.getKey())) {
                            tripleToUrbDeliver = entry.getKey();
                            deliveredMessages.add(tripleToUrbDeliver);
                            ackedMessages.remove(tripleToUrbDeliver);
                            deliver(tripleToUrbDeliver);
                            break;
                        }
                    }
                } finally {
                    lock.unlock();
                }
            } while (!(tripleToUrbDeliver == null));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Check if we can deliver the message (triple). We check if we received an acknowledgment from a majority of
     * processes. (We assume a majority of correct processes).
     */

    private boolean canDeliver(Triple<Integer, int[], ProcessDetails> triple) {
        int N = network.getProcessesInNetwork().length;
        Set numberAcked = ackedMessages.get(triple);
        if (!(numberAcked == null)) {
            int size = numberAcked.size();
            return 2 * size >= N; // check if a majority of processes acknowledged the message
        }
        return false;
    }

    @Override
    public void callback(Message bebDeliveredMessage) {
        Triple<Integer, int[], ProcessDetails> t = new Triple<>(bebDeliveredMessage.getMessageID(),
                bebDeliveredMessage.getVectorClock(), bebDeliveredMessage.getSource());
        ProcessDetails sender = bebDeliveredMessage.getSender();

        /*
         * We check if the message is in our pending map. If not we add it, and add the source process in the
         * acknowledgment value set. If the message is present, we update our acknowledgment value set to add the source
         * process
         */

        if (!ackedMessages.containsKey(t)) {
            ackedMessages.put(t, new HashSet<>(Collections.singletonList(sender)));
            beb.bebBroadcast(t.getZ(), t.getX(), bebDeliveredMessage.getVectorClock());
        } else {
            Set<ProcessDetails> set = ackedMessages.get(t);
            set.add(sender);
            ackedMessages.put(t, set);
        }

        checkToDeliver(); // We check if we can deliver some pending messages
    }

    @Override
    public void callback(Triple t) {}
}
