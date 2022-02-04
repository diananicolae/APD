package multipleProducersMultipleConsumers;

import java.util.concurrent.ArrayBlockingQueue;

public class Buffer {
	ArrayBlockingQueue<Integer> queue;

	public Buffer() {
		this.queue = new ArrayBlockingQueue<Integer>(10);
	}

	void put(int value) {
		try {
			this.queue.put(value);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	int get() {
		try {
			return this.queue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
