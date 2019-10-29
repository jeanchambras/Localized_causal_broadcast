import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainTmp {
	public static void main(String args[]){
		// Start 2 processes that exchange messages through stubborn links
		Process proc1 = new Process(1,"127.0.0.1", 4446, 500);
		Process proc2 = new Process(2,"127.0.0.1", 4445, 500);
		proc1.sendMessages(new HashSet<>(Arrays.asList(new Tuple<>(proc2, "Hello from 1"),new Tuple<>(proc2, "Other Message from 1"))));
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		proc1.addMessages(new HashSet<>(Arrays.asList(new Tuple<>(proc2, "New message from 1"))));
		proc2.sendMessages(new HashSet<>(Arrays.asList(new Tuple<>(proc1, "Hello from 2"),new Tuple<>(proc1, "hey from 2"))));
	}
}
