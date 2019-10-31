import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class MainTmp {
	public static void main(String args[]) throws SocketException, UnknownHostException {

		ArrayList<ProcessDetails> initalProcessInNetwork = new ArrayList<>(Arrays.asList(
				new ProcessDetails(1,"127.0.0.1", 4445),
				new ProcessDetails(2, "127.0.0.1", 4446)));


		Process proc1 = new Process(1,"127.0.0.1", 4445, initalProcessInNetwork);
		Process proc2 = new Process(2,"127.0.0.1", 4446, initalProcessInNetwork);

		ArrayList<Tuple<ProcessDetails, String>> messagesToSendByProc1 = new ArrayList<Tuple<ProcessDetails, String>>(Arrays.asList(
				new Tuple<>(initalProcessInNetwork.get(1), "1"),
				new Tuple<>(initalProcessInNetwork.get(0), "2"),
				new Tuple<>(initalProcessInNetwork.get(1), "3")


		));

		proc1.addMessagesToQueue(messagesToSendByProc1);
		proc1.startClient();
		proc2.startClient();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Kill process 2");
		proc2.getPerfectLink().getMessagesToSend().clear();

	}
}
