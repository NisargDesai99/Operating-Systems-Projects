compile:
	@echo "Compiling C++ code..."
	# g++ -o run memory.cpp main.cpp

	g++ -std=c++11 -o main.o -c main.cpp
	g++ -std=c++11 -o memory.o -c memory.cpp
	g++ -std=c++11 -o cpu.o -c cpu.cpp
	g++ -o run main.o memory.o cpu.o

	# 	g++ -o main.o -c main.cpp
	# 	g++ -o memory.o -c memory.cpp
	# 	g++ -o cpu.o -c cpu.cpp
	# 	g++ -o run main.o memory.o cpu.o

itrun:

	g++ -std=c++11 -o main.o -c main.cpp
	g++ -std=c++11 -o memory.o -c memory.cpp
	g++ -std=c++11 -o cpu.o -c cpu.cpp
	g++ -o run main.o memory.o cpu.o

	# g++ -o main.o -c main.cpp
	# g++ -o memory.o -c memory.cpp
	# g++ -o cpu.o -c cpu.cpp
	# g++ -o run main.o memory.o cpu.o
	
	./run

