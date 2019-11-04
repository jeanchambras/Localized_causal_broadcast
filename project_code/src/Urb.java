import java.lang.reflect.Array;
import java.net.DatagramSocket;
import java.util.*;

public class Urb implements Listener {
    private Beb beb;
    private NetworkTopology network;
    private HashSet<Message> pendingMessages;
    private HashMap<Message, ArrayList<ProcessDetails>> ackedMessages;



    public Urb (DatagramSocket socket, NetworkTopology network, int numberOfMessages, int timeout){
        System.out.println("URB..");
        this.beb = new Beb(socket, network, numberOfMessages, timeout, this);
        this.pendingMessages = new HashSet<>();
        this.ackedMessages = new HashMap<>();
        this.network = network;

        /*
        ArrayList<ProcessDetails> newArray = new ArrayList<>();
        for (Message msg : beb.getMessages()){
            ackedMessages.put(msg, newArray);
        }*/

    }

    public void sendMessages(){
        ArrayList<Message> newMessages = beb.getMessages();
        for (Message message: newMessages){
            //System.out.print("message" + message.getPayload());
            pendingMessages.add(message);
        }
        beb.sendMessages();
    }

    public void deliver(Message m){

        //ACK THE MESSAGE
        if (ackedMessages.containsKey(m)){
            ackedMessages.get(m).add(m.getSource());
        } else {
            ProcessDetails localDetail = network.getProcessFromPort(beb.getSocket().getLocalPort());
            ArrayList<ProcessDetails> localDetails = new ArrayList<>();
            localDetails.add(localDetail);
            ackedMessages.put(m, localDetails);
        }
        //CHECK PENDING
        if (!pendingMessages.contains(m)){

        }


        System.out.println("Process " + m.getSource().getId() + " urb-delivered message : "+ m.getPayload() + " from process "+ m.getSource().getId());
    }

    public boolean checkPending(Message m ){


        return true;
    }

    @Override
    public void callback(Message m) {
        deliver(m);
    }
}
