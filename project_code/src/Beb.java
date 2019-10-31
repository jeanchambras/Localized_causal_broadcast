import java.net.DatagramSocket;
import java.util.ArrayList;

public class Beb implements Listener {
    private PerfectLink perfectLink;


    public Beb (DatagramSocket socket, NetworkTopology network, String message, PerfectFailureDetector failureDetector, int timeout){
        this.perfectLink = new PerfectLink(socket, network, failureDetector, timeout, this);
        ArrayList<Message> messages = new ArrayList();
        for (ProcessDetails processDetails : network.getProcessesInNetwork()) {
            messages.add(new Message(processDetails,network.getProcessFromPort(socket.getLocalPort()), message));
        }
        perfectLink.addMessagesToQueue(messages);
    }

    public void sendMessages(){
        perfectLink.sendMessages();
    }
    @Override
    public void callback() {
        System.out.println("Callback");
        deliver();
    }

    public void deliver(){

    }

}
