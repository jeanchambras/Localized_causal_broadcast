import java.io.Serializable;


/**
 * The message class defines a message that is sent between different processes. It contains the field destination, source (original sender) and sender (the address of the process which forwarded the message).
 *
 */

public class Message implements Serializable {
    private ProcessDetails destination;
    private ProcessDetails source;
    private String payload;
    private ProcessDetails sender;

    public Message(ProcessDetails destination, ProcessDetails source, String payload, ProcessDetails sender) {
        this.destination = destination;
        this.source = source;
        this.payload = payload;
        this.sender = sender;
    }

    public String getPayload() {
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

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Message)) {
            return false;
        }
        Message c = (Message) o;
        System.out.println();
        return this.destination.equals(c.destination) && this.source.equals(c.source) && this.payload.equals(c.payload) && this.sender.equals(c.sender);
    }

    @Override
    public int hashCode() {
        return destination.hashCode() * source.hashCode() * payload.hashCode();
    }

}
