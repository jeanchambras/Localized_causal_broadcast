import java.net.DatagramSocket;
import java.util.ArrayList;

public class Beb implements Listener {
    private int c;
    private PerfectLink perfectLink;


    public Beb (DatagramSocket socket, NetworkTopology network, int numberOfMessages, PerfectFailureDetector failureDetector, int timeout){
        this.c = 0;
        this.perfectLink = new PerfectLink(socket, network, failureDetector, timeout, this);
        ArrayList<Message> messages = new ArrayList();
        for (ProcessDetails processDetails : network.getProcessesInNetwork()) {
            for (int i = 1; i <= numberOfMessages; ++i){
                messages.add(new Message(processDetails,network.getProcessFromPort(socket.getLocalPort()), Integer.toString(i)));
            }
        }
        perfectLink.addMessagesToQueue(messages);
    }

    public void sendMessages(){
        perfectLink.sendMessages();
    }
    @Override
    public void callback() {
        deliver();
        System.out.println(++c);
    }

    public void deliver(){

    }

}
