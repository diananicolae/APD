package task1;

import java.beans.IntrospectionException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        int cores = Runtime.getRuntime().availableProcessors();
        MyThread[] threads = new MyThread[cores];

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

    }
}
