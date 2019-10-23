import java.io.*;
import java.net.*;

public class Da_proc {
    // parse membership file
    public static void main(String[] args) {

        String thisLine = null;

        if(args.length == 0)
        {
            System.out.println("No membership file provided");
            System.exit(0);
        }

        try {
            // open input stream membership file
            BufferedReader br = new BufferedReader(new FileReader(args[0]));
//            Read the first line of the membership file
            int numberProc = 0;
            if((thisLine = br.readLine()) != null) {
                numberProc = Integer.parseInt(thisLine);
            }

            int i =0;
            Process[] processes = new Process[numberProc];
            while ((thisLine = br.readLine()) != null) {
                String[] process_details = thisLine.split("\\s+");
                processes[i] = new Process(Integer.parseInt(process_details[0]),process_details[1],Integer.parseInt(process_details[2]));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }


    }
}


