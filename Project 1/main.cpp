//
//  main.cpp
//  OSProject1
//
//  Created by Nisarg Desai on 9/25/19.
//  Copyright © 2019 Nisarg Desai. All rights reserved.
//

#include <stdio.h>
#include <unistd.h>
#include <iostream>
#include <string>
#include <fstream>

#include "cpu.hpp"
#include "memory.hpp"

using namespace std;

void readFileToMemory(string filename, int cpuToMem[2]) {
    int readFileInstr = 10;
    int filenameLength = 20;
    // cout << "filename = " << filename << endl;
    write(cpuToMem[1], &readFileInstr, sizeof(int));
    write(cpuToMem[1], &filenameLength, sizeof(int));
    write(cpuToMem[1], &filename, filenameLength);
}

int main(int argc, const char * argv[]) {

    // cout << "Hello, World!" << endl;

    string filename;
    int timerFlag;

    // cout << "-----" << endl;
    for (int i = 0; i < argc; ++i) {
        if (i == 1) {
            filename = argv[i];
        }
        if (i == 2) {
            timerFlag = stoi(argv[2]);
        }
    }
    cout << "filename: " << filename << "  timerFlag: " << timerFlag << endl;
    // cout << "-----" << endl;

    int cpuToMem[2];
    int memToCpu[2];

    if (pipe(cpuToMem)==-1) {
        fprintf(stderr, "CPU to Mem Pipe Failed" );
        return 1;
    }
    if (pipe(memToCpu)==-1) {
        fprintf(stderr, "Mem To CPU Pipe Failed" );
        return 1;
    }

    int pid = fork();

    if (pid < 0) {
        // cout << "Fork failed..." << endl;
        return 10;
    } else if (pid == 0) {
        // child process for memory
        Memory *mem = new Memory();

        // cout << "calling run on memory" << endl;
        mem->run(cpuToMem, memToCpu);

    } else {
        // parent process for cpu
        CPU *cpu = new CPU(timerFlag);

        readFileToMemory(filename, cpuToMem);

        int readStatus;
        read(memToCpu[0], &readStatus, sizeof(int));

//        cout << "readStatus: " << readStatus << endl;

        if (readStatus == 0) {
            exit(10);
        } else {
            cpu->run(cpuToMem, memToCpu);
        }

        // while (true) {
        // fetch instruction
        // call run instruction
        // }


        // cpu->writeToMemory(1, 500, cpuToMem, memToCpu);
        // wait(NULL);
        // cout << "written, back in main" << endl;
        // cout << "--------" << endl;
        // cpu->readFromMemory(1, cpuToMem, memToCpu);



        // cout << "back" << endl;
        // cpu->readFileIntoMem("instr1.txt");

    }



    return 0;
}
