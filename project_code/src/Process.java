import java.io.IOException;
import java.net.*;
import java.util.*;

public class Process{
    private int processId;
    private InetAddress processIP;
    private int processReceivePort;
    private Thread receiveInterface;
    private Set<String> receivedMessages;

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
    public Process(int processId, String processIP, int processReceivePort) {
        this.processId = processId;
        this.processIP = parseAddress(processIP);
        this.processReceivePort = processReceivePort;
        this.receivedMessages = new HashSet<>();
        this.receiveInterface = new Thread(new StubbornLinkServer(processReceivePort, receivedMessages));
        this.receiveInterface.start();
    }

    /**
     * This method makes a process send a message to another process
     * @param msg The message we want the process to send
     * @param dstAddress The destination ip addess
     * @param dstPort The destination port number
     */
    public void sendMessage(String msg, String dstAddress, int dstPort){
        new Thread(new Runnable() {
            public void run() {
                try {
                    // We initialize the sending socket in another thread not to block the main loop of the process
                    StubbornLinkSend sendingInterface = new StubbornLinkSend(parseAddress(dstAddress), dstPort);
                    sendingInterface.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}