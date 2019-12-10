public interface Listener {
    void callback(Message m);
    void callback(Triple<Integer,int[],ProcessDetails> t);
}
