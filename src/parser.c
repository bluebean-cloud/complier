#include "../include/compiler.h"

// parser
CompUnitNode* nodesRoot;
extern int errOccur;
// 开始进行语法分析
void analyzeSyntax() {
    nodesRoot = parseCompUnit();
    if (!errOccur) {
        printNodeTree((Node*)nodesRoot);
    }
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
    node->constDecl = NULL;
    node->varDecl = NULL;
    if (peekToken(0)->type == CONSTTK) {
        node->constDecl = parseConstDecl((Node*)node);
    } else {
        node->varDecl = parseVarDecl((Node*)node);
    }
    return node;
}

ConstDeclNode* parseConstDecl(Node* parent) {
    ConstDeclNode* node = (ConstDeclNode*)malloc(sizeof(ConstDeclNode));
    node->node = newNode(ConstDecl, parent, NULL);
    nextToken(); // const
    node->typeBType = nextToken()->type == CHARTK ? CHAR : INT;
    node->constDefs = createVector();
    pushVector(node->constDefs, parseConstDef((Node*)node));
    while (peekToken(0)->type == COMMA) {
        nextToken(); // ,
        pushVector(node->constDefs, parseConstDef((Node*)node));
    }
    if (peekToken(0)->type != SEMICN) {
        addError(peekToken(-1)->line, 'i');
        return node;
    }
    nextToken(); // ;
    return node;
}

ConstDefNode* parseConstDef(Node* parent) {
    ConstDefNode* node = (ConstDefNode*)malloc(sizeof(ConstDefNode));
    node->node = newNode(ConstDef, parent, NULL);
    node->ident = parseIdent((Node*)node);
    node->name = node->ident->value;
    node->constExp = NULL;
    if (peekToken(0)->type == LBRACK) {
        nextToken(); // [
        node->constExp = parseConstExp((Node*)node);
        if (peekToken(0)->type == RBRACK) {
            nextToken(); // ]
        } else {
            addError(peekToken(-1)->line, 'k');
        }
    }
    nextToken(); // =
    node->constInitVal = parseConstInitVal((Node*)node);
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
        node->stringConst = parseStringConst((Node*)node);
        break;
    case LBRACE:
        node->initValType = INIT_ARR;
        node->constExps = createVector();
        nextToken(); // {
        if (peekToken(0)->type == RBRACE) {
            nextToken(); // }
            break;
        }
        pushVector(node->constExps, parseConstExp((Node*)node));
        while (peekToken(0)->type == COMMA) {
            nextToken(); // ,
            pushVector(node->constExps, parseConstExp((Node*)node));
        }
        nextToken(); // }
        break;
    default:
        node->initValType = INIT_VAR;
        node->constExp = parseConstExp((Node*)node);
        break;
    }
    return node;
}

VarDeclNode* parseVarDecl(Node* parent) {
    VarDeclNode* node = (VarDeclNode*)malloc(sizeof(VarDeclNode));
    node->node = newNode(VarDecl, parent, NULL);
    node->typeBType = nextToken()->type == CHARTK ? CHAR : INT;
    node->varDefs = createVector();
    pushVector(node->varDefs, parseVarDef((Node*)node));
    while (peekToken(0)->type == COMMA) {
        nextToken(); // ,
        pushVector(node->varDefs, parseVarDef((Node*)node));
    }
    if (peekToken(0)->type != SEMICN) {
        addError(peekToken(-1)->line, 'i');
        return node;
    }
    nextToken(); // ;
    return node;
}

