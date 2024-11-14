#ifndef _COMPILER
#define _COMPILER

#include "node.h"
#include <ctype.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

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
const char inputFile[] = "testfile.txt";
const char outputFile[] = "ans.txt";

// Lexer
void lexAnalyse();
void getInt(Token *t);
void getWord(Token *t);
void getOthers(Token *t);
void handleComments(int c);
int isKeyWord(char *word);
int judgeCharType(int c);
int escToValue(int c);
void addToken(Token *token);
void printTokens();
Token *peekToken(int step);
char *peekTokenValue(int step);
Token *nextToken();

Token *tokenRoot;
Token *curToken;
char curStr[1024];
FILE *input;
FILE *output;
int curLine;

// parser
Node *nodesRoot;

#endif
