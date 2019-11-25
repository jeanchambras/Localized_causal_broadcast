import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class VectorClockTests {
    private NetworkTopology net;

    public VectorClockTests(){
        ArrayList<ProcessDetails> processesInNetwork = new ArrayList<>();
        ProcessDetails proc1 = new ProcessDetails(1,"127.0.0.1", 1001);
        ProcessDetails proc2 = new ProcessDetails(2,"127.0.0.1", 2002);
        processesInNetwork.add(proc1);
        processesInNetwork.add(proc2);
        this.net = new NetworkTopology(processesInNetwork);

    }
    @Test
    public void testEquality() {
        VectorClock vc1 = new VectorClock(net);
        VectorClock vc2 = new VectorClock(net);
        assertEquals(vc1, vc2);

    }

    @Test
    public void testHashes() {
        VectorClock vc1 = new VectorClock(net);
        VectorClock vc2 = new VectorClock(net);
        assertEquals(vc1.hashCode(), vc2.hashCode());

    }

}