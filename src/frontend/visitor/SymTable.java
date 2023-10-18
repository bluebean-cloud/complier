package frontend.visitor;

import java.util.ArrayList;

public class SymTable {
    public SymTable() {}
    public static SymTable SYMTABLE = new SymTable(); // 指向全局符号表

    public SymTable parent = null;
    public boolean inLoop = false;
    public boolean inCond = false;
    public ArrayList<SymTable> symTables = new ArrayList<>(); // 符号表树
    public ArrayList<Symbol> symbols = new ArrayList<>();

    public void addSymbol(Symbol symbol) {
        symbols.add(symbol);
    }

    public void setInLoop() {
        inLoop = true;
    }

    public void setInCond() {
        inCond = true;
    }

    public void setParent(SymTable symTable) {
        parent = symTable;
    }

    public SymTable(SymTable parent) {
        this.parent = parent;
    }

}
