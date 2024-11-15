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
void getInt(Token* t);
void getWord(Token* t);
void getOthers(Token* t);
void handleComments(int c);
int isKeyWord(char* word);
int judgeCharType(int c);
int escToValue(int c);
void addToken(Token* token);
void printTokens();
Token* peekToken(int step);
Token* nextToken();
int hasNextToken();

// Parser
void analyzeSyntax();
Node newNode(NodeType NodeType, Node* parent, Token* token);
CompUnitNode* parseCompUnit();
DeclNode* parseDecl(Node* parent);
ConstDeclNode* parseConstDecl(Node* parent);
ConstDefNode* parseConstDef(Node* parent);
ConstInitValNode* parseConstInitVal(Node* parent);
VarDeclNode* parseVarDecl(Node* parent);
VarDefNode* parseVarDef(Node* parent);
InitValNode* parseInitVal(Node* parent);
MainFuncDefNode* parseMainFuncDef(Node* parent);
FuncDefNode* parseFuncDef(Node* parent);
FuncFParamsNode* parseFuncFParams(Node* parent);
FuncFParamNode* parseFuncFParam(Node* parent);
BlockNode* parseBlock(Node* parent);
ConstExpNode* parseConstExp(Node* parent);
ExpNode* parseExp(Node* parent);
IdentNode* parseIdent(Node* parent);
StringConstNode* parseStringConst(Node* parent);

#endif
