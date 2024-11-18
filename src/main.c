#include "../include/compiler.h"

FILE* input;
FILE* output;
const char inputFile[] = "testfile.txt";
const char outputFile[] = "ans.txt";

// just go go
int main() {
    input = fopen(inputFile, "rb");
    output = fopen(outputFile, "w");
    
    lexAnalyse();
    analyzeSyntax();
    printf("end\n");

    fclose(input);
    fclose(output);
    return 0;
}
