import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;

public class ProcessDetails {
    private InetAddress address;
    private int port;
    private int id;


    public ProcessDetails(int id, String address, int port) throws UnknownHostException {
        this.address = InetAddress.getByName(address);
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

    public InetAddress getAddress() {
        return address;
    }
}