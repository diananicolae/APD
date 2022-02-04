build:
	mpicc polynomial.c -o polynomial -lm
run1:
	mpirun --allow-run-as-root -np 2 polynomial a1.txt 
run2:
	mpirun --allow-run-as-root --oversubscribe -np 14 polynomial a2.txt 
clear:
	rm polynomial
