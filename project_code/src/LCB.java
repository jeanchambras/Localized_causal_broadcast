import java.io.FileWriter;
import java.net.DatagramSocket;
import java.util.HashSet;

public class LCB implements Listener{



    private NetworkTopology network;
    private DatagramSocket socket;
    private FIFO fifo;
    private VectorClock vcSend;
    private VectorClock vcReceive;
    private HashSet<Triple<ProcessDetails, VectorClock, Message>> pending;





    public LCB(ProcessDetails sender, DatagramSocket socket, int numberOfMessages, int timeout, FileWriter f, NetworkTopology network) throws Exception {
        this.fifo = new FIFO(sender, socket, numberOfMessages, timeout, f, network, this);
        this.network = network;
        this.socket = socket;
        this.vcSend = new VectorClock(network);
        this.vcReceive = new VectorClock(network);
        this.pending = new HashSet<>();

    }


    public void sendMessage(Message m){
        int[] VCSEND = vcSend.getArray();
        try {
            int localId = network.getProcessFromPort(socket.getLocalPort()).getId();
            //TODO: think about the indexing
            VCSEND[localId-1]++;
        } catch (Exception e) {
            e.printStackTrace();
        }
        fifo.sendMessages();
        System.out.println("SENDING MESSAGE to "+ m.getDestination());
    }





    public void receiveMessage(Message m){
        VectorClock vc = m.getVectorClock();
        try {
            int localId = network.getProcessFromPort(socket.getLocalPort()).getId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int[] VC = vc.getArray();
        //TODO: here should we take the source or the sender?
        ProcessDetails source = m.getSource();
        VC[source.getId()-1]++;


    }



    public void deliver()
    {
        System.out.println("LCB DELIVERED");
    }

    @Override
    public void callback(Message m){
        deliver();
    }

    @Override
    public void callback(Tuple t){
        deliver();

    }

}
