import java.io.Serializable;
import java.util.Arrays;
import java.util.Vector;


/**
 * The message class defines a message that is sent between different processes. It contains the field destination, source (original sender) and sender (the address of the process which forwarded the message).
 */

public class Message implements Serializable {
    private ProcessDetails destination;
    private ProcessDetails source;
    private Integer payload;
    private ProcessDetails sender;
    private int[] vectorClock;

    public Message(ProcessDetails destination, ProcessDetails source, Integer payload, ProcessDetails sender, int[] vc) {
        this.destination = destination;
        this.source = source;
        this.payload = payload;
        this.sender = sender;
        this.vectorClock = vc;
    }

    public Integer getPayload() {
        return payload;
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
        return this.destination.equals(c.destination) && this.source.equals(c.source) && this.payload.equals(c.payload) && this.sender.equals(c.sender) && Arrays.equals(this.vectorClock, c.vectorClock);
    }

    @Override
    public int hashCode() {
        int hash = destination.hashCode();
        hash = 31 * hash + source.hashCode();
        hash = 31 * hash + payload.hashCode();
        hash = 31 * hash + sender.hashCode();
        return 31 * hash + Arrays.hashCode(vectorClock);
    }

}
