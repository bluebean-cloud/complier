#ifndef _VECTOR
#define VECTOR

/*
    动态数组
*/

typedef struct Vector Vector;
struct Vector {
    int limit;
    int length;
    void** values; // 可理解为一个指针数组
};

Vector* createVector();
void pushVector(Vector* vector, void* item);

#endif