VarDefNode* parseVarDef(Node* parent) {
    VarDefNode* node = (VarDefNode*)malloc(sizeof(VarDefNode));
    node->node = newNode(VarDef, parent, NULL);
    node->constExp = NULL;
    node->initVal = NULL;
    node->ident = parseIdent((Node*)node);
    node->name = node->ident->value;
    if (peekToken(0)->type == LBRACK) {
        nextToken(); // [
        node->constExp = parseConstExp((Node*)node);
        if (peekToken(0)->type == RBRACK) {
            nextToken(); // ]
        } else {
            addError(peekToken(-1)->line, 'k');
        }
    }
    if (peekToken(0)->type == ASSIGN) {
        nextToken(); // =
        node->initVal = parseInitVal((Node*)node);
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
        node->stringConst = parseStringConst((Node*)node);
        break;
    case LBRACE:
        node->initValType = INIT_ARR;
        node->exps = createVector();
        nextToken(); // {
        if (peekToken(0)->type == RBRACE) {
            nextToken(); // }
            break;
        }
        pushVector(node->exps, parseExp((Node*)node));
        while (peekToken(0)->type == COMMA) {
            nextToken(); // ,
            pushVector(node->exps, parseExp((Node*)node));
        }
        nextToken(); // }
        break;
    default:
        node->initValType = INIT_VAR;
        node->exp = parseExp((Node*)node);
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
    if (peekToken(0)->type == RPARENT) {
        nextToken(); // )
    } else {
        addError(peekToken(-1)->line, 'j');
    }
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
    node->ident = parseIdent((Node*)node);
    node->funcName = node->ident->value;
    nextToken(); // (
    node->funcFParams = parseFuncFParams((Node*)node);
    if (peekToken(0)->type == RPARENT) {
        nextToken(); // )
    } else {
        addError(peekToken(-1)->line, 'j');
    }
    node->block = parseBlock((Node*)node);
    return node;
}

FuncFParamsNode* parseFuncFParams(Node* parent) {
    FuncFParamsNode* node = (FuncFParamsNode*)malloc(sizeof(FuncFParamsNode));
    node->node = newNode(FuncFParams, parent, NULL);
    node->funcFParams = createVector();
    if (peekToken(0)->type == RPARENT) {
        return node;
    }
    pushVector(node->funcFParams, parseFuncFParam((Node*)node));
    while (peekToken(0)->type == COMMA) {
        nextToken(); // ,
        pushVector(node->funcFParams, parseFuncFParam((Node*)node));
    }
    return node;
}

FuncFParamNode* parseFuncFParam(Node* parent) {
    FuncFParamNode* node = (FuncFParamNode*)malloc(sizeof(FuncFParamNode));
    node->node = newNode(FuncFParam, parent, NULL);
    node->typeBType = nextToken()->type == CHARTK ? CHAR : INT;
    node->ident = parseIdent((Node*)node);
    node->name = node->ident->value;
    if (peekToken(0)->type == LBRACK) {
        node->isArray = 1;
        nextToken(); // [
        if (peekToken(0)->type == RBRACK) {
            nextToken(); // ]
        } else {
            addError(peekToken(-1)->line, 'k');
        }
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
        pushVector(node->blockItems, parseBlockItem((Node*)node));
    }
    nextToken(); // }
    return node;
}

BlockItemNode* parseBlockItem(Node* parent) {
    BlockItemNode* node = (BlockItemNode*)malloc(sizeof(BlockItemNode));
    node->node = newNode(BlockItem, parent, NULL);
    node->decl = NULL;
    node->stmt = NULL;
    if (peekToken(0)->type == INTTK || peekToken(0)->type == CHARTK ||
        peekToken(0)->type == CONSTTK) {
        node->decl = parseDecl((Node*)node);
    } else {
        node->stmt = parseStmt((Node*)node);
    }
    return node;
}

int isExp() {
    int type = peekToken(0)->type;
    return type == LPARENT || type == MINU || type == PLUS || type == IDENFR ||
           type == INTCON || type == CHRCON;
}

extern Token* curToken;

StmtNode* parseStmt(Node* parent) {
    StmtNode* node = (StmtNode*)malloc(sizeof(StmtNode));
    memset(node, 0, sizeof(StmtNode));
    node->node = newNode(Stmt, parent, NULL);
    switch (peekToken(0)->type) {
    case IFTK:
        node->stmtType = IF_STMT;
        nextToken(); // if
        nextToken(); // (
        node->cond = parseCond((Node*)node);
        if (peekToken(0)->type == RPARENT) {
            nextToken(); // )
        } else {
            addError(peekToken(-1)->line, 'j');
        }
        node->ifStmt = parseStmt((Node*)node);
        if (peekToken(0)->type == ELSETK) {
            nextToken(); // else
            node->elStmt = parseStmt((Node*)node);
        }
        return node;
    case FORTK:
        node->stmtType = FOR_STMT;
        nextToken(); // for
        nextToken(); // (
        if (peekToken(0)->type != SEMICN) {
            node->forStmt1 = parseForStmt((Node*)node);
        }
        nextToken(); // ;
        if (peekToken(0)->type != SEMICN) {
            node->cond = parseCond((Node*)node);
        }
        nextToken(); // ;
        if (peekToken(0)->type != RPARENT) {
            node->forStmt2 = parseForStmt((Node*)node);
        }
        if (peekToken(0)->type == RPARENT) {
            nextToken(); // )
        } else {
            addError(peekToken(-1)->line, 'j');
        }
        node->forStmt = parseStmt((Node*)node);
        return node;
    case BREAKTK:
        node->stmtType = BREAK_STMT;
        nextToken(); // break
        if (peekToken(0)->type != SEMICN) {
            addError(peekToken(-1)->line, 'i');
            return node;
        }
        nextToken(); // ;
        return node;
    case CONTINUETK:
        node->stmtType = CONTINUE_STMT;
        nextToken(); // continue
        if (peekToken(0)->type != SEMICN) {
            addError(peekToken(-1)->line, 'i');
            return node;
        }
        nextToken(); // ;
        return node;
    case RETURNTK:
        node->stmtType = RETURN_STMT;
        nextToken(); // return
        if (peekToken(0)->type != SEMICN && isExp()) {
            node->exp = parseExp((Node*)node);
        }
        if (peekToken(0)->type != SEMICN) {
            addError(peekToken(-1)->line, 'i');
            return node;
        }
        nextToken(); // ;
        return node;
    case PRINTFTK:
        node->stmtType = PRINTF_STMT;
        node->exps = createVector();
        nextToken(); // printf
        nextToken(); // (
        node->stringConst = parseStringConst((Node*)node);
        while (peekToken(0)->type == COMMA) {
            nextToken(); //,
            pushVector(node->exps, parseExp((Node*)node));
        }
        if (peekToken(0)->type == RPARENT) {
            nextToken(); // )
        } else {
            addError(peekToken(-1)->line, 'j');
        }
        if (peekToken(0)->type != SEMICN) {
            addError(peekToken(-1)->line, 'i');
            return node;
        }
        nextToken(); // ;
        return node;
    case LBRACE:
        node->stmtType = BLOCK_STMT;
        node->block = parseBlock((Node*)node);
        return node;
    case SEMICN:
        node->stmtType = EMPTY_STMT;
        nextToken(); // ;
        return node;
    default:
        break;
    }
    Token* tmpToken = curToken;
    node->exp = parseExp((Node*)node);
    if (peekToken(0)->type != ASSIGN) {
        node->stmtType = EXP_STMT;
        if (peekToken(0)->type != SEMICN) {
            addError(peekToken(-1)->line, 'i');
            return node;
        }
        nextToken(); // ;
        return node;
    }
    // exp 的下一符号为 =，说明为赋值语句，进行回溯
    curToken = tmpToken;
    freeNode((Node*)(node->exp));

    node->lVal = parseLVal((Node*)node);
    nextToken(); // =
    switch (peekToken(0)->type) {
    case GETINTTK:
        node->stmtType = GETINT_STMT;
        nextToken(); // getint
        nextToken(); // (
        if (peekToken(0)->type == RPARENT) {
            nextToken(); // )
        } else {
            addError(peekToken(-1)->line, 'j');
        }
        if (peekToken(0)->type != SEMICN) {
            addError(peekToken(-1)->line, 'i');
            return node;
        }
        nextToken(); // ;
        break;
    case GETCHARTK:
        node->stmtType = GETCHAR_STMT;
        nextToken(); // getchar
        nextToken(); // (
        if (peekToken(0)->type == RPARENT) {
            nextToken(); // )
        } else {
            addError(peekToken(-1)->line, 'j');
        }
        if (peekToken(0)->type != SEMICN) {
            addError(peekToken(-1)->line, 'i');
            return node;
        }
        nextToken(); // ;
        break;
    default:
        node->exp = ASSIGN_STMT;
        node->exp = parseExp((Node*)node);
        if (peekToken(0)->type != SEMICN) {
            addError(peekToken(-1)->line, 'i');
            return node;
        }
        nextToken(); // ;
        break;
    }
    return node;
}

ForStmtNode* parseForStmt(Node* parent) {
    ForStmtNode* node = (ForStmtNode*)malloc(sizeof(ForStmtNode));
    node->node = newNode(ForStmt, parent, NULL);
    node->lVal = parseLVal((Node*)node);
    nextToken(); // =
    node->exp = parseExp((Node*)node);
    return node;
}

ConstExpNode* parseConstExp(Node* parent) {
    ConstExpNode* node = (ConstExpNode*)malloc(sizeof(ConstExpNode));
    node->node = newNode(ConstExp, parent, NULL);
    node->addExp = parseAddExp((Node*)node);
    return node;
}

ExpNode* parseExp(Node* parent) {
    ExpNode* node = (ExpNode*)malloc(sizeof(ExpNode));
    node->node = newNode(Exp, parent, NULL);
    node->addExp = parseAddExp((Node*)node);
    return node;
}

AddExpNode* parseAddExp(Node* parent) {
    AddExpNode* node = (AddExpNode*)malloc(sizeof(AddExpNode));
    node->node = newNode(AddExp, parent, NULL);
    node->mulExps = createVector();
    node->operators = createVector();
    pushVector(node->mulExps, parseMulExp((Node*)node));
    while (peekToken(0)->type == PLUS || peekToken(0)->type == MINU) {
        pushVector(node->operators, nextToken());
        pushVector(node->mulExps, parseMulExp((Node*)node));
    }
    return node;
}

MulExpNode* parseMulExp(Node* parent) {
    MulExpNode* node = (MulExpNode*)malloc(sizeof(MulExpNode));
    node->node = newNode(MulExp, parent, NULL);
    node->unaryExps = createVector();
    node->operators = createVector();
    pushVector(node->unaryExps, parseUnaryExp((Node*)node));
    while (peekToken(0)->type == MULT || peekToken(0)->type == DIV ||
           peekToken(0)->type == MOD) {
        pushVector(node->operators, nextToken());
        pushVector(node->unaryExps, parseUnaryExp((Node*)node));
    }
    return node;
}

UnaryExpNode* parseUnaryExp(Node* parent) {
    UnaryExpNode* node = (UnaryExpNode*)malloc(sizeof(UnaryExpNode));
    memset(node, 0, sizeof(UnaryExpNode));
    node->node = newNode(UnaryExp, parent, NULL);
    if (peekToken(0)->type == IDENFR && peekToken(1)->type == LPARENT) {
        node->unaryExpType = CALL;
        node->ident = parseIdent((Node*)node);
        node->name = node->ident->value;
        nextToken(); // (
        node->funcRParams = parseFuncRParams((Node*)node);
        if (peekToken(0)->type == RPARENT) {
            nextToken(); // )
        } else {
            addError(peekToken(-1)->line, 'j');
        }
        return node;
    }
    if (peekToken(0)->type == PLUS || peekToken(0)->type == MINU ||
        peekToken(0)->type == NOT) {
        node->unaryExpType = UNARY;
        node->unaryOp = parseUnaryOp((Node*)node);
        node->unaryExp = parseUnaryExp((Node*)node);
        return node;
    }
    node->unaryExpType = PRIMARY;
    node->primaryExp = parsePrimaryExp((Node*)node);
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
    pushVector(node->exps, parseExp((Node*)node));
    while (peekToken(0)->type == COMMA) {
        nextToken(); // ,
        pushVector(node->exps, parseExp((Node*)node));
    }
    return node;
}

PrimaryExpNode* parsePrimaryExp(Node* parent) {
    PrimaryExpNode* node = (PrimaryExpNode*)malloc(sizeof(PrimaryExpNode));
    memset(node, 0, sizeof(PrimaryExpNode));
    node->node = newNode(PrimaryExp, parent, NULL);
    if (peekToken(0)->type == INTCON) {
        node->primaryType = NUMBER_PRIMARY;
        node->number = parseNumber((Node*)node);
        return node;
    }
    if (peekToken(0)->type == CHRCON) {
        node->primaryType = CHARACTER_PRIMARY;
        node->character = parseCharacter((Node*)node);
        return node;
    }
    if (peekToken(0)->type == LPARENT) {
        node->primaryType = EXP_PRIMARY;
        nextToken(); // (
        node->exp = parseExp((Node*)node);
        if (peekToken(0)->type == RPARENT) {
            nextToken(); // )
        } else {
            addError(peekToken(-1)->line, 'j');
        }
        return node;
    }
    node->primaryType = LVAL_PRIMARY;
    node->lVal = parseLVal((Node*)node);
    return node;
}

CondNode* parseCond(Node* parent) {
    CondNode* node = (CondNode*)malloc(sizeof(CondNode));
    node->node = newNode(Cond, parent, NULL);
    node->lOrExp = parseLOrExp((Node*)node);
    return node;
}

LOrExpNode* parseLOrExp(Node* parent) {
    LOrExpNode* node = (LOrExpNode*)malloc(sizeof(LOrExpNode));
    node->node = newNode(LOrExp, parent, NULL);
    node->lAndExps = createVector();
    pushVector(node->lAndExps, parseLAndExp((Node*)node));
    while (peekToken(0)->type == OR) {
        nextToken(); // ||
        pushVector(node->lAndExps, parseLAndExp((Node*)node));
    }
    return node;
}

LAndExpNode* parseLAndExp(Node* parent) {
    LAndExpNode* node = (LAndExpNode*)malloc(sizeof(LAndExpNode));
    node->node = newNode(LAndExp, parent, NULL);
    node->eqExps = createVector();
    pushVector(node->eqExps, parseEqExp((Node*)node));
    while (peekToken(0)->type == AND) {
        nextToken(); // &&
        pushVector(node->eqExps, parseEqExp((Node*)node));
    }
    return node;
}

EqExpNode* parseEqExp(Node* parent) {
    EqExpNode* node = (EqExpNode*)malloc(sizeof(EqExpNode));
    node->node = newNode(EqExp, parent, NULL);
    node->relExps = createVector();
    node->operators = createVector();
    pushVector(node->relExps, parseRelExp((Node*)node));
    while (peekToken(0)->type == EQL || peekToken(0)->type == NEQ) {
        pushVector(node->operators, nextToken()); // == || !=
        pushVector(node->relExps, parseRelExp((Node*)node));
    }
    return node;
}

RelExpNode* parseRelExp(Node* parent) {
    RelExpNode* node = (RelExpNode*)malloc(sizeof(RelExpNode));
    node->node = newNode(RelExp, parent, NULL);
    node->addExps = createVector();
    node->operators = createVector();
    pushVector(node->addExps, parseAddExp((Node*)node));
    TokenType type = peekToken(0)->type;
    while (type == LSS || type == LEQ || type == GRE || type == GEQ) {
        pushVector(node->operators, nextToken());
        pushVector(node->addExps, parseAddExp((Node*)node));
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
    node->ident = parseIdent((Node*)node);
    node->name = node->ident->value;
    if (peekToken(0)->type == LBRACK) {
        nextToken(); // [
        node->exp = parseExp((Node*)node);
        if (peekToken(0)->type == RBRACK) {
            nextToken(); // ]
        } else {
            addError(peekToken(-1)->line, 'k');
        }
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
    node->str = ((Node*)node)->token->content;
    return node;
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
        FREE(FuncDefNode, ident)
        FREE(FuncDefNode, funcFParams)
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

char typeToPrint[][15] = {"INTTK int", "CHARTK char", "VOIDTK void"};
extern const char tokenString[][15];
extern FILE* output;

void printNodeTree(Node* node) {
    if (node == NULL) {
        return;
    }
    switch (node->nodeType) {
        NODECASE(CompUnit)
        for (int i = 0; i < nodeCompUnit->decls->length; i++) {
            printNodeTree((Node*)nodeCompUnit->decls->values[i]);
        }
        for (int i = 0; i < nodeCompUnit->funcDefs->length; i++) {
            printNodeTree((Node*)nodeCompUnit->funcDefs->values[i]);
        }
        printNodeTree((Node*)nodeCompUnit->mainFuncDef);
        fprintf(output, "<CompUnit>\n");
        NODECASE(Decl)
        if (nodeDecl->constDecl) {
            printNodeTree((Node*)nodeDecl->constDecl);
        }
        if (nodeDecl->varDecl) {
            printNodeTree((Node*)nodeDecl->varDecl);
        }
        NODECASE(FuncDef)
        fprintf(output, "%s\n<FuncType>\nIDENFR %s\nLPARENT (\n",
                typeToPrint[nodeFuncDef->typeFuncType], nodeFuncDef->funcName);
        printNodeTree((Node*)nodeFuncDef->funcFParams);
        fprintf(output, "RPARENT )\n");
        printNodeTree((Node*)nodeFuncDef->block);
        fprintf(output, "<FuncDef>\n");
        NODECASE(MainFuncDef)
        fprintf(output, "INTTK int\nMAINTK main\nLPARENT (\nRPARENT )\n");
        printNodeTree((Node*)nodeMainFuncDef->block);
        fprintf(output, "<MainFuncDef>\n");
        NODECASE(ConstDecl)
        fprintf(output, "CONSTTK const\n%s\n",
                typeToPrint[nodeConstDecl->typeBType]);
        for (int i = 0; i < nodeConstDecl->constDefs->length; i++) {
            if (i) {
                fprintf(output, "COMMA ,\n");
            }
            printNodeTree((Node*)nodeConstDecl->constDefs->values[i]);
        }
        fprintf(output, "SEMICN ;\n<ConstDecl>\n");
        NODECASE(ConstDef)
        fprintf(output, "IDENFR %s\n", nodeConstDef->name);
        if (nodeConstDef->constExp) {
            fprintf(output, "LBRACK [\n");
            printNodeTree((Node*)nodeConstDef->constExp);
            fprintf(output, "RBRACK ]\n");
        }
        fprintf(output, "ASSIGN =\n");
        printNodeTree((Node*)nodeConstDef->constInitVal);
        fprintf(output, "<ConstDef>\n");
        NODECASE(ConstInitVal)
        if (nodeConstInitVal->constExp) {
            printNodeTree((Node*)nodeConstInitVal->constExp);
        } else if (nodeConstInitVal->stringConst) {
            fprintf(output, "STRCON %s\n", nodeConstInitVal->stringConst->str);
        } else {
            fprintf(output, "LBRACE {\n");
            for (int i = 0; i < nodeConstInitVal->constExps->length; i++) {
                if (i) {
                    fprintf(output, "COMMA ,\n");
                }
                printNodeTree((Node*)nodeConstInitVal->constExps->values[i]);
            }
            fprintf(output, "RBRACE }\n");
        }
        fprintf(output, "<ConstInitVal>\n");
        NODECASE(ConstExp)
        printNodeTree((Node*)nodeConstExp->addExp);
        fprintf(output, "<ConstExp>\n");
        NODECASE(VarDecl)
        fprintf(output, "%s\n", typeToPrint[nodeVarDecl->typeBType]);
        for (int i = 0; i < nodeVarDecl->varDefs->length; i++) {
            if (i) {
                fprintf(output, "COMMA ,\n");
            }
            printNodeTree((Node*)nodeVarDecl->varDefs->values[i]);
        }
        fprintf(output, "SEMICN ;\n<VarDecl>\n");
        NODECASE(VarDef) fprintf(output, "IDENFR %s\n", nodeVarDef->name);
        if (nodeVarDef->constExp) {
            fprintf(output, "LBRACK [\n");
            printNodeTree((Node*)nodeVarDef->constExp);
            fprintf(output, "RBRACK ]\n");
        }
        if (nodeVarDef->initVal) {
            fprintf(output, "ASSIGN =\n");
            printNodeTree((Node*)nodeVarDef->initVal);
        }
        fprintf(output, "<VarDef>\n");
        NODECASE(InitVal)
        if (nodeInitVal->stringConst) {
            printNodeTree((Node*)nodeInitVal->stringConst);
        } else if (nodeInitVal->exp) {
            printNodeTree((Node*)nodeInitVal->exp);
        } else {
            fprintf(output, "LBRACE {\n");
            for (int i = 0; i < nodeInitVal->exps->length; i++) {
                if (i) {
                    fprintf(output, "COMMA ,\n");
                }
                printNodeTree((Node*)nodeInitVal->exps->values[i]);
            }
            fprintf(output, "RBRACE }\n");
        }
        fprintf(output, "<InitVal>\n");
        NODECASE(LVal)
        fprintf(output, "IDENFR %s\n", nodeLVal->name);
        if (nodeLVal->exp) {
            fprintf(output, "LBRACK [\n");
            printNodeTree((Node*)nodeLVal->exp);
            fprintf(output, "RBRACK ]\n");
        }
        fprintf(output, "<LVal>\n");
        NODECASE(FuncFParams)
        if (nodeFuncFParams->funcFParams->length == 0) {
            return;
        }
        for (int i = 0; i < nodeFuncFParams->funcFParams->length; i++) {
            if (i) {
                fprintf(output, "COMMA ,\n");
            }
            printNodeTree((Node*)nodeFuncFParams->funcFParams->values[i]);
        }
        fprintf(output, "<FuncFParams>\n");
        NODECASE(Block)
        fprintf(output, "LBRACE {\n");
        for (int i = 0; i < nodeBlock->blockItems->length; i++) {
            printNodeTree((Node*)nodeBlock->blockItems->values[i]);
        }
        fprintf(output, "RBRACE }\n");
        fprintf(output, "<Block>\n");
        NODECASE(BlockItem) if (nodeBlockItem->decl) {
            printNodeTree((Node*)nodeBlockItem->decl);
        }
        if (nodeBlockItem->stmt) {
            printNodeTree((Node*)nodeBlockItem->stmt);
        }
        NODECASE(Stmt) switch (nodeStmt->stmtType) {
        case ASSIGN_STMT:
            printNodeTree((Node*)nodeStmt->lVal);
            fprintf(output, "ASSIGN =\n");
            printNodeTree((Node*)nodeStmt->exp);
            fprintf(output, "SEMICN ;\n");
            break;
        case EXP_STMT:
            printNodeTree((Node*)nodeStmt->exp);
            fprintf(output, "SEMICN ;\n");
            break;
        case EMPTY_STMT:
            fprintf(output, "SEMICN ;\n");
            break;
        case BLOCK_STMT:
            printNodeTree((Node*)nodeStmt->block);
            break;
        case IF_STMT:
            fprintf(output, "IFTK if\nLPARENT (\n");
            printNodeTree((Node*)nodeStmt->cond);
            fprintf(output, "RPARENT )\n");
            printNodeTree((Node*)nodeStmt->ifStmt);
            if (nodeStmt->elStmt) {
                fprintf(output, "ELSETK else\n");
                printNodeTree((Node*)nodeStmt->elStmt);
            }
            break;
        case FOR_STMT:
            fprintf(output, "FORTK for\nLPARENT (\n");
            if (nodeStmt->forStmt1) {
                printNodeTree((Node*)nodeStmt->forStmt1);
            }
            fprintf(output, "SEMICN ;\n");
            if (nodeStmt->cond) {
                printNodeTree((Node*)nodeStmt->cond);
            }
            fprintf(output, "SEMICN ;\n");
            if (nodeStmt->forStmt2) {
                printNodeTree((Node*)nodeStmt->forStmt2);
            }
            fprintf(output, "RPARENT )\n");
            printNodeTree((Node*)nodeStmt->forStmt);
            break;
        case BREAK_STMT:
            fprintf(output, "BREAKTK break\nSEMICN ;\n");
            break;
        case CONTINUE_STMT:
            fprintf(output, "CONTINUETK continue\nSEMICN ;\n");
            break;
        case RETURN_STMT:
            fprintf(output, "RETURNTK return\n");
            if (nodeStmt->exp) {
                printNodeTree((Node*)nodeStmt->exp);
            }
            fprintf(output, "SEMICN ;\n");
            break;
        case GETINT_STMT:
            printNodeTree((Node*)nodeStmt->lVal);
            fprintf(
                output,
                "ASSIGN =\nGETINTTK getint\nLPARENT (\nRPARENT )\nSEMICN ;\n");
            break;
        case GETCHAR_STMT:
            printNodeTree((Node*)nodeStmt->lVal);
            fprintf(output, "ASSIGN =\nGETCHARTK getchar\nLPARENT (\nRPARENT "
                            ")\nSEMICN ;\n");
            break;
        case PRINTF_STMT:
            fprintf(output, "PRINTFTK printf\nLPARENT (\nSTRCON %s\n",
                    nodeStmt->stringConst->str);
            for (int i = 0; i < nodeStmt->exps->length; i++) {
                fprintf(output, "COMMA ,\n");
                printNodeTree((Node*)nodeStmt->exps->values[i]);
            }
            fprintf(output, "RPARENT )\nSEMICN ;\n");
            break;
        default:
            break;
        }
        fprintf(output, "<Stmt>\n");
        NODECASE(Exp)
        printNodeTree((Node*)nodeExp->addExp);
        fprintf(output, "<Exp>\n");
        NODECASE(AddExp)
        printNodeTree((Node*)nodeAddExp->mulExps->values[0]);
        fprintf(output, "<AddExp>\n");
        for (int i = 1; i < nodeAddExp->mulExps->length; i++) {
            Token* operator=(Token*) nodeAddExp -> operators->values[i - 1];
            fprintf(output, "%s %s\n", tokenString[operator->type], operator->content);
            printNodeTree((Node*)nodeAddExp->mulExps->values[i]);
            fprintf(output, "<AddExp>\n");
        }
        NODECASE(MulExp)
        printNodeTree((Node*)nodeMulExp->unaryExps->values[0]);
        fprintf(output, "<MulExp>\n");
        for (int i = 1; i < nodeMulExp->unaryExps->length; i++) {
            Token* operator=(Token*) nodeMulExp -> operators->values[i - 1];
            fprintf(output, "%s %s\n", tokenString[operator->type], operator->content);
            printNodeTree((Node*)nodeMulExp->unaryExps->values[i]);
            fprintf(output, "<MulExp>\n");
        }
        NODECASE(UnaryExp)
        if (nodeUnaryExp->primaryExp) {
            printNodeTree((Node*)nodeUnaryExp->primaryExp);
        }
        if (nodeUnaryExp->ident) {
            fprintf(output, "IDENFR %s\nLPARENT (\n", nodeUnaryExp->name);
            printNodeTree((Node*)nodeUnaryExp->funcRParams);
            fprintf(output, "RPARENT )\n");
        }
        if (nodeUnaryExp->unaryExp) {
            printNodeTree((Node*)nodeUnaryExp->unaryOp);
            printNodeTree((Node*)nodeUnaryExp->unaryExp);
        }
        fprintf(output, "<UnaryExp>\n");
        NODECASE(FuncRParams)
        if (nodeFuncRParams->exps->length == 0) {
            return;
        }
        for (int i = 0; i < nodeFuncRParams->exps->length; i++) {
            if (i) {
                fprintf(output, "COMMA ,\n");
            }
            printNodeTree((Node*)nodeFuncRParams->exps->values[i]);
        }
        fprintf(output, "<FuncRParams>\n");
        NODECASE(FuncFParam)
        fprintf(output, "%s\nIDENFR %s\n",
                typeToPrint[nodeFuncFParam->typeBType], nodeFuncFParam->name);
        if (nodeFuncFParam->isArray) {
            fprintf(output, "LBRACK [\nRBRACK ]\n");
        }
        fprintf(output, "<FuncFParam>\n");
        NODECASE(PrimaryExp)
        if (nodePrimaryExp->exp) {
            fprintf(output, "LPARENT (\n");
            printNodeTree((Node*)nodePrimaryExp->exp);
            fprintf(output, "RPARENT )\n");
        }
        if (nodePrimaryExp->lVal) {
            printNodeTree((Node*)nodePrimaryExp->lVal);
        }
        if (nodePrimaryExp->number) {
            fprintf(output, "INTCON %d\n<Number>\n",
                    nodePrimaryExp->number->value);
        }
        if (nodePrimaryExp->character) {
            fprintf(output, "CHRCON %s\n<Character>\n",
                    nodePrimaryExp->character->str);
        }
        fprintf(output, "<PrimaryExp>\n");
        NODECASE(Cond)
        printNodeTree((Node*)nodeCond->lOrExp);
        fprintf(output, "<Cond>\n");
        NODECASE(LOrExp)
        printNodeTree((Node*)nodeLOrExp->lAndExps->values[0]);
        fprintf(output, "<LOrExp>\n");
        for (int i = 1; i < nodeLOrExp->lAndExps->length; i++) {
            fprintf(output, "OR ||\n");
            printNodeTree((Node*)nodeLOrExp->lAndExps->values[i]);
            fprintf(output, "<LOrExp>\n");
        }
        NODECASE(LAndExp)
        printNodeTree((Node*)nodeLAndExp->eqExps->values[0]);
        fprintf(output, "<LAndExp>\n");
        for (int i = 1; i < nodeLAndExp->eqExps->length; i++) {
            fprintf(output, "AND &&\n");
            printNodeTree((Node*)nodeLAndExp->eqExps->values[i]);
            fprintf(output, "<LAndExp>\n");
        }
        NODECASE(EqExp)
        printNodeTree((Node*)nodeEqExp->relExps->values[0]);
        fprintf(output, "<EqExp>\n");
        for (int i = 1; i < nodeEqExp->relExps->length; i++) {
            Token* operator=(Token*)(nodeEqExp->operators->values[i - 1]);
            fprintf(output, "%s %s\n", tokenString[operator->type], operator->content);
            printNodeTree((Node*)nodeEqExp->relExps->values[i]);
            fprintf(output, "<EqExp>\n");
        }
        NODECASE(RelExp)
        printNodeTree((Node*)nodeRelExp->addExps->values[0]);
        fprintf(output, "<RelExp>\n");
        for (int i = 1; i < nodeRelExp->addExps->length; i++) {
            Token* operator=(Token*) nodeRelExp -> operators->values[i - 1];
            fprintf(output, "%s %s\n", tokenString[operator->type], operator->content);
            printNodeTree((Node*)nodeRelExp->addExps->values[i]);
            fprintf(output, "<RelExp>\n");
        }
        NODECASE(ForStmt)
        printNodeTree((Node*)nodeForStmt->lVal);
        fprintf(output, "ASSIGN =\n");
        printNodeTree((Node*)nodeForStmt->exp);
        fprintf(output, "<ForStmt>\n");
        NODECASE(UnaryOp)
        fprintf(output, "%s %s\n<UnaryOp>\n",
                tokenString[nodeUnaryOp->unaryOpType],
                nodeUnaryOp->node.token->content);
        NODECASE(StringConst)
        fprintf(output, "STRCON %s\n", nodeStringConst->str);
    default:
        return;
    }
}
