#ifndef _COMPILER
#define _COMPILER

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <ctype.h>

const int numOfKey = 37;
const char keyWords[][10] = {
    "main", "const", "int", "char", "break", "continue", "if", "else", "for", "getint", "getchar", "printf", "return", "void",
    "!", "&&", "||", "+", "-", "*", "/", "%", "<", "<=", ">", ">=", "==", "!=", "=", ";", ",", "(", ")", "[", "]", "{", "}",
};
const char tokenString[][15] = {
    "MAINTK", "CONSTTK", "INTTK", "CHARTK", "BREAKTK", "CONTINUETK", "IFTK", "ELSETK", "FORTK", "GETINTTK", "GETCHARTK", "PRINTFTK", "RETURNTK", "VOIDTK",
    "NOT", "AND", "OR", "PLUS", "MINU", "MULT", "DIV", "MOD", "LSS", "LEQ", "GRE", "GEQ", "EQL", "NEQ", "ASSIGN", "SEMICN", "COMMA", "LPARENT", "RPARENT", "LBRACK", "RBRACK", "LBRACE", "RBRACE",
    "INTCON", "CHRCON", "STRCON", "IDENFR", "KEYWORD",
    "END",
};
const char inputFile[] = "testfile.txt";
const char outputFile[] = "ans.txt";

typedef enum TokenType {
    MAINTK, CONSTTK, INTTK, CHARTK, BREAKTK, CONTINUETK, IFTK, ELSETK, FORTK, GETINTTK, GETCHARTK, PRINTFTK, RETURNTK, VOIDTK,
    NOT, AND, OR, PLUS, MINU, MULT, DIV, MOD, LSS, LEQ, GRE, GEQ, EQL, NEQ, ASSIGN, SEMICN, COMMA, LPARENT, RPARENT, LBRACK, RBRACK, LBRACE, RBRACE,
    INTCON, CHRCON, STRCON, IDENFR, KEYWORD,
    END,
} TokenType;

typedef struct Token {
    char* content;
    int value;
    int line;
    TokenType type;
} Token;

// Lexer
void lexAnalyse();
void getInt(Token* t);
void getWord(Token* t);
void getOthers(Token* t);
void handleComments(int c);
int isKeyWord(char* word);
int judgeCharType(int c);
int escToValue(int c);
void addToken(Token* token);
void printTokens();

Token** tokenRoot;
int sizeOfTokens;
char curStr[1024];
FILE* input;
FILE* output;
int curLine;

#endif
