package frontend.lexer;

public class Token {
    public String type;
    public String value;
    public int lineNumber;

    Token(String type, String value, int line) {
        this.type = type;
        this.value = value;
        this.lineNumber = line;
    }

    @Override
    public String toString() {
        return String.format("%s %s", type, value);
    }
}
