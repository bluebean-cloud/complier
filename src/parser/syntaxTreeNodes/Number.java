package parser.syntaxTreeNodes;

import lexer.Token;

public class Number implements SyntaxTreeNode {
    public Token number;
    @Override
    public String printSyntaxTree() {
        return number.toString() + "\n" +
                "<Number>";
    }
}
