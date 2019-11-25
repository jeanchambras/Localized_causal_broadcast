import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramSocket;
import java.util.HashSet;

public class LCB implements Listener{
    private NetworkTopology network;
    private DatagramSocket socket;
    private FIFO fifo;
    private VectorClock vcSend;
    private VectorClock vcReceive;
    private HashSet<Triple<ProcessDetails, VectorClock, Message>> pending;
    private int numberOfMessages;
    private FileWriter f;
    private ProcessDetails sender;

    public LCB(ProcessDetails sender, DatagramSocket socket, int numberOfMessages, int timeout, FileWriter f, NetworkTopology network) throws Exception {
        this.fifo = new FIFO(sender, socket, numberOfMessages, timeout, f, network, this);
        this.network = network;
        this.socket = socket;
        this.vcSend = new VectorClock(network);
        this.vcReceive = new VectorClock(network);
        this.pending = new HashSet<>();
        this.numberOfMessages = numberOfMessages;
        this.sender = sender;
        this.f = f;
    }


    public void sendMessages(){
        for (int i = 1; i <= numberOfMessages; ++i) {
//            int[] VCSEND = vcSend.getArray();
//            int localId = sender.getId();
            //TODO: think about the indexing
//            VCSEND[localId-1]++;
            fifo.sendMessages(Integer.toString(i));
            try {
                f.write("b " + i + "\n");
                f.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
