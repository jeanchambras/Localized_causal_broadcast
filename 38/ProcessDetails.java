import java.io.Serializable;

/**
 * The ProcessDetails class defines a process object through its ip address, its ip port and its id.
 */

public class ProcessDetails implements Serializable {
    private String address;
    private int port;
    private int id;

    public ProcessDetails(int id, String address, int port) {
        this.address = address;
        this.port = port;
        this.id = id;
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
        if (o == this) {
            return true;
        }
        if (!(o instanceof ProcessDetails)) {
            return false;
        }
        ProcessDetails c = (ProcessDetails) o;
        return this.id == c.id;
    }

    @Override
    public int hashCode() {
        return port + id * 31;
    }
}