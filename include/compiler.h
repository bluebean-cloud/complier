#ifndef _COMPILER
#define _COMPILER

#include "node.h"
#include <ctype.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

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


#endif
