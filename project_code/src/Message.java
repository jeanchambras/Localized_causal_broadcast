import java.io.Serializable;

public class Message implements Serializable {
    private ProcessDetails destination;
    private ProcessDetails source;
    private String payload;

    public Message(ProcessDetails destination, ProcessDetails source, String payload){
        this.payload = payload;
        this.destination = destination;
        this.source = source;
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

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Message)) {
            return false;
        }
        Message c = (Message) o;
        return this.destination.equals(c.destination)&& this.source.equals(c.source) && this.payload.equals(c.payload);
    }
}
