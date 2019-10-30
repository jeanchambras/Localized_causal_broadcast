import java.net.DatagramSocket;

public class PerfectLinkSender extends StubbornLinkSender {
    public PerfectLinkSender(int timeout, DatagramSocket socket){
        super(timeout, socket);
    }
}
