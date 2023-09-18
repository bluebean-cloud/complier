//
// Created by Lenovo on 2023/9/12.
//

#ifndef BLUEBEAN_COMPLIER_LEXER_H
#define BLUEBEAN_COMPLIER_LEXER_H

#include "unordered_map"
#include "string"
#include "vector"

#include "Token.h"

class Lexer {
public:
    Lexer() = default;
    static void init(const std::string& input) {
        Lexer::content = input;
    }
    static const std::unordered_map<std::string, std::string> KEY_TYPE_MAP;

    static std::string content;
    static Token next();
    static std::string getWord();
    static std::string getNum();
    static Token getSym();
    static Token getStr();
    static void skipComment();
    static void run();
    static std::vector<Token> tokens;
};

inline bool isUpper(char ch) {
    return (ch >= 'A' && ch <= 'Z');
}

inline bool isLower(char ch) {
    return (ch >= 'a' && ch <= 'z');
}

inline bool isAlpha(char ch) {
    return (isUpper(ch) || isLower(ch));
}

inline bool isNum(char ch) {
    return (ch >= '0' && ch <= '9');
}

inline bool isSym(char ch) {
    std::string_view str = "!*/+-=<>(){}[];|&\"\\";
    return str.find(ch);
}

#endif //BLUEBEAN_COMPLIER_LEXER_H
