import java.util.*;

public class PerfectLinkServer extends StubbornLinkServer {
    private Set<String> receivedMessages;
    /**
     * Instantiate the UDP socket
     *
     * @param processReceivePort port number to listen
     * @param receivedMessages   Array of all messages delivered by the process
     */
    public PerfectLinkServer(int processReceivePort, Set<String> receivedMessages) {
        super(processReceivePort);
        this.receivedMessages = receivedMessages;
    }

    /**
     * Deliver new received messages
     * @param received
     */
    @Override
    public void deliver(String received) {
        if(!receivedMessages.contains(received)){
            receivedMessages.add(received);
            System.out.println("On port : " + this.getReceivePort() + ", packet received : " + received);
        }
    }
}
