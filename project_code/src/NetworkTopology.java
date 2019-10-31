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
        ProcessDetails process = null;
        for (ProcessDetails p : processesInNetwork) {
            if (p.getPort() == port) {
                process = p;
            }
        }
        if (process.getPort() == port) {
            return process;
        } else {
            throw new ValueException("Not valid port number");
        }
    }
}
