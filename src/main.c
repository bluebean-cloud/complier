#include "../include/compiler.h"

FILE* input;
FILE* output;
const char inputFile[] = "testfile.txt";
const char outputFile[] = "parser.txt";

// just go go
int main() {
    input = fopen(inputFile, "rb");
    output = fopen(outputFile, "w");

    lexAnalyse();
    analyzeSyntax();

    fclose(input);
    fclose(output);
    return 0;
}
