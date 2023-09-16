//
// Created by Lenovo on 2023/9/15.
//

#ifndef BLUEBEAN_COMPLIER_EXCEPTIONS_H
#define BLUEBEAN_COMPLIER_EXCEPTIONS_H


#include <exception>
#include "string"

class LexerException : public std::exception {
private:
    std::string message;
public:
    LexerException(const std::string& message): message(message) {}
    [[nodiscard]] const char* what() const noexcept override {
        return message.c_str();
    }
};


#endif //BLUEBEAN_COMPLIER_EXCEPTIONS_H
