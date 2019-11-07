import java.math.BigInteger;

public class Encoder {
    NetworkTopology nT;

    public Encoder(NetworkTopology networkTopology){
        this.nT = networkTopology;
    }

    public byte[] encode (Message m ) {
        byte[] b = new byte[9];
        ProcessDetails send = m.getSender();
        ProcessDetails source = m.getSource();
        String payload = m.getPayload();

        b[0] = 0;
        b[1] = IntToByteArray(m.getDestination().getId())[1];
        b[2] = IntToByteArray(m.getDestination().getId())[0];
        b[3] = IntToByteArray(m.getSource().getId())[1];
        b[4] = IntToByteArray(m.getSource().getId())[0];
        b[5] = IntToByteArray(m.getSender().getId())[1];
        b[6] = IntToByteArray(m.getSender().getId())[0];
        b[7] = IntToByteArray(Integer.parseInt(m.getPayload()))[1];
        b[8] = IntToByteArray(Integer.parseInt(m.getPayload()))[0];

        return b;
    }

    /*
    if(decode(byte).getX() == 0){
        Message m = decode(byte).getY()
    }
    else {
        Ack a = new Ack(decode(byte).getY());
    }
     */

    public Tuple<Integer, Message> decode (Byte[] b){
        int ack = ByteArrayToInt(new byte[] {b[0]});
        return new Tuple<>(ack, getMessage(b));
    }


    //todo checker l'ordre
    private Message getMessage(Byte[] b) {
        ProcessDetails desti = nT.getProcessFromId(ByteArrayToInt(new byte[] {b[1], b[2]}));
        ProcessDetails source = nT.getProcessFromId(ByteArrayToInt(new byte[] {b[3], b[4]}));
        String payload = Integer.toString(ByteArrayToInt(new byte[] {b[5], b[6]}));
        ProcessDetails sender = nT.getProcessFromId(ByteArrayToInt(new byte[] {b[7], b[8]}));
        return new Message(desti, source, payload, sender);
    }

    private byte[] IntToByteArray( int data ) {
        byte[] result = new byte[2];
        result[0] = (byte) ((data & 0x0000FF00) >> 8);
        result[1] = (byte) ((data & 0x000000FF));
        return result;
    }

    private int ByteArrayToInt (byte[] bytes) {
        return new BigInteger(1, bytes).intValue();
    }
}
