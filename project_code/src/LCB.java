import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.IntStream;

public class LCB implements Listener {
    private Urb Urb;
    private int[] vcSend;
    private int[] vcReceive;
    private HashSet<Triple<String, int[], ProcessDetails>> pending;
    private int numberOfMessages;
    private FileWriter f;
    private ProcessDetails sender;
    private HashSet<ProcessDetails> causality;

    public LCB(ProcessDetails sender, DatagramSocket socket, int numberOfMessages, int timeout, FileWriter f, NetworkTopology network, HashSet<ProcessDetails> causality){
        this.Urb = new Urb(sender,socket,network,timeout,f,this);
        this.vcSend = new int[network.getProcessesInNetwork().size()];
        this.vcReceive = new int[network.getProcessesInNetwork().size()];
        this.pending = new HashSet<>();
        this.numberOfMessages = numberOfMessages;
        this.sender = sender;
        this.causality = causality;
        this.f = f;
    }


    public void sendMessages() {
        for (int i = 1; i <= numberOfMessages; ++i) {
            int localId = sender.getId();
            Urb.addMessages(sender,Integer.toString(i), vcSend.clone());
            vcSend[localId - 1]++;
            try {
                f.write("b " + i + "\n");
                f.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void deliver(Triple<String, int[], ProcessDetails> ts) {
        try {
            f.write("d " + ts.getZ().getId() + " " + ts.getX() + "\n");
            f.flush();
        } catch (IOException e) {
        }
    }
    public boolean lessThan (int[] v1, int[]v2){
        return IntStream.range(0,v1.length).allMatch(i -> v1[i] <= v2[i]);
    }

    @Override
    public void callback(Message m) {
    }

    @Override
    public void callback(Triple t) {
        pending.add(t);
        Triple<String,int[], ProcessDetails> ts;
        do {
            ts = pending.stream().filter(Objects::nonNull).filter(o ->lessThan(o.getY(),vcReceive)).findAny().orElse(null);
            if (!(ts == null)) {
                pending.remove(ts);
                vcReceive[ts.getZ().getId()-1]++;
                if (causality.contains(ts.getZ())){
                    vcSend[ts.getZ().getId() -1]++;
                }
                deliver(ts);
            }
        } while (!(ts == null));


    }

}
