package frontend.visitor;

import frontend.lexer.Token;
import frontend.parser.Type;

public class Symbol {
    public Token token;
    public boolean isConst = false;
    public Type type;
    public boolean isFunc = false;
    public Function function;


}
