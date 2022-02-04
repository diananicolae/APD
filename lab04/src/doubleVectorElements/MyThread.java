package doubleVectorElements;

public class MyThread extends Thread {
    private final int id;

    public MyThread(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        int start = (int) (id * (double) Main.N / Main.cores);
        int end = (int) Math.min((id + 1) * ((double) Main.N / Main.cores), Main.N);

        for (int i = start; i < end; i++) {
            Main.v[i] *= 2;
        }
    }
}
