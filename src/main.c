#include "../include/compiler.h"

FILE* input;
FILE* output;
FILE* errOut;
const char inputFile[] = "testfile.txt";
const char outputFile[] = "parser.txt";
const char errFile[] = "error.txt";
extern int errOccur;
extern CompUnitNode* nodesRoot;
COMPILER_TYPE RUNNER_TYPE = PARSER;

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
    switch (RUNNER_TYPE) {
    case LEXER:
        printTokens();
        break;
    case PARSER:
        printNodeTree((Node*)nodesRoot);
        break;
    case PCODE:
    case LLVM:
    case MIPS:
        break;
    }

end:
    fclose(input);
    fclose(output);
    fclose(errOut);
    return 0;
}
