package lexer;

import util.GlobalConfigure;

public class Token {
    TokenType tokenType;
    String content;      // 字面量
    int number;          // 常数值
    int line;           // 行号

    public Token(TokenType tokenType, String content, int line) {
        this.tokenType = tokenType;
        this.content = content;
        this.line = line;
    }

    public Token(TokenType tokenType, String content, int number, int line) {
        this.tokenType = tokenType;
        this.content = content;
        this.number = number;
        this.line = line;
    }

    @Override
    public String toString() {
        if (GlobalConfigure.DEBUG) {
            return "Line " + line + ": " + content;
        }
        return tokenType + " " + content;
    }
}
