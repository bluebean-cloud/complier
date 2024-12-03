#include "../include/compiler.h"

int PC;
int SP;
int MP;
int* baseStack;

Vector* instructions;
P_SCOPE* pGlobalScope;
int globalVarCnt = 0;
P_SCOPE* curScope;
P_FUNC* mainFunc;
P_FUNC* curFunc = NULL;
P_VAR* curVar = NULL;
TypeBorFuncType curType;
int cycleCnt = 0;

extern CompUnitNode* nodesRoot;
extern COMPILER_TYPE RUNNER_TYPE;
extern int errOccur;

void pCodeRun() {
    pCodeInit();
    pCodeVisit((Node*)nodesRoot);
    if (errOccur || RUNNER_TYPE != PCODE) {
        goto pCodeEnd;
    }
    pCodeTrans();
    while (execPCode())
        ;
pCodeEnd:
    free(baseStack);
}

void pCodeInit() {
    instructions = createVector();
    baseStack = (int*)malloc(sizeof(int) * STACK_SIZE);
    pGlobalScope = newPScope(NULL);
    curScope = pGlobalScope;
}

P_SCOPE* newPScope(P_SCOPE* parent) {
    P_SCOPE* scope = (P_SCOPE*)malloc(sizeof(P_SCOPE));
    scope->parent = parent;
    scope->funcs = createVector();
    scope->sons = createVector();
    scope->trieRoot = createTrie();
    scope->vars = createVector();
    scope->isCycle = cycleCnt;
    return scope;
}

P_FUNC* newPFunc(TypeBorFuncType type, char* funcName) {
    P_FUNC* func = (P_FUNC*)malloc(sizeof(P_FUNC));
    func->funcType = type;
    func->funcName = funcName;
    func->params = createVector();
    func->vars = createVector();
    func->scope = newPScope(pGlobalScope);
    func->scope->blockItems = createVector();
    func->varCnt = 0;
    return func;
}

// 返回值为 0 代表程序结束
int execPCode() {
    P_INS* ins = (P_INS*)instructions->values[PC];
    switch (ins->insType) {

    default:
        break;
    }

    return 1;
}

