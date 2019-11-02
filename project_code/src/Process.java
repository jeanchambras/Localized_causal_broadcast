import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

public class Process{
    private DatagramSocket UDPinterface;
    private NetworkTopology network;
    private Beb broadcast;
    private Urb urb;
    private final int timeout;

    public Process(int processReceivePort, ArrayList<ProcessDetails> processesInNetwork, int numberOfMessages) throws SocketException {
        this.timeout = 5;
        this.network = new NetworkTopology(processesInNetwork);
        this.UDPinterface = new DatagramSocket(processReceivePort);
        this.urb = new Urb(UDPinterface, network, numberOfMessages, timeout);
        this.broadcast = new Beb(UDPinterface, network, numberOfMessages, timeout, urb);
        urb.sendMessages();
    }

    public void startClient(){
        broadcast.sendMessages();
    }
}