import java.util.ArrayList;

public class VectorClock {
    private int[] vectorClock;


    public VectorClock(NetworkTopology nt){
        this.vectorClock = new int[nt.getProcessesInNetwork().size()];
        System.out.println("HELLLO"+this.vectorClock.length);
        for (ProcessDetails p : nt.getProcessesInNetwork()) {
            //id of the next message we are waiting for
            //messages ids start at 1
            vectorClock[p.getId()-1] = 1;
        }
    }



}
