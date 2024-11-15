#include "../include/compiler.h"

// parser
CompUnitNode* nodesRoot;

// 开始进行语法分析
void analyzeSyntax() {
    nodesRoot = parseCompUnit();
}

Node newNode(NodeType NodeType, Node* parent, Token* token) {
    Node node;
    node.nodeType = NodeType;
    node.parent = parent;
    node.token = token;
    return node;
}

CompUnitNode* parseCompUnit() {
    CompUnitNode* node = (CompUnitNode*)malloc(sizeof(CompUnitNode));
    node->node = newNode(CompUnit, NULL, NULL);
    node->decls = createVector();
    node->funcDefs = createVector();
    while (hasNextToken()) {
        if (peekToken(1)->type == MAINTK) {
            // mainFuncDef
            node->mainFuncDef = parseMainFuncDef((Node*)node);
        } else if (peekToken(2)->type == LPARENT) {
            // funcDef
            pushVector(node->funcDefs, parseFuncDef((Node*)node));
        } else {
            // decl
            pushVector(node->decls, parseDecl((Node*)node));
        }
    }
    return node;
}

DeclNode* parseDecl(Node* parent) {
    DeclNode* node = (DeclNode*)malloc(sizeof(DeclNode));
    node->node = newNode(Decl, parent, NULL);

    return node;
}

MainFuncDefNode* parseMainFuncDef(Node* parent) {
    MainFuncDefNode* node = (MainFuncDefNode*)malloc(sizeof(MainFuncDefNode));
    node->node = newNode(MainFuncDef, parent, NULL);
    nextToken(); // int
    nextToken(); // main
    nextToken(); // (
    nextToken(); // )
    node->block = parseBlock((Node*)node);
    return node;
}

FuncDefNode* parseFuncDef(Node* parent) {
    FuncDefNode* node = (FuncDefNode*)malloc(sizeof(FuncDefNode));
    node->node = newNode(FuncDef, parent, NULL);
    node->typeFuncType = peekToken(0)->type == CHARTK ? CHAR :
                         peekToken(0)->type == INTTK  ? INT :
                                                        VOID;
    nextToken(); // int | char | void
    node->funcName = parseIdent(node);
    nextToken(); // (
    node->funcFParams = parseFuncFParams(node);
    nextToken(); // )
    node->block = parseBlock(node);
    return node;
}

IdentNode* parseIdent(Node* parent) {
    IdentNode* node = (IdentNode*)malloc(sizeof(IdentNode));
    Token* curToken = nextToken();
    node->node = newNode(Ident, parent, curToken);
    node->value = curToken->content;
    return node;
}

FuncFParamsNode* parseFuncFParams(Node* parent) {
    if (peekToken(0)->type == RPARENT) {
        return NULL;
    }
    FuncFParamsNode* node = (FuncFParamsNode*)malloc(sizeof(FuncFParamsNode));
    node->node = newNode(FuncFParams, parent, NULL);
    node->funcFParams = createVector();
    pushVector(node->funcFParams, parseFuncFParam(node));
    while (peekToken(0)->type == COMMA) {
        nextToken(); // ,
        pushVector(node->funcFParams, parseFuncFParam(node));
    }
    return node;
}

FuncFParamNode* parseFuncFParam(Node* parent) {
    FuncFParamNode* node = (FuncFParamNode*)malloc(sizeof(FuncFParamNode));
    node->node = newNode(FuncFParam, parent, NULL);
    node->typeBType = nextToken()->type == CHARTK ? CHAR : INT;
    node->ident = parseIdent(node);
    node->name = node->ident->value;
    if (peekToken(0)->type == LBRACK) {
        node->isArray = 1;
        nextToken(); // [
        nextToken(); // ]
    } else {
        node->isArray = 0;
    }
    return node;
}

BlockNode* parseBlock(Node* parent) {
}
