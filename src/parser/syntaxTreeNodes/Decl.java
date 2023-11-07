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

    @Override
    public SyntaxTreeNode clone() {
        Decl decl = new Decl();
        if (constDecl != null) {
            decl.constDecl = (ConstDecl) constDecl.clone();
        } else {
            decl.varDecl = (VarDecl) varDecl.clone();
        }
        return decl;
    }


}
