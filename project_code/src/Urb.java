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
    private HashSet<Triple<String,int[], ProcessDetails>> pendingMessages;
    private HashSet<Triple<String,int[], ProcessDetails>> delivered;
    private HashSet<ProcessDetails> aliveProcesses;
    private HashMap<Triple<String,int[], ProcessDetails>, Set<ProcessDetails>> ackedMessages;
    private Listener lcb;



    public Urb(ProcessDetails sender, DatagramSocket socket, NetworkTopology network, int timeout, FileWriter f, Listener lcb) {
        this.beb = new Beb(sender, socket, network, timeout, this);
        this.network = network;
        this.ackedMessages = new HashMap<>();
        this.pendingMessages = new HashSet<>();
        this.delivered = new HashSet<>();
        this.aliveProcesses = new HashSet<>();
        this.lcb = lcb;

        //We add to the set of alive processes all known processes initially
        aliveProcesses.addAll(network.getProcessesInNetwork());
    }


    public void sendMessages() {
        beb.sendMessages();
    }

    public void addMessages(ProcessDetails source, String payload, int[] vc) {
        pendingMessages.add(new Triple<>(payload,vc, source));
        beb.addMessage(source, payload, vc);
    }

    public void deliver(Triple<String,int[], ProcessDetails> t) {
        lcb.callback(t);
    }

    public void checkToDeliver() {
            Triple<String,int[], ProcessDetails> ts;
            do {
                ts = pendingMessages.stream().filter(t-> canDeliver(t) && !delivered.contains(t)).findAny().orElse(null);
                if (!(ts == null)) {
                    delivered.add(ts);
                    pendingMessages.remove(ts);
                    deliver(ts);
                }
            } while (!(ts == null));
        }

    public boolean canDeliver(Triple<String,int[], ProcessDetails> t) {
        int N = network.getProcessesInNetwork().size();
        if (ackedMessages.containsKey(t)) {
            int numberAcked = ackedMessages.get(t).size();
            return 2 * numberAcked >= N;
        }

        return false;
    }

    @Override
    public void callback(Message m) {
        Triple<String,int[], ProcessDetails> t = new Triple<>(m.getPayload(),m.getVectorClock(), m.getSource());
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
            beb.addMessage(t.getZ(), t.getX(), m.getVectorClock());
        }
        checkToDeliver();
    }

    @Override
    public void callback(Triple t) {
    }

}
