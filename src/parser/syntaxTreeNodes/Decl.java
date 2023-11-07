package parser.syntaxTreeNodes;

public class Decl implements SyntaxTreeNode {
    public ConstDecl constDecl;
    public VarDecl varDecl;

    @Override
    public String printSyntaxTree() {
        StringBuilder stringBuilder = new StringBuilder();
        if (constDecl != null) {
            stringBuilder.append(constDecl.printSyntaxTree());
        } else {
            stringBuilder.append(varDecl.printSyntaxTree());
        }
        return stringBuilder.toString();
    }
}
