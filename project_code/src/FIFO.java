import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramSocket;

public class FIFO implements Listener {
    private Urb urb;
    private NetworkTopology network;
    private DatagramSocket socket;
    private Integer numberOfMessages;
    private FileWriter f;

    public FIFO(DatagramSocket socket, int numberOfMessages,int timeout, FileWriter f, NetworkTopology network){
    this.urb = new Urb(socket,network,numberOfMessages,timeout,f,this);
    this.network = network;
    this.socket = socket;
    this.numberOfMessages=numberOfMessages;
    this.f = f;

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
    public void callback(Tuple t) {
        deliver(t);
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