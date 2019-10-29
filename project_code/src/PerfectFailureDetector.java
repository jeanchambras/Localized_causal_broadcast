import java.util.*;
import java.util.concurrent.RunnableScheduledFuture;


public class PerfectFailureDetector implements Runnable {
    Set<Process> alive;
    Set<Process> detected;
    Integer timeout;

    public  PerfectFailureDetector(Set<Process> processes, Integer timeout){
        this.alive = new HashSet<>();
        this.alive.addAll(processes);
        this.detected = new HashSet<>();
        this.timeout = timeout;
    }

    @Override
    public void run() {
        while(true){
            alive.forEach((Process p) -> {
                if (!alive.contains(p) && !detected.contains(p)) {
                    detected.add(p);
                    System.out.println("Process : " + Integer.toString(p.getProcessId()) + " has crashed");
                }
                alive.clear();
            });

            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
