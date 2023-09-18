//
// Created by Lenovo on 2023/9/12.
//

#include <iostream>
#include "Lexer.h"
#include "../util/Exceptions.h"
#include <cctype>

std::vector<Token> Lexer::tokens = {};
std::string Lexer::content;

const std::unordered_map<std::string, std::string> Lexer::KEY_TYPE_MAP = {
        {"int", "INTTK"},
        {"printf", "PRINTFTK"},
        {"getint", "GETINTTK"},
        {"for", "FORTK"},
        {"const", "CONSTTK"},
        {"main", "MAINTK"},
        {"if", "IFTK"},
        {"else", "ELSETK"},
        {"void", "VOIDTK"},
        {"break", "BREAKTK"},
        {"continue", "CONTINUETK"},
        {"return", "RETURNTK"},
        {"+", "PLUS"},
        {"==", "EQL"},
        {"-", "MINU"},
        {"!=", "NEQ"},
        {"<", "LSS"},
        {"<=", "LEQ"},
        {">", "GRE"},
        {">=", "GEQ"},
        {"=", "ASSIGN"},
        {"/", "DIV"},
        {";", "SEMICN"},
        {"%", "MOD"},
        {",", "COMMA"},
        {"(", "LPARENT"},
        {")", "RPARENT"},
        {"[", "LBRACK"},
        {"]", "RBRACK"},
        {"{", "LBRACE"},
        {"}", "RBRACE"},
        {"||", "OR"},
        {"&&", "AND"},
        {"!", "NOT"},
        {"*", "MULT"},
};

static int position = 0;


Token Lexer::next() {
    std::string buf;
    while (isspace(content[position])) position++;
    for (; position < content.size();) {
        if (isAlpha(content[position])) {
            buf = getWord();
            return {KEY_TYPE_MAP.find(buf) != KEY_TYPE_MAP.end() ? std::string(KEY_TYPE_MAP.at(buf)) : "IDENFR", buf};
        } else if (isNum(content[position])) {
            buf = getNum();
            return {"INTCON", buf};
        } else {
            return getSym();
        }
    }
    return {"ERR", "ERR"};
    throw LexerException("Error at Lexer.next\n");
}

std::string Lexer::getWord() {
    std::string buf;
    while (position < content.size() && (isAlpha(content[position]) || isNum(content[position]) || content[position] == '_'))
        buf += content[position++];
    return buf;
}

std::string Lexer::getNum() {
    std::string buf;
    while (position < content.size() && isNum(content[position]))
        buf += content[position++];
    return buf;
}

Token Lexer::getSym() {
    if (content[position] == '/') {
        position++;
        if (content[position] == '/' || content[position] == '*') { // 注释
            skipComment();
            return next();
        } else {
            return {"DIV", "/"};
        }
    } else {
        position++;
        std::string buf;
        switch (content[position - 1]) {
            case '!':
            case '=':
            case '<':
            case '>':
                buf = content[position - 1];
                if (content[position] == '=') {
                    buf += content[position++];
                    return {std::string(KEY_TYPE_MAP.at(buf)), buf};
                } else {
                    return {std::string(KEY_TYPE_MAP.at(buf)), buf};
                }
            case '|':
            case '&':
                buf = std::string(2, content[position++]);
                return {std::string(KEY_TYPE_MAP.at(buf)), buf};
            case '"':
                return getStr();
            default:
                buf = content[position - 1];
                if (KEY_TYPE_MAP.find(buf) != KEY_TYPE_MAP.end())
                    return {std::string(KEY_TYPE_MAP.at(buf)), buf};
                else {
                    return {"ERR", "ERR"};
                    throw LexerException("Error at Lexer.getSym\n" + buf);
                }
        }
    }
}

void Lexer::skipComment() {
    if (content[position] == '/') {
        while (position < content.size() && content[position] != '\n')
            position++;
    } else {
        position++;
        while (position + 1 < content.size() && !(content[position] == '*' && content[position + 1] == '/'))
            position++;
        position += 2;
    }
}

Token Lexer::getStr() {
    std::string buf = "\"";
    while (content[position] != '"')
        buf += content[position++];
    buf += content[position++];
    return {"STRCON", buf};
}

void Lexer::run() {
    while (position < content.size()) {
        try {
            tokens.push_back(next());
        } catch(const std::out_of_range& e) {
            std::cout << e.what() << std::endl;
            std::cout << content[position - 1] << std::endl;
        }
    }
}

// Fast Inverse Square Root
