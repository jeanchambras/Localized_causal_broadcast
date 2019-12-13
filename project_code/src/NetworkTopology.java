/*
 * The NetworkTopology class defines the network topology, an array containing all informations about processes in
 * the network.
 */

public class NetworkTopology {
    private ProcessDetails[] processesInNetwork;
    private int numberOfpeers;

    public NetworkTopology(ProcessDetails[] processesInNetwork) {
        this.processesInNetwork = processesInNetwork;
        this.numberOfpeers = processesInNetwork.length;
    }

    int getNumberOfpeers() {
        return numberOfpeers;
    }

    ProcessDetails[] getProcessesInNetwork() {
        return processesInNetwork;
    }

    ProcessDetails getProcessFromId(int id) throws IllegalArgumentException {
        for (ProcessDetails p : processesInNetwork) {
            if (p.getId() == id) {
                return p;
            }
        }
        throw new IllegalArgumentException("Not a valid id, the process " + id + " is not present in the network");
    }
}
