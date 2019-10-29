import java.io.IOException;
import java.net.*;
import java.util.*;

public class Process{
    private int processId;
    private InetAddress processIP;
    private int processReceivePort;
    private Thread receiveInterface;
    private Set<String> receivedMessages;
    private int timeout;
    private Set<Tuple<Process, String>> messagesToSend;

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
    public Process(int processId, String processIP, int processReceivePort, int timeout) {
        this.processId = processId;
        this.processIP = parseAddress(processIP);
        this.processReceivePort = processReceivePort;
        this.receivedMessages = new HashSet<>();
        this.receiveInterface = new Thread(new PerfectLinkServer(processReceivePort, receivedMessages));
        this.receiveInterface.start();
        this.timeout = timeout;
    }

    /**
     * The process start to send messages
     * @param messagesToSend list of messages to send
     */
    public void sendMessages(Set<Tuple<Process, String>> messagesToSend){
        this.messagesToSend = messagesToSend;
        new Thread(new Runnable() {
            public void run() {
                try {
                    // We initialize the sending socket in another thread not to block the main loop of the process
                    PerfectLinkSender sendingInterface = new PerfectLinkSender(timeout);
                    sendingInterface.sendMessage(messagesToSend);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Add messages to send by perfect link sender
     * @param messagesToAdd
     */
    public void addMessages(Set<Tuple<Process, String>> messagesToAdd) {
        messagesToSend.addAll(messagesToAdd);
    }
}