public class Ack {
    private Message message;
    public Ack(Message m){
        this.message = m;
    }

    public Message getMessage() {
        return message;
    }
}
