#include "mpi.h"
#include <stdio.h>
#include <stdlib.h>

int main (int argc, char *argv[])
{
    MPI_Request request;
    MPI_Status status;
    int numtasks, rank, recv_num, flag;

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &numtasks);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    // First process starts the circle.
    if (rank == 0) {
        // First process starts the circle.
        // Generate a random number.
        recv_num = rand() % 10;
        // Send the number to the next process.
        MPI_Isend(&recv_num, 1, MPI_INT, 1, 0, MPI_COMM_WORLD, &request);

        MPI_Test(&request, &flag, &status);

        if (flag) {
            printf("[P%d] The send operation is over\n", rank);
        } else {
            printf("[P%d] The send operation is not over yet\n", rank);
            MPI_Wait(&request, &status);
        }

    } else if (rank == numtasks - 1) {
        // Last process close the circle.
        // Receives the number from the previous process.
        MPI_Irecv(&recv_num, 1, MPI_INT, rank - 1, 0, MPI_COMM_WORLD, &request);

        MPI_Test(&request, &flag, &status);

        if (flag) {
            printf("[P%d] The recv operation is over\n", rank);
        } else {
            printf("[P%d] The recv operation is not over yet\n", rank);
            MPI_Wait(&request, &status);
        }
        // Increments the number.
        recv_num += 2;
        // Sends the number to the first process.
        MPI_Isend(&recv_num, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, &request);

        MPI_Test(&request, &flag, &status);

        if (flag) {
            printf("[P%d] The send operation is over\n", rank);
        } else {
            printf("[P%d] The send operation is not over yet\n", rank);
            MPI_Wait(&request, &status);
        }

    } else {
        // Middle process.
        // Receives the number from the previous process.
        MPI_Irecv(&recv_num, 1, MPI_INT, rank - 1, 0, MPI_COMM_WORLD, &request);

        MPI_Test(&request, &flag, &status);

        if (flag) {
            printf("[P%d] The recv operation is over\n", rank);
        } else {
            printf("[P%d] The recv operation is not over yet\n", rank);
            MPI_Wait(&request, &status);
        }
        // Increments the number.
        recv_num += 2;
        // Sends the number to the next process.
        MPI_Isend(&recv_num, 1, MPI_INT, rank + 1, 0, MPI_COMM_WORLD, &request);

        MPI_Test(&request, &flag, &status);

        if (flag) {
            printf("[P%d] The send operation is over\n", rank);
        } else {
            printf("[P%d] The send operation is not over yet\n", rank);
            MPI_Wait(&request, &status);
        }

    }

    printf("Process [%d] has number %d.\n", rank, recv_num);

    MPI_Finalize();

}

