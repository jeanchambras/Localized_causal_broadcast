public class MainTmp {
	public static void main(String args[]){
		// Start 2 processes that exchange messages through stubborn links
		Process proc1 = new Process(1,"127.0.0.1", 4446);
		Process proc2 = new Process(2,"127.0.0.1", 4445);
		Process proc3 = new Process(3,"127.0.0.1", 4447);
		proc1.sendMessage(Integer.toString(proc1.getProcessId()),"127.0.0.1",proc2.getProcessReceivePort());
		proc2.sendMessage(Integer.toString(proc2.getProcessId()),"127.0.0.1",proc1.getProcessReceivePort());
		proc3.sendMessage(Integer.toString(proc3.getProcessId()),"127.0.0.1",proc1.getProcessReceivePort());

	}
}
