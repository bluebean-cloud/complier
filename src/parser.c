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
    node->varDecl = NULL;
    if (peekToken(0)->type == CONSTTK) {
        node->constDecl = parseConstDecl(node);
    } else {
        node->varDecl = parseVarDecl(node);
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
    FuncFParamsNode* node = (FuncFParamsNode*)malloc(sizeof(FuncFParamsNode));
    node->node = newNode(FuncFParams, parent, NULL);
    node->funcFParams = createVector();
    if (peekToken(0)->type == RPARENT) {
        return node;
    }
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
    BlockNode* node = (BlockNode*)malloc(sizeof(BlockNode));
    node->node = newNode(Block, parent, NULL);
    node->blockItems = createVector();
    nextToken(); // {
    while (peekToken(0)->type != RBRACE) {
        pushVector(node->blockItems, parseBlockItem(node));
    }
    nextToken(); // }
    return node;
}

BlockItemNode* parseBlockItem(Node* parent) {
    BlockItemNode* node = (BlockItemNode*)malloc(sizeof(BlockItemNode));
    node->node = newNode(BlockItem, parent, NULL);
    node->decl = NULL;
    node->stmt = NULL;
    if (peekToken(0)->type == INTTK || peekToken(0)->type == CONSTTK) {
        node->decl = parseDecl(node);
    } else {
        node->stmt = parseStmt(node);
    }
    return node;
}

extern Token* curToken;

StmtNode* parseStmt(Node* parent) {
    StmtNode* node = (StmtNode*)malloc(sizeof(StmtNode));
    memset(node, 0, sizeof(StmtNode));
    switch (peekToken(0)->type) {
    case IFTK:
        node->stmtType = IF_STMT;
        nextToken(); // if
        nextToken(); // (
        node->cond = parseCond(node);
        nextToken(); // )
        node->ifStmt = parseStmt(node);
        if (peekToken(0)->type == ELSETK) {
            nextToken(); // else
            node->elStmt = parseStmt(node);
        }
        return node;
    case FORTK:
        node->stmtType = FOR_STMT;
        nextToken(); // for
        nextToken(); // (
        if (peekToken(0)->type != SEMICN) {
            node->forStmt1 = parseForStmt(node);
        }
        nextToken(); // ;
        if (peekToken(0)->type != SEMICN) {
            node->cond = parseCond(node);
        }
        nextToken(); // ;
        if (peekToken(0)->type != RPARENT) {
            node->forStmt2 = parseForStmt(node);
        }
        nextToken(); // )
        node->forStmt = parseStmt(node);
        return node;
    case BREAKTK:
        node->stmtType = BREAK_STMT;
        nextToken(); // break
        nextToken(); // ;
        return node;
    case CONTINUETK:
        node->stmtType = CONTINUE_STMT;
        nextToken(); // continue
        nextToken(); // ;
        return node;
    case RETURNTK:
        node->stmtType = RETURN_STMT;
        nextToken(); // return
        if (peekToken(0)->type != SEMICN) {
            node->exp = parseExp(node);
        }
        nextToken(); // ;
        return node;
    case PRINTFTK:
        node->stmtType = PRINTF_STMT;
        node->exps = createVector();
        nextToken(); // printf
        nextToken(); // (
        node->stringConst = parseStringConst(node);
        while (peekToken(0)->type == COMMA) {
            nextToken(); //,
            pushVector(node->exps, parseExp(node));
        }
        nextToken(); // )
        nextToken(); // ;
        return node;
    case LBRACE:
        node->stmtType = BLOCK_STMT;
        node->block = parseBlock(node);
        return node;
    case SEMICN:
        node->stmtType = EMPTY_STMT;
        return node;
    default:
        break;
    }
    Token* tmpToken = curToken;
    node->exp = parseExp(node);
    if (peekToken(0)->type != ASSIGN) {
        node->stmtType = EXP_STMT;
        nextToken(); // ;
        return node;
    }
    // exp 的下一符号为 =，说明为赋值语句，进行回溯
    curToken = tmpToken;
    freeNode(node->exp);

    node->lVal = parseLVal(node);
    nextToken(); // =
    switch (peekToken(0)->type) {
    case GETINTTK:
        node->stmtType = GETINT_STMT;
        nextToken();    // getint
        nextToken();    // (
        nextToken();    // )
        nextToken();    // ;
    case GETCHARTK:
        node->stmtType = GETCHAR_STMT;
        nextToken(); // getchar
        nextToken(); // (
        nextToken(); // )
        nextToken(); // ;
    default:
        node->exp = parseExp(node);
        nextToken(); // ;
        break;
    }
    return node;
}

