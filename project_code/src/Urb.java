import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramSocket;
import java.sql.SQLOutput;
import java.util.*;

public class Urb implements Listener {
    private Beb beb;
    private NetworkTopology network;
    private HashSet<Tuple<String, ProcessDetails>> pendingMessages;
    private HashSet<Tuple<String, ProcessDetails>> delivered;
    private HashSet<ProcessDetails> aliveProcesses;
    private HashMap<Tuple<String, ProcessDetails>, Set<ProcessDetails>> ackedMessages;
    private DatagramSocket socket;
    private Integer numberOfMessages;
    private int port;
    private FileWriter f;


    public Urb (DatagramSocket socket, NetworkTopology network, int numberOfMessages, int timeout, FileWriter f){
        this.beb = new Beb(socket, network, numberOfMessages, timeout, this);
        this.network = network;
        this.numberOfMessages = numberOfMessages;
        this.socket = socket;
        this.port = socket.getLocalPort();
        this.ackedMessages = new HashMap<>();
        this.pendingMessages = new HashSet<>();
        this.delivered = new HashSet<>();
        this.aliveProcesses = new HashSet<>();
        this.f=f;
        //We add to the set of alive processes all known processes initially
        aliveProcesses.addAll(network.getProcessesInNetwork());
    }

    public void sendMessages(){
        ProcessDetails source = network.getProcessFromPort(socket.getLocalPort());
            for (int i = 1; i <= numberOfMessages; ++i){
                beb.addMessages(source, Integer.toString(i));
                pendingMessages.add(new Tuple<>(Integer.toString(i), source));
                try {
                    f.write("b " + i + "\n");
                    f.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            beb.sendMessages();
    }


    public void deliver(Tuple<String, ProcessDetails> t){
        try {
            f.write("d "+ t.y.getId() +" "+ t.x + "\n");
            f.flush();
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }

    public void checkToDeliver(){
            Iterator<Tuple<String, ProcessDetails>> it = pendingMessages.iterator();
            while (it.hasNext()){
                Tuple t = it.next();
                if (pendingMessages.contains(t) && canDeliver(t) && !delivered.contains(t)){
                    delivered.add(t);
                    it.remove();
                    deliver(t);

            }
        }
    }


    public boolean canDeliver(Tuple<String, ProcessDetails> t){
//        System.out.println(port + " :" + ackedMessages.get(t));
        int N = network.getProcessesInNetwork().size();
        if (ackedMessages.containsKey(t)){
            int numberAcked = ackedMessages.get(t).size();
//            System.out.println("2 * " + numberAcked + ">= " + N);
            return 2*numberAcked >= N;
        }

        return false;
    }



    @Override
    public void callback(Message m) {
        Tuple<String, ProcessDetails> t = new Tuple(m.getPayload(), m.getSource());
//        System.out.println(m.getSender().getPort() + " -> " + port + " : beb - deliver = " + t.x + " : " + t.y.getId());
        beb.addMessages(t.y, t.x);
        //ADD ACK
        ProcessDetails localDetail = network.getProcessFromPort(beb.getSocket().getLocalPort());
        ProcessDetails sender = m.getSender();

        if(!ackedMessages.containsKey(t)) {
            ackedMessages.put(t, new HashSet<>(Arrays.asList(sender)));
        } else {
            Set<ProcessDetails> set = ackedMessages.get(t);
            set.add(sender);
            ackedMessages.put(t, set);
        }

        //CHECK PENDING
        if (!pendingMessages.contains(t)){
            m.setSender(network.getProcessFromPort(socket.getLocalPort()));
            pendingMessages.add(t);
            beb.addMessages(t.y, t.x);
        }
        checkToDeliver();
    }

    public void stop(){
        beb.stop();
    }
}
