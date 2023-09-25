package frontend.lexer;

import java.util.ArrayList;
import java.util.HashMap;

public class Lexer {
    public static Lexer LEXER = new Lexer();
    private Lexer() {}

    private static HashMap<String, String> KEY_TYPE_MAP = new HashMap<>();
    private int position = 0;
    private int lineNumber = 1;
    public String content;
    public static ArrayList<Token> tokens = new ArrayList<>();
    static {
        KEY_TYPE_MAP.put("int", "INTTK");
        KEY_TYPE_MAP.put("printf", "PRINTFTK");
        KEY_TYPE_MAP.put("getint", "GETINTTK");
        KEY_TYPE_MAP.put("for", "FORTK");
        KEY_TYPE_MAP.put("const", "CONSTTK");
        KEY_TYPE_MAP.put("main", "MAINTK");
        KEY_TYPE_MAP.put("if", "IFTK");
        KEY_TYPE_MAP.put("else", "ELSETK");
        KEY_TYPE_MAP.put("void", "VOIDTK");
        KEY_TYPE_MAP.put("break", "BREAKTK");
        KEY_TYPE_MAP.put("continue", "CONTINUETK");
        KEY_TYPE_MAP.put("return", "RETURNTK");
        KEY_TYPE_MAP.put("+", "PLUS");
        KEY_TYPE_MAP.put("==", "EQL");
        KEY_TYPE_MAP.put("-", "MINU");
        KEY_TYPE_MAP.put("!=", "NEQ");
        KEY_TYPE_MAP.put("<", "LSS");
        KEY_TYPE_MAP.put("<=", "LEQ");
        KEY_TYPE_MAP.put(">", "GRE");
        KEY_TYPE_MAP.put(">=", "GEQ");
        KEY_TYPE_MAP.put("=", "ASSIGN");
        KEY_TYPE_MAP.put("/", "DIV");
        KEY_TYPE_MAP.put(";", "SEMICN");
        KEY_TYPE_MAP.put("%", "MOD");
        KEY_TYPE_MAP.put(",", "COMMA");
        KEY_TYPE_MAP.put("(", "LPARENT");
        KEY_TYPE_MAP.put(")", "RPARENT");
        KEY_TYPE_MAP.put("[", "LBRACK");
        KEY_TYPE_MAP.put("]", "RBRACK");
        KEY_TYPE_MAP.put("{", "LBRACE");
        KEY_TYPE_MAP.put("}", "RBRACE");
        KEY_TYPE_MAP.put("||", "OR");
        KEY_TYPE_MAP.put("&&", "AND");
        KEY_TYPE_MAP.put("!", "NOT");
        KEY_TYPE_MAP.put("*", "MULT");
        KEY_TYPE_MAP.put("while", "WHILETK");
    }

    int cnt = 0;
    public void nextToken() {
        cnt++;
    }

    public boolean containGetInt() {
        for (int i = cnt; i < tokens.size(); i++) {
            if (tokens.get(i).value.equals("=")) {
                return tokens.get(i + 1).value.equals("getint");
            }
            if (tokens.get(i).value.equals(";")) {
                return false;
            }
        }
        return false;
    }

    public boolean containAssign() {
        for (int i = cnt; i < tokens.size(); i++) {
            if (tokens.get(i).value.equals("=")) {
                return true;
            }
            if (tokens.get(i).value.equals(";")) {
                return false;
            }
        }
        return false;
    }

    public Token peek() {
        if (cnt >= tokens.size()) {
            return new Token("END", "END", -1);
        }
        return tokens.get(cnt);
    }

    public Token preView(int offset) {
        return tokens.get(cnt + offset);
    }

    public boolean isEnd() {
        return cnt >= tokens.size();
    }

    public void clear() {
        tokens.clear();
        position = 0;
        lineNumber = 1;
    }

    public void run(String str) {
        content = str;
        preProcess();
        while (position < content.length()) {
            tokens.add(next());
        }
    }

    private boolean isSpace(char ch) {
        return ch == '\r' || ch == '\n' || ch == ' ' || ch == '\t';
    }

    private void preProcess() { // 去除句尾空格和换行符
        int endIndex = content.length() - 1;
        while (endIndex >= 0 && isSpace(content.charAt(endIndex))) {
            endIndex--;
        }
        content = content.substring(0, endIndex + 1);
    }

    private Token next() {
        while (position < content.length() && isSpace(content.charAt(position))) {
            if (content.charAt(position) == '\n') {
                lineNumber++;
            }
            position++;
        }
        if (position >= content.length()) {
            return new Token("End", "END", lineNumber);
        }
        if (isWordBegin(content.charAt(position))) {
            return getWord();
        } else if (Character.isDigit(content.charAt(position))) {
            return getDigit();
        } else {
            return getSym();
        }
    }

    private Token getWord() {
        StringBuilder buf = new StringBuilder();
        while (isWord(content.charAt(position))) {
            buf.append(content.charAt(position++));
        }
        return new Token(KEY_TYPE_MAP.getOrDefault(buf.toString(), "IDENFR"), buf.toString(), lineNumber);
    }

    private Token getDigit() {
        StringBuilder buf = new StringBuilder();
        while (Character.isDigit(content.charAt(position))) {
            buf.append(content.charAt(position++));
        }
        return new Token("INTCON", buf.toString(), lineNumber);
    }

    private Token getSym() {
        if (content.charAt(position) == '/') {
            position++;
            if (content.charAt(position) == '/' || content.charAt(position) == '*') {
                skipComment();
                return next();
            } else {
                return new Token("DIV", "/", lineNumber);
            }
        } else {
            StringBuilder buf = new StringBuilder();
            buf.append(content.charAt(position));
            if ("!=<>".indexOf(content.charAt(position)) != -1) {
                position++;
                if (content.charAt(position) == '=') {
                    buf.append(content.charAt(position++));
                }
                return new Token(KEY_TYPE_MAP.get(buf.toString()), buf.toString(), lineNumber);
            } else if ("|&".indexOf(content.charAt(position)) != -1) {
                position++;
                buf.append(content.charAt(position++));
                return new Token(KEY_TYPE_MAP.get(buf.toString()), buf.toString(), lineNumber);
            } else if (content.charAt(position) == '"') {
                return getStr();
            } else {
                position++;
                return new Token(KEY_TYPE_MAP.get(buf.toString()), buf.toString(), lineNumber);
            }
        }
    }

    private void skipComment() {
        if (content.charAt(position) == '/') { // "//" 型注释
            while (position < content.length() && content.charAt(position) != '\n') {
                position++;
            }
        } else {
            position++;
            while (position + 1 < content.length() && !(content.charAt(position) == '*' && content.charAt(position + 1) == '/')) {
                if (content.charAt(position) == '\n') {
                    lineNumber++;
                }
                position++;
            }
            position += 2;
        }
    }

    private Token getStr() {
        StringBuilder buf = new StringBuilder();
        buf.append(content.charAt(position++));
        while (position < content.length() && content.charAt(position) != '"') {
            buf.append(content.charAt(position++));
        }
        buf.append(content.charAt(position++));
        return new Token("STRCON", buf.toString(), lineNumber);
    }

    private boolean isWordBegin(char ch) {
        return Character.isLetter(ch) || ch == '_';
    }

    private boolean isWord(char ch) {
        return isWordBegin(ch) || Character.isDigit(ch);
    }


}
