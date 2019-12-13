/*
 * The listener interface defines the callback model of the abstraction delivery process.
 * Each top level abstraction implements the Listener interface to be notified by lower level abstractions
 */

public interface Listener {
    void callback(Message m);
    void callback(Triple<Integer,int[],ProcessDetails> t);
}
