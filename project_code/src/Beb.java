import java.net.DatagramSocket;
import java.util.ArrayList;

public class Beb implements Listener {
    private PerfectLink perfectLink;


    public Beb (DatagramSocket socket, NetworkTopology network, int numberOfMessages, int timeout){
        this.perfectLink = new PerfectLink(socket, timeout, this);
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
    public void callback(Message m) {
        deliver(m);
    }

    public void deliver(Message m){
        System.out.println("Process " + m.getSource().getId() + " beb-delivered message : "+ m.getPayload() + " from process "+ m.getSource().getId());
    }

}
