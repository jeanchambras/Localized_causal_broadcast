import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

public class Process{
    private DatagramSocket UDPinterface;
    private NetworkTopology network;
    private Urb urb;
    private final int timeout;

    public Process(int processReceivePort, ArrayList<ProcessDetails> processesInNetwork, int numberOfMessages) throws SocketException {
        this.timeout = 100;
        this.network = new NetworkTopology(processesInNetwork);
        this.UDPinterface = new DatagramSocket(processReceivePort);
        this.urb = new Urb(UDPinterface, network, numberOfMessages, timeout);
        urb.sendMessages();
    }

    public void startClient(){
        urb.sendMessages();
    }
}