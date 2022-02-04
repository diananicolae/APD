package utils;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Class for the fibonacci expression
 * Determines the value using ForkJoinPool
 */
public class Fibonacci extends RecursiveTask<Integer> {
    private final int n;

    public Fibonacci(int n) {
        this.n = n;
    }

    @Override
    protected Integer compute() {
        if (n < 3) {
            return n;
        }

        Fibonacci first = new Fibonacci(n - 1);
        Fibonacci second = new Fibonacci(n - 2);

        first.fork();
        second.fork();

        return first.join() + second.join();
    }

    public static int get(int n) {
        ForkJoinPool fjp = new ForkJoinPool(4);
        int result = fjp.invoke(new Fibonacci(n));
        fjp.shutdown();

        return result;
    }
}
