import java.net.DatagramSocket;
import java.util.*;


public class PerfectFailureDetector{
    private Set<ProcessDetails> alive;
    private Set<ProcessDetails> detected;
    private DatagramSocket socket;
    private NetworkTopology network;
    private int timeout;


    public  PerfectFailureDetector(NetworkTopology network, int timeout){
        this.alive = new HashSet<>();
        this.detected = new HashSet<>();
        this.timeout = timeout;
        alive.addAll(network.getProcessesInNetwork());
    }

    public void start(){
        new Thread(new Runnable() {
            public void run() {
                boolean running = true;
                while (running) {
                    getNetwork().getProcessesInNetwork().forEach((ProcessDetails p) -> {
                        if(!alive.contains(p) && !detected.contains(p)){
                            System.out.println("Process " + p.getId() + " has crashed");
                            detected.add(p);
                        }
                    });
                    alive.clear();
                    try {
                        Thread.sleep(2* timeout);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public Set<ProcessDetails> getAlive() {
        return alive;
    }

    public NetworkTopology getNetwork() {
        return network;
    }
}