void pCodeVisit(Node* node) {
    if (node == NULL) {
        return;
    }
    switch (node->nodeType) {
        NODECASE(CompUnit) {
            for (int i = 0; i < nodeCompUnit->decls->length; i++) {
                pCodeVisit((Node*)nodeCompUnit->decls->values[i]);
            }
            for (int i = 0; i < nodeCompUnit->funcDefs->length; i++) {
                pCodeVisit((Node*)nodeCompUnit->funcDefs->values[i]);
            }
            pCodeVisit((Node*)nodeCompUnit->mainFuncDef);
        }
        NODECASE(Decl) {
            pCodeVisit((Node*)nodeDecl->constDecl);
            pCodeVisit((Node*)nodeDecl->varDecl);
        }
        NODECASE(FuncDef) {
            if (checkFuncName(nodeFuncDef->funcName) == 0) {
                addError(nodeFuncDef->ident->node.token->line, 'b');
            }
            curFunc = newPFunc(nodeFuncDef->typeFuncType, nodeFuncDef->ident->value);
            curScope = curFunc->scope;
            pushVector(pGlobalScope->funcs, curFunc);
            pCodeVisit((Node*)nodeFuncDef->funcFParams);
            pCodeVisit((Node*)nodeFuncDef->block);
            curScope = curScope->parent;
        }
        NODECASE(MainFuncDef) {
            curFunc = mainFunc = newPFunc(INT, "main");
            curScope = mainFunc->scope;
            pushVector(pGlobalScope->funcs, mainFunc);
            pCodeVisit((Node*)nodeMainFuncDef->block);
            curScope = curScope->parent;
        }
        NODECASE(ConstDecl) {
            curType = nodeConstDecl->typeBType;
            for (int i = 0; i < nodeConstDecl->constDefs->length; i++) {
                pCodeVisit((Node*)nodeConstDecl->constDefs->values[i]);
            }
        }
        NODECASE(ConstDef) {
            if (getTrieData(curScope->trieRoot, nodeConstDef->name) != NULL) {
                addError(nodeConstDef->ident->node.token->line, 'b');
                break;
            }
            curVar = (P_VAR*)malloc(sizeof(P_VAR));
            curVar->varType = curType;
            curVar->isArr = nodeConstDef->constExp != NULL;
            curVar->isCon = 1;
            curVar->name = nodeConstDef->name;
            curVar->rename = (char*)malloc(sizeof(char) * (strlen(curVar->name) + 6));
            if (curFunc) {
                sprintf(curVar->rename, "%s%%%d", curVar->name, curFunc->varCnt);
                curVar->id = curFunc->varCnt;
                curFunc->varCnt++;
                pushVector(curFunc->vars, curVar);
                insertTrie(curScope->trieRoot, curVar->name, curVar);
            } else {
                sprintf(curVar->rename, "%s%%%d", curVar->name, globalVarCnt);
                curVar->id = globalVarCnt;
                globalVarCnt++;
                pushVector(pGlobalScope->vars, curVar);
                insertTrie(pGlobalScope->trieRoot, curVar->name, curVar);
            }
            pCodeVisit((Node*)nodeConstDef->constExp);
            pCodeVisit((Node*)nodeConstDef->constInitVal);
        }
        NODECASE(ConstInitVal) {
            switch (nodeConstInitVal->initValType) {
            case INIT_VAR:
                pCodeVisit((Node*)nodeConstInitVal->constExp);
                break;
            case INIT_ARR:
                for (int i = 0; i < nodeConstInitVal->constExps->length; i++) {
                    pCodeVisit((Node*)nodeConstInitVal->constExps->values[i]);
                }
                break;
            case INIT_STR:
                break;
            }
        }
        NODECASE(ConstExp) {
            pCodeVisit((Node*)nodeConstExp->addExp);
        }
        NODECASE(VarDecl) {
            curType = nodeVarDecl->typeBType;
            for (int i = 0; i < nodeVarDecl->varDefs->length; i++) {
                pCodeVisit((Node*)nodeVarDecl->varDefs->values[i]);
            }
        }
        NODECASE(VarDef) {
            if (getTrieData(curScope->trieRoot, nodeVarDef->name) != NULL) {
                addError(nodeVarDef->ident->node.token->line, 'b');
                break;
            }
            curVar = (P_VAR*)malloc(sizeof(P_VAR));
            curVar->varType = curType;
            curVar->isArr = nodeVarDef->constExp != NULL;
            curVar->isCon = 0;
            curVar->name = nodeVarDef->name;
            curVar->rename = (char*)malloc(sizeof(char) * (strlen(curVar->name) + 6));
            if (curFunc) {
                sprintf(curVar->rename, "%s%%%d", curVar->name, curFunc->varCnt);
                curVar->id = curFunc->varCnt;
                curFunc->varCnt++;
                pushVector(curFunc->vars, curVar);
                insertTrie(curScope->trieRoot, curVar->name, curVar);
            } else {
                sprintf(curVar->rename, "%s%%%d", curVar->name, globalVarCnt);
                curVar->id = globalVarCnt;
                globalVarCnt++;
                pushVector(pGlobalScope->vars, curVar);
                insertTrie(pGlobalScope->trieRoot, curVar->name, curVar);
            }
            pCodeVisit((Node*)nodeVarDef->constExp);
            pCodeVisit((Node*)nodeVarDef->initVal);
        }
        NODECASE(InitVal) {
            switch (nodeInitVal->initValType) {
            case INIT_VAR:
                pCodeVisit((Node*)nodeInitVal->exp);
                break;
            case INIT_ARR:
                for (int i = 0; i < nodeInitVal->exps->length; i++) {
                    pCodeVisit((Node*)nodeInitVal->exps->values[i]);
                }
                break;
            case INIT_STR:
                break;
            }
        }
        NODECASE(LVal) {
            P_VAR* tVar = findPVar(nodeLVal->name);
            if (tVar == NULL) {
                addError(nodeLVal->ident->node.token->line, 'c');
                break;
            }
            nodeLVal->rename = tVar->rename;
            nodeLVal->id = tVar->id;
        }
        // NODECASE(Ident)
        NODECASE(FuncFParams) {
            for (int i = 0; i < nodeFuncFParams->funcFParams->length; i++) {
                pCodeVisit((Node*)nodeFuncFParams->funcFParams->values[i]);
            }
        }
        NODECASE(FuncFParam) {
            curVar = (P_VAR*)malloc(sizeof(P_VAR));
            curVar->varType = nodeFuncFParam->typeBType;
            curVar->isArr = nodeFuncFParam->isArray;
            curVar->isCon = 0;
            curVar->name = nodeFuncFParam->name;
            if (checkFuncFParam(curVar->name) == 0) {
                addError(nodeFuncFParam->ident->node.token->line, 'b');
                break;
            }
            curVar->rename = (char*)malloc(sizeof(char) * (strlen(curVar->name) + 6));
            sprintf(curVar->rename, "%s%%%d", curVar->name, curFunc->varCnt);
            curVar->id = curFunc->varCnt;
            curFunc->varCnt++;
            pushVector(curFunc->params, curVar);
            insertTrie(curScope->trieRoot, curVar->name, curVar);
        }
        NODECASE(Block) {
            curScope = newPScope(curScope);
            curScope->blockItems = nodeBlock->blockItems;
            for (int i = 0; i < nodeBlock->blockItems->length; i++) {
                pCodeVisit((Node*)nodeBlock->blockItems->values[i]);
            }
            if (curScope->parent == curFunc->scope && curFunc->funcType != VOID) {
                if (nodeBlock->blockItems->length == 0) {
                    addError(nodeBlock->endLine, 'g');
                } else {
                    BlockItemNode* lastItem =
                        (BlockItemNode*)nodeBlock->blockItems->values[nodeBlock->blockItems->length - 1];
                    if (lastItem->stmt == NULL || lastItem->stmt->stmtType != RETURN_STMT) {
                        addError(nodeBlock->endLine, 'g');
                    }
                }
            }
            curScope = curScope->parent;
        }
        NODECASE(BlockItem) {
            pCodeVisit((Node*)nodeBlockItem->decl);
            pCodeVisit((Node*)nodeBlockItem->stmt);
        }
        NODECASE(Stmt) {
            P_VAR* tmpVar = NULL;
            if (nodeStmt->lVal) {
                tmpVar = findPVar(nodeStmt->lVal->name);
            }
            switch (nodeStmt->stmtType) {
            case EXP_STMT:
                pCodeVisit((Node*)nodeStmt->exp);
                break;
            case ASSIGN_STMT:
                pCodeVisit((Node*)nodeStmt->lVal);
                if (tmpVar == NULL) {
                    break;
                }
                if (tmpVar->isCon) {
                    addError(nodeStmt->line, 'h');
                    break;
                }
                pCodeVisit((Node*)nodeStmt->exp);
                break;
            case EMPTY_STMT:
                break;
            case BLOCK_STMT:
                pCodeVisit((Node*)nodeStmt->block);
                break;
            case IF_STMT:
                pCodeVisit((Node*)nodeStmt->cond);
                pCodeVisit((Node*)nodeStmt->ifStmt);
                pCodeVisit((Node*)nodeStmt->elStmt);
                break;
            case FOR_STMT:
                pCodeVisit((Node*)nodeStmt->forStmt1);
                pCodeVisit((Node*)nodeStmt->cond);
                pCodeVisit((Node*)nodeStmt->forStmt2);
                cycleCnt++;
                pCodeVisit((Node*)nodeStmt->forStmt);
                cycleCnt--;
                break;
            case BREAK_STMT:
            case CONTINUE_STMT:
                if (cycleCnt == 0) {
                    addError(nodeStmt->line, 'm');
                }
                break;
            case RETURN_STMT:
                if (curFunc->funcType == VOID && nodeStmt->exp != NULL) {
                    addError(nodeStmt->line, 'f');
                }
                pCodeVisit((Node*)nodeStmt->exp);
                break;
            case GETINT_STMT:
                pCodeVisit((Node*)nodeStmt->lVal);
                if (tmpVar == NULL) {
                    break;
                }
                if (tmpVar->isCon) {
                    addError(nodeStmt->line, 'h');
                    break;
                }
                break;
            case GETCHAR_STMT:
                pCodeVisit((Node*)nodeStmt->lVal);
                if (tmpVar == NULL) {
                    break;
                }
                if (tmpVar->isCon) {
                    addError(nodeStmt->line, 'h');
                    break;
                }
                break;
            case PRINTF_STMT:
                for (int i = 0; i < nodeStmt->exps->length; i++) {
                    pCodeVisit((Node*)nodeStmt->exps->values[i]);
                }
                if (getFormatNumber(nodeStmt->stringConst->str) != nodeStmt->exps->length) {
                    addError(nodeStmt->line, 'l');
                    break;
                }
                break;
            }
        }
        NODECASE(Exp) {
            pCodeVisit((Node*)nodeExp->addExp);
        }
        NODECASE(AddExp) {
            for (int i = 0; i < nodeAddExp->mulExps->length; i++) {
                pCodeVisit((Node*)nodeAddExp->mulExps->values[i]);
            }
        }
        NODECASE(MulExp) {
            for (int i = 0; i < nodeMulExp->unaryExps->length; i++) {
                pCodeVisit((Node*)nodeMulExp->unaryExps->values[i]);
            }
        }
        // NODECASE(UnaryOp)
        NODECASE(UnaryExp) {
            P_FUNC* tmpFunc;
            switch (nodeUnaryExp->unaryExpType) {
            case PRIMARY:
                pCodeVisit((Node*)nodeUnaryExp->primaryExp);
                break;
            case CALL:
                if ((tmpFunc = getFunc(nodeUnaryExp->name)) == NULL) {
                    addError(nodeUnaryExp->ident->node.token->line, 'c');
                    break;
                }
                if (tmpFunc->params->length != nodeUnaryExp->funcRParams->exps->length) {
                    addError(nodeUnaryExp->ident->node.token->line, 'd');
                    break;
                }
                for (int i = 0; i < tmpFunc->params->length; i++) {
                    P_VAR* tmpVar = NULL;
                    int isArr = expIsArr((ExpNode*)nodeUnaryExp->funcRParams->exps->values[i], tmpVar);
                    if (tmpVar == NULL) {
                        break;
                    }
                    if (((P_VAR*)tmpFunc->params->values[i])->isArr != isArr) {
                        addError(nodeUnaryExp->ident->node.token->line, 'e');
                        break;
                    }
                    if (((P_VAR*)tmpFunc->params->values[i])->varType != tmpVar->varType) {
                        addError(nodeUnaryExp->ident->node.token->line, 'e');
                        break;
                    }
                }
                pCodeVisit((Node*)nodeUnaryExp->funcRParams);
                break;
            case UNARY:
                pCodeVisit((Node*)nodeUnaryExp->unaryExp);
                break;
            }
        }
        NODECASE(FuncRParams) {
            for (int i = 0; i < nodeFuncRParams->exps->length; i++) {
                pCodeVisit((Node*)nodeFuncRParams->exps->values[i]);
            }
        }
        NODECASE(PrimaryExp) {
            switch (nodePrimaryExp->primaryType) {
            case EXP_PRIMARY:
                pCodeVisit((Node*)nodePrimaryExp->exp);
                break;
            case LVAL_PRIMARY:
                pCodeVisit((Node*)nodePrimaryExp->lVal);
                break;
            case NUMBER_PRIMARY:
                break;
            case CHARACTER_PRIMARY:
                break;
            }
        }
        NODECASE(Cond) {
            pCodeVisit((Node*)nodeCond->lOrExp);
        }
        NODECASE(LOrExp) {
            for (int i = 0; i < nodeLOrExp->lAndExps->length; i++) {
                pCodeVisit((Node*)nodeLOrExp->lAndExps->values[i]);
            }
        }
        NODECASE(LAndExp) {
            for (int i = 0; i < nodeLAndExp->eqExps->length; i++) {
                pCodeVisit((Node*)nodeLAndExp->eqExps->values[i]);
            }
        }
        NODECASE(EqExp) {
            for (int i = 0; i < nodeEqExp->relExps->length; i++) {
                pCodeVisit((Node*)nodeEqExp->relExps->values[i]);
            }
        }
        NODECASE(RelExp) {
            for (int i = 0; i < nodeRelExp->addExps->length; i++) {
                pCodeVisit((Node*)nodeRelExp->addExps->values[i]);
            }
        }
        // NODECASE(Number)
        // NODECASE(Character)
        NODECASE(ForStmt) {
            pCodeVisit((Node*)nodeForStmt->lVal);
            pCodeVisit((Node*)nodeForStmt->exp);
        }
        // NODECASE(StringConst)
    default:
        break;
    }
}

