import java.lang.reflect.Array;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainTmp {
	public static void main(String args[]) throws SocketException {
		// Start 2 processes that exchange messages through stubborn links
		Process proc1 = new Process(1,"127.0.0.1", 4445, 500);
		Process proc2 = new Process(2,"127.0.0.1", 4446, 500);
		Process proc3 = new Process(3,"127.0.0.1", 4447, 500);
		Process proc4 = new Process(4,"127.0.0.1", 4448, 500);
		Process proc5 = new Process(5,"127.0.0.1", 4449, 500);
		proc1.sendMessages(new HashSet<>(Arrays.asList(new Tuple<>(proc2, "1"),new Tuple<>(proc2, "2"),new Tuple<>(proc5, "3"),new Tuple<>(proc3, "4"))));
		proc2.sendMessages(new HashSet<>(Arrays.asList(new Tuple<>(proc4, "5"),new Tuple<>(proc5, "6"),new Tuple<>(proc3, "7"))));
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		proc1.addMessages(new HashSet<>(Arrays.asList(new Tuple<>(proc2, "8"))));
		proc4.sendMessages(new HashSet<>(Arrays.asList(new Tuple<>(proc1, "9"),new Tuple<>(proc1, "10"))));
	}
}
