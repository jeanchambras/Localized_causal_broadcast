import java.util.ArrayList;

/**
 * NetworkTopology class defines the network topology, an array list of Process details.
 */


public class NetworkTopology {
    private ArrayList<ProcessDetails> processesInNetwork;
    private int numberOfpeers;
    public NetworkTopology(ArrayList<ProcessDetails> processesInNetwork) {
        this.processesInNetwork = processesInNetwork;
        this.numberOfpeers = processesInNetwork.size();
    }

    public int getNumberOfpeers() {
        return numberOfpeers;
    }

    public ArrayList<ProcessDetails> getProcessesInNetwork() {
        return processesInNetwork;
    }

    public ProcessDetails getProcessFromId(int id) throws IllegalArgumentException {
        for (ProcessDetails p : processesInNetwork) {
            if (p.getId() == id) {
                return p;
            }
        }
        throw new IllegalArgumentException("Not a valid id, the process " + id + " is not present in the network");
    }
}
