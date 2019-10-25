//
//  CPU.cpp
//  OSProject1
//
//  Created by Nisarg Desai on 9/25/19.
//  Copyright © 2019 Nisarg Desai. All rights reserved.
//


#include "cpu.hpp"

CPU::CPU(int timer) {

    PC = 0;
    AC = 0;
    IR = 0;
    X = 0;
    Y = 0;
    isUserMode = true;
    isInterrupted = false;
    interruptsEnabled = true;
    numInstructions = 0;
    timerFlag = timer;

    if (isUserMode) {
        SP = userMemEnd;
    } else {
        SP = systemMemEnd;
    }

}

void CPU::run(int cpuToMemPipe[2], int memToCpuPipe[2]) {

    pid_t pid = getpid();
    cout << "pid from cpu process: " << pid << endl;

    while (true) {
        // cout << "interruptsEnabled = " << interruptsEnabled << "  numInstructions = " << numInstructions << endl;

        // Timer interrupt
        if (interruptsEnabled && numInstructions > 0 && numInstructions % timerFlag == 0) {

            // cout << "interrupting process...\nnumInstructions = " << numInstructions << endl;
            this->handleTimerInterrupt(cpuToMemPipe, memToCpuPipe);
        }

        // cout << "PC: " << PC << endl;
        IR = this->readFromMemory(PC, cpuToMemPipe, memToCpuPipe);
        // cout << "PC = " << PC << "  instruction = " << IR << "  AC = " << AC << "  numInstructions = " << numInstructions << "  userMode?: " << isUserMode << endl;
    //    cout << "instruction = " << instruction << "  PC = " << PC << endl;
        if (IR != 0) {
            this->runInstruction(cpuToMemPipe, memToCpuPipe);
        }
        // cout << "value/instr: " << value << endl;
        // usleep(100000);
    }

}

