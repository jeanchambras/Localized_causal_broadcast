import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

import java.util.ArrayList;

public class NetworkTopology {
    ArrayList<ProcessInformations> processesInNetwork;

    public NetworkTopology (ArrayList<ProcessInformations> processesInNetwork){
        this.processesInNetwork = processesInNetwork;
    }

    public int getProcessIdFromPort(int port){
        ProcessInformations process = null;
        for (ProcessInformations p : processesInNetwork) {
            if (p.getPort() == port) {
                process = p;
            }
        }
        if (process.getPort() == port) {
            return process.getId();
        } else {
            throw new ValueException("Not valid port number");
        }

    }
}
