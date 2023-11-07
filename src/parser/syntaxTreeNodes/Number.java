package parser.syntaxTreeNodes;

import lexer.Token;

public class Number implements SyntaxTreeNode {
    public Token number;
    @Override
    public String printSyntaxTree() {
        return number.toString() + "\n" +
                "<Number>";
    }

    public Number() {}

    public Number(Token number) {
        this.number = number;
    }

    @Override
    public SyntaxTreeNode clone() {
        return new Number(number);
    }
}
