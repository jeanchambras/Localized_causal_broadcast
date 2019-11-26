import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;


/** FIFO implements the FIFO algorithm by using the URB algorithm lower in the stack. It also writes the expected output to a log file.
 *
 */

public class FIFO implements Listener {
    private Urb urb;
    private Listener lcb;
    private FileWriter f;
    private HashMap<ProcessDetails, Integer> nextMessageToDeliver;
    private HashSet<Triple<String,VectorClock, ProcessDetails>> pending;
    private ProcessDetails  source;
    public FIFO(ProcessDetails sender, DatagramSocket socket, int timeout, FileWriter f, NetworkTopology network, Listener lcb) throws Exception {
        this.urb = new Urb(sender, socket, network, timeout, f, this);
        this.pending = new HashSet<>();
        this.nextMessageToDeliver = new HashMap<>();
        this.f = f;
        this.lcb = lcb;

        for (ProcessDetails process : network.getProcessesInNetwork()) {
            nextMessageToDeliver.put(process, 1);
        }
        this.source = network.getProcessFromPort(socket.getLocalPort());
    }

    public void sendMessages(String s,int[] vc) {
        urb.addMessages(source, s,vc);
    }

    @Override
    public void callback(Message m) {
    }

    @Override
    public void callback(Triple<String,int[], ProcessDetails> t) {
//        pending.add(t);
//
//        Triple<String,VectorClock, ProcessDetails> ts = null;
//        do {
//            ts = pending.stream().filter(Objects::nonNull).filter(o -> nextMessageToDeliver.get(o.getZ()).equals(Integer.parseInt(o.getX()))).findAny().orElse(null);
//
//            if (!(ts == null)) {
//                deliver(ts);
//                int next = nextMessageToDeliver.get(ts.getZ());
//                next++;
//                nextMessageToDeliver.put(ts.getZ(), next);
//
//                pending.remove(ts);
//            }
//        } while (!(ts == null));

    }

    public void deliver(Triple<String,VectorClock, ProcessDetails> ts) {
//        this.lcb.callback(ts);

    }


}