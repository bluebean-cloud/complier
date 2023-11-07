package parser.syntaxTreeNodes;

public enum SyntaxType {
    // for Decl
    ConstDecl, VarDecl,

    // for BlockItem
    Decl, Stmt,

    // for Stmt
    Assign, Exp, Block, If, For, Break, Continue, Return, GetInt, Printf, Empty,

    // for PrimaryExp
    LVal, Number,   // Exp

    // for UnaryExp
    PrimaryExp, FuncCall, UnaryOp,


}
