#include "../include/vector.h"
#include <stdlib.h>
#include <string.h>
/*
    实现动态数组
*/

Vector createVector() {
    Vector vector;
    vector.limit = 8;
    vector.length = 0;
    vector.values = malloc(sizeof(void *) * vector.limit);
    vector.push = push;
    return vector;
}

void push(Vector *vector, void *item) {
    if (vector->length == vector->limit) {
        void *newMemory = malloc(sizeof(void **) * (vector->limit * 2));
        memcpy(newMemory, vector->values, sizeof(void **) * (vector->limit));
        vector->limit *= 2;
    }
    // printf("length: %d\n", vector->length);
    vector->values[vector->length] = item;
    vector->length++;
}
