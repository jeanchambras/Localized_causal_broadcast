import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;

public class Da_proc {
    public static void main(String[] args) {

        // Check number of arguments
        if (args.length != 3) {
            System.out.println("Wrong number of arguments");
            System.exit(-1);
        }

        // #################### parse command line arguments #########################

        int ProcessToLaunchId = Integer.parseInt(args[0]);
        String membershipFilePath = args[1];
        int numberOfMessages = Integer.parseInt(args[2]);

        // ###################### parse membership file ##############################

        String line;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(membershipFilePath));

            int numberOfProcesses = 0;
            if ((line = bufferedReader.readLine()) != null) {
                numberOfProcesses = Integer.parseInt(line);
            }

            /*
             * Read the first n (number of processes in the membership file) lines, and create all the processes objects.
             * Each process is described as a ProcessDetails object.
             */

            ProcessDetails[] processesInNetwork = new ProcessDetails[numberOfProcesses];
            for (int i = 0; i < numberOfProcesses; ++i) {
                line = bufferedReader.readLine();
                String[] process_details = line.split("\\s+");

                int id = Integer.parseInt(process_details[0]);
                String address = process_details[1];
                int port = Integer.parseInt(process_details[2]);

                processesInNetwork[i] = new ProcessDetails(id, address, port);
            }

            /*
             * Read the remaining n lines of the membership file. Get the set of localized processes for the
             * ProcessToLaunchId.
             */

            HashSet<ProcessDetails> localized = new HashSet<>();
            for (int i = 1; i <= numberOfProcesses; ++i) {
                line = bufferedReader.readLine();
                if (i == ProcessToLaunchId) {
                    String[] causality_informations = line.split("\\s+");

                    /* The first element (0) in the causality_informations list is the id of the ProcessToLaunchId.
                     * We do not to include in the localized set.
                     */

                    for (int j = 1; j < causality_informations.length; ++j) {
                        localized.add(processesInNetwork[Integer.parseInt(causality_informations[j]) - 1]);
                    }
                }
            }

            bufferedReader.close();
            ProcessDetails processToLaunch = processesInNetwork[ProcessToLaunchId - 1];
            new Process(processToLaunch.getPort(), ProcessToLaunchId, processesInNetwork, numberOfMessages, localized);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


