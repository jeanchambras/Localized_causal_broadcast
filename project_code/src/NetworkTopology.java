import java.util.ArrayList;

public class NetworkTopology {
    private ArrayList<ProcessDetails> processesInNetwork;

    public NetworkTopology (ArrayList<ProcessDetails> processesInNetwork){
        this.processesInNetwork = processesInNetwork;
    }

    public ArrayList<ProcessDetails> getProcessesInNetwork() {
        return processesInNetwork;
    }

    public ProcessDetails getProcessFromPort(int port) throws Exception {
        for (ProcessDetails p : processesInNetwork) {
            if (p.getPort() == port) {
                return p;
            }
        }
        throw new Exception("Not valid port number");
    }

    public ProcessDetails getProcessFromId(int id) throws Exception {
        for (ProcessDetails p : processesInNetwork) {
            if (p.getId() == id) {
                return p;
            }
        }
        throw new Exception("Not valid id");
    }
}
