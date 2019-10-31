public class Message {
    private ProcessDetails destination;
    private String payload;

    public Message(ProcessDetails destination, String payload){
        this.payload = payload;
        this.destination = destination;
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
        return this.destination.equals(c.destination) && this.payload.equals(c.payload);
    }
}
