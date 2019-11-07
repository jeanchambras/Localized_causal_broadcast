import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

public class Process {
    private DatagramSocket UDPinterface;
    private NetworkTopology network;
    private FIFO fifo;
    private final int timeout;
    private File logfile;
    private ProcessDetails sender;
    private FileWriter writer;

    public Process(int processReceivePort, int id, ArrayList<ProcessDetails> processesInNetwork, int numberOfMessages) throws SocketException {
        this.timeout = 10;
        this.network = new NetworkTopology(processesInNetwork);
        this.UDPinterface = new DatagramSocket(processReceivePort);
        this.UDPinterface.setSoTimeout(timeout);
        this.sender = network.getProcessFromId(id);
        this.logfile = new File("./outfiles/da_proc_" + id + ".out");
        try {
            this.writer = new FileWriter(logfile, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.fifo = new FIFO(sender, UDPinterface, numberOfMessages, timeout, writer, network);
        Process.SigHandlerIntTerm sigHandlerInt = new Process.SigHandlerIntTerm(this);
        Signal signalInt = new Signal("INT");
        Signal.handle(signalInt, sigHandlerInt);

        Signal signalTerm = new Signal("TERM");
        Signal.handle(signalTerm, sigHandlerInt);

        Process.SigHandlerUsr2 sigHandlerUsr2 = new SigHandlerUsr2(this);
        Signal signalUsr2 = new Signal("USR2");
        Signal.handle(signalUsr2, sigHandlerUsr2);
    }

    public void startClient() {
        fifo.sendMessages();
    }

    public static class SigHandlerIntTerm implements SignalHandler {
        Process p;

        private SigHandlerIntTerm(Process p) {
            super();
            this.p = p;
        }

        @Override
        public void handle(Signal signal) {
            try {
                p.writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            p.fifo.stop();
            p.UDPinterface.close();
            Thread.currentThread().interrupt();
        }
    }

    public static class SigHandlerUsr2 implements SignalHandler {
        Process p;

        private SigHandlerUsr2(Process p) {
            super();
            this.p = p;
        }

        @Override
        public void handle(Signal signal) {
            p.startClient();
        }
    }

}