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
    node->mainFuncDef = NULL;
    while (hasNextToken()) {
        if (peekToken(1)->type == MAINTK) {
            // mainFuncDef
            node->mainFuncDef = parseMainFuncDef(node);
        } else if (peekToken(2)->type == LPARENT) {
            // funcDef
            pushVector(node->funcDefs, parseFuncDef(node));
        } else {
            // decl
            pushVector(node->decls, parseDecl(node));
        }
    }
    return node;
}

DeclNode* parseDecl(Node* parent) {
    DeclNode* node = (DeclNode*)malloc(sizeof(DeclNode));
    node->node = newNode(Decl, parent, NULL);
    node->constDecl = NULL;
    node->VarDecl = NULL;
    if (peekToken(0)->type == CONSTTK) {
        node->constDecl = parseConstDecl(node);
    } else {
        node->VarDecl = parseVarDecl(node);
    }
    return node;
}

ConstDeclNode* parseConstDecl(Node* parent) {
    ConstDeclNode* node = (ConstDeclNode*)malloc(sizeof(ConstDeclNode));
    node->node = newNode(ConstDecl, parent, NULL);
    nextToken(); // const
    node->typeBType = nextToken()->type == CHARTK ? CHAR : INT;
    node->constDefs = createVector();
    pushVector(node->constDefs, parseConstDef(node));
    while (peekToken(0)->type == COMMA) {
        nextToken(); // ,
        pushVector(node->constDefs, parseConstDef(node));
    }
    nextToken(); // ;
    return node;
}

ConstDefNode* parseConstDef(Node* parent) {
    ConstDefNode* node = (ConstDefNode*)malloc(sizeof(ConstDefNode));
    node->node = newNode(ConstDef, parent, NULL);
    node->ident = parseIdent(node);
    node->constExp = NULL;
    if (peekToken(0)->type == LBRACK) {
        nextToken(); // [
        node->constExp = parseConstExp(node);
        nextToken(); // ]
    }
    nextToken(); // =
    node->constInitVal = parseConstInitVal(node);
    return node;
}

ConstInitValNode* parseConstInitVal(Node* parent) {
    ConstInitValNode* node =
        (ConstInitValNode*)malloc(sizeof(ConstInitValNode));
    node->node = newNode(ConstInitVal, parent, NULL);
    node->constExp = NULL;
    node->constExps = NULL;
    node->stringConst = NULL;
    switch (peekToken(0)->type) {
    case STRCON:
        node->initValType = INIT_STR;
        node->stringConst = parseStringConst(node);
        break;
    case LBRACE:
        node->initValType = INIT_ARR;
        node->constExps = createVector();
        nextToken(); // {
        if (peekToken(0)->type == RBRACE) {
            nextToken(); // }
            break;
        }
        pushVector(node->constExps, parseConstExp(node));
        while (peekToken(0)->type == COMMA) {
            nextToken(); // ,
            pushVector(node->constExps, parseConstExp(node));
        }
        nextToken(); // }
        break;
    default:
        node->initValType = INIT_VAR;
        node->constExp = parseConstExp(node);
        break;
    }
    return node;
}

VarDeclNode* parseVarDecl(Node* parent) {
    VarDeclNode* node = (VarDeclNode*)malloc(sizeof(VarDeclNode));
    node->node = newNode(VarDecl, parent, NULL);
    node->typeBType = nextToken()->type == CHARTK ? CHAR : INT;
    node->varDefs = createVector();
    pushVector(node->varDefs, parseVarDef(node));
    while (peekToken(0)->type == COMMA) {
        nextToken(); // ,
        pushVector(node->varDefs, parseVarDef(node));
    }
    nextToken(); // ;
    return node;
}

VarDefNode* parseVarDef(Node* parent) {
    VarDefNode* node = (VarDefNode*)malloc(sizeof(VarDefNode));
    node->node = newNode(VarDef, parent, NULL);
    node->constExp = NULL;
    node->initVal = NULL;
    node->ident = parseIdent(node);
    node->name = node->ident->value;
    if (peekToken(0)->type == LBRACK) {
        nextToken(); // [
        node->constExp = parseConstExp(node);
        nextToken(); // ]
    }
    if (peekToken(0)->type == ASSIGN) {
        nextToken(); // =
        node->initVal = parseInitVal(node);
    }
    return node;
}

InitValNode* parseInitVal(Node* parent) {
    InitValNode* node = (InitValNode*)malloc(sizeof(InitValNode));
    node->node = newNode(InitVal, parent, NULL);
    node->exp = NULL;
    node->exps = NULL;
    node->stringConst = NULL;
    switch (peekToken(0)->type) {
    case STRCON:
        node->initValType = INIT_STR;
        node->stringConst = parseStringConst(node);
        break;
    case LBRACE:
        node->initValType = INIT_ARR;
        node->exps = createVector();
        nextToken(); // {
        if (peekToken(0)->type == RBRACE) {
            nextToken(); // }
            break;
        }
        pushVector(node->exps, parseExp(node));
        while (peekToken(0)->type == COMMA) {
            nextToken(); // ,
            pushVector(node->exps, parseExp(node));
        }
        nextToken(); // }
        break;
    default:
        node->initValType = INIT_VAR;
        node->exp = parseExp(node);
        break;
    }
    return node;
}

MainFuncDefNode* parseMainFuncDef(Node* parent) {
    MainFuncDefNode* node = (MainFuncDefNode*)malloc(sizeof(MainFuncDefNode));
    node->node = newNode(MainFuncDef, parent, NULL);
    nextToken(); // int
    nextToken(); // main
    nextToken(); // (
    nextToken(); // )
    node->block = parseBlock(node);
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

ConstExpNode* parseConstExp(Node* parent) {
}

ExpNode* parseExp(Node* parent) {
}

IdentNode* parseIdent(Node* parent) {
    IdentNode* node = (IdentNode*)malloc(sizeof(IdentNode));
    node->node = newNode(Ident, parent, nextToken());
    node->value = ((Node*)node)->token->content;
    return node;
}

StringConstNode* parseStringConst(Node* parent) {
    StringConstNode* node = (StringConstNode*)malloc(sizeof(StringConstNode));
    node->node = newNode(StringConst, parent, nextToken());
    node->str = ((Node*)node)->token->content;
    return node;
}
