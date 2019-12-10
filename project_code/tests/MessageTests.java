import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class MessageTests {

    @Test
    public void testEquality() {
        ProcessDetails proc1 = new ProcessDetails(1, "127.0.0.1", 1001);
        ProcessDetails proc11 = new ProcessDetails(1, "127.0.0.1", 1001);
        ProcessDetails proc2 = new ProcessDetails(2, "127.0.0.1", 2002);
        ProcessDetails proc22 = new ProcessDetails(2, "127.0.0.1", 2002);
        ProcessDetails proc3 = new ProcessDetails(3, "127.0.0.1", 3003);
        ProcessDetails proc33 = new ProcessDetails(3, "127.0.0.1", 3003);

        Message message1 = new Message(proc2, proc1, 12345, proc3, new int[]{1, 2, 3});
        Message message11 = new Message(proc2, proc1, 12345, proc3, new int[]{1, 2, 3});
        Message message111 = new Message(proc22, proc1, 12345, proc33, new int[]{1, 2, 3});
        Message message2 = new Message(proc22, proc1, 12347, proc33, new int[]{1, 2, 3});
        Message message3 = new Message(proc1, proc2, 12345, proc3, new int[]{1, 2, 3});
        Message message33 = new Message(proc11, proc2, 12345, proc3, new int[]{1, 2, 3});
        Message message4 = new Message(proc11, proc2, 12345, proc3, new int[]{1, 1, 1});

        assertEquals(message1, message11);
        assertNotEquals(message1, message2);
        assertNotEquals(message1, message2);
        assertEquals(message1, message111);
        assertNotEquals(message1, message3);
        assertEquals(message33, message3);
        assertNotEquals(message4, message3);
    }

    @Test
    public void testHashcode() {
        ProcessDetails proc1 = new ProcessDetails(1, "127.0.0.1", 1001);
        ProcessDetails proc11 = new ProcessDetails(1, "127.0.0.1", 1001);
        ProcessDetails proc2 = new ProcessDetails(2, "127.0.0.1", 2002);
        ProcessDetails proc22 = new ProcessDetails(2, "127.0.0.1", 2002);
        ProcessDetails proc3 = new ProcessDetails(3, "127.0.0.1", 3003);
        ProcessDetails proc33 = new ProcessDetails(3, "127.0.0.1", 3003);

        Message message1 = new Message(proc2, proc1, 12345, proc3, new int[]{1, 2, 3});
        Message message11 = new Message(proc2, proc1, 12345, proc3, new int[]{1, 2, 3});
        Message message111 = new Message(proc22, proc1, 12345, proc33, new int[]{1, 2, 3});
        Message message2 = new Message(proc22, proc1, 12347, proc33, new int[]{1, 2, 3});
        Message message3 = new Message(proc1, proc2, 12345, proc3, new int[]{1, 2, 3});
        Message message33 = new Message(proc11, proc2, 12345, proc3, new int[]{1, 2, 3});
        Message message4 = new Message(proc11, proc2, 12345, proc3, new int[]{1, 1, 1});
        Message message5 = new Message(proc2, proc3, 12345, proc1, new int[]{1, 2, 3});

        assertEquals(message1.hashCode(), message11.hashCode());
        assertNotEquals(message1.hashCode(), message2.hashCode());
        assertNotEquals(message1.hashCode(), message2.hashCode());
        assertEquals(message1.hashCode(), message111.hashCode());
        assertNotEquals(message1.hashCode(), message3.hashCode()); // reversed destination and source should produce different hashcode
        assertNotEquals(message1.hashCode(), message5.hashCode()); // reversed sender and source should produce different hashcode
        assertEquals(message33.hashCode(), message3.hashCode());
        assertNotEquals(message4.hashCode(), message3.hashCode());
    }
}
