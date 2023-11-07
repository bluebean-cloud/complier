package parser.syntaxTreeNodes;

import lexer.Token;

public class FuncType implements SyntaxTreeNode{
    public Token type;    // void | int

    @Override
    public String printSyntaxTree() {
        return type.toString() + '\n'
                + "<FuncType>";
    }
}
