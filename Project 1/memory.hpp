//
//  memory.hpp
//  OSProject1
//
//  Created by Nisarg Desai on 9/25/19.
//  Copyright © 2019 Nisarg Desai. All rights reserved.
//

// Memory
// It will consist of 2000 integer entries, 0-999 for the user program, 1000-1999 for system code.
// It will support two operations:
// read(address) - returns the value at the address
// write(address, data) - writes the data to the address
// Memory will initialize itself by reading a program file.
// Note that the memory is simply storage; it has no real logic beyond reading and writing.


#ifndef memory_hpp
#define memory_hpp

#include <string>
#include <unistd.h>
#include <stdio.h>
#include <iostream>

using namespace std;

class Memory {
    
private:
    int memory[2000];
    
public:
    
    void run(int[], int[]);
    
    bool readFile(const string&);
    
    // bool writeMem(int *, int);
    bool writeMem(int, int);
    int readMem(int);
};


#endif /* memory_hpp */


