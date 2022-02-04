import mapreduce.Map;
import mapreduce.Reduce;
import utils.Output;
import tasks.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

public class Tema2 {
    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("Usage: Tema2 <workers> <in_file> <out_file>");
            return;
        }

        /* Parse arguments, create reader and writer for input and output files */
        int workersNumber = Integer.parseInt(args[0]);

        Scanner scanner = new Scanner(new File(args[1]));
        PrintWriter writer = new PrintWriter(args[2], StandardCharsets.UTF_8);

        /* Parse the main document to determine the chunk size and number of files */
        int chunkSize = Integer.parseInt(scanner.nextLine());
        int filesNumber = Integer.parseInt(scanner.nextLine());

        /* Create a ForkJoinPool with the parallelism level of the number of workers */
        ForkJoinPool fjp = new ForkJoinPool(workersNumber);

        /* Create a list of tasks used for the pool which return the wanted result */
        List<Task<Output>> tasks = new ArrayList<>();

        /* Parse the list of files from the main file */
        for (int i = 0; i < filesNumber; i++) {
            /* Open every file and determine the file size */
            String fileName = scanner.nextLine();
            File file = new File(fileName);
            long size = file.length();
            int offset = 0;

            /* Create a Map task for each chunk of text of the given size and fork */
            while (offset < size) {
                Task<Output> task = new Task<>(new Map(fileName, file, size, offset, chunkSize));
                tasks.add(task);
                task.fork();
                offset += chunkSize;
            }
        }

        /* Create a map containing a list of partial results for each file */
        HashMap<String, List<Output>> partialResults = new HashMap<>();

        /* Join the Map tasks and retrieve the results */
        for (Task<Output> task : tasks) {
            Output output = task.join();

            /* Put the result in the corresponding list for the file */
            if (partialResults.containsKey(output.getFileName())) {
                List<Output> list = partialResults.get(output.getFileName());
                list.add(output);
                partialResults.replace(output.getFileName(), list);
            } else {
                List<Output> list = new ArrayList<>();
                list.add(output);
                partialResults.put(output.getFileName(), list);
            }
        }

        /* Clear the list of tasks to prepare for the Reduce tasks */
        tasks.clear();

        /* For every file, create a Reduce task and fork */
        for (java.util.Map.Entry<String, List<Output>> entry : partialResults.entrySet()) {
            Task<Output> task = new Task<>(new Reduce(entry.getKey(), entry.getValue()));
            tasks.add(task);
            task.fork();
        }

        /* Create a list of final results with one result for each document */
        List<Output> finalResults = new ArrayList<>();

        /* Join the Reduce tasks and retrieve the results */
        for (Task<Output> task : tasks) {
            Output output = task.join();
            finalResults.add(output);
        }

        /* Sort the list of file results by the file's rank */
        finalResults.sort(Comparator.comparing(Output::getRank).reversed());

        /* Print the results in the output file */
        for (Output output : finalResults) {
            int maxLen = output.getMaximalWords().get(0).length();
            int size = output.getMaximalWords().size();

            writer.println(output.getFileName().substring(12) + "," +
                    String.format("%.2f", output.getRank()) +
                    "," + maxLen + "," + size);
        }

        fjp.shutdown();
        scanner.close();
        writer.close();
    }
}
