package parser.syntaxTreeNodes;

import lexer.Token;

public class UnaryOp implements SyntaxTreeNode {
    public Token op;
    @Override
    public String printSyntaxTree() {
        return op.toString() + '\n' +
                "<UnaryOp>";
    }

    public UnaryOp() {}

    public UnaryOp(Token op) {
        this.op = op;
    }

    @Override
    public SyntaxTreeNode clone() {
        return new UnaryOp(op);
    }
}
