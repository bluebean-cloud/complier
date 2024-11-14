#ifndef _NODE
#define _NODE
#include "token.h"
#include "vector.h"

typedef enum NodeType {
    CompUnit,
    Decl,
    FuncDef,
    MainFuncDef,
    BType,
    FuncType,
    UnaryOp,
    Number,
    Character,
    ConstExp,
    ConstDecl,
    VarDecl,
    Ident,
    FuncFParams,
    FuncFParam,
    Block,
    BlockItem,
    ConstDef,
    ConstInitVal,
    VarDef,
    InitVal,
    LVal,
    Stmt,
    Exp,
    AddExp,
    MulExp,
    UnaryExp,
    FuncRParams,
    PrimaryExp,
    Cond,
    LOrExp,
    LAndExp,
    EqExp,
    RelExp,
    StringConst,
} NodeType;

typedef enum StmtType {
    ASSIGN_STMT,
    EXP_STMT,
    BLOCK_STMT,
    IF_STMT,
    FOR_STMT,
    BREAK_STMT,
    CONTINUE_STMT,
    RETURN_STMT,
    GETINT_STMT,
    GETCHAR_STMT,
    PRINTF_STMT
} StmtType;

typedef enum UnaryExpType { PRIMARY, CALL, UNARY } UnaryExpType;

typedef enum TypeBorFuncType { INT, CHAR, VOID } TypeBorFuncType;

typedef struct Node Node;
typedef struct CompUnitNode CompUnitNode;
typedef struct DeclNode DeclNode;
typedef struct FuncDefNode FuncDefNode;
typedef struct MainFuncDefNode MainFuncDefNode;
typedef struct FuncTypeNode FuncTypeNode;
typedef struct ConstDeclNode ConstDeclNode;
typedef struct ConstInitValNode ConstInitValNode;
typedef struct ConstExpNode ConstExpNode;
typedef struct VarDeclNode VarDeclNode;
typedef struct VarDefNode VarDefNode;
typedef struct InitValNode InitValNode;
typedef struct LValNode LValNode;
typedef struct IdentNode IdentNode;
typedef struct FuncFParamsNode FuncFParamsNode;
typedef struct FuncFParamNode FuncFParamNode;
typedef struct BlockNode BlockNode;
typedef struct BlockItemNode BlockItemNode;
typedef struct StmtNode StmtNode;
typedef struct ExpNode ExpNode;
typedef struct AddExpNode AddExpNode;
typedef struct MulExpNode MulExpNode;
typedef struct UnaryExpNode UnaryExpNode;
typedef struct FuncRParamsNode FuncRParamsNode;
typedef struct PrimaryExpNode PrimaryExpNode;
typedef struct CondNode CondNode;
typedef struct LOrExpNode LOrExpNode;
typedef struct LAndExpNode LAndExpNode;
typedef struct EqExpNode EqExpNode;
typedef struct RelExpNode RelExpNode;
typedef struct NumberNode NumberNode;
typedef struct CharacterNode CharacterNode;
typedef struct ForStmtNode ForStmtNode;
typedef struct UnaryOpNode UnaryOpNode;
typedef struct StringConstNode StringConstNode;

/*
    所有子 Node 类型都继承于 Node。
    举例而言，对于 CompUnit* compUnit
    可以通过 ((node*)compUnit)->nodeType 访问父类中的信息
*/
struct Node {
    NodeType nodeType; // type of Node
    Token *token;      // token
    char *word;        // word
    Node *parent;      // parent
};

struct CompUnitNode {
    Node node;
    Vector decls;
    Vector funcDefs;
    MainFuncDefNode *mainFuncDef;
};

struct DeclNode {
    Node node;
    ConstDeclNode *constDecl;
    VarDeclNode *VarDecl;
};

struct FuncDefNode {
    Node node;
    FuncTypeNode *funcType;
    IdentNode *funcName;
    Vector funcFParams;
    BlockNode *block;
};

struct MainFuncDefNode {
    Node node;
    BlockNode *block;
};

struct FuncTypeNode {
    Node node;
    TypeBorFuncType typeFuncType;
};

struct ConstDeclNode {
    Node node;
    TypeBorFuncType typeBType;
    Vector constDefs;
};

struct ConstInitValNode {
    Node node;
    ConstExpNode *constExp;
    Vector constExps;
    StringConstNode *stringConst;
};

struct ConstExpNode {
    Node node;
    AddExpNode *addExp;
};

struct VarDeclNode {
    Node node;
    TypeBorFuncType typeBType;
    Vector varDefs;
};

struct VarDefNode {
    Node node;
    IdentNode *name;
    ConstExpNode *size;
    InitValNode *initVal;
};

struct InitValNode {
    Node node;
    ExpNode *exp;
    Vector exps;
    StringConstNode *stringConst;
};

struct LValNode {
    Node node;
    IdentNode *ident;
    ExpNode *exp;
};

struct IdentNode {
    Node node;
    char *name;
};

struct FuncFParamsNode {
    Node node;
    Vector funcFParams;
};

struct BlockNode {
    Node node;
    Vector blockItems;
};

struct BlockItemNode {
    Node node;
    DeclNode *decl;
    StmtNode *stmt;
};

struct StmtNode {
    Node node;
    StmtType stmtType;
    LValNode *lVal;
    ExpNode *exp;
    BlockNode *block;
    CondNode *cond;
    StmtNode *ifStmt;
    StmtNode *elStmt;
    ForStmtNode *forStmt1;
    ForStmtNode *forStmt2;
    StmtNode *forStmt;
    StringConstNode *stringConst;
    Vector exps;
};

struct ExpNode {
    Node node;
    AddExpNode *addExp;
};

struct AddExpNode {
    Node node;
    NodeType addType;
    Vector mulExps;
};

struct MulExpNode {
    Node node;
    NodeType mulType;
    Vector unaryExps;
};

struct UnaryExpNode {
    Node node;
    UnaryExpType unaryExpType;
    PrimaryExpNode *primaryExp;
    IdentNode *name;
    FuncRParamsNode *funcRParams;
    UnaryOpNode *unaryOp;
    UnaryExpNode *unaryExp;
};

struct FuncRParamsNode {
    Node node;
    Vector exps;
};

struct FuncFParamNode {
    Node node;
};

struct PrimaryExpNode {
    Node node;
};

struct CondNode {
    Node node;
};

struct LOrExpNode {
    Node node;
};

struct LAndExpNode {
    Node node;
};

struct EqExpNode {
    Node node;
};

struct RelExpNode {
    Node node;
};

struct NumberNode {
    Node node;
};

struct CharacterNode {
    Node node;
};

struct ForStmtNode {
    Node node;
};

struct UnaryOpNode {};

struct StringConstNode {};

#endif
