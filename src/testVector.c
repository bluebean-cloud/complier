#include <stdio.h>
#include <stdlib.h>
#include "vector.h"
typedef struct Test {
    int id;
} Test;

int main() {
    Vector vector = createVector();
    for (int i = 0; i < 10; i++) {
        int id;
        scanf("%d", &id);
        Test* test = (Test*)malloc(sizeof(Test));
        printf("read: %d\n", id);
        test->id = id;
        vector.push(&vector, test);
    }
    printf("length: %d limit: %d\n", vector.length, vector.limit);
    for (int i = 0; i < vector.length; i++) {
        printf("%d\n", ((Test*)vector.values[i])->id);
    }
    return 0;
}
