#include "../include/tool.h"
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

// 将数据推入数组。动态增长。void* 是史上最大骗局xd
void pushVector(Vector* vector, void* item) {
    if (vector->length == vector->limit) {
        void** newMemory = (void**)malloc(sizeof(void*) * (vector->limit * 2));
        memcpy(newMemory, vector->values, sizeof(void*) * (vector->limit));
        free(vector->values);
        vector->values = newMemory;
        vector->limit *= 2;
    }
    vector->values[vector->length] = item;
    vector->length++;
}

Trie* createTrie() {
    Trie* node = (Trie*)malloc(sizeof(Trie));
    node->data = NULL;
    memset(node->map, 0, sizeof(Trie*) * TRIECODE(128));
    return node;
}

void* getTrieData(Trie* trie, char* str, int cnt) {
    if (cnt == strlen(str)) {
        return trie->data;
    }
    if (trie->map[str[cnt]] == NULL) {
        return NULL;
    }
    return getTrieData(trie->map[TRIECODE(str[cnt])], str, cnt + 1);
}

void insertTrie(Trie* root, char* str, void* data) {
    int len = strlen(str);
    Trie* tmp = root;
    for (int i = 0; i < len; i++) {
        if (tmp->map[TRIECODE(str[i])] == NULL) {
            tmp->map[TRIECODE(str[i])] = createTrie();
        }
        tmp = tmp->map[TRIECODE(str[i])];
    }
    tmp->data = data;
}
