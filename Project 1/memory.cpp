//
//  memory.cpp
//  OSProject1
//
//  Created by Nisarg Desai on 9/25/19.
//  Copyright © 2019 Nisarg Desai. All rights reserved.
//

#include <unistd.h>
#include <fstream>

#include "memory.hpp"

void Memory::run(int cpuToMemPipe[2], int memToCpuPipe[2]) {

//    cout << "in memory->run()" << endl;
    pid_t pid = getpid();
    cout << "pid from memory process: " << pid << endl;

    while (true) {
        // cout << "in run method in memory class" << endl;

        int addr;
        int cpuInstr;
        read(cpuToMemPipe[0], &cpuInstr, sizeof(int));

        // cout << "readAndLoadFile(10)/read(100)/write(200): " << cpuInstr << endl;

        if (cpuInstr == 10) {

            string filename;
            int filenameLength;
            read(cpuToMemPipe[0], &filenameLength, sizeof(int));
            read(cpuToMemPipe[0], &filename, filenameLength);

            int status = this->readFile(filename);
            if (status == false) {
                int exitCode = 0;
                write(memToCpuPipe[1], &exitCode, sizeof(int));
                exit(10);
            } else {
                int exitCode = 1;
                write(memToCpuPipe[1], &exitCode, sizeof(int));
            }

            // cout << "memory[3]: " << memory[3] << endl;

        } else if (cpuInstr == 100) {

            // cout << "address to read: " << addr << endl;
            read(cpuToMemPipe[0], &addr, sizeof(int));
            int result = this->readMem(addr);
            // cout << "result from readMem: " << result << endl;
            write(memToCpuPipe[1], &result, sizeof(int));
            // cout << "written to memToCpu pipe" << endl;

        } else if (cpuInstr == 200) {

            // cout << "address to write to: " << addr << endl;

            read(cpuToMemPipe[0], &addr, sizeof(int));

            int valueToWrite;
            read(cpuToMemPipe[0], &valueToWrite, sizeof(int));
            // cout << "value to write: " << valueToWrite << endl;

            bool isWritten = this->writeMem(valueToWrite, addr);
            // cout << "isWritten: " << isWritten << endl;
            // cout << "--" << endl;
            write(memToCpuPipe[1], &isWritten, 1);
        }
    }

    // cout << "returning from run method" << endl;
    return;
}

bool Memory::readFile(const string& filename) {

    ifstream inFile;
    // ofstream outFile;

    // cout << "filename from memory->readFile: " << filename << endl;
    inFile.open(filename);
    // outFile.open("test");

    if (inFile.fail()) {
        cout << "File could not be found. Exiting process..." << endl;
        return false;
    }

    // cout << "file opened" << endl << endl;

    char ch;
    int n;
    int index = 0;

    cout << ch << endl;

    while (!inFile.eof()) {
        ch = inFile.get();

        if ((ch >= '0') && (ch <= '9')) {
            inFile.putback(ch);
            inFile >> n;
            memory[index] = n;
//            cout << "memory[" << index << "]: " << memory[index] << endl;
            // outFile << memory[index] << endl;
            index++;
        } else if (ch == '.') {
            inFile >> n;
            index = n;
        } else if (ch == EOF) {
            break;
        } else {
            string str;
            inFile.putback(ch);
            getline(inFile, str);
        }
    }

    return true;
}

// bool Memory::writeMem(int *data, int size) {
//     for (int i = 0; i < size; i++) {
//         storage[i] = data[i];
//     }

//     return true;
// }

bool Memory::writeMem(int data, int addr) {
    memory[addr] = data;
    return true;
}


int Memory::readMem(int addr) {

    // for (int i = 0; i < 20; i++) {
    //     cout << memory[i] << " ";
    // }
    // cout << endl;
    // cout << "reading mem addr: " << addr << endl;
    // cout << "memory[" << addr << "]: " << memory[addr] << endl;
    return memory[addr];
}


