package mapreduce;

/**
 * Interface for the Map and Reduce operations
 * @param <T> generic output result
 */
public interface MapReduce<T> {
    T execute();
}
