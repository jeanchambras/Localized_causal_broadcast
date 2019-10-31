import java.net.UnknownHostException;

public class ProcessDetails {
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
}