import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class UrbTest {
    private NetworkTopology net;

    public UrbTest (){
        ArrayList<ProcessDetails> processesInNetwork = new ArrayList<>();
        ProcessDetails proc1 = new ProcessDetails(1,"127.0.0.1", 1001);
        ProcessDetails proc2 = new ProcessDetails(2,"127.0.0.1", 2002);
        processesInNetwork.add(proc1);
        processesInNetwork.add(proc2);
        this.net = new NetworkTopology(processesInNetwork);

    }
    @Test
    public void testEquality() {
        ProcessDetails proc1 = new ProcessDetails(1,"127.0.0.1", 1001);
        ProcessDetails proc11 = new ProcessDetails(1,"127.0.0.1", 1001);
        ProcessDetails proc2 = new ProcessDetails(2,"127.0.0.1", 2002);
        ProcessDetails proc22 = new ProcessDetails(2,"127.0.0.1", 2002);
        VectorClock vc1 = new VectorClock(net);
        VectorClock vc2 = new VectorClock(net);
        Triple t1 = new Triple("3", vc1, proc1);
        Triple t2 = new Triple("3", vc2, proc11);
        assertEquals(t1, t2);

    }

    @Test
    public void testHashes() {
        ProcessDetails proc1 = new ProcessDetails(1,"127.0.0.1", 1001);
        ProcessDetails proc11 = new ProcessDetails(1,"127.0.0.1", 1001);
        ProcessDetails proc2 = new ProcessDetails(2,"127.0.0.1", 2002);
        ProcessDetails proc22 = new ProcessDetails(2,"127.0.0.1", 2002);
        VectorClock vc1 = new VectorClock(net);
        VectorClock vc2 = new VectorClock(net);
        Triple t1 = new Triple("3", vc1, proc1);
        Triple t2 = new Triple("3", vc2, proc11);
        assertEquals(t1.hashCode(), t2.hashCode());

    }
}