import java.io.*;
import java.net.*;
import java.util.*;

public class Da_proc {
    // parse membership file
    public static void main(String[] args) {

        String thisLine = null;

        if(args.length != 2)
        {
            System.out.println("Wrong number of arguments");
            System.exit(0);
        }

        int processToLaunch = Integer.parseInt(args[0]);
        try {
            // open input stream membership file
            BufferedReader br = new BufferedReader(new FileReader(args[1]));
//            Read the first line of the membership file
            int numberProc = 0;
            if((thisLine = br.readLine()) != null) {
                numberProc = Integer.parseInt(thisLine);
            }

            int i =0;
            ArrayList<ProcessDetails> processesInNetwork = new ArrayList<>();
            while ((thisLine = br.readLine()) != null) {
                String[] process_details = thisLine.split("\\s+");
                processesInNetwork.add(new ProcessDetails(Integer.parseInt(process_details[0]),process_details[1],Integer.parseInt(process_details[2])));
            }
            ProcessDetails processToLaunchDetails = processesInNetwork.get(processToLaunch - 1);
            Process process = new Process(processToLaunchDetails.getId(), processToLaunchDetails.getAddress(), processToLaunchDetails.getPort(), processesInNetwork);
            process.startClient();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}


