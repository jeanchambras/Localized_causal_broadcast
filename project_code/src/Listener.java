public interface Listener {
    void callback(Message m);
    void callback(Triple<String,VectorClock,ProcessDetails> t);
}
