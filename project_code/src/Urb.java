import java.io.FileWriter;
import java.net.DatagramSocket;
import java.util.*;

/**
 * URB class defines the URB algorithm. Like every algorithm in the stack it has the sendMessages and deliver functions which corresponds to the Broadcast and Deliver functions of the algorithms.
 * The deliver function always calls the corresponding function above in the abstraction stack.
 */


public class Urb implements Listener {
    private Beb beb;
    private NetworkTopology network;
    private HashSet<Tuple<String, ProcessDetails>> pendingMessages;
    private HashSet<Tuple<String, ProcessDetails>> delivered;
    private HashSet<ProcessDetails> aliveProcesses;
    private HashMap<Tuple<String, ProcessDetails>, Set<ProcessDetails>> ackedMessages;
    private Listener fifo;


    public Urb(ProcessDetails sender, DatagramSocket socket, NetworkTopology network, int timeout, FileWriter f, Listener fifo) {
        this.beb = new Beb(sender, socket, network, timeout, this);
        this.network = network;
        this.ackedMessages = new HashMap<>();
        this.pendingMessages = new HashSet<>();
        this.delivered = new HashSet<>();
        this.aliveProcesses = new HashSet<>();
        this.fifo = fifo;
        //We add to the set of alive processes all known processes initially
        aliveProcesses.addAll(network.getProcessesInNetwork());
    }


    public void sendMessages() {
        beb.sendMessages();
    }

    public void addMessages(ProcessDetails source, String payload) {
        pendingMessages.add(new Tuple<>(payload, source));
        beb.addMessage(source, payload);
    }

    public void deliver(Tuple<String, ProcessDetails> t) {
        fifo.callback(t);
    }

    public void checkToDeliver() {
        Iterator<Tuple<String, ProcessDetails>> it = pendingMessages.iterator();
        ////
        while (it.hasNext()) {
            /////
            Tuple<String, ProcessDetails> t = it.next();
            if (pendingMessages.contains(t) && canDeliver(t) && !delivered.contains(t)) {
                delivered.add(t);
                it.remove();
                deliver(t);
            }
        }
    }

    public boolean canDeliver(Tuple<String, ProcessDetails> t) {
        int N = network.getProcessesInNetwork().size();
        if (ackedMessages.containsKey(t)) {
            int numberAcked = ackedMessages.get(t).size();
            return 2 * numberAcked >= N;
        }

        return false;
    }

    @Override
    public void callback(Message m) {
        Tuple<String, ProcessDetails> t = new Tuple<>(m.getPayload(), m.getSource());
        beb.addMessage(t.getY(), t.getX());
        ProcessDetails sender = m.getSender();

        if (!ackedMessages.containsKey(t)) {
            ackedMessages.put(t, new HashSet<>(Collections.singletonList(sender)));
        } else {
            Set<ProcessDetails> set = ackedMessages.get(t);
            set.add(sender);
            ackedMessages.put(t, set);
        }

        //CHECK PENDING
        if (!pendingMessages.contains(t)) {
            m.setSender(sender);
            pendingMessages.add(t);
            beb.addMessage(t.getY(), t.getX());
        }
        checkToDeliver();
    }

    @Override
    public void callback(Tuple t) {
    }

}
