#include "./compiler.h"

int main() {
    tokenRoot = (Token**)malloc(sizeof(Token*) * 100000);

    lexAnalyse(tokenRoot);
    printTokens();
    return 0;
}

void lexAnalyse() {
    input = fopen(inputFile, "rb");
    int c;
    curLine = 1;
    while ((c = fgetc(input)) != EOF) {
        if (c == '\n') curLine++;
        if (isspace(c)) continue;
        Token* newToken = (Token*)malloc(sizeof(Token));
        curStr[0] = c;
        switch (judgeCharType(c)) {
        case 1: // digit
            getInt(newToken);
            break;
        case 2: // alpha
            getWord(newToken);
            break;
        default: // others
            getOthers(newToken);
            break;
        }
    }
    fclose(input);
}

void addToken(Token* token) {
    tokenRoot[sizeOfTokens++] = token;
}

int isKeyWord(char* word) {
    for (int i = 0; i < numOfKey; i++) {
        if (strcmp(word, keyWords[i]) == 0) {
            return i;
        }
    }
    return -1;
}

int judgeCharType(int c) {
    if (isdigit(c)) {
        return 1;
    }
    if (c == '_' || isalpha(c)) {
        return 2;
    }
    return 0;
}

void getInt(Token* t) {
    int number = curStr[0] - '0';
    int c;
    while ((c = fgetc(input)) != EOF) {
        if (!isdigit(c)) {
            break;
        }
        number = number * 10 + (c - '0');
    }
    t->line = curLine;
    t->type = INTCON;
    t->value = number;
    t->content = (char*)malloc(sizeof(char) * 10);
    sprintf(t->content, "%d", number);
    addToken(t);
    
    fseek(input, -1, SEEK_CUR);
}

void getWord(Token* t) {
    int c;
    int len = 1;
    while ((c = fgetc(input)) != EOF) {
        if (!(c == '_' || isalnum(c))) {
            break;
        }
        curStr[len++] = c;
    }
    curStr[len] = '\0';
    t->line = curLine;
    t->content = (char*)malloc(sizeof(char) * (len + 1));
    strcpy(t->content, curStr);
    c = isKeyWord(curStr);
    t->type = c == -1 ? IDENFR : c;
    addToken(t);
    
    fseek(input, -1, SEEK_CUR);
}

void getOthers(Token* t) {
    int c = curStr[0];
    int len = 1;
    if (c == '/') {    // is comment?
        c = fgetc(input);
        if (c == '/' || c == '*') {
            handleComments(c);
            free(t);
            return;
        } else {
            fseek(input, -1, SEEK_CUR);
        }
    }
    if (c == '\'') { // CHRCON
        curStr[len++] = fgetc(input);
        if (curStr[1] == '\\') {
            curStr[len++] = fgetc(input);
        }
        curStr[len++] = fgetc(input);   // must be '\''
        curStr[len] = '\0';
        t->line = curLine;
        t->type = CHRCON;
        t->content = (char*)malloc(sizeof(char) * (len + 1));
        strcpy(t->content, curStr);
        if (len == 3) {
            t->value = curStr[1];
        } else {
            t->value = escToValue(curStr[2]);
        }
        addToken(t);
        return;
    } else if (c == '"') {  // STRCON
        while ((c = fgetc(input)) != EOF) {
            curStr[len++] = c;
            if (c == '"') break;
        }
        curStr[len] = '\0';
        t->line = curLine;
        t->type = STRCON;
        t->content = (char*)malloc(sizeof(char) * (len + 1));
        strcpy(t->content, curStr);
        t->value = 0;
        addToken(t);
        return;
    }
    if (strchr("&|", c) != NULL) {
        curStr[len++] = fgetc(input);
        curStr[len] = '\0';
        t->line = curLine;
        t->content = (char*)malloc(sizeof(char) * (len + 1));
        strcpy(t->content, curStr);
        t->type = curStr[0] == '&' ? AND : OR;
        addToken(t);
        return;
    }
    if (strchr("!=<>", c) != NULL) {
        c = fgetc(input);
        if (c == '=') curStr[len++] = c;
        else fseek(input, -1, SEEK_CUR);
        curStr[len] = '\0';
        t->line = curLine;
        t->content = (char*)malloc(sizeof(char) * (len + 1));
        strcpy(t->content, curStr);
        t->type = isKeyWord(curStr);
        addToken(t);
        return;
    }
    curStr[len] = '\0';
    t->line = curLine;
    t->content = (char*)malloc(sizeof(char) * (len + 1));
    strcpy(t->content, curStr);
    t->type = isKeyWord(curStr);
    addToken(t);
}

int escToValue(int c) {
    switch (c) {
    case 'a':
        return 7;
    case 'b':
        return 8;
    case 't':
        return 9;
    case 'n':
        return 10;
    case 'v':
        return 11;
    case 'f':
        return 12;
    case '"':
        return 34;
    case '\'':
        return 39;
    case '\\':
        return 92;
    case '0':
        return 0;
    default:
        return -1;
    }
}

void handleComments(int c) {
    if (c == '/') {
        while ((c = fgetc(input)) != EOF) {
            if (c == '\n') {
                curLine++;
                return;
            }
        }
    } else {
        int pre = 0;
        while ((c = fgetc(input)) != EOF) {
            if (c == '\n') curLine++;
            if (pre == '*' && c == '/') {
                return;
            }
            pre = c;
        }
    }
}

void printTokens() {
    output = fopen(outputFile, "w");
    for (int i = 0; i < sizeOfTokens; i++) {
        fprintf(output, "%s %s\n", tokenString[tokenRoot[i]->type], tokenRoot[i]->content);
    }
    fclose(output);
}
