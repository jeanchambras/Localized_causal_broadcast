import java.io.IOException;
import java.net.*;

public class Process{
    private int processId;
    private InetAddress processIP;
    private int processReceivePort;
    private Thread receiveInterface;


    private InetAddress parseAddress(String ip){
        InetAddress address = null;
        try {
            address = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return address;
    }

    public Process(int processId, String processIP, int processReceivePort) {
        this.processId = processId;
        this.processIP = parseAddress(processIP);
        this.processReceivePort = processReceivePort;
        this.receiveInterface = new Thread(new StubbornLinkServer(processReceivePort));
        this.receiveInterface.start();
    }

    public int getProcessId() {
        return processId;
    }

    public InetAddress getProcessIP() {
        return processIP;
    }

    public int getProcessReceivePort() {
        return processReceivePort;
    }

    public void sendMessage(String msg, String dstAddress, int dstPort){
        new Thread(new Runnable() {
            public void run() {
                try {
                    StubbornLinkSend sendingInterface = new StubbornLinkSend(parseAddress(dstAddress), dstPort);
                    sendingInterface.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}