void pCodeTrans() {
}

P_VAR* findPVar(char* name) {
    P_SCOPE* tmpScope = curScope;
    P_VAR* data;
    while (tmpScope != NULL) {
        if ((data = getTrieData(tmpScope->trieRoot, name)) != NULL) {
            return data;
        }
        tmpScope = tmpScope->parent;
    }
    return NULL;
}

// 为 1 代表合法
int checkFuncName(char* name) {
    for (int i = 0; i < pGlobalScope->funcs->length; i++) {
        if (strcmp(name, ((P_FUNC*)pGlobalScope->funcs->values[i])->funcName) == 0) {
            return 0;
        }
    }
    if (getTrieData(pGlobalScope->trieRoot, name) != NULL) {
        return 0;
    }
    return 1;
}

// 为 1 代表合法
int checkFuncFParam(char* name) {
    for (int i = 0; i < curFunc->params->length; i++) {
        if (strcmp(name, ((P_VAR*)curFunc->params->values[i])->name) == 0) {
            return 0;
        }
    }
    return 1;
}

P_FUNC* getFunc(char* name) {
    for (int i = 0; i < pGlobalScope->funcs->length; i++) {
        if (strcmp(name, ((P_FUNC*)pGlobalScope->funcs->values[i])->funcName) == 0) {
            return (P_FUNC*)pGlobalScope->funcs->values[i];
        }
    }
    return NULL;
}

