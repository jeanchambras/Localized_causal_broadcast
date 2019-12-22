import java.util.Arrays;

public class Message {
    private ProcessDetails destination;
    private ProcessDetails source;
    private Integer messageID;
    private ProcessDetails sender;
    private int[] vectorClock;

    public Message(ProcessDetails destination, ProcessDetails source, Integer messageID, ProcessDetails sender, int[] vc) {
        this.destination = destination;
        this.source = source;
        this.messageID = messageID;
        this.sender = sender;
        this.vectorClock = vc;
    }

    public Integer getMessageID() {
        return messageID;
    }

    public ProcessDetails getDestination() {
        return destination;
    }

    public ProcessDetails getSource() {
        return source;
    }

    public ProcessDetails getSender() {
        return sender;
    }

    public void setSender(ProcessDetails sender) {
        this.sender = sender;
    }

    public int[] getVectorClock() {
        return vectorClock;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }
        if (!(o instanceof Message)) {
            return false;
        }

        Message c = (Message) o;

        return this.destination.equals(c.destination)
                && this.source.equals(c.source)
                && this.messageID.equals(c.messageID)
                && this.sender.equals(c.sender)
                && Arrays.equals(this.vectorClock, c.vectorClock);
    }

    @Override
    public int hashCode() {
        int hash = destination.hashCode();
        hash = 31 * hash + source.hashCode();
        hash = 31 * hash + messageID.hashCode();
        hash = 31 * hash + sender.hashCode();
        return 31 * hash + Arrays.hashCode(vectorClock);
    }

}
