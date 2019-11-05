import sun.misc.Signal;
import sun.misc.SignalHandler;

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
        this.timeout = 10;
        this.network = new NetworkTopology(processesInNetwork);
        this.UDPinterface = new DatagramSocket(processReceivePort);
        this.UDPinterface.setSoTimeout(timeout);
        this.fnew = new File("./da_proc_n"+id +".out");
        try {
            this.f2 = new FileWriter(fnew,false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.urb = new Urb(UDPinterface, network, numberOfMessages, timeout, f2);
        Process.SigHandlerInt sigHandlerInt = new Process.SigHandlerInt(this);
        Signal signalInt = new Signal("INT");
        Signal.handle(signalInt, sigHandlerInt);

        Process.SigHandlerUsr2 sigHandlerUsr2 = new SigHandlerUsr2(this);
        Signal signalUsr2 = new Signal("USR2");
        Signal.handle(signalUsr2, sigHandlerUsr2);
    }

    public void startClient(){
        urb.sendMessages();
    }

    public static class SigHandlerInt implements SignalHandler {
        Process p;

        private SigHandlerInt(Process p) {
            super();
            this.p = p;
        }

        @Override
        public void handle(Signal signal) {
            System.out.format("Handling signal: %s\n", signal.toString());
            p.urb.stop();
            try {
                p.f2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

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
            System.out.format("Handling signal: %s\n", signal.toString());
            p.startClient();
        }
    }

}