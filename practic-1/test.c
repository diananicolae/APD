#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <math.h>

pthread_mutex_t mutex;
pthread_cond_t cond;
int id;
int count;

typedef struct _thread_arg {
	int thread_id;
	int thread_num;
	int *sleep_time;
} thread_arg;

void *thread_function(void *arg)
{
	thread_arg *t_arg = (thread_arg *) arg;
    
    int thread_id = t_arg->thread_id;
    int thread_num = t_arg->thread_num;
	int *sleep_time = t_arg->sleep_time;

	if (thread_id != 0) {
		sleep(sleep_time[thread_id]);
		pthread_mutex_lock(&mutex);
		id = thread_id;
		count++;
		pthread_cond_signal(&cond);
		pthread_mutex_unlock(&mutex);
	}

	if (thread_id == 0) {
		while (count != thread_num - 1) {
			pthread_cond_wait(&cond, &mutex);
			printf("Thread-ul %d a terminat\n", id);
		}
	}
	
	pthread_exit(NULL);
}

int main(int argc, char *argv[])
{
	int P;
	P = atoi(argv[1]);

	if(argc < P + 1) {
		printf("Numar insuficient de parametri: ./test N P\n");
		exit(1);
	}

	int *sleep_time = calloc(P, sizeof(int));

	for (int i = 0; i < P - 1; i++) {
		sleep_time[i + 1] = atoi(argv[i + 2]);
	}

	pthread_t *threads = calloc(P, sizeof(pthread_t));
	thread_arg *t_args = calloc(P, sizeof(thread_arg));

	pthread_mutex_init(&mutex, NULL);
	pthread_cond_init(&cond, NULL);
	count = 0;

	// se creeaza thread-urile
	for (int i = 0; i < P; i++) {
		t_args[i].thread_id = i;
		t_args[i].thread_num = P;
		t_args[i].sleep_time = sleep_time;
		int r = pthread_create(&threads[i], NULL, thread_function, &t_args[i]);
        
        if (r) {
			printf("Error when creating thread %d\n", i);
			exit(-1);
		}
	}

	// se asteapta thread-urile
	for (int i = 0; i < P; i++) {
		int r = pthread_join(threads[i], NULL);
        
        if (r) {
			printf("Error when joining thread %d\n", i);
			exit(-1);
		}
	}

	pthread_cond_destroy(&cond);
	pthread_mutex_destroy(&mutex);
	free(threads);
	free(t_args);
	free(sleep_time);

	return 0;
}
