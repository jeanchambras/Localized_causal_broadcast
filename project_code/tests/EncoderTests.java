import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class EncoderTests {

    @Test
    public void intToByte() {
        ProcessDetails proc1 = new ProcessDetails(1, "127.0.0.1", 1001);
        ProcessDetails proc2 = new ProcessDetails(2, "127.0.0.1", 2002);
        ProcessDetails proc3 = new ProcessDetails(3, "127.0.0.1", 3003);

        ArrayList processes = new ArrayList();
        processes.add(proc1);
        processes.add(proc2);
        processes.add(proc3);

        NetworkTopology net = new NetworkTopology(processes);
        Encoder encoder = new Encoder(net);
        int number = 60000;
        byte[] b = encoder.IntToByteArray(number);
        assertEquals(number, encoder.ByteArrayToInt(new byte[]{b[2], b[1], b[0]}));

        number = 70000;
        b = encoder.IntToByteArray(number);
        assertEquals(number, encoder.ByteArrayToInt(new byte[]{b[2], b[1], b[0]}));

        number = 0;
        b = encoder.IntToByteArray(number);
        assertEquals(number, encoder.ByteArrayToInt(new byte[]{b[2], b[1], b[0]}));

        number = 1;
        b = encoder.IntToByteArray(number);
        assertEquals(number, encoder.ByteArrayToInt(new byte[]{b[2], b[1], b[0]}));

        number = 16777216;
        b = encoder.IntToByteArray(number);
        assertNotEquals(number, encoder.ByteArrayToInt(new byte[]{b[2], b[1], b[0]}));

    }

    @Test
    public void testEncode1() {
        ProcessDetails proc1 = new ProcessDetails(0, "127.0.0.1", 1001);
        ProcessDetails proc2 = new ProcessDetails(1, "127.0.0.1", 2002);
        ProcessDetails proc3 = new ProcessDetails(2, "127.0.0.1", 3003);

        ArrayList processes = new ArrayList();
        processes.add(proc1);
        processes.add(proc2);
        processes.add(proc3);

        NetworkTopology net = new NetworkTopology(processes);
        Encoder encoder = new Encoder(net);
        int idSender = 1, idDestination = 2, idSource = 0;
        Integer payload = 1;
        int[] vc = new int[]{0, 1, 2};
        Message m = new Message(net.getProcessFromId(idDestination), net.getProcessFromId(idSource),
                payload, net.getProcessFromId(idSender), vc);
        byte[] b = encoder.encode(false, m);

        assertEquals(idSource, encoder.ByteArrayToInt(new byte[]{b[2], b[1]}));
        assertEquals(0, encoder.ByteArrayToInt(new byte[]{b[0]}));
        assertEquals(idDestination, encoder.ByteArrayToInt(new byte[]{b[4], b[3]}));
        assertEquals(idSender, encoder.ByteArrayToInt(new byte[]{b[6], b[5]}));
        assertEquals(payload, Integer.toString(encoder.ByteArrayToInt(new byte[]{b[9],b[8], b[7]})));
        assertArrayEquals(vc, encoder.byteArray2intArray(Arrays.copyOfRange(b,10,b.length)));
    }

    @Test
    public void testEncode2() {
        ProcessDetails proc1 = new ProcessDetails(18, "127.0.0.1", 1001);
        ProcessDetails proc2 = new ProcessDetails(19, "127.0.0.1", 2002);
        ProcessDetails proc3 = new ProcessDetails(20, "127.0.0.1", 3003);

        ArrayList processes = new ArrayList();
        processes.add(proc1);
        processes.add(proc2);
        processes.add(proc3);

        NetworkTopology net = new NetworkTopology(processes);
        Encoder encoder = new Encoder(net);
        int idSender = proc1.getId(), idDestination = proc2.getId(), idSource = proc3.getId();
        Integer payload = 789000;
        int[] vc = new int[]{7000000, 1, 2};
        Message m = new Message(net.getProcessFromId(idDestination), net.getProcessFromId(idSource),
                payload, net.getProcessFromId(idSender), vc);
        byte[] b = encoder.encode(false, m);

        assertEquals(idSource, encoder.ByteArrayToInt(new byte[]{b[2], b[1]}));
        assertEquals(0, encoder.ByteArrayToInt(new byte[]{b[0]}));
        assertEquals(idDestination, encoder.ByteArrayToInt(new byte[]{b[4], b[3]}));
        assertEquals(idSender, encoder.ByteArrayToInt(new byte[]{b[6], b[5]}));
        assertEquals(payload, Integer.toString(encoder.ByteArrayToInt(new byte[]{b[9],b[8], b[7]})));
        assertArrayEquals(vc, encoder.byteArray2intArray(Arrays.copyOfRange(b,10,b.length)));
    }


    @Test
    public void testEncodeDecode1() {
        ProcessDetails proc1 = new ProcessDetails(18, "127.0.0.1", 1001);
        ProcessDetails proc2 = new ProcessDetails(65535, "127.0.0.1", 2002);
        ProcessDetails proc3 = new ProcessDetails(20, "127.0.0.1", 3003);

        ArrayList processes = new ArrayList();
        processes.add(proc1);
        processes.add(proc2);
        processes.add(proc3);

        NetworkTopology net = new NetworkTopology(processes);
        Encoder encoder = new Encoder(net);
        Integer payload = 789000;
        int[] vc = new int[]{7000000, 1, 2};
        Message m = new Message(proc1, proc2,
                payload, proc1, vc);
        byte[] b = encoder.encode(false, m);



        Message m2 = new Message(proc3, proc1,
                payload, proc2, new int[]{7000000, 19877, 1232});
        byte[] b2 = encoder.encode(false, m2);
        assertEquals(m2, encoder.getMessage(b2));
    }

    @Test
    public void testEncodeDecode2() {
        ProcessDetails proc1 = new ProcessDetails(18, "127.0.0.1", 1001);
        ProcessDetails proc2 = new ProcessDetails(65535, "127.0.0.1", 2002);
        ProcessDetails proc3 = new ProcessDetails(20, "127.0.0.1", 3003);

        ArrayList processes = new ArrayList();
        processes.add(proc1);
        processes.add(proc2);
        processes.add(proc3);

        NetworkTopology net = new NetworkTopology(processes);
        Encoder encoder = new Encoder(net);

        Message m1 = new Message(proc3, proc3,
                765, proc3, new int[]{7, 547, 12});

        Message m2 = new Message(proc3, proc1,
                800, proc2, new int[]{7000000, 19877, 1232});

        Message m3 = new Message(proc3, proc1,
                800, proc2, new int[]{7000000, 19877, 1232});


        Tuple<Integer, Message> t1= new Tuple<>(0,m2);
        byte[] b1 = encoder.encode(false, m2);
        assertEquals(t1, encoder.decode(b1));

        Tuple<Integer, Message> t2= new Tuple<>(1,m2);
        byte[] b2 = encoder.encode(true, m2);
        assertEquals(t2, encoder.decode(b2));

        Tuple<Integer, Message> t3= new Tuple<>(1,m1);
        byte[] b3 = encoder.encode(true, m1);
        assertEquals(t3, encoder.decode(b3));
    }
}
