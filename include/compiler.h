#ifndef _COMPILER
#define _COMPILER

#include "node.h"
#include "token.h"
#include "error.h"
#include "pcode.h"
#include <ctype.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/*
    compiler 头文件，包含所有函数声明
*/
#define TYPE2NODE(TYPE) TYPE##Node
#define FREE(POINTER, CONTENT)                                                 \
    if (((POINTER*)node)->CONTENT) {                                           \
        freeNode((Node*)(((POINTER*)node)->CONTENT));                          \
        free(((POINTER*)node)->CONTENT);                                       \
        ((POINTER*)node)->CONTENT = NULL;                                      \
    }
#define FREE_V(POINTER, CONTENT, FLAG)                                         \
    if (((POINTER*)node)->CONTENT) {                                           \
        freeVector(((POINTER*)node)->CONTENT, FLAG);                           \
        free(((POINTER*)node)->CONTENT);                                       \
        ((POINTER*)node)->CONTENT = NULL;                                      \
    }
#define NODECASE(NODE_TYPE)                                                   \
    break;                                                                    \
    case NODE_TYPE:;                                                           \
        TYPE2NODE(NODE_TYPE)* node##NODE_TYPE = (TYPE2NODE(NODE_TYPE)*)node;

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
void freeVector(Vector* vector, int type);
void freeNode(Node* node);
void printNodeTree(Node* node);
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
BlockItemNode* parseBlockItem(Node* parent);
StmtNode* parseStmt(Node* parent);
ForStmtNode* parseForStmt(Node* parent);
ConstExpNode* parseConstExp(Node* parent);
ExpNode* parseExp(Node* parent);
AddExpNode* parseAddExp(Node* parent);
MulExpNode* parseMulExp(Node* parent);
UnaryExpNode* parseUnaryExp(Node* parent);
UnaryOpNode* parseUnaryOp(Node* parent);
FuncRParamsNode* parseFuncRParams(Node* parent);
PrimaryExpNode* parsePrimaryExp(Node* parent);
CondNode* parseCond(Node* parent);
LOrExpNode* parseLOrExp(Node* parent);
LAndExpNode* parseLAndExp(Node* parent);
EqExpNode* parseEqExp(Node* parent);
RelExpNode* parseRelExp(Node* parent);
IdentNode* parseIdent(Node* parent);
LValNode* parseLVal(Node* parent);
StringConstNode* parseStringConst(Node* parent);
NumberNode* parseNumber(Node* parent);
CharacterNode* parseCharacter(Node* parent);

// PCode
void pCodeRun();
void pCodeInit();
int execPCode();
void pCodeVisit(Node* node);
void pCodeTrans();
P_SCOPE* newPScope(P_SCOPE* parent);
P_VAR* findPVar(char* name);
int checkFuncName(char* name);

#endif
