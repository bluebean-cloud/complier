package frontend.lexer;

import Util.DebugConfig;

public class Token {
    public String type;
    public String value;
    public int lineNumber;
    public Token before = null;
    public Token next = null;

    Token(String type, String value, int line) {
        this.type = type;
        this.value = value;
        this.lineNumber = line;
    }

    @Override
    public String toString() {
        if (DebugConfig.DEBUG) {
            String preStr = before == null ? "BEGIN" : before.value;
            String nexStr = next == null ? "END" : next.value;
            return String.format("line%d: %s <%s %s> %s", lineNumber, preStr, type, value, nexStr);
        }
        return type + " " + value;
    }

}
