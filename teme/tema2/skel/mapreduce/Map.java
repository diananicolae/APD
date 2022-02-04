package mapreduce;

import utils.Output;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

/**
 * Class for the Map operation, produces a result of type Output
 */
public class Map implements MapReduce<Output> {
    private final String fileName;
    private final File file;
    private final long fileSize;
    private int offset;
    private int chunkSize;

    public Map(String fileName, File file, long fileSize, int offset, int chunkSize) {
        this.fileName = fileName;
        this.file = file;
        this.fileSize = fileSize;
        this.offset = offset;
        this.chunkSize = chunkSize;
    }

    @Override
    public Output execute() {
        String delimiters = "[;:/?~.,><`\\[\\]{}()!@#$%^&\\-_+'=*\"| \t\r\n]+";
        Output output = new Output(fileName);

        try {
            /* Create random access file */
            RandomAccessFile f = new RandomAccessFile(file, "r");

            /* Check whether the chunk starts in the middle of a word */
            if (offset != 0) {
                /* Read the last character from the previous chunk and the first
                * character from the current chunk */
                f.seek(offset - 1);
                byte[] b = new byte[2];
                f.read(b);
                String s = new String(b, StandardCharsets.UTF_8);

                /* If both characters are letters, it means that the file starts
                * in the middle of the word */
                if (s.matches("[a-zA-Z]+")) {
                    /* Read the full word and modify the offset in order to skip it */
                    f.seek(offset);
                    s = f.readLine();
                    String[] tokens = s.split(delimiters);
                    offset += tokens[0].length();
                    chunkSize -= tokens[0].length();
                }
            }

            /* Check whether the chunk ends in the middle of a word */
            if (fileSize - offset > chunkSize) {
                /* Read the last character from the current chunk and the first
                 * character from the following chunk */
                f.seek(offset + chunkSize - 1);
                byte[] b = new byte[2];
                f.read(b);
                String s = new String(b, StandardCharsets.UTF_8);

                /* If both characters are letters, it means that the file ends
                 * in the middle of the word */
                if (s.matches("[a-zA-Z]+")) {
                    /* Read the full word and modify the chunk size in order to keep it */
                    f.seek(offset + chunkSize);
                    s = f.readLine();
                    String[] tokens = s.split(delimiters);
                    chunkSize += tokens[0].length();
                }
            }

            /* Read the chunk of text from the file */
            f.seek(offset);
            chunkSize = (int) Math.min(chunkSize, fileSize - offset);
            byte[] bytes = new byte[chunkSize];
            f.read(bytes);
            String data = new String(bytes, StandardCharsets.UTF_8);

            /* Split the chunk of text into words using the delimiters */
            for (String s : data.split(delimiters)) {
                int len = s.length();
                /* Skip empty words */
                if (len == 0) {
                    continue;
                }

                /* If the dictionary already contains the word length,
                * increase the occurrence number */
                if (output.getDictionary().containsKey(len)) {
                    int value = output.getDictionary().get(len);
                    output.getDictionary().replace(len, value + 1);
                } else {
                    /* Otherwise, add the word length in the dictionary */
                    output.getDictionary().put(len, 1);
                }

                /* If the maximal words list is empty or the current word
                * has the current maximal length, add it to the list */
                if (output.getMaximalWords().isEmpty()||
                        output.getMaximalWords().get(0).length() == len) {
                    output.getMaximalWords().add(s);
                } else if (len > output.getMaximalWords().get(0).length()) {
                    /* Otherwise, if the current word is longer than the
                    * maximal length, clear the list and add it */
                    output.getMaximalWords().clear();
                    output.getMaximalWords().add(s);
                }
            }

            f.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;
    }
}
