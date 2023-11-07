package parser.syntaxTreeNodes;

import lexer.Token;

public class ForStmt implements SyntaxTreeNode {
    public LVal lVal;
    public Exp exp;
    @Override
    public String printSyntaxTree() {
        return lVal.printSyntaxTree() + '\n' +
                Token.getTokenString("=") + '\n' +
                exp.printSyntaxTree() + '\n' +
                "<ForStmt>";
    }

    @Override
    public SyntaxTreeNode clone() {
        ForStmt forStmt = new ForStmt();
        forStmt.lVal = (LVal) lVal.clone();
        forStmt.exp = (Exp) exp.clone();
        return forStmt;
    }

}
