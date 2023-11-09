package parser.syntaxTreeNodes;

public class BlockItem implements SyntaxTreeNode {
    public SyntaxType type;     // Stmt | Decl
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

    @Override
    public SyntaxTreeNode clone() {
        BlockItem blockItem = new BlockItem();
        blockItem.type = type;
        if (decl != null) {
            blockItem.decl = (Decl) decl.clone();
        } else {
            blockItem.stmt = (Stmt) stmt.clone();
        }
        return blockItem;
    }
}
