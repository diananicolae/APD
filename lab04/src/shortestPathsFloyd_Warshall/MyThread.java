package shortestPathsFloyd_Warshall;

public class MyThread extends Thread {
    private final int id;

    public MyThread(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        int start = (int) (id * (double) 5 / MainPara.cores);
        int end = (int) Math.min((id + 1) * ((double) 5 / MainPara.cores), 5);

        for (int k = start; k < end; k++) {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    MainPara.graph[i][j] =
                            Math.min(MainPara.graph[i][k] +
                                     MainPara.graph[k][j],
                                     MainPara.graph[i][j]);
                }
            }
        }
    }
}