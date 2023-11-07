package parser.syntaxTreeNodes;

public class Cond implements SyntaxTreeNode {
    public LOrExp lOrExp;
    @Override
    public String printSyntaxTree() {
        return lOrExp.printSyntaxTree() + '\n' +
                "<Cond>";
    }

    @Override
    public SyntaxTreeNode clone() {
        Cond cond = new Cond();
        cond.lOrExp = (LOrExp) lOrExp.clone();
        return cond;
    }
}
