package parser.syntaxTreeNodes;

public class ConstExp implements SyntaxTreeNode {
    public AddExp addExp;

    @Override
    public String printSyntaxTree() {
        return addExp.printSyntaxTree() + "\n<ConstExp>";
    }

    @Override
    public SyntaxTreeNode clone() {
        ConstExp constExp = new ConstExp();
        constExp.addExp = (AddExp) addExp.clone();
        return constExp;
    }

}
