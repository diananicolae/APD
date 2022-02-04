package utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class for the output result
 * Contains a dictionary and a list with the words of maximal length
 */
public class Output {
    private final String fileName;
    private final HashMap<Integer, Integer> dictionary;
    private final ArrayList<String> maximalWords;
    private float rank;

    public Output(String fileName) {
        this.fileName = fileName;
        this.dictionary = new HashMap<>();
        this.maximalWords = new ArrayList<>();
    }

    public String getFileName() {
        return fileName;
    }

    public HashMap<Integer, Integer> getDictionary() {
        return dictionary;
    }

    public ArrayList<String> getMaximalWords() {
        return maximalWords;
    }

    public float getRank() {
        return rank;
    }

    public void setRank(float rank) {
        this.rank = rank;
    }
}
