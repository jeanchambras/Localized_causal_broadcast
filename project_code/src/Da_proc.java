import java.io.*;
import java.util.*;

/** Da_proc is the main process, the application starts here. It reads the membership file and creates a new Process
 */

public class Da_proc {
    public static void main(String[] args) {

        String thisLine;

        if(args.length != 3)
        {
            System.out.println("Wrong number of arguments");
            System.exit(0);
        }

        int processToLaunch = Integer.parseInt(args[0]);
        try {
            // open input stream membership file
            BufferedReader br = new BufferedReader(new FileReader(args[1]));
            // Read the first line of the membership file
            int numberProc = 0;
            if((thisLine = br.readLine()) != null) {
                numberProc = Integer.parseInt(thisLine);
            }

            ArrayList<ProcessDetails> processesInNetwork = new ArrayList<>();
            for (int i =0; i < numberProc;++i){
                thisLine = br.readLine();
                String[] process_details = thisLine.split("\\s+");
                processesInNetwork.add(new ProcessDetails(Integer.parseInt(process_details[0]),process_details[1],Integer.parseInt(process_details[2])));
            }


            HashSet<ProcessDetails> causality = new HashSet<>();
            for (int i =1; i <= numberProc;++i){
                thisLine = br.readLine();
                if(i == processToLaunch){
                    String[] causality_informations = thisLine.split("\\s+");
                    for(int j = 1; j < causality_informations.length; ++j){
                        causality.add(processesInNetwork.get(Integer.parseInt(causality_informations[j])-1));
                    }
                    System.out.println(i + "|" + Arrays.toString(causality_informations) + " | " + causality.size());
                }
            }

            ProcessDetails processToLaunchDetails = processesInNetwork.get(processToLaunch - 1);
            Process process = new Process(processToLaunchDetails.getPort(),processToLaunch, processesInNetwork,Integer.parseInt(args[2]),causality);



        } catch(Exception e) {

            e.printStackTrace();
        }


    }
}


