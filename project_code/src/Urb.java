import java.net.DatagramSocket;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * URB class defines the URB algorithm. Like every algorithm in the stack it has the sendMessages and deliver functions which corresponds to the Broadcast and Deliver functions of the algorithms.
 * The deliver function always calls the corresponding function above in the abstraction stack.
 */


public class Urb implements Listener {
    private Beb beb;
    private NetworkTopology network;
    private HashSet<Triple<Integer, int[], ProcessDetails>> pendingMessages;
    private HashSet<Triple<Integer, int[], ProcessDetails>> delivered;
    private HashMap<Triple<Integer, int[], ProcessDetails>, Set<ProcessDetails>> ackedMessages;
    private Listener lcb;
    private ReentrantLock lock = new ReentrantLock();


    public Urb(ProcessDetails sender, DatagramSocket socket, NetworkTopology network, int timeout, Listener lcb) {
        this.beb = new Beb(sender, socket, network, timeout, this);
        this.network = network;
        this.ackedMessages = new HashMap<>();
        this.pendingMessages = new HashSet<>();
        this.delivered = new HashSet<>();
        this.lcb = lcb;
    }

    public void addMessages(ProcessDetails source, Integer payload, int[] vc) {
        lock.lock();
        try {
            pendingMessages.add(new Triple<>(payload, vc, source));
        } finally {
            lock.unlock();
        }
        beb.addMessage(source, payload, vc);
    }

    public void deliver(Triple<Integer, int[], ProcessDetails> t) {
        lcb.callback(t);
    }

    public void checkToDeliver() {
        Triple<Integer, int[], ProcessDetails> ts;
        try {

            do {
                lock.lock();
                try {
                    ts = pendingMessages.stream().filter(t -> canDeliver(t)
                            && !delivered.contains(t)).findAny().orElse(null);
                    if (!(ts == null)) {
                        delivered.add(ts);
                        pendingMessages.remove(ts);
                        ackedMessages.remove(ts);
                        deliver(ts);
                    }
                } finally {
                    lock.unlock();
                }
            } while (!(ts == null));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean canDeliver(Triple<Integer, int[], ProcessDetails> t) {
        int N = network.getProcessesInNetwork().length;
        Set numberAcked = ackedMessages.get(t);
        if (!(numberAcked == null)) {
            int size = numberAcked.size();
            return 2 * size >= N;
        }
        return false;
    }

    @Override
    public void callback(Message m) {
        Triple<Integer, int[], ProcessDetails> t = new Triple<>(m.getPayload(), m.getVectorClock(), m.getSource());
        ProcessDetails sender = m.getSender();

        if (!ackedMessages.containsKey(t)) {
            ackedMessages.put(t, new HashSet<>(Collections.singletonList(sender)));
        } else {
            Set<ProcessDetails> set = ackedMessages.get(t);
            set.add(sender);
            ackedMessages.put(t, set);
        }

        if (!pendingMessages.contains(t)) {
            m.setSender(sender);
            lock.lock();
            try {
                pendingMessages.add(t);
            } finally {
                lock.unlock();
            }
            beb.addMessage(t.getZ(), t.getX(), m.getVectorClock());
        }
        checkToDeliver();
    }

    @Override
    public void callback(Triple t) {
    }

}
