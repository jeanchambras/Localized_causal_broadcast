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
    private FileWriter f;
    private HashMap<ProcessDetails, Integer> nextMessageToDeliver;
    private HashSet<Tuple<String, ProcessDetails>> pending;

    public FIFO(ProcessDetails sender, DatagramSocket socket, int numberOfMessages, int timeout, FileWriter f, NetworkTopology network) throws Exception {
        this.urb = new Urb(sender, socket, network, timeout, f, this);
        this.pending = new HashSet<>();
        this.nextMessageToDeliver = new HashMap<>();
        this.f = f;
        for (ProcessDetails process : network.getProcessesInNetwork()) {
            nextMessageToDeliver.put(process, 1);
        }
        ProcessDetails source = network.getProcessFromPort(socket.getLocalPort());
        for (int i = 1; i <= numberOfMessages; ++i) {
            urb.addMessages(source, Integer.toString(i));
            try {
                f.write("b " + i + "\n");
                f.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessages() {
        urb.sendMessages();
    }

    @Override
    public void callback(Message m) {
    }

    @Override
    public void callback(Tuple<String, ProcessDetails> t) {
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

    public void deliver(Tuple<String, ProcessDetails> t) {
        try {
            f.write("d " + t.getY().getId() + " " + t.getX() + "\n");
            f.flush();
        } catch (IOException e) {}
    }


}