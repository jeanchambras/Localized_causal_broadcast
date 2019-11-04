import java.net.DatagramSocket;
import java.util.*;


public class PerfectFailureDetector{
    private Set<ProcessDetails> alive;
    private Set<ProcessDetails> detected;
    private DatagramSocket socket;


    public  PerfectFailureDetector(NetworkTopology network, int timeout){
        this.alive = new HashSet<>();
        this.detected = new HashSet<>();
        alive.addAll(network.getProcessesInNetwork());

        new Thread(() -> {
            boolean running = true;
            while (running) {
                ArrayList<ProcessDetails> processesInNetwork = network.getProcessesInNetwork();
                for (ProcessDetails p: processesInNetwork) {
                    if(!alive.contains(p) && !detected.contains(p)){
                        System.out.println("Process " + p.getId() + " has crashed");
                        detected.add(p);
                    }
                }
                alive.clear();
                try {
                    Thread.sleep(2* timeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public Set<ProcessDetails> getAlive() {
        return alive;
    }
}
