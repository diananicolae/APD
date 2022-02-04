package doubleVectorElements;

import java.util.ArrayList;

public class Main {
    public static int N = 100000013;
    public static int cores;
    public static int[] v = new int[N];

    public static void main(String[] args) {

        for (int i = 0; i < N; i++) {
            v[i] = i;
        }

//        // Parallelize me
//        for (int i = 0; i < N; i++) {
//            v[i] = v[i] * 2;
//        }

        cores = Runtime.getRuntime().availableProcessors();
        Thread[] threads = new Thread[cores];

        for (int i = 0; i < cores; i++) {
            threads[i] = new MyThread(i);
            threads[i].start();
        }

        for (int i = 0; i < cores; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        for (int i = 0; i < N; i++) {
            if (v[i] != i * 2) {
                System.out.println("Wrong answer");
                System.exit(1);
            }
        }
        System.out.println("Correct");
    }

}
