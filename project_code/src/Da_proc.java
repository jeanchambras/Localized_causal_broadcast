import java.io.*;
import java.util.*;

/**
 * Da_proc is the main process, the application starts here. It reads the membership file and creates a new Process
 */

public class Da_proc {
    public static void main(String[] args) {

        // Check number of arguments
        if (args.length != 3) {
            System.out.println("Wrong number of arguments");
            System.exit(-1);
        }

        // parse arguments
        int processToLaunch = Integer.parseInt(args[0]);
        String membershipFilePath = args[1];
        int numberOfMessages = Integer.parseInt(args[2]);
        // start to parse membership file
        String line;
        try {
            // open input stream membership file
            BufferedReader br = new BufferedReader(new FileReader(membershipFilePath));
            // Read the first line of the membership file
            int numberOfProcesses = 0;
            if ((line = br.readLine()) != null) {
                numberOfProcesses = Integer.parseInt(line);
            }

            ProcessDetails[] processesInNetwork = new ProcessDetails[numberOfProcesses];
            for (int i = 0; i < numberOfProcesses; ++i) {
                line = br.readLine();
                String[] process_details = line.split("\\s+");

                int id = Integer.parseInt(process_details[0]);
                String address = process_details[1];
                int port = Integer.parseInt(process_details[2]);

                processesInNetwork[i] = new ProcessDetails(id, address, port);
            }

            HashSet<ProcessDetails> causality = new HashSet<>();
            for (int i = 1; i <= numberOfProcesses; ++i) {
                line = br.readLine();
                if (i == processToLaunch) {
                    String[] causality_informations = line.split("\\s+");
                    // The first element in the causality informations list is the id of the process
                    // (not to include in the causality list as each process has a causal relationship with itself)
                    for (int j = 1; j < causality_informations.length; ++j) {
                        causality.add(processesInNetwork[Integer.parseInt(causality_informations[j]) - 1]);
                    }
                }
            }
            br.close();
            ProcessDetails processToLaunchDetails = processesInNetwork[processToLaunch - 1];
            new Process(processToLaunchDetails.getPort(), processToLaunch, processesInNetwork, numberOfMessages, causality);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}


