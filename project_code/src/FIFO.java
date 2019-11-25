import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.HashSet;


/** FIFO implements the FIFO algorithm by using the URB algorithm lower in the stack. It also writes the expected output to a log file.
 *
 */

public class FIFO implements Listener {
    private Urb urb;
    private Listener lcb;
    private FileWriter f;
    private HashMap<ProcessDetails, Integer> nextMessageToDeliver;
    private HashSet<Tuple<String, ProcessDetails>> pending;
    private ProcessDetails source;

    public FIFO(ProcessDetails sender, DatagramSocket socket, int numberOfMessages, int timeout, FileWriter f, NetworkTopology network, Listener lcb) throws Exception {
        this.urb = new Urb(sender, socket, network, timeout, f, this);
        this.pending = new HashSet<>();
        this.nextMessageToDeliver = new HashMap<>();
        this.f = f;
        this.lcb = lcb;

        for (ProcessDetails process : network.getProcessesInNetwork()) {
            nextMessageToDeliver.put(process, 1);
        }
        ProcessDetails source = network.getProcessFromPort(socket.getLocalPort());
    }

    public void sendMessages(String s) {
        urb.addMessages(source, s);
    }

    @Override
    public void callback(Message m) {
    }

    @Override
    public void callback(Tuple<String, ProcessDetails> t) {
        // System.out.println(pending.size());
        pending.add(t);

        Tuple<String, ProcessDetails> ts;
        do {
            ts = pending.stream().filter(o -> nextMessageToDeliver.get(o.getY()) == Integer.parseInt(o.getX())).findAny().orElse(null);
            if (!(ts == null)) {
                deliver(ts);
                int next = nextMessageToDeliver.get(ts.getY());
                next++;
                nextMessageToDeliver.put(ts.getY(), next);

                pending.remove(ts);
            }
        } while (!(ts == null));

    }

    public void deliver(Tuple<String, ProcessDetails> ts) {
        this.lcb.callback(ts);
        try {
            f.write("d " + ts.getY().getId() + " " + ts.getX() + "\n");
            f.flush();
        } catch (IOException e) {}
    }


}