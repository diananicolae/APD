package tasks;

import mapreduce.MapReduce;
import java.util.concurrent.RecursiveTask;

/**
 * Class used for the ForkJoinPool tasks
 * Depending on the type, can execute the Map or Reduce operation
 * @param <T> generic output result
 */
public class Task<T> extends RecursiveTask<T> {
    private final MapReduce<T> task;

    public Task(MapReduce<T> task) {
        this.task = task;
    }

    @Override
    protected T compute() {
        return task.execute();
    }
}
