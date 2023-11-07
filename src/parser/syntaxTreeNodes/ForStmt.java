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
}