ForStmtNode* parseForStmt(Node* parent) {
    ForStmtNode* node = (ForStmtNode*)malloc(sizeof(ForStmtNode));
    node->node = newNode(ForStmt, parent, NULL);
    node->lVal = parseLVal(node);
    nextToken(); // =
    node->exp = parseExp(node);
    return node;
}

ConstExpNode* parseConstExp(Node* parent) {
    ConstExpNode* node = (ConstExpNode*)malloc(sizeof(ConstExpNode));
    node->node = newNode(ConstExp, parent, NULL);
    node->addExp = parseAddExp(node);
    return node;
}

ExpNode* parseExp(Node* parent) {
    ExpNode* node = (ExpNode*)malloc(sizeof(ExpNode));
    node->node = newNode(Exp, parent, NULL);
    node->addExp = parseAddExp(node);
    return node;
}

AddExpNode* parseAddExp(Node* parent) {
    AddExpNode* node = (AddExpNode*)malloc(sizeof(AddExpNode));
    node->node = newNode(AddExp, parent, NULL);
    node->mulExps = createVector();
    node->operators = createVector();
    pushVector(node->mulExps, parseMulExp(node));
    while (peekToken(0)->type == PLUS || peekToken(0)->type == MINU) {
        pushVector(node->operators, nextToken());
        pushVector(node->mulExps, parseMulExp(node));
    }
    return node;
}

MulExpNode* parseMulExp(Node* parent) {
    MulExpNode* node = (MulExpNode*)malloc(sizeof(MulExpNode));
    node->node = newNode(MulExp, parent, NULL);
    node->unaryExps = createVector();
    node->operators = createVector();
    pushVector(node->unaryExps, parseUnaryExp(node));
    while (peekToken(0)->type == MULT || peekToken(0)->type == DIV ||
           peekToken(0)->type == MOD) {
        pushVector(node->operators, nextToken());
        pushVector(node->unaryExps, parseUnaryExp(node));
    }
    return node;
}

UnaryExpNode* parseUnaryExp(Node* parent) {
    UnaryExpNode* node = (UnaryExpNode*)malloc(sizeof(UnaryExpNode));
    memset(node, 0, sizeof(UnaryExpNode));
    node->node = newNode(UnaryExp, parent, NULL);
    if (peekToken(0)->type == IDENFR) {
        node->unaryExpType = CALL;
        node->ident = parseIdent(node);
        node->name = node->ident->value;
        nextToken(); // (
        node->funcRParams = parseFuncRParams(node);
        nextToken(); // )
        return node;
    }
    if (peekToken(0)->type == PLUS || peekToken(0)->type == MINU ||
        peekToken(0)->type == NOT) {
        node->unaryExpType = UNARY;
        node->unaryOp = parseUnaryOp(node);
        node->unaryExp = parseUnaryExp(node);
        return node;
    }
    node->unaryExpType = PRIMARY;
    node->primaryExp = parsePrimaryExp(node);
    return node;
}

UnaryOpNode* parseUnaryOp(Node* parent) {
    UnaryOpNode* node = (UnaryOpNode*)malloc(sizeof(UnaryOpNode));
    node->node = newNode(UnaryOp, parent, nextToken());
    node->unaryOpType = ((Node*)node)->token->type;
    return node;
}

FuncRParamsNode* parseFuncRParams(Node* parent) {
    FuncRParamsNode* node = (FuncRParamsNode*)malloc(sizeof(FuncRParamsNode));
    node->node = newNode(FuncRParams, parent, NULL);
    node->exps = createVector();
    if (peekToken(0)->type == RPARENT) {
        return node;
    }
    pushVector(node->exps, parseExp(node));
    while (peekToken(0)->type == COMMA) {
        nextToken(); // ,
        pushVector(node->exps, parseExp(node));
    }
    return node;
}

PrimaryExpNode* parsePrimaryExp(Node* parent) {
    PrimaryExpNode* node = (PrimaryExpNode*)malloc(sizeof(PrimaryExpNode));
    memset(node, 0, sizeof(PrimaryExpNode));
    node->node = newNode(PrimaryExp, parent, NULL);
    if (peekToken(0)->type == INTCON) {
        node->primaryType = NUMBER_PRIMARY;
        node->number = parseNumber(node);
        return node;
    }
    if (peekToken(0)->type == CHRCON) {
        node->primaryType = CHARACTER_PRIMARY;
        node->character = parseCharacter(node);
        return node;
    }
    if (peekToken(0)->type == LPARENT) {
        node->primaryType = EXP_PRIMARY;
        nextToken(); // (
        node->exp = parseExp(node);
        nextToken(); // )
        return node;
    }
    node->primaryType = LVAL_PRIMARY;
    node->lVal = parseLVal(node);
    return node;
}

