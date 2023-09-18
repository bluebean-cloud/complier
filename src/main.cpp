//
// Created by Lenovo on 2023/9/12.
//
#include <sstream>
#include "iostream"
#include "string"
#include "fstream"
#include "lexer/Lexer.h"
#include <cctype>

int main() {

    std::ifstream input_file("testfile.txt");
    std::ofstream output_file("output.txt");
    if (!input_file.is_open()) {
        std::cerr << "Failed to open input_file" << std::endl;
        return 1;
    } else if (!output_file.is_open()) {
        std::cerr << "Failed to open output_file" << std::endl;
        return 1;
    }
    std::stringstream input;
    input << input_file.rdbuf();

    std::string contents(input.str());
    while (!contents.empty() && isspace(contents[contents.size() - 1]))
        contents.pop_back();
    Lexer::init(contents);
    Lexer::run();
    for (const Token& token: Lexer::tokens) {
        output_file << token.to_string() << std::endl;
    }
    input_file.close();
    output_file.close();
    return 0;
}
