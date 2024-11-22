#include "../include/compiler.h"

int PC;
int SP;
int MP;
int* baseStack;

Vector* instructions;
P_SCOPE* pGlobalScope;

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
}

P_SCOPE* newPScope(P_SCOPE* parent) {
    P_SCOPE* scope = (P_SCOPE*)malloc(sizeof(P_SCOPE));
    scope->parent = parent;
    scope->funcs = createVector();
    scope->sons = createVector();
    scope->vars = createVector();
    return scope;
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
        NODECASE(CompUnit)

        NODECASE(Decl)
        NODECASE(FuncDef)
        NODECASE(MainFuncDef)
        NODECASE(ConstDecl)
        NODECASE(ConstDef)
        NODECASE(ConstInitVal)
        NODECASE(ConstExp)
        NODECASE(VarDecl)
        NODECASE(VarDef)
        NODECASE(InitVal)
        NODECASE(LVal)
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