void CPU::runInstruction(int cpuToMemPipe[2], int memToCpuPipe[2]) {

    int temp;
    int value;
    int addr;
    int a;
    int b;
//    int c;
//    int port;

    switch (IR) {
        case 1:
            PC++;
            temp = this->readFromMemory(PC, cpuToMemPipe, memToCpuPipe);
            AC = temp;
//            cout << " AC: " << AC << endl;
            PC++;
            numInstructions++;
            break;
        case 2:
            PC++;
            // read next value (should be address) from file
            temp = this->readFromMemory(PC, cpuToMemPipe, memToCpuPipe);
            // cout << "addr: " << addr << endl;
            // get value at address from memory
            // set AC to that value
            AC = this->readFromMemory(temp, cpuToMemPipe, memToCpuPipe);
            //            cout << " AC: " << AC << endl;
            PC++;
            numInstructions++;
            break;
        case 3:
            // TODO: make this use only one "register" like temp
            PC++;
            a = this->readFromMemory(PC, cpuToMemPipe, memToCpuPipe);
            b = this->readFromMemory(a, cpuToMemPipe, memToCpuPipe);
            AC = this->readFromMemory(b, cpuToMemPipe, memToCpuPipe);
            PC++;
            numInstructions++;
            break;
        case 4:
            PC++;
            // read next value (should be an address) from file
            temp = this->readFromMemory(PC, cpuToMemPipe, memToCpuPipe);
            temp = temp + X;
            AC = this->readFromMemory(temp, cpuToMemPipe, memToCpuPipe);
//            cout << " AC: " << AC << endl;
            PC++;
            numInstructions++;
            break;
        case 5:
            PC++;
            temp = this->readFromMemory(PC, cpuToMemPipe, memToCpuPipe);
            temp = temp + Y;
            AC = this->readFromMemory(temp, cpuToMemPipe, memToCpuPipe);
            PC++;
            numInstructions++;
            break;
        case 6:
            AC = this->readFromMemory(SP + X, cpuToMemPipe, memToCpuPipe);
            PC++;
            numInstructions++;
            break;
        case 7:
            PC++;
            temp = this->readFromMemory(PC, cpuToMemPipe, memToCpuPipe);
            this->writeToMemory(temp, AC, cpuToMemPipe, memToCpuPipe);
            PC++;
            numInstructions++;
            break;
        case 8:
            // generate random integer and store in AC
            PC++;
            break;
        case 9:
            PC++;
            temp = this->readFromMemory(PC, cpuToMemPipe, memToCpuPipe);
            if (temp == 1) {
                // print as int
//                cout << "as int: " << AC << endl;
                cout << AC;
            } else if (temp == 2) {
                // print as char
                // cout << "as char: " << (char)AC << endl;
                cout << (char)AC;
            } else {
                // bs
                cout << "Error: Invalid port" << endl;
                cout << "Port = " << temp << endl;
                exit(10);
            }
            PC++;
            numInstructions++;
            break;
        case 10:
            AC += X;
            PC++;
            numInstructions++;
            break;
        case 11:
            AC += Y;
            PC++;
            numInstructions++;
            break;
        case 12:
            AC -= X;
            PC++;
            numInstructions++;
            break;
        case 13:
            AC -= Y;
            PC++;
            numInstructions++;
            break;
        case 14:
            X = AC;
            PC++;
            numInstructions++;
            break;
        case 15:
            AC = X;
            PC++;
            numInstructions++;
            break;
        case 16:
//            cout << "REACHED CASE 16" << endl;
            Y = AC;
            PC++;
            numInstructions++;
            break;
        case 17:
            AC = Y;
            PC++;
            numInstructions++;
            break;
        case 18:
            SP = AC;
            PC++;
            numInstructions++;
            break;
        case 19:
            AC = SP;
            PC++;
            numInstructions++;
            break;
        case 20:
            PC++;
            PC = this->readFromMemory(PC, cpuToMemPipe, memToCpuPipe);
            // PC = temp;
            numInstructions++;
            break;
        case 21:
            PC++;
            temp = this->readFromMemory(PC, cpuToMemPipe, memToCpuPipe);
            if (AC == 0) {
                PC = temp;
                break;
            }
            PC++;
            numInstructions++;
            break;
        case 22:
            PC++;
            temp = this->readFromMemory(PC, cpuToMemPipe, memToCpuPipe);
            if (AC != 0) {
                PC = temp;
                break;
            }
            PC++;
            numInstructions++;
            break;
        case 23:
            // Push return address onto stack, jump to the address
            PC++;
            temp = this->readFromMemory(PC, cpuToMemPipe, memToCpuPipe);
            this->pushToStack(PC+1, cpuToMemPipe, memToCpuPipe);
            PC = temp;
            numInstructions++;
            break;
        case 24:
            // Pop return address from the stack, jump to the address
            PC = this->popFromStack(cpuToMemPipe, memToCpuPipe);
            numInstructions++;
            break;
        case 25:
            X++;
            PC++;
            numInstructions++;
            break;
        case 26:
            X--;
            PC++;
            numInstructions++;
            break;
        case 27:
            this->pushToStack(AC, cpuToMemPipe, memToCpuPipe);
            PC++;
            numInstructions++;
            break;
        case 28:
            AC = this->popFromStack(cpuToMemPipe, memToCpuPipe);
            // cout << "At case 28" << endl;
            PC++;
            numInstructions++;
            break;
        case 29:
//            cout << "At case 29" << endl;
            // perform system call
            isInterrupted = true;
            interruptsEnabled = false;
            isUserMode = false;

            temp = SP;
            SP = systemMemEnd;
            this->pushToStack(temp, cpuToMemPipe, memToCpuPipe);

            temp = PC + 1;
            PC = 1500;
            this->pushToStack(temp, cpuToMemPipe, memToCpuPipe);
            numInstructions++;
            break;
        case 30:
            // cout << "At case 30" << endl;

            PC = this->popFromStack(cpuToMemPipe, memToCpuPipe);
            Y = this->popFromStack(cpuToMemPipe, memToCpuPipe);
            X = this->popFromStack(cpuToMemPipe, memToCpuPipe);
            AC = this->popFromStack(cpuToMemPipe, memToCpuPipe);
            SP = this->popFromStack(cpuToMemPipe, memToCpuPipe);

            isUserMode = true;
            isInterrupted = false;
            interruptsEnabled = true;
            numInstructions++;

            break;
        case 50:
            cout << "Program Done. Exiting..." << endl;
            exit(200);
        default:
            break;
    }

}

