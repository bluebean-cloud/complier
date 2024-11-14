#ifndef _TOKEN
#define _TOKEN
#include <stdio.h>

/*
    词法分析头文件
*/

typedef struct Token Token;
typedef enum TokenType {
    // main
    MAINTK,
    // const
    CONSTTK,
    // int
    INTTK,
    // char
    CHARTK,
    // break
    BREAKTK,
    // continue
    CONTINUETK,
    // if
    IFTK,
    // else
    ELSETK,
    // for
    FORTK,
    // getint
    GETINTTK,
    // getchar
    GETCHARTK,
    // printf
    PRINTFTK,
    // return
    RETURNTK,
    // void
    VOIDTK,
    // !
    NOT,
    // &&
    AND,
    // ||
    OR,
    // +
    PLUS,
    // -
    MINU,
    // *
    MULT,
    // /
    DIV,
    // %
    MOD,
    // <
    LSS,
    // <=
    LEQ,
    // >
    GRE,
    // >=
    GEQ,
    // ==
    EQL,
    // !=
    NEQ,
    // =
    ASSIGN,
    // ;
    SEMICN,
    // ,
    COMMA,
    // (
    LPARENT,
    // )
    RPARENT,
    // [
    LBRACK,
    // ]
    RBRACK,
    // {
    LBRACE,
    // }
    RBRACE,
    // int const value
    INTCON,
    // char const value
    CHRCON,
    // string const value
    STRCON,
    // idenfr
    IDENFR,
    END,
} TokenType;

struct Token {
    char *content;
    int value;
    int line;
    TokenType type;
    Token *next;
    Token *pre;
};

#endif
