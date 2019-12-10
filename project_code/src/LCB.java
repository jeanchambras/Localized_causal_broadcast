import java.io.BufferedWriter;
import java.io.IOException;
import java.net.DatagramSocket;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.IntStream;

public class LCB implements Listener {
    private Urb Urb;
    private int[] vcSend;
    private int[] vcReceive;
    private HashSet<Triple<Integer, int[], ProcessDetails>> pending;
    private int numberOfMessages;
    private BufferedWriter f;
    private ProcessDetails sender;
    private HashSet<ProcessDetails> causality;
    private ConcurrentLinkedQueue<Integer> sending;
    private int window;

    public LCB(ProcessDetails sender, DatagramSocket socket, int numberOfMessages, int timeout, BufferedWriter f, NetworkTopology network, HashSet<ProcessDetails> causality){
        this.Urb = new Urb(sender,socket,network,timeout,this);
        this.vcSend = new int[network.getProcessesInNetwork().size()];
        this.vcReceive = new int[network.getProcessesInNetwork().size()];
        this.pending = new HashSet<>();
        this.numberOfMessages = numberOfMessages;
        this.sender = sender;
        this.causality = causality;
        this.sending = new ConcurrentLinkedQueue<>();
        this.f = f;
        this.window = Math.max(1, 100/network.getNumberOfpeers());
    }


    public void sendMessages() {
        for (int i = 1; i <= numberOfMessages; ++i) {
            while(!(sending.size() < window)){

            }
            int localId = sender.getId();
            Urb.addMessages(sender,i, vcSend.clone());
            vcSend[localId - 1]++;
            try {
                f.write("b " + i + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            sending.add(i);
        }
    }

    public void deliver(Triple<Integer, int[], ProcessDetails> ts) {
        try {
            f.write("d " + ts.getZ().getId() + " " + ts.getX() + "\n");
        } catch (IOException e) {
        }
        if(ts.getZ().equals(sender)){
            sending.poll();
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
        Triple<Integer,int[], ProcessDetails> ts;
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
