import java.math.BigInteger;
import java.util.Arrays;

import static java.lang.System.arraycopy;


/**
 * The encoder class defines an encoder and decoder for the message object. To reduce network traffic instead of sending the full Message object we serialize its information into a 9 bytes
 * ByteArray. Made by Léo Bouraux, designed by Jean Chambras.
 */

public class Encoder {
    NetworkTopology nT;
    private final int SIZE;
    public Encoder(NetworkTopology networkTopology){
        this.nT = networkTopology;
        this.SIZE = 10 + 4 * networkTopology.getNumberOfpeers();
    }

    /**
     * +---------+-----------+--------+-------------+---------+---------+--------------+
     * | bits #  |   0 - 7   | 8 - 23 |   24 - 39   | 40 - 55 | 56 - 79 |   80 - ....  |
     * +---------+-----------+--------+-------------+---------+---------+--------------+
     * | bytes # |     0     |   1-2  |     3-4     |   5-6   |   7-9   |    10-...    |
     * +---------+-----------+--------+-------------+---------+---------+--------------+
     * | fields  | ack flag  | source | destination | sender  | payload | vector clock |
     * +---------+-----------+--------+-------------+---------+---------+--------------+
     * @param ack
     * @param m
     * @return
     */
    public byte[] encode (boolean ack, Message m) {
        byte[] b = new byte[SIZE];
        if (ack)
            b[0] = 1;
        else
            b[0] = 0;
        b[1] = IntToByteArray(m.getSource().getId())[0];
        b[2] = IntToByteArray(m.getSource().getId())[1];
        b[3] = IntToByteArray(m.getDestination().getId())[0];
        b[4] = IntToByteArray(m.getDestination().getId())[1];
        b[5] = IntToByteArray(m.getSender().getId())[0];
        b[6] = IntToByteArray(m.getSender().getId())[1];
        b[7] = IntToByteArray(m.getPayload())[0];
        b[8] = IntToByteArray(m.getPayload())[1];
        b[9] = IntToByteArray(m.getPayload())[2];
        byte[] vc_b = intArray2byteArray(m.getVectorClock());
        arraycopy(vc_b,0,b,10,vc_b.length);
        return b;
    }

    public Tuple<Integer, Message> decode (byte[] b) {
        int ack = ByteArrayToInt(new byte[] {b[0]});
        return new Tuple<>(ack, getMessage(b));
    }


    public Message getMessage(byte[] b){
        ProcessDetails source = nT.getProcessFromId(ByteArrayToInt(new byte[] {b[2], b[1]}));
        ProcessDetails destination = nT.getProcessFromId(ByteArrayToInt(new byte[] {b[4], b[3]}));
        ProcessDetails sender = nT.getProcessFromId(ByteArrayToInt(new byte[] {b[6], b[5]}));
        Integer payload = ByteArrayToInt(new byte[] {b[9],b[8], b[7]});
        int[] vector_clock = byteArray2intArray(Arrays.copyOfRange(b,10,SIZE));
        return new Message(destination, source, payload, sender, vector_clock);
    }

    public byte[] IntToByteArray( int data ) {
        byte[] result = new byte[3];
        result[0] = (byte) ((data & 0x000000FF));
        result[1] = (byte) ((data & 0x0000FF00) >> 8);
        result[2] = (byte) ((data & 0x00FF0000)>> 16);
        return result;
    }




        public  byte[] intArray2byteArray(int[]src) {
            int srcLength = src.length;
            byte[]dst = new byte[srcLength << 2];

            for (int i=0; i<srcLength; i++) {
                int x = src[i];
                int j = i << 2;
                dst[j++] = (byte) ((x >>> 0) & 0xff);
                dst[j++] = (byte) ((x >>> 8) & 0xff);
                dst[j++] = (byte) ((x >>> 16) & 0xff);
                dst[j++] = (byte) ((x >>> 24) & 0xff);
            }
            return dst;
        }

    public  int[] byteArray2intArray(byte[]src) {
        int dstLength = src.length >>> 2;
        int[]dst = new int[dstLength];

        for (int i=0; i<dstLength; i++) {
            int j = i << 2;
            int x = 0;
            x += (src[j++] & 0xff) << 0;
            x += (src[j++] & 0xff) << 8;
            x += (src[j++] & 0xff) << 16;
            x += (src[j++] & 0xff) << 24;
            dst[i] = x;
        }
        return dst;
    }
    public int ByteArrayToInt (byte[] bytes) {
        return new BigInteger(1, bytes).intValue();
    }
}
