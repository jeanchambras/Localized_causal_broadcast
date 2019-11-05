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
				new ProcessDetails(3, "127.0.0.1", 3003)
		));

		int n = 1;
	Process proc1 = new Process(1001, initalProcessInNetwork, n);
	Process proc2 = new Process(2002, initalProcessInNetwork,n);
	Process proc3 = new Process(3003, initalProcessInNetwork,n);

	}
}
