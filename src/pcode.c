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

extern CompUnitNode* nodesRoot;
extern int errOccur;
extern int RUNNER_TYPE;

void pCodeRun() {
    pCodeVisit(nodesRoot);
    if (errOccur || RUNNER_TYPE != 1) {
        return;
    }
    pCodeInit();
    pCodeTrans();
    while (execPCode())
        ;
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
    return scope;
}

P_FUNC* newPFunc(TypeBorFuncType type, char* funcName) {
    P_FUNC* func = (P_FUNC*)malloc(sizeof(P_FUNC));
    func->funcType = type;
    func->funcName = funcName;
    func->params = createVector();
    func->vars = createVector();
    func->scope = newPScope(pGlobalScope);
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
                pCodeVisit((Node*)nodeCompUnit->decls->values[i]);
            }
            pCodeVisit((Node*)nodeCompUnit->mainFuncDef);
        }
        NODECASE(Decl) {
            pCodeVisit((Node*)nodeDecl->constDecl);
            pCodeVisit((Node*)nodeDecl->varDecl);
        }
        NODECASE(FuncDef) {
            curFunc = newPFunc(nodeFuncDef->typeFuncType, nodeFuncDef->ident->value);
            pushVector(pGlobalScope->funcs, curFunc);
            pCodeVisit((Node*)nodeFuncDef->funcFParams);
            pCodeVisit((Node*)nodeFuncDef->block);
        }
        NODECASE(MainFuncDef) {
            mainFunc = newPFunc(INT, "main");
            pushVector(pGlobalScope->funcs, mainFunc);
            pCodeVisit((Node*)nodeMainFuncDef);
        }
        NODECASE(ConstDecl) {
            curType = nodeConstDecl->typeBType;
            for (int i = 0; i < nodeConstDecl->constDefs->length; i++) {
                pCodeVisit((Node*)nodeConstDecl->constDefs->values[i]);
            }
        }
        NODECASE(ConstDef) {
            curVar = (P_VAR*)malloc(sizeof(P_VAR));
            curVar->varType = curType;
            curVar->isArr = nodeConstDef->constExp != NULL;
            curVar->isCon = 1;
            curVar->name = nodeConstDef->name;
            curVar->rename = (char*)malloc(sizeof(char) * (strlen(curVar->name) + 6));
            if (curFunc) {
                sprintf(curVar->rename, "%s%%%d", curVar->name, curFunc->varCnt);
                curFunc->varCnt++;
                pushVector(curFunc->vars, curVar);
                insertTrie(curScope->trieRoot, curVar->name, curVar);
            } else {
                sprintf(curVar->rename, "%s%%%d", curVar->name, globalVarCnt);
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
            curVar = (P_VAR*)malloc(sizeof(P_VAR));
            curVar->varType = curType;
            curVar->isArr = nodeVarDef->constExp != NULL;
            curVar->isCon = 0;
            curVar->name = nodeVarDef->name;
            curVar->rename = (char*)malloc(sizeof(char) * (strlen(curVar->name) + 6));
            if (curFunc) {
                sprintf(curVar->rename, "%s%%%d", curVar->name, curFunc->varCnt);
                curFunc->varCnt++;
                pushVector(curFunc->vars, curVar);
                insertTrie(curScope->trieRoot, curVar->name, curVar);
            } else {
                sprintf(curVar->rename, "%s%%%d", curVar->name, globalVarCnt);
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

        }
        NODECASE(Ident)
        NODECASE(FuncFParams)
        NODECASE(FuncFParam)
        NODECASE(Block)
        NODECASE(BlockItem)
        NODECASE(Stmt)
        NODECASE(Exp)
        NODECASE(AddExp)
        NODECASE(MulExp)
        NODECASE(UnaryOp)
        NODECASE(UnaryExp)
        NODECASE(FuncRParams)
        NODECASE(PrimaryExp)
        NODECASE(Cond)
        NODECASE(LOrExp)
        NODECASE(LAndExp)
        NODECASE(EqExp)
        NODECASE(RelExp)
        NODECASE(Number)
        NODECASE(Character)
        NODECASE(ForStmt)
        NODECASE(StringConst)
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
        if (data = getTrieData(tmpScope->trieRoot, name)) {
            return data;
        }
        tmpScope = tmpScope->parent;
    }
    return NULL;
}
