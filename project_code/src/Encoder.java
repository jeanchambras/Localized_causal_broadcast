import java.math.BigInteger;

public class Encoder {
    NetworkTopology nT;

    public Encoder(NetworkTopology networkTopology){
        this.nT = networkTopology;
    }

    public byte[] encode (Message m ) {
        byte[] b = new byte[9];
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

    public byte[] encode (Ack a ) {
        Message m = a.getMessage();
        byte[] b = new byte[9];

        b[0] = 1;
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

    public Tuple<Integer, Message> decode (byte[] b){
        int ack = ByteArrayToInt(new byte[] {b[0]});
        return new Tuple<>(ack, getMessage(b));
    }


    //todo checker l'ordre
    private Message getMessage(byte[] b) {
        ProcessDetails desti = nT.getProcessFromId(ByteArrayToInt(new byte[] {b[2], b[1]}));
        ProcessDetails source = nT.getProcessFromId(ByteArrayToInt(new byte[] {b[4], b[3]}));
        String payload = Integer.toString(ByteArrayToInt(new byte[] {b[6], b[5]}));
        ProcessDetails sender = nT.getProcessFromId(ByteArrayToInt(new byte[] {b[8], b[7]}));
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
