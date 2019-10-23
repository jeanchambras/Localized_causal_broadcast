public class MainTmp {
	public static void main(String args[]){
		// Start 2 processes that exchange messages through stubborn links
		Process proc1 = new Process(1,"127.0.0.1", 4446);
		Process proc2 = new Process(2,"127.0.0.1", 4445);
		proc1.sendMessage(Integer.toString(proc1.getProcessId()),"127.0.0.1",4445);
		proc2.sendMessage(Integer.toString(proc2.getProcessId()),"127.0.0.1",4446);

	}
}
