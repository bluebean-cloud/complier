package frontend.parser;

public enum Type {  // 类型
    MAIN_FUNC_DEF, FUNC_DEF,
    DECL, STMT,

    // stmt
    BLOCK_STMT, IF_STMT, FOR_STMT, WHILE_STMT, BREAK_STMT, CONTINUE_STMT, RETURN_STMT, PRINTF_STMT, GETINT_STMT, ASSIGN_STMT, EXP_STMT, EMPTY_STMT,
    VAR,
    FUNC_CALL, PRIMARY_EXP, OP_EXP,
    // primary exp type
    WITH_BRACKET, INTCON, IDENFR,

    // data type
    INT, VOID, ARRAY;

}
