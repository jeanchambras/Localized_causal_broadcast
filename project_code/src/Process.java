import java.net.*;
import java.util.*;

public class Process{
    private int processId;
    private InetAddress processIP;
    private int processReceivePort;
    private DatagramSocket UDPinterface;
    private ArrayList<Tuple<ProcessDetails, String>> perfectLinkDeliveredMessages;
    private NetworkTopology network;
    private PerfectLink perfectLink;

    /**
     * This method is used to parse a string into an InetAddress
     * @param ip the ip address as a String
     * @return the ip address as an InetAddress of the process
     */
    private InetAddress parseAddress(String ip){
        InetAddress address = null;
        try {
            address = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return address;
    }

    /**
     * @return the id of the process
     */
    public int getProcessId() {
        return processId;
    }

    /**
     * @return the ip address of the process
     */
    public InetAddress getProcessIP() {
        return processIP;
    }

    /**
     * @return the port number of the process
     */
    public int getProcessReceivePort() {
        return processReceivePort;
    }

    /**
     * Constructor of the process
     * @param processId The ID of the process
     * @param processIP The IP address of the process
     * @param processReceivePort The port number the process is listening for incoming packets
    * */
    public Process(int processId, String processIP, int processReceivePort, ArrayList<ProcessDetails> processesInNetwork) throws SocketException {
        this.processId = processId;
        this.processIP = parseAddress(processIP);
        this.processReceivePort = processReceivePort;
        this.network = new NetworkTopology(processesInNetwork);
        this.UDPinterface = new DatagramSocket(processReceivePort);
        this.perfectLinkDeliveredMessages = new ArrayList<>();
        this.perfectLink = new PerfectLink(UDPinterface, network, perfectLinkDeliveredMessages);
    }

    public void addMessagesToQueue(ArrayList<Tuple<ProcessDetails, String>> messagesToAdd) {
        perfectLink.addMessagesToQueue(messagesToAdd);
    }

    public void startClient(){
        perfectLink.sendMessages();
    }
}