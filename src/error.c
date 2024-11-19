#include "..\include\compiler.h"

int errOccur = 0;
Vector* errList = NULL;
extern FILE* errOut;
extern FILE* output;

int _cmp(const void* pa, const void* pb) {
    ErrItem* a = (ErrItem*)pa;
    ErrItem* b = (ErrItem*)pb;
    return a->line - b->line;
}

void errInit() {
    errList = createVector();
}

void addError(int line, char type) {
    if (errList == NULL) {
        errInit();
    }
    errOccur = 1;
    ErrItem* item = (ErrItem*)malloc(sizeof(ErrItem));
    item->line = line;
    item->type = type;
    pushVector(errList, item);
}

void printErr() {
    qsort(errList->values, errList->length, sizeof(ErrItem), _cmp);
    for (int i = 0; i < errList->length; i++) {
        fprintf(errOut, "%d %c\n", ((ErrItem*)errList->values[i])->line,
                ((ErrItem*)errList->values[i])->type);
        fprintf(output, "%d %c\n", ((ErrItem*)errList->values[i])->line,
                ((ErrItem*)errList->values[i])->type);
    }
}
