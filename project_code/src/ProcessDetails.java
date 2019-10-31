import java.io.Serializable;
import java.net.UnknownHostException;

public class ProcessDetails implements Serializable {
    private String address;
    private int port;
    private int id;


    public ProcessDetails(int id, String address, int port) throws UnknownHostException {
        this.address = address;
        this.port = port;
        this.id = id;
    }

    public int getPortFromID(int id) {
        return port;
    }

    public int getIdFromPort(int port) {
        return id;
    }

    public int getId() {
        return id;
    }

    public int getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof ProcessDetails)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        ProcessDetails c = (ProcessDetails) o;

        // Compare the data members and return accordingly
        return this.id == c.id;
    }
}