void CPU::isValidAddress(int addr) {
    if (isUserMode && addr >= 1000) {
        cout << "Memory Violation Error: Process is trying to access system data at 0d" << addr << " while in user mode. Stopping process..." << endl;
        exit(100);
    }
}

int CPU::readFromMemory(int addr, int cpuToMemPipe[2], int memToCpuPipe[2]) {

    this->isValidAddress(addr);

    int result = -1;
    // cout << "in cpu->readFromMemory" << endl;

    int readInstr = 100;
    write(cpuToMemPipe[1], &readInstr, sizeof(int));
    write(cpuToMemPipe[1], &addr, sizeof(int));
    // close(cpuToMemPipe[1]);

    // cout << "wrote readInstr and address to read to cpuToMemPipe from cpu->readFromMemory" << endl;

    read(memToCpuPipe[0], &result, sizeof(int));

    // cout << "result: " << result << endl;
    return result;
}

void CPU::writeToMemory(int addr, int value, int cpuToMemPipe[2], int memToCpuPipe[2]) {
    int writeInstr = 200;
    // cout << "in cpu->writeToMemory" << endl;
    write(cpuToMemPipe[1], &writeInstr, sizeof(int));
    write(cpuToMemPipe[1], &addr, sizeof(int));
    write(cpuToMemPipe[1], &value, sizeof(int));
    // close(cpuToMemPipe[1]);

    // cout << "wrote writeInstr, addr, and value to write to cpuToMemPipe form cpu->writeToMemory" << endl;

    bool result;
    read(memToCpuPipe[0], &result, sizeof(bool));
    // cout << "result after write: " << result << endl;
}

void CPU::handleTimerInterrupt(int cpuToMemPipe[2], int memToCpuPipe[2]) {
    // Disable interrupts and set interrupted flag to true

    // cout << "AC = " << AC << " X = " << X << " Y = " << Y << " isUserMode = " << isUserMode << endl;

    interruptsEnabled = false;
    isInterrupted = true;
    isUserMode = false;

    int temp = SP;
    // set stack pointer to new value
    SP = systemMemEnd;
    this->pushToStack(temp, cpuToMemPipe, memToCpuPipe);

    // save AC, X, and Y in system stack as well
    this->pushToStack(AC, cpuToMemPipe, memToCpuPipe);
    this->pushToStack(X, cpuToMemPipe, memToCpuPipe);
    this->pushToStack(Y, cpuToMemPipe, memToCpuPipe);

    // save current PC and set PC to 1000 to run timer interrupt code
    temp = PC;
    PC = 1000;
    this->pushToStack(temp, cpuToMemPipe, memToCpuPipe);
}

void CPU::pushToStack(int value, int cpuToMemPipe[2], int memToCpuPipe[2]) {
    SP--;
    this->writeToMemory(SP, value, cpuToMemPipe, memToCpuPipe);
}

int CPU::popFromStack(int cpuToMemPipe[2], int memToCpuPipe[2]) {

    // get value from stack
    int value = this->readFromMemory(SP, cpuToMemPipe, memToCpuPipe);

    // set it to zero and increment SP
    this->writeToMemory(SP, 0, cpuToMemPipe, memToCpuPipe);
    SP++;

    return value;
}