// 获取格式化字符串中的格式字符数目
int getFormatNumber(char* str) {
    int ans = 0;
    int len = strlen(str);
    for (int i = 0; i < len; i++) {
        if (str[i] == '%' && strchr("dcs", str[i + 1]) != NULL) {
            ans++;
        }
    }
    return ans;
}

// 判断一个 exp 是否为数组，参数中 tmpVar 为找到的 tmpVar
int expIsArr(ExpNode* node, P_VAR* tmpVar) {
    if (node->addExp->mulExps->length != 1) {
        return 0;
    }
    MulExpNode* mulNode = (MulExpNode*)node->addExp->mulExps->values[0];
    if (mulNode->unaryExps->length != 1) {
        return 0;
    }
    UnaryExpNode* unaryNode = (UnaryExpNode*)mulNode->unaryExps->values[0];
    if (unaryNode->unaryExpType != PRIMARY) {
        return 0;
    }
    PrimaryExpNode* primaryNode = unaryNode->primaryExp;
    if (primaryNode->primaryType == NUMBER_PRIMARY || primaryNode->primaryType == CHARACTER_PRIMARY) {
        return 0;
    }
    if (primaryNode->primaryType == EXP_PRIMARY) {
        return expIsArr(primaryNode->exp, tmpVar);
    }
    tmpVar = findPVar(primaryNode->lVal->name);
    return tmpVar->isArr && primaryNode->lVal->exp == NULL;
}
