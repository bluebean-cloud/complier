#ifndef _TOKEN
#define _TOKEN
typedef enum TokenType {
    MAINTK,
    CONSTTK,
    INTTK,
    CHARTK,
    BREAKTK,
    CONTINUETK,
    IFTK,
    ELSETK,
    FORTK,
    GETINTTK,
    GETCHARTK,
    PRINTFTK,
    RETURNTK,
    VOIDTK,
    NOT,
    AND,
    OR,
    PLUS,
    MINU,
    MULT,
    DIV,
    MOD,
    LSS,
    LEQ,
    GRE,
    GEQ,
    EQL,
    NEQ,
    ASSIGN,
    SEMICN,
    COMMA,
    LPARENT,
    RPARENT,
    LBRACK,
    RBRACK,
    LBRACE,
    RBRACE,
    INTCON,
    CHRCON,
    STRCON,
    IDENFR,
    KEYWORD,
    END,
} TokenType;

typedef struct Token {
    char *content;
    int value;
    int line;
    TokenType type;
    Token *next;
    Token *pre;
} Token;

#endif
