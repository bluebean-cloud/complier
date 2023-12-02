package parser.syntaxTreeNodes;

public class Exp implements SyntaxTreeNode{
    public AddExp addExp;

    public boolean isConst;

    public UnaryExp getFirstUnaryExp() {
        return addExp.mulExps.get(0).unaryExps.get(0);
    }

    @Override
    public String printSyntaxTree() {
        return addExp.printSyntaxTree() + '\n' +
                "<Exp>";
    }

    @Override
    public SyntaxTreeNode clone() {
        Exp exp = new Exp();
        exp.addExp = (AddExp) addExp.clone();
        return exp;
    }

}
