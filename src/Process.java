import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramSocket;
import java.util.HashSet;

/**
 * The Process class is the actual process of the application. It handle the different signals and start the different
 * broadcasting abstractions.
 *
 *    _______________                        |*\_/*|________
 *   |  ___________  |     .-.     .-.      ||_/-\_|______  |
 *   | |           | |    .****. .****.     | |           | |
 *   | |   0   0   | |    .*****.*****.     | |   0   0   | |
 *   | |     -     | |     .*********.      | |     -     | |
 *   | |   \___/   | |      .*******.       | |   \___/   | |
 *   | |___     ___| |       .*****.        | |___________| |
 *   |_____|\_/|_____|        .***.         |_______________|
 *     _|__|/ \|_|_.............*.............._|________|_
 *    / ********** \                          / ********** \
 *  /  ************  \                      /  ************  \
 * --------------------                    --------------------
 */

public class Process {
    private LCB lcb;
    private BufferedWriter bufferedWriter;

    Process(int processReceivePort, int id, ProcessDetails[] processesInNetwork, int numberOfMessages, HashSet<ProcessDetails> causality) throws Exception {
        NetworkTopology network = new NetworkTopology(processesInNetwork);
        DatagramSocket udpSocket = new DatagramSocket(processReceivePort);
        ProcessDetails processDetails = network.getProcessFromId(id);
        File logfile = new File("./da_proc_" + id + ".out");

        try {
            final int BUFFER_SIZE = 1024 * 8;
            this.bufferedWriter = new BufferedWriter(new FileWriter(logfile, false), BUFFER_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
         *  TIMEOUT_TIME: The time the sending thread will wait before resending a batch of messages. This value should
         *  be optimized to let the application thread check messages to deliver and not to flood the network.
         *  This value has been optimized to be resilient to different system configurations from 4 to 2 cores.
         */

        int TIMEOUT_TIME = 35;
        this.lcb = new LCB(processDetails, udpSocket, numberOfMessages, TIMEOUT_TIME, bufferedWriter, network, causality);

        /*
         *  initialize signals handlers
         */

        IntTermSignalHandler sigHandlerInt = new IntTermSignalHandler(this);
        Signal signalInt = new Signal("INT");
        Signal.handle(signalInt, sigHandlerInt);
        Signal signalTerm = new Signal("TERM");
        Signal.handle(signalTerm, sigHandlerInt);
        USR2SignalHandler sigHandlerUsr2 = new USR2SignalHandler(this);
        Signal signalUsr2 = new Signal("USR2");
        Signal.handle(signalUsr2, sigHandlerUsr2);
    }

    private void startToBroadcast() {
        lcb.startToBroadcast();
    }

    // ###################### Signal Handlers ##########################

    public static class IntTermSignalHandler implements SignalHandler {
        Process process;

        private IntTermSignalHandler(Process p) {
            super();
            this.process = p;
        }

        @Override
        public void handle(Signal signal) {
            try {
                process.bufferedWriter.flush();
                process.bufferedWriter.close();
            } catch (IOException e) {
            }
            System.exit(-1);
        }
    }

    public static class USR2SignalHandler implements SignalHandler {
        Process process;

        private USR2SignalHandler(Process p) {
            super();
            this.process = p;
        }

        @Override
        public void handle(Signal signal) {
            process.startToBroadcast();
        }
    }
}