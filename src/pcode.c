#include "../include/compiler.h"

int PC;
int SP;
int MP;
int* baseStack;
Vector* instructions;

extern int errOccur;
extern int RUNNER;
void pCodeRun() {
    pCodeVisit();
    if (errOccur || RUNNER != 1) {
        return;
    }

    baseStack = (int*)malloc(sizeof(int) * STACK_SIZE);
    while (execPCode())
        ;
    free(baseStack);
}

// 返回值为 0 代表程序结束
int execPCode() {

    return 0;
}

void pCodeVisit() {
}

void pCodeTrans() {

}
