#ifndef _NODE
#define _NODE
#include "token.h"
#include "tool.h"

/*
    语法分析头文件
*/

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
    ForStmt,
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
    // 此处将 [Exp]';' 文法中 Exp 存在与否分为两种情况
    EMPTY_STMT,
    BLOCK_STMT,
    IF_STMT,
    FOR_STMT,
    BREAK_STMT,
    CONTINUE_STMT,
    RETURN_STMT,
    GETINT_STMT,
    GETCHAR_STMT,
    PRINTF_STMT,
} StmtType;

typedef enum PrimaryNodeType {
    EXP_PRIMARY,
    LVAL_PRIMARY,
    NUMBER_PRIMARY,
    CHARACTER_PRIMARY,
} PrimaryNodeType;

typedef enum InitValType { INIT_VAR, INIT_ARR, INIT_STR } InitValType;

typedef enum UnaryExpType { PRIMARY, CALL, UNARY } UnaryExpType;

typedef enum TypeBorFuncType { INT, CHAR, VOID } TypeBorFuncType;

typedef struct Node Node;
typedef struct CompUnitNode CompUnitNode;
typedef struct DeclNode DeclNode;
typedef struct FuncDefNode FuncDefNode;
typedef struct MainFuncDefNode MainFuncDefNode;
typedef struct ConstDeclNode ConstDeclNode;
typedef struct ConstDefNode ConstDefNode;
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
    Token* token;      // token
    Node* parent;      // parent
};

struct CompUnitNode {
    Node node;
    Vector* decls;
    Vector* funcDefs;
    MainFuncDefNode* mainFuncDef;
};

struct DeclNode {
    Node node;
    ConstDeclNode* constDecl;
    VarDeclNode* varDecl;
};

struct FuncDefNode {
    Node node;
    TypeBorFuncType typeFuncType;
    IdentNode* ident;
    FuncFParamsNode* funcFParams;
    BlockNode* block;
    char* funcName;
};

struct MainFuncDefNode {
    Node node;
    BlockNode* block;
};

struct ConstDeclNode {
    Node node;
    TypeBorFuncType typeBType;
    Vector* constDefs;
};

struct ConstDefNode {
    Node node;
    IdentNode* ident;
    // 若不 NULL 则是数组
    ConstExpNode* constExp;
    ConstInitValNode* constInitVal;
    char* name;
};

struct ConstInitValNode {
    Node node;
    InitValType initValType;
    ConstExpNode* constExp;
    Vector* constExps;
    StringConstNode* stringConst;
};

struct ConstExpNode {
    Node node;
    AddExpNode* addExp;
};

struct VarDeclNode {
    Node node;
    TypeBorFuncType typeBType;
    Vector* varDefs;
};

struct VarDefNode {
    Node node;
    IdentNode* ident;
    // 若不 NULL 则是数组
    ConstExpNode* constExp;
    // 不为 NULL 则有初始化
    InitValNode* initVal;
    char* name;
};

struct InitValNode {
    Node node;
    InitValType initValType;
    ExpNode* exp;
    Vector* exps;
    StringConstNode* stringConst;
};

struct LValNode {
    Node node;
    IdentNode* ident;
    ExpNode* exp;
    char* name;
    char* rename;
    int id;
};

struct IdentNode {
    Node node;
    char* value;
};

struct FuncFParamsNode {
    Node node;
    Vector* funcFParams;
};

struct BlockNode {
    Node node;
    Vector* blockItems;
};

struct BlockItemNode {
    Node node;
    DeclNode* decl;
    StmtNode* stmt;
};

struct StmtNode {
    Node node;
    StmtType stmtType;
    LValNode* lVal;
    ExpNode* exp;
    BlockNode* block;
    CondNode* cond;
    StmtNode* ifStmt;
    StmtNode* elStmt;
    ForStmtNode* forStmt1;
    ForStmtNode* forStmt2;
    StmtNode* forStmt;
    StringConstNode* stringConst;
    Vector* exps;
};

struct ExpNode {
    Node node;
    AddExpNode* addExp;
};

struct AddExpNode {
    Node node;
    // 元素类型为 Token*
    Vector* operators;
    Vector* mulExps;
};

struct MulExpNode {
    Node node;
    // 元素类型为 Token*
    Vector* operators;
    Vector* unaryExps;
};

struct UnaryExpNode {
    Node node;
    UnaryExpType unaryExpType;
    PrimaryExpNode* primaryExp;
    IdentNode* ident;
    FuncRParamsNode* funcRParams;
    UnaryOpNode* unaryOp;
    UnaryExpNode* unaryExp;
    char* name;
};

struct FuncRParamsNode {
    Node node;
    Vector* exps;
};

struct FuncFParamNode {
    Node node;
    TypeBorFuncType typeBType;
    IdentNode* ident;
    char* name;
    int isArray;
};

struct PrimaryExpNode {
    Node node;
    PrimaryNodeType primaryType;
    ExpNode* exp;
    LValNode* lVal;
    NumberNode* number;
    CharacterNode* character;
};

struct CondNode {
    Node node;
    LOrExpNode* lOrExp;
};

struct LOrExpNode {
    Node node;
    Vector* lAndExps;
};

struct LAndExpNode {
    Node node;
    Vector* eqExps;
};

struct EqExpNode {
    Node node;
    // 元素类型为 Token*
    Vector* operators;
    Vector* relExps;
};

struct RelExpNode {
    Node node;
    // 元素类型为 Token*
    Vector* operators;
    Vector* addExps;
};

struct NumberNode {
    Node node;
    int value;
};

struct CharacterNode {
    Node node;
    int value;
    char* str;
};

struct ForStmtNode {
    Node node;
    LValNode* lVal;
    ExpNode* exp;
};

struct UnaryOpNode {
    Node node;
    // PLUS | MINU | NOT
    TokenType unaryOpType;
};

struct StringConstNode {
    Node node;
    char* str;
};

#endif
