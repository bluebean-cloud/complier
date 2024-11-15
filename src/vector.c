#include "../include/vector.h"
#include <stdlib.h>
#include <string.h>
/*
    实现动态数组
*/

Vector* createVector() {
    Vector* vector = (Vector*)malloc(sizeof(Vector));
    vector->limit = 8;
    vector->length = 0;
    vector->values = malloc(sizeof(void*) * vector->limit);
    return vector;
}

void pushVector(Vector* vector, void* item) {
    if (vector->length == vector->limit) {
        void* newMemory = malloc(sizeof(void**) * (vector->limit * 2));
        memcpy(newMemory, vector->values, sizeof(void**) * (vector->limit));
        free(vector->values);
        vector->values = newMemory;
        vector->limit *= 2;
    }
    vector->values[vector->length] = item;
    vector->length++;
}
