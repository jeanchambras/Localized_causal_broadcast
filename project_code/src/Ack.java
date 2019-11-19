import java.io.Serializable;

public class Ack implements Serializable {
    private Message message;

    public Ack(Message m){
        this.message = m;
    }

    public Message getMessage() {
        return message;
    }
}
