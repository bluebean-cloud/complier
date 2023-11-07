package parser.syntaxTreeNodes;

public class ConstExp implements SyntaxTreeNode {
    public AddExp addExp;

    @Override
    public String printSyntaxTree() {
        return addExp.printSyntaxTree() + "\n<ConstExp>";
    }
}
