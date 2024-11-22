#ifndef _VECTOR
#define VECTOR

/**
 * 动态数组 
 * */

typedef struct Vector Vector;
struct Vector {
    int limit;
    int length;
    void** values; // 可理解为一个指针数组
};

Vector* createVector();
void pushVector(Vector* vector, void* item);

/**
 * Trie
 */
#define TRIECODE(code) (code - 32)

typedef struct Trie Trie;
struct Trie {
    Trie* map[TRIECODE(128)];
    void* data;
};

Trie* createTrie();
void* getTrieData(Trie* root, char* str);
void insertTrie(Trie* root, char* str, void* data);

#endif
