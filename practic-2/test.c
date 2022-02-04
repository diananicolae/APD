#include "mpi.h"
#include <stdio.h>
#include <stdlib.h>

#define max(a,b) (((a) > (b)) ? (a) : (b))
#define min(a,b) (((a) < (b)) ? (a) : (b))

int main (int argc, char *argv[])
{
    int num_procs, rank, num, neigh_num;

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &num_procs);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    
    num = rank * 2;

    for (int i = 0; i < num_procs; i++) {
        if (i % 2 == 0) {
            if (rank % 2 == 0 && rank + 1 < num_procs) {
                // task-urile pare compara val cu vecinul drept, pastreaza maximul
                MPI_Send(&num, 1, MPI_INT, rank + 1, 0, MPI_COMM_WORLD);
                MPI_Recv(&neigh_num, 1, MPI_INT, rank + 1, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
                num = max(num, neigh_num);
            } else if (rank % 2 == 1 && rank - 1 >= 0) {
                // task-urile impare compara val cu vecinul stang, pastreaza minimul
                MPI_Send(&num, 1, MPI_INT, rank - 1, 0, MPI_COMM_WORLD);
                MPI_Recv(&neigh_num, 1, MPI_INT, rank - 1, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
                num = min(num, neigh_num);
            }
        } else {
            if (rank % 2 == 0 && rank - 1 >= 0) {
                // task-urile pare compara val cu vecinul stang, pastreaza minimul
                MPI_Send(&num, 1, MPI_INT, rank - 1, 0, MPI_COMM_WORLD);
                MPI_Recv(&neigh_num, 1, MPI_INT, rank - 1, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
                num = min(num, neigh_num);
            } else if (rank % 2 == 1 && rank + 1 < num_procs) {
                // task-urile impare compara val cu vecinul drept, pastreaza maximul
                MPI_Send(&num, 1, MPI_INT, rank + 1, 0, MPI_COMM_WORLD);
                MPI_Recv(&neigh_num, 1, MPI_INT, rank + 1, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
                num = max(num, neigh_num);
            }
        }
    }

    printf("%d - %d\n", rank, num);

    // fiecare proces retine valorile proceselor dinaintea lui
    int *v = (int *) malloc((rank + 1) * sizeof(int));
    // completeaza vectorul cu valoarea cunoscuta
    v[rank] = num;

    // toate procesele (fara primul) primesc valorile de la procesul anterior
    if (rank != 0) {
        MPI_Recv(v, rank, MPI_INT, rank - 1, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
    }

    // toate procesele (fara ultimul) trimit valorile la procesul urmator
    if (rank != num_procs - 1) {
        MPI_Send(v, rank + 1, MPI_INT, rank + 1, 0, MPI_COMM_WORLD);
    }

    MPI_Barrier(MPI_COMM_WORLD);
    // ultimul proces afiseaza vectorul final
    if (rank == num_procs - 1) {
        for (int i = 0; i < num_procs; i++) {
            printf("%d ", v[i]);
        }
        printf("\n");
    }
    
    MPI_Finalize();
}
