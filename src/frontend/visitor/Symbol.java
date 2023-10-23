package frontend.visitor;

import frontend.lexer.Token;
import frontend.parser.Type;

public class Symbol {
    public Token token;
    public boolean isConst = false;
    public Type type;
    public DataType dataType;
    public String name;
    public boolean isFunc = false;
    public Function function;

    public Symbol() {}
    public Symbol(FuncFParam funcFParam) {
        this.type = Type.VAR;
        this.dataType = funcFParam.dataType;
        this.name = funcFParam.name;
    }


}
