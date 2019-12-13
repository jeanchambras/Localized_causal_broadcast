import java.io.BufferedWriter;
import java.io.IOException;
import java.net.DatagramSocket;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.IntStream;

public class LCB implements Listener {
    private ConcurrentLinkedQueue<Integer> sendingQueue;
    private BufferedWriter bufferedWriter;
    private ProcessDetails processDetails;
    private int numberOfMessages;
    private Urb Urb;
    private int[] vectorClockSend;
    private int[] vectorClockReceive;
    private HashSet<Triple<Integer, int[], ProcessDetails>> pendingMessages;
    private HashSet<ProcessDetails> localized;
    private int sendingQueueSize;

    LCB(ProcessDetails sender, DatagramSocket socket, int numberOfMessages, int timeout, BufferedWriter bufferedWriter,
        NetworkTopology network, HashSet<ProcessDetails> causality) {
        this.Urb = new Urb(sender, socket, network, timeout, this);
        this.vectorClockSend = new int[network.getProcessesInNetwork().length];
        this.vectorClockReceive = new int[network.getProcessesInNetwork().length];
        this.pendingMessages = new HashSet<>();
        this.numberOfMessages = numberOfMessages;
        this.processDetails = sender;
        this.localized = causality;
        this.bufferedWriter = bufferedWriter;

        /*
         * The sending queue behaves like a sliding window. Each process broadcast messages, sending batches of messages.
         * This size is determined not to flood the network and the sending, receiving and handling threads.
         * To guarantee the reliability of the broadcast, each process rebroadcast messages until a majority of processes
         * acknowledge it. This property leads to a quadratic growth in the number of exchanged messages regarding the
         * number of processes  (p) in the network.
         *
         * The sliding window size has been chosen to limit this quadratic increase :
         * sendingQueueSize <= Constant / numberOfpeers^2.
         *
         * The 2.7 exponent has been determined, by a series of tests to guarantee reliability,
         * for a maximum of low machines specs
         *
         * 1545 has been defined to have a window size of 20 with 5 processes
         */

        this.sendingQueue = new ConcurrentLinkedQueue<>();
        this.sendingQueueSize = Math.max(1, 1545 / (int) Math.pow(network.getNumberOfpeers(), 2.7));
    }

    public void startToBroadcast() {
        for (int messageID = 1; messageID <= numberOfMessages; ++messageID) {
            int localId = processDetails.getId();
            Urb.urbBroadcast(processDetails, messageID, vectorClockSend.clone());
            vectorClockSend[localId - 1]++;

            try {
                bufferedWriter.write("b " + messageID + "\n");
            } catch (IOException e) {}

            sendingQueue.add(messageID);

            while (!(sendingQueue.size() < sendingQueueSize)) {} // We wait while the queue is full
        }
    }

    private void deliver(Triple<Integer, int[], ProcessDetails> ts) {
        try {
            bufferedWriter.write("d " + ts.getZ().getId() + " " + ts.getX() + "\n");
        } catch (IOException e) {}

        if (ts.getZ().equals(processDetails)) {
            sendingQueue.poll();
        }
    }

    /*
     * Check if the vector clock v1 is smaller than the vector clock v2
     */

    private boolean lessThan(int[] v1, int[] v2) {
        return IntStream.range(0, v1.length).allMatch(i -> v1[i] <= v2[i]);
    }

    @Override
    public void callback(Triple<Integer, int[], ProcessDetails> urbDeliveredTriple) {
        pendingMessages.add(urbDeliveredTriple);
        Triple<Integer, int[], ProcessDetails> tripleToLcbDeliver;

        /*
         * While there is some messages that can be delivered (their vector clock is smaller than the local
         * vectorClockReceive, we deliver them.
         */

        do {
            tripleToLcbDeliver = pendingMessages.stream().filter(Objects::nonNull).filter(o -> lessThan(o.getY(),
                    vectorClockReceive)).findAny().orElse(null);
            if (!(tripleToLcbDeliver == null)) {
                pendingMessages.remove(tripleToLcbDeliver);
                vectorClockReceive[tripleToLcbDeliver.getZ().getId() - 1]++;

                // we update our send vector clock only if the source process is in our localized set
                if (localized.contains(tripleToLcbDeliver.getZ())) {
                    vectorClockSend[tripleToLcbDeliver.getZ().getId() - 1]++;
                }

                deliver(tripleToLcbDeliver);
            }
        } while (!(tripleToLcbDeliver == null));
    }

    @Override
    public void callback(Message m) {}
}
