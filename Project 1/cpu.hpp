//
//  CPU.hpp
//  OSProject1
//
//  Created by Nisarg Desai on 9/25/19.
//  Copyright © 2019 Nisarg Desai. All rights reserved.
//

#ifndef cpu_hpp
#define cpu_hpp

#include <string>
#include <unistd.h>
#include <stdio.h>
#include <iostream>

#include "memory.hpp"

using namespace std;

class CPU {

private:

    // registers
    int PC, SP, IR, AC, X, Y;

    // to keep track of how many instructions have been run,
    // so we can run timer every x instructions
    int numInstructions;

    // keeps track of user or kernel mode
    bool isUserMode;

    // keeps track of interruptions
    bool isInterrupted;
    bool interruptsEnabled;

    int timerFlag;
    static const int userMemEnd = 1000;
    static const int systemMemEnd = 2000;

public:
    CPU(int);

    void readFileIntoMem(string);

    void isValidAddress(int);

    void run(int[2], int[2]);
    void runInstruction(int[2], int[2]);

    int readFromMemory(int,int[2], int[2]);       // takes PC as argument
    void writeToMemory(int, int, int[2], int[2]);

    void handleTimerInterrupt(int[2], int[2]);

    void pushToStack(int, int[2], int[2]);
    int popFromStack(int[2], int[2]);
};


#endif /*CPU_hpp*/
