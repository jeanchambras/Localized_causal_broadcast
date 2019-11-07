public interface Listener {
    void callback(Message m);
    void callback(Tuple<String,ProcessDetails> t);
}
