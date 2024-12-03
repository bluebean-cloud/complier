#include "..\include\compiler.h"

int errOccur = 0;
Vector* errList = NULL;
extern FILE* errOut;
extern FILE* output;

int _cmp(const void* pa, const void* pb) {
    ErrItem* a = *(ErrItem**)pa;
    ErrItem* b = *(ErrItem**)pb;
    return a->line - b->line;
}

void errInit() {
    errList = createVector();
}

void addError(int line, char type) {
    if (errList == NULL) {
        errInit();
    }
    errOccur = 1;
    ErrItem* item = (ErrItem*)malloc(sizeof(ErrItem));
    item->line = line;
    item->type = type;
    pushVector(errList, item);
}

void printErr() {
    qsort(errList->values, errList->length, sizeof(ErrItem*), _cmp);
    for (int i = 0; i < errList->length; i++) {
        fprintf(errOut, "%d %c\n", ((ErrItem*)errList->values[i])->line,
                ((ErrItem*)errList->values[i])->type);
        fprintf(output, "%d %c\n", ((ErrItem*)errList->values[i])->line,
                ((ErrItem*)errList->values[i])->type);
    }
}

extern CompUnitNode* nodesRoot;
/*
void errDetect(Node* node) {
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
*/