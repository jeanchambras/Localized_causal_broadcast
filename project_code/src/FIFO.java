import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class FIFO implements Listener {
    private Urb urb;
    private NetworkTopology network;
    private DatagramSocket socket;
    private Integer numberOfMessages;
    private FileWriter f;
    private HashMap<ProcessDetails, Integer> nextMessageToDeliver;
    private HashSet<Tuple<String,ProcessDetails>> pending;

    public FIFO(DatagramSocket socket, int numberOfMessages,int timeout, FileWriter f, NetworkTopology network){
    this.urb = new Urb(socket,network,numberOfMessages,timeout,f,this);
    this.network = network;
    this.socket = socket;
    this.numberOfMessages=numberOfMessages;
    this.pending = new HashSet<>();
    this.nextMessageToDeliver = new HashMap<>();
    this.f = f;
        for (ProcessDetails process : network.getProcessesInNetwork()) {
            nextMessageToDeliver.put(process,1);
        }
        ProcessDetails source = network.getProcessFromPort(socket.getLocalPort());
        for (int i = 1; i <= numberOfMessages; ++i){
            urb.addMessages(source, Integer.toString(i));
            try {
                f.write("b " + i + "\n");
                f.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessages(){
        urb.sendMessages();
    }


    @Override
    public void callback(Message m) {
    }

    @Override
    public void callback(Tuple<String,ProcessDetails> t) {
        pending.add(t);

            Tuple<String, ProcessDetails> ts;
            do {
            ts = pending.stream().filter(o -> nextMessageToDeliver.get(o.y) == Integer.parseInt(o.x)).findAny().orElse(null);
            if (!(ts == null)){
                deliver(ts);
                int next = nextMessageToDeliver.get(ts.y);
                next++;
                nextMessageToDeliver.put(ts.y,next);
                pending.remove(ts);
            }
            } while(!(ts == null));
//        Iterator<Tuple<String, ProcessDetails>> it = pending.iterator();
//        while(it.hasNext()){
//            Tuple<String,ProcessDetails> ts = it.next();
//            if (nextMessageToDeliver.get(ts.y) == Integer.parseInt(ts.x)){
//                deliver(ts);
//                int next = nextMessageToDeliver.get(ts.y);
//                next++;
//                nextMessageToDeliver.put(ts.y,next);
//                it.remove();
//            }
//        }
    }

    public void deliver(Tuple<String, ProcessDetails> t){
        try {
            f.write("d "+ t.y.getId() +" "+ t.x + "\n");
            f.flush();
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }

    public void stop(){
        urb.stop();
    }


}