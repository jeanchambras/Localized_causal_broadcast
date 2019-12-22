import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TripleTests {
    @Test
    public void testEquality() {
        ProcessDetails proc1 = new ProcessDetails(1,"127.0.0.1", 1001);
        ProcessDetails proc11 = new ProcessDetails(1,"127.0.0.1", 1001);
        ProcessDetails proc2 = new ProcessDetails(2,"127.0.0.1", 2002);
        ProcessDetails proc22 = new ProcessDetails(2,"127.0.0.1", 2002);

        Triple triple1 = new Triple("1", new int[]{1, 2, 3},proc1);
        Triple triple11 = new Triple("1", new int[]{1, 2, 3},proc11);
        Triple triple111 = new Triple("1", new int[]{1, 2, 3},proc1);
        Triple triple2 = new Triple("1", new int[]{3, 2, 1},proc1);
        Triple triple3 = new Triple("1", new int[]{3, 2, 1},proc2);

        assertEquals(triple1, triple11);
        assertEquals(triple1, triple111);
        assertNotEquals(triple1,triple2);
        assertNotEquals(triple3,triple2);
    }

    @Test
    public void testHashcode() {
        ProcessDetails proc1 = new ProcessDetails(1,"127.0.0.1", 1001);
        ProcessDetails proc11 = new ProcessDetails(1,"127.0.0.1", 1001);
        ProcessDetails proc2 = new ProcessDetails(2,"127.0.0.1", 2002);

        Triple triple1 = new Triple("1", new int[]{1, 2, 3},proc1);
        Triple triple11 = new Triple("1", new int[]{1, 2, 3},proc11);
        Triple triple111 = new Triple("1", new int[]{1, 2, 3},proc1);
        Triple triple2 = new Triple("1", new int[]{3, 2, 1},proc1);
        Triple triple3 = new Triple("1", new int[]{3, 2, 1},proc2);

        assertEquals(triple1.hashCode(), triple11.hashCode());
        assertEquals(triple1.hashCode(), triple111.hashCode());
        assertNotEquals(triple1.hashCode(), triple2.hashCode());
        assertNotEquals(triple2.hashCode(), triple3.hashCode());
    }

}