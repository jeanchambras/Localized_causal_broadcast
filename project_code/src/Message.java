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

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Message)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        Message c = (Message) o;

        // Compare the data members and return accordingly
        return this.destination.equals(c.destination)&& this.source.equals(c.source) && this.payload.equals(c.payload);
    }
}
