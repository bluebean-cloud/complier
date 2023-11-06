package frontend.visitor;

import frontend.lexer.Token;
import frontend.parser.Type;

public class Symbol {
    public Token token;
    public int offset;  // 变量在局部区的偏移
    public int level;   // 变量的所处层级
    private boolean isConst = false;
    public Type type;
    public DataType dataType;
    public String name;
    private boolean isFunc = false;
    public Function function;

    public Symbol() {}

    public Symbol(FuncFParam funcFParam) {
        this.type = Type.VAR;
        this.dataType = funcFParam.dataType;
        this.name = funcFParam.name;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setConst(boolean aConst) {
        isConst = aConst;
    }

    public boolean isConst() {
        return isConst;
    }

    public boolean isFunc() {
        return isFunc;
    }

    public void setFunc(boolean func) {
        isFunc = func;
    }

    public void setName(String name) {
        this.name = name;
    }
}
