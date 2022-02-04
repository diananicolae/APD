package shortestPathsFloyd_Warshall;

public class MainPara {
    public static int cores;
    public static int M = 9;
    public static int[][] graph =
            {{0, 1, M, M, M},
            {1, 0, 1, M, M},
            {M, 1, 0, 1, 1},
            {M, M, 1, 0, M},
            {M, M, 1, M, 0}};

    public static void main(String[] args) {

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

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                System.out.print(graph[i][j] + " ");
            }
            System.out.println();
        }
    }
}
