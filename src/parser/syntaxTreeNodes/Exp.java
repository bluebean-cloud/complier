package parser.syntaxTreeNodes;

public class Exp implements SyntaxTreeNode{
    public AddExp addExp;
    @Override
    public String printSyntaxTree() {
        return addExp.printSyntaxTree() + '\n' +
                "<Exp>";
    }
}
