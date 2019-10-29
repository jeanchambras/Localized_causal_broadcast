import java.net.SocketException;

public class PerfectLinkSender extends StubbornLinkSender {
    public PerfectLinkSender(int timeout) throws SocketException {
        super(timeout);
    }
}
