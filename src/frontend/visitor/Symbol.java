package frontend.visitor;

import frontend.lexer.Token;

public class Symbol {
    public Token token;
    public boolean isConst = false;
    public Type type;
    public boolean isFunc = false;



    enum Type {
        INT, VOID, ARRAY
    }

}
