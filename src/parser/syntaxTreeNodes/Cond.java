package parser.syntaxTreeNodes;

public class Cond implements SyntaxTreeNode {
    public LOrExp lOrExp;
    @Override
    public String printSyntaxTree() {
        return lOrExp.printSyntaxTree() + '\n' +
                "<Cond>";
    }
}
