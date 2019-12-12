import org.junit.Test;

import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class LCBTest {
    public boolean lessThan (int[] v1, int[]v2){
        return IntStream.range(0,v1.length).allMatch(i -> v1[i] <= v2[i]);
    }

    @Test
    public void lessThanTests(){
        int[] a = new int[]{1,0,0,0,0,0,0};
        int[] b = new int[]{0,0,0,1,0,0,0};
        int[] c = new int[]{0,0,0,0,0,0,0};
        int[] d = new int[]{0,0,0,2,0,0,0};

        int[] e = new int[]{1,2,3,4,5,5,5};
        int[] f = new int[]{2,4,2,4,5,5,5};
        int[] g = new int[]{2,4,3,5,6,6,6};

        assertFalse(lessThan(a,b));
        assertFalse(lessThan(b,a));
        assertTrue(lessThan(c,a));
        assertTrue(lessThan(c,b));
        assertFalse(lessThan(b,c));
        assertTrue(lessThan(b,d));

        assertFalse(lessThan(e,f));
        assertTrue(lessThan(e,g));
    }
}
