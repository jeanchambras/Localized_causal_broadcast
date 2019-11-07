import java.math.BigInteger;

public class Encoder {
    public Encoder(){
    }

    public Byte[] encode (Message m ) {
        byte[] b = new byte[7];
        ProcessDetails send = m.getSender();
        ProcessDetails source = m.getSource();
        String payload = m.getPayload();

    }

    public Message decode (Byte[] payloadData){

    }
}