CondNode* parseCond(Node* parent) {
    CondNode* node = (CondNode*)malloc(sizeof(CondNode));
    node->node = newNode(Cond, parent, NULL);
    node->lOrExp = parseLOrExp(node);
    return node;
}

LOrExpNode* parseLOrExp(Node* parent) {
    LOrExpNode* node = (LOrExpNode*)malloc(sizeof(LOrExpNode));
    node->node = newNode(LOrExp, parent, NULL);
    node->lAndExps = createVector();
    pushVector(node->lAndExps, parseLAndExp(node));
    while (peekToken(0)->type == OR) {
        nextToken(); // ||
        pushVector(node->lAndExps, parseLAndExp(node));
    }
    return node;
}

LAndExpNode* parseLAndExp(Node* parent) {
    LAndExpNode* node = (LAndExpNode*)malloc(sizeof(LAndExpNode));
    node->node = newNode(LAndExp, parent, NULL);
    node->eqExps = createVector();
    pushVector(node->eqExps, parseEqExp(node));
    while (peekToken(0)->type == AND) {
        nextToken(); // &&
        pushVector(node->eqExps, parseEqExp(node));
    }
    return node;
}

EqExpNode* parseEqExp(Node* parent) {
    EqExpNode* node = (EqExpNode*)malloc(sizeof(EqExpNode));
    node->node = newNode(EqExp, parent, NULL);
    node->relExps = createVector();
    node->operators = createVector();
    pushVector(node->relExps, parseRelExp(node));
    while (peekToken(0)->type == EQL || peekToken(0)->type == NEQ) {
        pushVector(node->operators, nextToken()); // == || !=
        pushVector(node->relExps, parseRelExp(node));
    }
    return node;
}

RelExpNode* parseRelExp(Node* parent) {
    RelExpNode* node = (RelExpNode*)malloc(sizeof(RelExpNode));
    node->node = newNode(RelExp, parent, NULL);
    node->addExps = createVector();
    node->operators = createVector();
    pushVector(node->addExps, parseAddExp(node));
    TokenType type = peekToken(0)->type;
    while (type == LSS || type == LEQ || type == GRE || type == GEQ) {
        pushVector(node->operators, nextToken());
        pushVector(node->addExps, parseAddExp(node));
        type = peekToken(0)->type;
    }
    return node;
}

IdentNode* parseIdent(Node* parent) {
    IdentNode* node = (IdentNode*)malloc(sizeof(IdentNode));
    node->node = newNode(Ident, parent, nextToken());
    node->value = ((Node*)node)->token->content;
    return node;
}

LValNode* parseLVal(Node* parent) {
    LValNode* node = (LValNode*)malloc(sizeof(LValNode));
    node->node = newNode(LVal, parent, NULL);
    node->exp = NULL;
    node->ident = parseIdent(node);
    if (peekToken(0)->type == LBRACK) {
        nextToken(); // [
        node->exp = parseExp(node);
        nextToken(); // ]
    }
    return node;
}

StringConstNode* parseStringConst(Node* parent) {
    StringConstNode* node = (StringConstNode*)malloc(sizeof(StringConstNode));
    node->node = newNode(StringConst, parent, nextToken());
    node->str = ((Node*)node)->token->content;
    return node;
}

NumberNode* parseNumber(Node* parent) {
    NumberNode* node = (NumberNode*)malloc(sizeof(NumberNode));
    node->node = newNode(Number, parent, nextToken());
    node->value = ((Node*)node)->token->value;
    return node;
}

CharacterNode* parseCharacter(Node* parent) {
    CharacterNode* node = (CharacterNode*)malloc(sizeof(CharacterNode));
    node->node = newNode(Character, parent, nextToken());
    node->value = ((Node*)node)->token->value;
    return 0;
}

// type: 0\~Token* 1\~Node*，其中对于 Token* 类型不进行 free
void freeVector(Vector* vector, int type) {
    if (type == 1) {
        for (int i = 0; i < vector->length; i++) {
            freeNode(vector->values[i]);
        }
    }
}

