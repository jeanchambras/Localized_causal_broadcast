import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.IntStream;

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


    public boolean lessThan (VectorClock v){
        return IntStream.range(0,this.vectorClock.length).allMatch(i -> this.vectorClock[i] <= v.getArray()[i]);
    }

    public int[] getArray(){
        return vectorClock.clone();
    }

    public void updateVc(int[] newVc){
        this.vectorClock = newVc;
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
