import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class MainTmp {
	public static void main(String args[]) throws SocketException, UnknownHostException {

		ArrayList<ProcessDetails> initalProcessInNetwork = new ArrayList<>(Arrays.asList(
				new ProcessDetails(1,"127.0.0.1", 1001),
				new ProcessDetails(2, "127.0.0.1", 2002),
				new ProcessDetails(3, "127.0.0.1", 3003),
				new ProcessDetails(4, "127.0.0.1", 4004),
				new ProcessDetails(5, "127.0.0.1", 5005),
				new ProcessDetails(6, "127.0.0.1", 6006),
				new ProcessDetails(7,"127.0.0.1", 7007),
				new ProcessDetails(8, "127.0.0.1", 8008),
				new ProcessDetails(9, "127.0.0.1", 9009),
				new ProcessDetails(10, "127.0.0.1", 1010),
				new ProcessDetails(11, "127.0.0.1", 1111),
				new ProcessDetails(12, "127.0.0.1", 1212),
				new ProcessDetails(13, "127.0.0.1", 1313)));

	Process proc1 = new Process(1,"127.0.0.1", 1001, initalProcessInNetwork);
	Process proc2 = new Process(2,"127.0.0.1", 2002, initalProcessInNetwork);
	Process proc3 = new Process(3,"127.0.0.1", 3003, initalProcessInNetwork);
	Process proc4 = new Process(4,"127.0.0.1", 4004, initalProcessInNetwork);
	Process proc5 = new Process(5,"127.0.0.1", 5005, initalProcessInNetwork);
	Process proc6 = new Process(6,"127.0.0.1", 6006, initalProcessInNetwork);
	Process proc7 = new Process(7,"127.0.0.1", 7007, initalProcessInNetwork);
	Process proc8 = new Process(8,"127.0.0.1", 8008, initalProcessInNetwork);
	Process proc9 = new Process(9,"127.0.0.1", 9009, initalProcessInNetwork);
	Process proc10 = new Process(10,"127.0.0.1", 1010, initalProcessInNetwork);
	Process proc11 = new Process(11,"127.0.0.1", 1111, initalProcessInNetwork);
	Process proc12 = new Process(12,"127.0.0.1", 1212, initalProcessInNetwork);
	Process proc13 = new Process(13,"127.0.0.1", 1313, initalProcessInNetwork);
}
}
