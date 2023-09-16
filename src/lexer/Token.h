//
// Created by Lenovo on 2023/9/14.
//

#ifndef BLUEBEAN_COMPLIER_TOKEN_H
#define BLUEBEAN_COMPLIER_TOKEN_H

#include <utility>

#include "string"

class Token {
public:
    std::string type;
    std::string value;

    Token(std::string type, std::string value): type(std::move(type)), value(std::move(value)) {}
    [[nodiscard]] std::string to_string() const {
        return type + " " + value;
    }
};

#endif //BLUEBEAN_COMPLIER_TOKEN_H
