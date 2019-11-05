import java.lang.reflect.Array;
import java.net.DatagramSocket;
import java.sql.SQLOutput;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Urb implements Listener {
    private Beb beb;
    private NetworkTopology network;
    private HashSet<Message> pendingMessages;
    private HashSet<Message> delivered;
    private HashSet<ProcessDetails> aliveProcesses;
    private HashMap<Message, Set<ProcessDetails>> ackedMessages;
    private DatagramSocket socket;
    private Integer numberOfMessages;
    private int port;


    public Urb (DatagramSocket socket, NetworkTopology network, int numberOfMessages, int timeout){
        this.beb = new Beb(socket, network, numberOfMessages, timeout, this);
        this.network = network;
        this.numberOfMessages = numberOfMessages;
        this.socket = socket;
        this.port = socket.getLocalPort();
        this.ackedMessages = new HashMap<>();

        this.pendingMessages = new HashSet<>();
        this.delivered = new HashSet<>();
        this.aliveProcesses = new HashSet<>();
        //We add to the set of alive processes all known processes initially
        aliveProcesses.addAll(network.getProcessesInNetwork());

//        Runnable checkpendingMsg = this::intermittentCallback;

//        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
//        executor.scheduleAtFixedRate(checkpendingMsg, 0, 100, TimeUnit.MICROSECONDS);


    }

    //TODO: why are working with set of messages instead of single messages?
    public void sendMessages(){
        ProcessDetails source = network.getProcessFromPort(socket.getLocalPort());
        ArrayList<Message> messages = new ArrayList<>();
        for (ProcessDetails destination : network.getProcessesInNetwork()) {
            for (int i = 1; i <= numberOfMessages; ++i){
                String messageNumber = Integer.toString(i);
                Message m = new Message(destination, source, messageNumber, source);
                messages.add(m);
                pendingMessages.add(m);
            }
        }
        beb.sendMessages(messages);
    }

    public  void deliver(Message m){

        //ADD ACK
        ProcessDetails localDetail = network.getProcessFromPort(beb.getSocket().getLocalPort());
        ProcessDetails sender = m.getSender();

        if(!ackedMessages.containsKey(m)) {
            ackedMessages.put(m, new HashSet<>(Arrays.asList(sender)));
        } else {
            System.out.println("wgerr");
            Set<ProcessDetails> set = ackedMessages.get(m);
            set.add(sender);
            ackedMessages.put(m, set);
        }
       

        //CHECK PENDING
        if (!pendingMessages.contains(m)){
            ArrayList<Message> messages = new ArrayList<>();
            m.setSender(network.getProcessFromPort(socket.getLocalPort()));
            messages.add(m);
            pendingMessages.add(m);
            beb.addMessages(messages);
        }

        intermittentCallback();
    }

    public void intermittentCallback(){
            Iterator<Message> it = pendingMessages.iterator();
            while (it.hasNext()){
                Message m = it.next();
                if (pendingMessages.contains(m) && canDeliver(m) && !delivered.contains(m)){
                    delivered.add(m);
                    it.remove();
                    System.out.println("URB-DELIVERED");

            }
        }
    }


    public boolean canDeliver(Message m){
        System.out.println(port + " :" + ackedMessages.get(m));
        int N = network.getProcessesInNetwork().size();
        if (ackedMessages.containsKey(m)){
            int numberAcked = ackedMessages.get(m).size();
            System.out.println("2 * " + numberAcked + ">= " + N);
            return 2*numberAcked >= N;
        }

        return false;
    }



    @Override
    public void callback(Message m) {
        deliver(m);
    }
}
