package lexer;

import util.GlobalConfigure;

import java.util.ArrayList;
import java.util.HashMap;

public class Lexer {
    public static final Lexer LEXER = new Lexer();
    private Lexer() {
        TYPE_MAP.put("main", TokenType.MAINTK);
        TYPE_MAP.put("const", TokenType.CONSTTK);
        TYPE_MAP.put("int", TokenType.INTTK);
        TYPE_MAP.put("break", TokenType.BREAKTK);
        TYPE_MAP.put("continue", TokenType.CONTINUETK);
        TYPE_MAP.put("if", TokenType.IFTK);
        TYPE_MAP.put("else", TokenType.ELSETK);
        TYPE_MAP.put("!", TokenType.NOT);
        TYPE_MAP.put("&&", TokenType.AND);
        TYPE_MAP.put("||", TokenType.OR);
        TYPE_MAP.put("for", TokenType.FORTK);
        TYPE_MAP.put("getint", TokenType.GETINTTK);
        TYPE_MAP.put("printf", TokenType.PRINTFTK);
        TYPE_MAP.put("return", TokenType.RETURNTK);
        TYPE_MAP.put("+", TokenType.PLUS);
        TYPE_MAP.put("-", TokenType.MINU);
        TYPE_MAP.put("void", TokenType.VOIDTK);
        TYPE_MAP.put("*", TokenType.MULT);
        TYPE_MAP.put("/", TokenType.DIV);
        TYPE_MAP.put("%",TokenType.MOD);
        TYPE_MAP.put("<", TokenType.LSS);
        TYPE_MAP.put("<=", TokenType.LEQ);
        TYPE_MAP.put(">", TokenType.GRE);
        TYPE_MAP.put(">=", TokenType.GEQ);
        TYPE_MAP.put("==", TokenType.EQL);
        TYPE_MAP.put("!=", TokenType.NEQ);
        TYPE_MAP.put("=", TokenType.ASSIGN);
        TYPE_MAP.put(";", TokenType.SEMICN);
        TYPE_MAP.put(",", TokenType.COMMA);
        TYPE_MAP.put("(", TokenType.LPARENT);
        TYPE_MAP.put(")", TokenType.RPARENT);
        TYPE_MAP.put("[", TokenType.LBRACK);
        TYPE_MAP.put("]", TokenType.RBRACK);
        TYPE_MAP.put("{", TokenType.LBRACE);
        TYPE_MAP.put("}", TokenType.RBRACE);
    }

    private final HashMap<String, TokenType> TYPE_MAP = new HashMap<>();

    int pos = 0;    // 当前处于字符串的位置
    int line = 1;
    int cnt = 0;    // 当前访问到哪个 token
    String content;
    ArrayList<Token> tokens = new ArrayList<>();

    public void run(String string) {
        content = string.trim();
        while (pos < content.length()) {
            addToken();
        }
    }

    public Token preView(int offset) {
        if (cnt + offset >= 0 && cnt + offset < tokens.size()) {
            return tokens.get(cnt + offset);
        }
        return null;
    }

    private void addToken() {
        while (pos < content.length() && isSpace(content.charAt(pos))) {
            if (content.charAt(pos) == '\n') {
                line++;
            }
            pos++;
        }
        if (pos > content.length()) {
            return;
        }
        if (isWordBegin(content.charAt(pos))) {
            tokens.add(getWord());
        } else if (Character.isDigit(content.charAt(pos))) {
            tokens.add(getDigit());
        } else {
            Token token = getSym();
            if (token != null) {
                tokens.add(token);
            }
        }
    }

    private Token getWord() {
        StringBuilder builder = new StringBuilder();
        while (isWord(content.charAt(pos))) {
            builder.append(content.charAt(pos));
            pos++;
        }
        return new Token(TYPE_MAP.getOrDefault(builder.toString(), TokenType.IDENFR),
                builder.toString(), line);
    }

    private Token getDigit() {
        StringBuilder builder = new StringBuilder();
        while (Character.isDigit(content.charAt(pos))) {
            builder.append(content.charAt(pos));
            pos++;
        }
        return new Token(TokenType.INTCON, builder.toString(), Integer.parseInt(builder.toString()), line);
    }

    private Token getSym() {
        StringBuilder builder = new StringBuilder();
        builder.append(content.charAt(pos));
        if (content.charAt(pos) == '/') {
            pos++;
            if (content.charAt(pos) == '/') {
                pos++;
                skipComment(1);
            } else if (content.charAt(pos) == '*') {
                pos++;
                skipComment(2);
            } else {
                return new Token(TokenType.DIV, builder.toString(), line);
            }
            return null;
        } else {
            if ("!=<>".indexOf(content.charAt(pos)) != -1) {
                pos++;
                if (content.charAt(pos) == '=') {
                    builder.append(content.charAt(pos));
                    pos++;
                }
                return new Token(TYPE_MAP.get(builder.toString()), builder.toString(), line);
            } else if ("|&".indexOf(content.charAt(pos)) != -1) {
                pos++;
                builder.append(content.charAt(pos));
                pos++;
                return new Token(TYPE_MAP.get(builder.toString()), builder.toString(), line);
            } else if (content.charAt(pos) == '"') {
                return getStr();
            } else {
                pos++;
                return new Token(TYPE_MAP.get(builder.toString()), builder.toString(), line);
            }
        }
    }

    private void skipComment(int type) {    // 1: //   2: /**/
        if (type == 1) {
            while (pos < content.length() && content.charAt(pos) != '\n') {
                pos++;
            }
        } else {
            while (pos + 1 < content.length() && !(content.charAt(pos) == '*' && content.charAt(pos + 1) == '/')) {
                if (content.charAt(pos) == '\n') {
                    line++;
                }
                pos++;
            }
            pos += 2;
        }
    }

    private Token getStr() {
        StringBuilder builder = new StringBuilder("\"");
        pos++;
        while (pos < content.length() && content.charAt(pos) != '"') {
            builder.append(content.charAt(pos));
            pos++;
        }
        builder.append(content.charAt(pos++));
        if (GlobalConfigure.ERROR && isLegal(builder.toString())) {

        }
        return new Token(TokenType.STRCON, builder.toString(), line);
    }

    public boolean isLegal(String buf) {
        for (int i = 1; i < buf.length() - 1; i++) {
            char ch = buf.charAt(i);
            if ((ch == '\\' && buf.charAt(i + 1) != 'n') || (ch == '%' && buf.charAt(i + 1) != 'd')) {
                return false;
            }
            if (!(ch == '%' || ch == 32 || ch == 33 || ch >= 40 && ch <= 126)) {
                return false;
            }
        }
        return true;
    }

    private boolean isWordBegin(char ch) {
        return Character.isLetter(ch) || ch == '_';
    }

    private boolean isWord(char ch) {
        return isWordBegin(ch) || Character.isDigit(ch);
    }

    public String printThis() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Token token: tokens) {
            stringBuilder.append(token.toString()).append('\n');
        }
        return stringBuilder.toString();
    }

    private boolean isSpace(char ch) {
        return ch == '\r' || ch == '\n' || ch == ' ' || ch == '\t';
    }

}
