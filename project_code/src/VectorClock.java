import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class VectorClock implements Serializable {
    private int[] vectorClock;


    public VectorClock(NetworkTopology nt){
        this.vectorClock = new int[nt.getProcessesInNetwork().size()];
        for (ProcessDetails p : nt.getProcessesInNetwork()) {
            //id of the next message we are waiting for
            //messages ids start at 1
            vectorClock[p.getId()-1] = 1;
        }
    }


    public int[] getArray(){
        return vectorClock;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof VectorClock)) {
            return false;
        }
        VectorClock c = (VectorClock) o;
        return Arrays.equals(this.vectorClock,c.getArray());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(vectorClock);
    }




}
