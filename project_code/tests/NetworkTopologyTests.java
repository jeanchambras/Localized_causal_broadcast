import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class NetworkTopologyTests {

    @Test
    public void testGetProcessesInNetwork() {
        ProcessDetails proc1 = new ProcessDetails(1, "127.0.0.1", 1001);
        ProcessDetails proc2 = new ProcessDetails(2, "127.0.0.1", 2002);
        ProcessDetails proc3 = new ProcessDetails(3, "127.0.0.1", 3003);

        ArrayList processes = new ArrayList();
        processes.add(proc1);
        processes.add(proc2);
        processes.add(proc3);

        NetworkTopology net = new NetworkTopology(processes);
        assertEquals(processes, net.getProcessesInNetwork());
    }

    @Test
    public void testGetProcessesFromId() {
        ProcessDetails proc1 = new ProcessDetails(1, "127.0.0.1", 1001);
        ProcessDetails proc11 = new ProcessDetails(1, "127.0.0.1", 1001);
        ProcessDetails proc2 = new ProcessDetails(2, "127.0.0.1", 2002);
        ProcessDetails proc3 = new ProcessDetails(3, "127.0.0.1", 3003);

        ArrayList processes = new ArrayList();
        processes.add(proc1);
        processes.add(proc2);
        processes.add(proc3);

        NetworkTopology net = new NetworkTopology(processes);
        assertEquals(proc1, net.getProcessFromId(1));
        assertEquals(proc11, net.getProcessFromId(1));
        assertEquals(proc2, net.getProcessFromId(2));
        assertEquals(proc3, net.getProcessFromId(3));
        assertNotEquals(proc1, net.getProcessFromId(3));
    }


    @Test(expected = IllegalArgumentException.class)
    public void testGetProcessesFromIdException() {
        ProcessDetails proc1 = new ProcessDetails(1, "127.0.0.1", 1001);
        ProcessDetails proc2 = new ProcessDetails(2, "127.0.0.1", 2002);

        ArrayList processes = new ArrayList();
        processes.add(proc1);
        processes.add(proc2);

        NetworkTopology net = new NetworkTopology(processes);
        net.getProcessFromId(5);
    }

}
