package multipleProducersMultipleConsumersNBuffer;

import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Buffer {
    
    final Queue<Integer> queue;
    Semaphore plin, gol;

    public Buffer(int size) {
        queue = new LimitedQueue<>(size);
        plin = new Semaphore(0);
        gol = new Semaphore(size);
    }

	public void put(int value) {
        try {
            gol.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        synchronized (queue) {
            queue.add(value);
        }

        plin.release();

	}

	public int get() {
        int value;

        try {
            plin.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        synchronized (queue) {
            value = queue.remove();
        }

        gol.release();
        return value;
	}
}
