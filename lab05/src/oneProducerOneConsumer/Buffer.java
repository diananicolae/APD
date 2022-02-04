package oneProducerOneConsumer;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Buffer {
    final Queue<Integer> q;

    Semaphore gol, plin;

    public Buffer() {
        this.q = new LinkedList<>();
        this.gol = new Semaphore(1);
        this.plin = new Semaphore(0);
    }

    void put(int value) {
        try {
            gol.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        synchronized (q) {
            q.add(value);
        }

        plin.release();
    }

    int get() {
        int value;

        try {
            plin.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        synchronized (q) {
            value = q.remove();
        }

        gol.release();
        return value;
    }

}
