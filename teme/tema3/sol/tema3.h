#include "mpi.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define max(a,b) (((a) > (b)) ? (a) : (b))
#define min(a,b) (((a) < (b)) ? (a) : (b))
#define ROOT 0

static int **workers, *num_workers;
static int rank, num_clusters = 3;

/* Leaders read and store clusters from input file */
void read_clusters() {
    FILE *fp;
    char file_name[20];

    /* Open assigned file */
    sprintf(file_name, "cluster%d.txt", rank);
    fp = fopen(file_name, "r");

    /* Read and store the workers of the current cluster */
	fscanf(fp, "%d", &num_workers[rank]);
    workers[rank] = malloc(sizeof(int) * num_workers[rank]);

	for (int i = 0; i < num_workers[rank]; i++) {
        fscanf(fp, "%d", &workers[rank][i]);
    }	
}

/* Send MPI message with the given data */
void send_message(void* data, int count, int destination) {
    MPI_Send(data, count, MPI_INT, destination, 0, MPI_COMM_WORLD);
    printf("M(%d,%d)\n", rank, destination);
}

/* Receive MPI message with the given data */
void recv_message(void* data, int count, int source) {
    MPI_Recv(data, count, MPI_INT, source, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
}

/* Print the topology */
void print_topology() {
    printf("%d -> ", rank);
    for (int i = 0; i < num_clusters; i++) {
        printf("%d:%d", i, workers[i][0]);

        for (int j = 1; j < num_workers[i]; j++) {
            printf(",%d", workers[i][j]);
        }
        printf(" ");
    }
    printf("\n");
}
