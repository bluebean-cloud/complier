#ifndef _ERROR
#define _ERROR

typedef struct ErrItem {
    int line;
    char type;
} ErrItem;

void errInit();
void addError(int line, char type);
void printErr();

#endif
