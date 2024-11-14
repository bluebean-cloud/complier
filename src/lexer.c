#include "../include/compiler.h"

/*
    词法分析的实现部分
*/

const int numOfKey = 37;
const char keyWords[][10] = {
    "main", "const",  "int",     "char",   "break",  "continue", "if", "else",
    "for",  "getint", "getchar", "printf", "return", "void",     "!",  "&&",
    "||",   "+",      "-",       "*",      "/",      "%",        "<",  "<=",
    ">",    ">=",     "==",      "!=",     "=",      ";",        ",",  "(",
    ")",    "[",      "]",       "{",      "}",
};
const char tokenString[][15] = {
    "MAINTK",   "CONSTTK", "INTTK",   "CHARTK",   "BREAKTK",   "CONTINUETK",
    "IFTK",     "ELSETK",  "FORTK",   "GETINTTK", "GETCHARTK", "PRINTFTK",
    "RETURNTK", "VOIDTK",  "NOT",     "AND",      "OR",        "PLUS",
    "MINU",     "MULT",    "DIV",     "MOD",      "LSS",       "LEQ",
    "GRE",      "GEQ",     "EQL",     "NEQ",      "ASSIGN",    "SEMICN",
    "COMMA",    "LPARENT", "RPARENT", "LBRACK",   "RBRACK",    "LBRACE",
    "RBRACE",   "INTCON",  "CHRCON",  "STRCON",   "IDENFR",    "KEYWORD",
    "END",
};

Token *tokenRoot;
Token *curToken;
char curStr[1024];
FILE *input;
FILE *output;
int curLine;
const char inputFile[] = "testfile.txt";
const char outputFile[] = "ans.txt";

// peek Token relative to curToken
Token *peekToken(int step) {
    Token *tmp = curToken;
    if (step < 0) {
        return NULL;
    }
    while (step-- && tmp) {
        tmp = tmp->next;
    }
    return tmp;
}

char *peekTokenValue(int step) { return peekToken(step)->content; }

// get curToken and step next
Token *nextToken() {
    Token *tmp = curToken;
    if (curToken->next) {
        curToken = curToken->next;
    }
    return tmp;
}

// do lex
void lexAnalyse() {
    tokenRoot = curToken = NULL;
    input = fopen(inputFile, "rb");
    int c;
    curLine = 1;
    while ((c = fgetc(input)) != EOF) {
        if (c == '\n')
            curLine++;
        if (isspace(c))
            continue;
        Token *newToken = (Token *)malloc(sizeof(Token));
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
    curToken = tokenRoot;
    fclose(input);
}

// add token to tail of tokens
void addToken(Token *token) {
    if (tokenRoot == NULL) {
        token->next = token->pre = NULL;
        tokenRoot = curToken = token;
        return;
    }
    curToken->next = token;
    token->pre = curToken;
    token->next = NULL;
    curToken = token;
}

// judge is keyWord or not and return enum value of word
int isKeyWord(char *word) {
    for (int i = 0; i < numOfKey; i++) {
        if (strcmp(word, keyWords[i]) == 0) {
            return i;
        }
    }
    return -1;
}

// 0: others 1: digit 2: ident
int judgeCharType(int c) {
    if (isdigit(c)) {
        return 1;
    }
    if (c == '_' || isalpha(c)) {
        return 2;
    }
    return 0;
}

// get next int
void getInt(Token *t) {
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
    t->content = (char *)malloc(sizeof(char) * 10);
    sprintf(t->content, "%d", number);
    addToken(t);

    fseek(input, -1, SEEK_CUR);
}

// get next word
void getWord(Token *t) {
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
    t->content = (char *)malloc(sizeof(char) * (len + 1));
    strcpy(t->content, curStr);
    c = isKeyWord(curStr);
    t->type = c == -1 ? IDENFR : c;
    addToken(t);

    fseek(input, -1, SEEK_CUR);
}

// get operators or process comments
void getOthers(Token *t) {
    int c = curStr[0];
    int len = 1;
    if (c == '/') { // is comment?
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
        curStr[len++] = fgetc(input); // must be '\''
        curStr[len] = '\0';
        t->line = curLine;
        t->type = CHRCON;
        t->content = (char *)malloc(sizeof(char) * (len + 1));
        strcpy(t->content, curStr);
        if (len == 3) {
            t->value = curStr[1];
        } else {
            t->value = escToValue(curStr[2]);
        }
        addToken(t);
        return;
    } else if (c == '"') { // STRCON
        while ((c = fgetc(input)) != EOF) {
            curStr[len++] = c;
            if (c == '"')
                break;
        }
        curStr[len] = '\0';
        t->line = curLine;
        t->type = STRCON;
        t->content = (char *)malloc(sizeof(char) * (len + 1));
        strcpy(t->content, curStr);
        t->value = 0;
        addToken(t);
        return;
    }
    if (strchr("&|", c) != NULL) {
        curStr[len++] = fgetc(input);
        curStr[len] = '\0';
        t->line = curLine;
        t->content = (char *)malloc(sizeof(char) * (len + 1));
        strcpy(t->content, curStr);
        t->type = curStr[0] == '&' ? AND : OR;
        addToken(t);
        return;
    }
    if (strchr("!=<>", c) != NULL) {
        c = fgetc(input);
        if (c == '=')
            curStr[len++] = c;
        else
            fseek(input, -1, SEEK_CUR);
        curStr[len] = '\0';
        t->line = curLine;
        t->content = (char *)malloc(sizeof(char) * (len + 1));
        strcpy(t->content, curStr);
        t->type = isKeyWord(curStr);
        addToken(t);
        return;
    }
    curStr[len] = '\0';
    t->line = curLine;
    t->content = (char *)malloc(sizeof(char) * (len + 1));
    strcpy(t->content, curStr);
    t->type = isKeyWord(curStr);
    addToken(t);
}

// 将转义字符转换为值
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

// handle comments
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
            if (c == '\n')
                curLine++;
            if (pre == '*' && c == '/') {
                return;
            }
            pre = c;
        }
    }
}

// print token linked list
void printTokens() {
    output = fopen(outputFile, "w");
    Token *tmp = tokenRoot;
    while (tmp != NULL) {
        fprintf(output, "%s %s\n", tokenString[tmp->type], tmp->content);
        tmp = tmp->next;
    }
    fclose(output);
}
