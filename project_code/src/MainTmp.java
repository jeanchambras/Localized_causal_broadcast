import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

public class MainTmp {
	public static void main(String args[]) throws SocketException, UnknownHostException {

		ArrayList<ProcessDetails> initalProcessInNetwork = new ArrayList<>(Arrays.asList(
				new ProcessDetails(1,"127.0.0.1", 1001),
				new ProcessDetails(2, "127.0.0.1", 2002),
				new ProcessDetails(3, "127.0.0.1", 3003),
				new ProcessDetails(4, "127.0.0.1", 4004),
				new ProcessDetails(5, "127.0.0.1", 5005),
				new ProcessDetails(6, "127.0.0.1", 6006),
				new ProcessDetails(7, "127.0.0.1", 7007)
		));

		int n = 8;
	Process proc1 = new Process(1001,1, initalProcessInNetwork, n);
	Process proc2 = new Process(2002,2, initalProcessInNetwork,n);
	Process proc3 = new Process(3003,3, initalProcessInNetwork,n);
	Process proc4 = new Process(4004,4, initalProcessInNetwork,n);
	Process proc5 = new Process(5005,5, initalProcessInNetwork,n);
	Process proc6 = new Process(6006,6, initalProcessInNetwork,n);
	Process proc7 = new Process(7007,7, initalProcessInNetwork,n);

//		File fnew=new File("./test.out");
//		try {
//			FileWriter f2 = new FileWriter(fnew,false);
//			f2.write("1\n");
//			f2.write("2");
//			f2.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}


	}
}
