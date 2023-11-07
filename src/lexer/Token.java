package lexer;

import util.GlobalConfigure;

public class Token {
    public TokenType tokenType;
    public String content;      // 字面量
    public int number;          // 常数值
    public int line;           // 行号

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

    public static String getTokenString(String content) {
        return Lexer.LEXER.TYPE_MAP.get(content) + " " + content;
    }

}
