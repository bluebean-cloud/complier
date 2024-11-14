#ifndef _TOKEN
#define _TOKEN
#include <stdio.h>

typedef struct Token Token;
typedef enum TokenType {
    MAINTK,     // main
    CONSTTK,    // const
    INTTK,      // int
    CHARTK,     // char
    BREAKTK,    // break
    CONTINUETK, // continue
    IFTK,       // if
    ELSETK,     // else
    FORTK,      // for
    GETINTTK,   // getint
    GETCHARTK,  // getchar
    PRINTFTK,   // printf
    RETURNTK,   // return
    VOIDTK,     // void
    NOT,        // !
    AND,        // &&
    OR,         // ||
    PLUS,       // +
    MINU,       // -
    MULT,       // *
    DIV,        // /
    MOD,        // %
    LSS,        // <
    LEQ,        // <=
    GRE,        // >
    GEQ,        // >=
    EQL,        // ==
    NEQ,        // !=
    ASSIGN,     // =
    SEMICN,     // ;
    COMMA,      // ,
    LPARENT,    // (
    RPARENT,    // )
    LBRACK,     // [
    RBRACK,     // ]
    LBRACE,     // {
    RBRACE,     // }
    INTCON,     
    CHRCON,
    STRCON,
    IDENFR,
    KEYWORD,
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
