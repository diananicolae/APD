#include "tema3.h"

int main (int argc, char *argv[])
{
    int num_procs, leader;
    int n, *v, *final_v;
    int chunk, surplus, root_chunk, index;

    MPI_Init(&argc, &argv);
    /* Total number of processes */
    MPI_Comm_size(MPI_COMM_WORLD, &num_procs);
    /* The current process rank */
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    workers = (int **) malloc(sizeof(int *) * num_clusters);
    num_workers = (int *) malloc(sizeof(int) * num_clusters);

    /* Part 1 - establishing the topology*/
    if (rank < num_clusters) {
        /* Leaders read the clusters from file */
        read_clusters();

        /* Communicate with the other leaders */
        for (int i = 0; i < num_clusters; i++) {
            if (i == rank) {
                continue;
            }
            /* Each leader sends their topology to the other leaders */
            send_message(&num_workers[rank], 1, i);
            send_message(workers[rank], num_workers[rank], i);

            /* Each leader receives the topology from the other leaders */
            recv_message(&num_workers[i], 1, i);
            workers[i] = (int *) malloc(sizeof(int) * num_workers[i]);
            recv_message(workers[i], num_workers[i], i);
        }

        for (int i = 0; i < num_workers[rank]; i++) {
            /* Leader informs workers of their leader and sends the final topology */
            send_message(&rank, 1, workers[rank][i]);
            send_message(num_workers, num_clusters, workers[rank][i]);
            
            /* Send complete topology to workers */
            for (int j = 0; j < num_clusters; j++) {
                send_message(workers[j], num_workers[j], workers[rank][i]);
            }
        }
    } else {
        /* Workers receive message from with leader and topology */
        recv_message(&leader, 1, MPI_ANY_SOURCE);
        recv_message(num_workers, num_clusters, leader);

        for (int i = 0; i < num_clusters; i++) {
            workers[i] = (int *) malloc(sizeof(int) * num_workers[i]);
            recv_message(workers[i], num_workers[i], leader);
        }
    }

    /* Every process prints the topology */
    print_topology();

    /* Part 2 - perform array operations */
    /* Root creates the array and sends the chunks to leaders */
    if (rank == ROOT) {
        n = atoi(argv[1]);

        /* Create and fill the array with values */
        final_v = (int *) malloc(sizeof(int) * n);
        for (int i = 0; i < n; i++) {
            final_v[i] = i;
        }

        /* Determine the array surplus */
        surplus = n % (num_procs - num_clusters);
        index = 0;

        /* Send array chunks to leaders 1 & 2 */
        for (int i = 0; i < num_clusters; i++) {
            /* Determine the chunk size and add any surplus */
            chunk = (n / (num_procs - num_clusters)) * num_workers[i];
            chunk += min(surplus, num_workers[i]);
            surplus = max(0, surplus - num_workers[i]);

            /* Send chunks to leaders, root saves the chunk size */
            if (i == ROOT) {
                root_chunk = chunk;
            } else {
                send_message(&chunk, 1, i);
                send_message(&final_v[index], chunk, i);
            }
            index += chunk;
        }
    }

    /* Leaders receive array chunks from root and send them to workers */
    if (rank < num_clusters) {
        if (rank != ROOT) {
            /* Leaders 1 & 2 receive assigned array from root */
            recv_message(&chunk, 1, ROOT);
            v = (int *) malloc(sizeof(int) * chunk);
            recv_message(v, chunk, ROOT);
        } else {
            /* Root copies assigned chunk of the array */
            chunk = root_chunk;
            v = (int *) malloc(sizeof(int) * chunk);
            memcpy(v, final_v, sizeof(int) * chunk);
        }

        /* Determine the array surplus */
        surplus = chunk % num_workers[rank];
        index = 0;

        /* Split array and send chunks to workers */
        for (int i = 0; i < num_workers[rank]; i++) {
            int worker_chunk = chunk / num_workers[rank];
            if (surplus != 0) {
                worker_chunk++;
                surplus--;
            }

            /* Send array chunks to workers */
            send_message(&worker_chunk, 1, workers[rank][i]);
            send_message(&v[index], worker_chunk, workers[rank][i]);

            /* Receive modified array chunks from workers */
            recv_message(&v[index], worker_chunk, workers[rank][i]);
            index += worker_chunk;
        }

        /* Leaders 1 & 2 send modified array to root */
        if (rank != ROOT) {
            send_message(&chunk, 1, ROOT);
            send_message(v, chunk, ROOT);
        }
    } else {
        /* Workers receive array chunk from leader */
        int chunk;
        recv_message(&chunk, 1, leader);
        v = (int *) malloc(sizeof(int) * chunk);
        recv_message(v, chunk, leader);

        /* Workers modify assigned array chunk */
        for (int i = 0; i < chunk; i++) {
            v[i] *= 2;
        }

        /* Workers send modified chunk back to leader */
        send_message(v, chunk, leader);
    }
    
    /* Root receives the modified arrays from leaders */
    if (rank == ROOT) {
        /* Copy array modified by root workers */
        memcpy(final_v, v, chunk * sizeof(int));

        /* Receive arrays from leaders 1 & 2*/
        index = chunk;
        for (int i = 1; i < num_clusters; i++) {
            recv_message(&chunk, 1, i);
            recv_message(&final_v[index], chunk, i);
            index += chunk;
        }
    }

    /* Wait for all the processes to complete their work */
    MPI_Barrier(MPI_COMM_WORLD);

    /* Root prints the final result */
    if (rank == ROOT) {
        printf("Rezultat: ");
        for (int i = 0; i < n; i++) {
            printf("%d ", final_v[i]);
        }
        printf("\n");
    }

    MPI_Finalize();
}
