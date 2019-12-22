import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ProcessDetailsTests {
    @Test
    public void testEquality() {
        ProcessDetails proc1 = new ProcessDetails(1,"127.0.0.1", 1001);
        ProcessDetails proc11 = new ProcessDetails(1,"127.0.0.1", 1001);
        ProcessDetails proc2 = new ProcessDetails(2,"127.0.0.1", 2002);
        ProcessDetails proc22 = new ProcessDetails(2,"127.0.0.1", 2002);


        assertEquals(proc1,proc11);
        assertEquals(proc2,proc22);
        assertNotEquals(proc1,proc22);
    }

    @Test
    public void testHashcode() {
        ProcessDetails proc1 = new ProcessDetails(1,"127.0.0.1", 1001);
        ProcessDetails proc11 = new ProcessDetails(1,"127.0.0.1", 1001);
        ProcessDetails proc2 = new ProcessDetails(2,"127.0.0.1", 2002);
        ProcessDetails proc22 = new ProcessDetails(2,"127.0.0.1", 2002);


        assertEquals(proc1.hashCode(),proc11.hashCode());
        assertEquals(proc2.hashCode(),proc22.hashCode());
        assertNotEquals(proc1.hashCode(),proc22.hashCode());
    }

    @Test
    public void testGetId() {
        ProcessDetails proc1 = new ProcessDetails(1,"127.0.0.1", 1001);
        ProcessDetails proc2 = new ProcessDetails(2,"127.0.0.1", 2002);

        assertEquals(proc1.getId(),1);
        assertEquals(proc2.getId(),2);
        assertNotEquals(proc1.getId(),2);
    }

    @Test
    public void testGetPort() {
        ProcessDetails proc1 = new ProcessDetails(1,"127.0.0.1", 1001);
        ProcessDetails proc2 = new ProcessDetails(2,"127.0.0.1", 2002);

        assertEquals(proc1.getPort(),1001);
        assertEquals(proc2.getPort(),2002);
        assertNotEquals(proc1.getPort(),2002);
    }
    @Test
    public void testGetAddress() {
        ProcessDetails proc1 = new ProcessDetails(1,"127.0.0.1", 1001);
        ProcessDetails proc2 = new ProcessDetails(2,"127.0.0.1", 2002);

        assertEquals(proc1.getAddress(),"127.0.0.1");
        assertEquals(proc2.getAddress(),"127.0.0.1");
    }
}
