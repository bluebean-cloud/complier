package frontend.visitor;

public class SymTable {
    public static SymTable SYMTABLE = new SymTable();

    public SymTable parent = null;
    public boolean inLoop = false;
    public boolean inCond = false;


}
