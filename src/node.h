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
typedef struct CompUnit CompUnit;
typedef struct Decl Decl;
typedef struct FuncDef FuncDef;
typedef struct MainFuncDef MainFuncDef;
typedef struct FuncType FuncType;
typedef struct ConstDecl ConstDecl;
typedef struct ConstInitVal ConstInitVal;
typedef struct ConstExp ConstExp;
typedef struct VarDecl VarDecl;
typedef struct VarDef VarDef;
typedef struct InitVal InitVal;
typedef struct LVal LVal;
typedef struct Ident Ident;
typedef struct FuncFParams FuncFParams;
typedef struct FuncFParam FuncFParam;
typedef struct Block Block;
typedef struct BlockItem BlockItem;
typedef struct Stmt Stmt;
typedef struct Exp Exp;
typedef struct AddExp AddExp;
typedef struct MulExp MulExp;
typedef struct UnaryExp UnaryExp;
typedef struct FuncRParams FuncRParams;
typedef struct PrimaryExp PrimaryExp;
typedef struct Cond Cond;
typedef struct LOrExp LOrExp;
typedef struct LAndExp LAndExp;
typedef struct EqExp EqExp;
typedef struct RelExp RelExp;
typedef struct Number Number;
typedef struct Character Character;
typedef struct ForStmt ForStmt;
typedef struct UnaryOp UnaryOp;
typedef struct StringConst StringConst;

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

struct CompUnit {
    Node node;
    Vector decls;
    Vector funcDefs;
    MainFuncDef *mainFuncDef;
};

struct Decl {
    Node node;
    ConstDecl *constDecl;
    VarDecl *VarDecl;
};

struct FuncDef {
    Node node;
    FuncType *funcType;
    Ident *funcName;
    Vector funcFParams;
    Block *block;
};

struct MainFuncDef {
    Node node;
    Block *block;
};

struct FuncType {
    Node node;
    TypeBorFuncType typeFuncType;
};

struct ConstDecl {
    Node node;
    TypeBorFuncType typeBType;
    Vector constDefs;
};

struct ConstInitVal {
    Node node;
    ConstExp *constExp;
    Vector constExps;
    StringConst *stringConst;
};

struct ConstExp {
    Node node;
    AddExp *addExp;
};

struct VarDecl {
    Node node;
    TypeBorFuncType typeBType;
    Vector varDefs;
};

struct VarDef {
    Node node;
    Ident *name;
    ConstExp *size;
    InitVal *initVal;
};

struct InitVal {
    Node node;
    Exp *exp;
    Vector exps;
    StringConst *stringConst;
};

struct LVal {
    Node node;
    Ident *ident;
    Exp *exp;
};

struct Ident {
    Node node;
    char *name;
};

struct FuncFParams {
    Node node;
    Vector funcFParams;
};

struct Block {
    Node node;
    Vector blockItems;
};

struct BlockItem {
    Node node;
    Decl *decl;
    Stmt *stmt;
};

struct Stmt {
    Node node;
    StmtType stmtType;
    LVal *lVal;
    Exp *exp;
    Block *block;
    Cond *cond;
    Stmt *ifStmt;
    Stmt *elStmt;
    ForStmt *forStmt1;
    ForStmt *forStmt2;
    Stmt *forStmt;
    StringConst *stringConst;
    Vector exps;
};

struct Exp {
    Node node;
    AddExp *addExp;
};

struct AddExp {
    Node node;
    NodeType addType;
    Vector mulExps;
};

struct MulExp {
    Node node;
    NodeType mulType;
    Vector unaryExps;
};

struct UnaryExp {
    Node node;
    UnaryExpType unaryExpType;
    PrimaryExp *primaryExp;
    Ident *name;
    FuncRParams *funcRParams;
    UnaryOp *unaryOp;
    UnaryExp *unaryExp;
};

struct FuncRParams {
    Node node;
    Vector exps;
};

struct FuncFParam {
    Node node;
    
};

struct PrimaryExp {
    Node node;
};

struct Cond {
    Node node;
};

struct LOrExp {
    Node node;
};

struct LAndExp {
    Node node;
};

struct EqExp {
    Node node;
};

struct RelExp {
    Node node;
};

struct Number {
    Node node;
};

struct Character {
    Node node;
};

struct ForStmt {
    Node node;
};

struct UnaryOp {};

struct StringConst {};

#endif
