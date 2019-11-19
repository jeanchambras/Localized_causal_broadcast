import java.io.Serializable;

public class Packet implements Serializable {
    Message message;
    Ack ack;


    public Packet(Message message){
        this.message = message;
        this.ack = null;
    }
    public Packet(Ack ack){
        this.ack = ack;
        this.message = null;

    }
}
