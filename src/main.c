#include "../include/compiler.h"

FILE* input;
FILE* output;
FILE* errOut;
const char inputFile[] = "testfile.txt";
const char outputFile[] = "parser.txt";
const char errFile[] = "error.txt";
extern int errOccur;
int RUNNER = 1; // 1->PCode 2->LLVM 3->MIPS

// just go go
int main() {
    input = fopen(inputFile, "rb");
    output = fopen(outputFile, "w");
    errOut = fopen(errFile, "w");
    lexAnalyse();
    analyzeSyntax();
    pCodeRun();
    if (errOccur) {
        printErr();
        goto end;
    }

end:
    fclose(input);
    fclose(output);
    fclose(errOut);
    return 0;
}
