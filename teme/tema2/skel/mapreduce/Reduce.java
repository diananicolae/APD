package mapreduce;

import utils.Fibonacci;
import utils.Output;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for the Reduce operation, produces a result of type Output
 */
public class Reduce implements MapReduce<Output> {
    private final String fileName;
    private final List<Output> list;

    public Reduce(String fileName, List<Output> list) {
        this.fileName = fileName;
        this.list = list;
    }

    @Override
    public Output execute() {
        Output finalOutput = new Output(fileName);
        HashMap<Integer, Integer> dictionary = finalOutput.getDictionary();
        ArrayList<String> maximalWords = finalOutput.getMaximalWords();

        /* Iterate through the list of results for the current file */
        for (Output output : list) {
            List<String> currMaximalWords = output.getMaximalWords();
            /* If the final list of maximal words is empty or the current list
            * has words of the same maximal length, add all of them to the final list */
            if (maximalWords.isEmpty() || (!currMaximalWords.isEmpty() &&
                    currMaximalWords.get(0).length() == maximalWords.get(0).length())) {
                maximalWords.addAll(currMaximalWords);
            } else if (!currMaximalWords.isEmpty() &&
                    currMaximalWords.get(0).length() > maximalWords.get(0).length()) {
                /* If the current list has longer words than the final list,
                * clear the list and add all of them to the final list */
                maximalWords.clear();
                maximalWords.addAll(currMaximalWords);
            }

            for (Map.Entry<Integer, Integer> entry : output.getDictionary().entrySet()) {
                /* If the final dictionary already contains the current length
                * increase the total occurrence number */
                if (dictionary.containsKey(entry.getKey())) {
                    int value = dictionary.get(entry.getKey());
                    dictionary.replace(entry.getKey(), entry.getValue() + value);
                } else {
                    /* Otherwise, add a new entry in the dictionary */
                    dictionary.put(entry.getKey(), entry.getValue());
                }
            }
        }

        float rank = 0;
        int totalWords = 0;

        /* Iterate through the final dictionary and determine the file rank
        * using Fibonacci */
        for (Map.Entry<Integer, Integer> entry : dictionary.entrySet()) {
            rank += Fibonacci.get(entry.getKey()) * entry.getValue();
            totalWords += entry.getValue();
        }
        rank /= totalWords;
        finalOutput.setRank(rank);

        return finalOutput;
    }
}
