import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class MainTmp {
	public static void main(String args[]) throws SocketException, UnknownHostException {

		ArrayList<ProcessDetails> initalProcessInNetwork = new ArrayList<>(Arrays.asList(
				new ProcessDetails(1,"127.0.0.1", 1001),
				new ProcessDetails(2, "127.0.0.1", 2002)
		));

		int n = 2;
	Process proc1 = new Process(1001, initalProcessInNetwork, n);
	Process proc2 = new Process(2002, initalProcessInNetwork,n);

//		ProcessDetails p1 = new ProcessDetails(2, "127.0.0.1", 2002);
//		ProcessDetails p2 = new ProcessDetails(2, "127.0.0.1", 2002);
//		ProcessDetails p3 = new ProcessDetails(3, "127.0.0.1", 3003);
//		ProcessDetails p4 = new ProcessDetails(3, "127.0.0.1", 3003);
//
//		Message m1 = new Message(p1,p3,"1", p1);
//		Message m2 = new Message(p2,p4,"1", p2);
//		System.out.println(m1.equals());
	}
}