void freeNode(Node* node) {
    if (node == NULL) {
        return;
    }
    switch (node->nodeType) {
    case CompUnit:
        FREE_V(CompUnitNode, decls, 1)
        FREE_V(CompUnitNode, funcDefs, 1)
        FREE(CompUnitNode, mainFuncDef)
        return;
    case Decl:
        FREE(DeclNode, constDecl)
        FREE(DeclNode, varDecl)
        return;
    case FuncDef:
        FREE(FuncDefNode, funcName)
        FREE_V(FuncDefNode, funcFParams, 1)
        FREE(FuncDefNode, block)
        return;
    case MainFuncDef:
        FREE(MainFuncDefNode, block)
        return;
    case ConstDecl:
        FREE_V(ConstDeclNode, constDefs, 1)
        return;
    case ConstDef:
        FREE(ConstDefNode, ident)
        FREE(ConstDefNode, constExp)
        FREE(ConstDefNode, constInitVal)
        return;
    case ConstInitVal:
        FREE(ConstInitValNode, initValType)
        FREE(ConstInitValNode, constExp)
        FREE_V(ConstInitValNode, constExps, 1)
        FREE(ConstInitValNode, stringConst)
        return;
    case ConstExp:
        FREE(ConstExpNode, addExp)
        return;
    case VarDecl:
        FREE_V(VarDeclNode, varDefs, 1)
        return;
    case VarDef:
        FREE(VarDefNode, ident)
        FREE(VarDefNode, constExp)
        FREE(VarDefNode, initVal)
        return;
    case InitVal:
        FREE(InitValNode, exp)
        FREE_V(InitValNode, exps, 1)
        FREE(InitValNode, stringConst)
        return;
    case LVal:
        FREE(LValNode, ident)
        FREE(LValNode, exp)
        return;
    case FuncFParams:
        FREE_V(FuncFParamsNode, funcFParams, 1)
        return;
    case Block:
        FREE_V(BlockNode, blockItems, 1)
        return;
    case BlockItem:
        FREE(BlockItemNode, decl)
        FREE(BlockItemNode, stmt)
        return;
    case Stmt:
        FREE(StmtNode, lVal)
        FREE(StmtNode, exp)
        FREE(StmtNode, block)
        FREE(StmtNode, cond)
        FREE(StmtNode, ifStmt)
        FREE(StmtNode, elStmt)
        FREE(StmtNode, forStmt1)
        FREE(StmtNode, forStmt2)
        FREE(StmtNode, forStmt)
        FREE(StmtNode, stringConst)
        FREE_V(StmtNode, exps, 1)
        return;
    case Exp:
        FREE(ExpNode, addExp)
        return;
    case AddExp:
        FREE_V(AddExpNode, operators, 0)
        FREE_V(AddExpNode, mulExps, 1)
        return;
    case MulExp:
        FREE_V(MulExpNode, operators, 0)
        FREE_V(MulExpNode, unaryExps, 1)
        return;
    case UnaryExp:
        FREE(UnaryExpNode, primaryExp)
        FREE(UnaryExpNode, ident)
        FREE(UnaryExpNode, funcRParams)
        FREE(UnaryExpNode, unaryOp)
        FREE(UnaryExpNode, unaryExp)
        return;
    case FuncRParams:
        FREE_V(FuncRParamsNode, exps, 1)
        return;
    case FuncFParam:
        FREE(FuncFParamNode, ident)
        return;
    case PrimaryExp:
        FREE(PrimaryExpNode, exp)
        FREE(PrimaryExpNode, lVal)
        FREE(PrimaryExpNode, number)
        FREE(PrimaryExpNode, character)
        return;
    case Cond:
        FREE(CondNode, lOrExp)
        return;
    case LOrExp:
        FREE_V(LOrExpNode, lAndExps, 1)
        return;
    case LAndExp:
        FREE_V(LAndExpNode, eqExps, 1)
        return;
    case EqExp:
        FREE_V(EqExpNode, operators, 0)
        FREE_V(EqExpNode, relExps, 1)
        return;
    case RelExp:
        FREE_V(RelExpNode, operators, 0)
        FREE_V(RelExpNode, addExps, 1)
        return;
    case ForStmt:
        FREE(ForStmtNode, lVal)
        FREE(ForStmtNode, exp)
        return;
    case Ident:
    case UnaryOp:
    case Number:
    case Character:
    case StringConst:
    default:
        return;
    }
}
