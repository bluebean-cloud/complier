#ifndef _COMPILER
#define _COMPILER

#include "node.h"
#include "token.h"
#include <ctype.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/*
    compiler 头文件，包含所有函数声明
*/

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
Token *nextToken();
int hasNextToken();

// Parser
void analyzeSyntax();
Node newNode(NodeType NodeType, Node* parent, Token* token);
CompUnitNode *parseCompUnit();
MainFuncDefNode *parseMainFuncDef(Node* node);
FuncDefNode *parseFuncDef(Node* node);


#endif
