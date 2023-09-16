//
// Created by Lenovo on 2023/9/12.
//
#include <sstream>
#include "iostream"
#include "string"
#include "fstream"
#include "lexer/Lexer.h"
#include <filesystem>

int main() {
    std::ifstream file("input.txt");
    if (file.is_open()) {
        std::stringstream input;
        input << file.rdbuf();
        std::string contents(input.str());
        Lexer::init(contents);
        Lexer::run();
        for (const Token& token: Lexer::tokens) {
            std::cout << token.to_string() << std::endl;
        }

        file.close();
    } else {
        std::cerr << "Failed to open file" << std::endl;
    }
    return 0;
}
