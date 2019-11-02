import java.lang.reflect.Array;
import java.net.DatagramSocket;
import java.util.*;

public class Urb implements Listener {
    private Beb beb;
    private HashSet<Message> pendingMessages;
    private HashMap<Message, List<ProcessDetails>> ackedMessages;



    public Urb (DatagramSocket socket, NetworkTopology network, int numberOfMessages, int timeout){
        System.out.println("URB..");
        this.beb = new Beb(socket, network, numberOfMessages, timeout, this);
        this.pendingMessages = new HashSet<>();
        this.ackedMessages = new HashMap<>();

        ArrayList<ProcessDetails> newArray = new ArrayList<>();
        for (Message msg : beb.getMessages()){
            ackedMessages.put(msg, newArray);
        }

    }

    public void sendMessages(){
        ArrayList<Message> newMessages = beb.getMessages();
        //System.out.print("CURRENT PENDING: ");
        for (Message message: newMessages){
            System.out.print(message.getPayload());
            pendingMessages.add(message);
        }
        beb.sendMessages();
    }

    public void deliver(Message m){
        System.out.println("\n   Message sender:"+m.getSender());
        //System.out.println("ACKED" + ackedMessages.keySet());
        System.out.println("Process " + m.getSource().getId() + " urb-delivered message : "+ m.getPayload() + " from process "+ m.getSource().getId() + " sender "+ m.getSender().getAddress()+m.getSender().getPort());
        System.out.println("POPO"+ackedMessages.get(m));
        //todo not put only one PProcess detail
        ackedMessages.put(m, Collections.singletonList(m.getSender()));

        System.out.println("ACKED" + ackedMessages.get(m));

        //System.out.println(ackedMessages.keySet());
        //ackedMessages.get(m).add(m.getSender());
        //System.out.println(beb.getNetworkTopology().getProcessFromPort(beb.getSocket().getLocalPort()).getPort());
    }

    @Override
    public void callback(Message m) {
        deliver(m);
    }
}
