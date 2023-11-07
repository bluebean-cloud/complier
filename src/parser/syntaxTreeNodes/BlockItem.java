package parser.syntaxTreeNodes;

public class BlockItem implements SyntaxTreeNode {
    public SyntaxType type;     // ConstDecl | VarDecl
    public Decl decl;
    public Stmt stmt;
    @Override
    public String printSyntaxTree() {
        StringBuilder stringBuilder = new StringBuilder();
        if (decl != null) {
            stringBuilder.append(decl.printSyntaxTree());
        } else {
            stringBuilder.append(stmt.printSyntaxTree());
        }
        return stringBuilder.toString();
    }
}
