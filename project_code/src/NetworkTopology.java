import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

import java.util.ArrayList;

public class NetworkTopology {
    private ArrayList<ProcessDetails> processesInNetwork;

    public NetworkTopology (ArrayList<ProcessDetails> processesInNetwork){
        this.processesInNetwork = processesInNetwork;
    }

    public ArrayList<ProcessDetails> getProcessesInNetwork() {
        return processesInNetwork;
    }

    public ProcessDetails getProcessFromPort(int port){
        for (ProcessDetails p : processesInNetwork) {
            if (p.getPort() == port) {
                return p;
            }
        }
        throw new ValueException("Not valid port number");
    }
}
