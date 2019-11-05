import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

public class Process{
    private DatagramSocket UDPinterface;
    private NetworkTopology network;
    private Urb urb;
    private final int timeout;
    File fnew;
    FileWriter f2;

    public Process(int processReceivePort,int id, ArrayList<ProcessDetails> processesInNetwork, int numberOfMessages) throws SocketException {
        this.timeout = 100;
        this.network = new NetworkTopology(processesInNetwork);
        this.UDPinterface = new DatagramSocket(processReceivePort);
        this.fnew = new File("./da_proc_n"+id +".out");
        try {
            this.f2 = new FileWriter(fnew,false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.urb = new Urb(UDPinterface, network, numberOfMessages, timeout, f2);
        urb.sendMessages();
    }

    public void startClient(){
        urb.sendMessages();
    }
}