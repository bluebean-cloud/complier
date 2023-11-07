package parser.syntaxTreeNodes;

import lexer.Token;

import java.util.ArrayList;

public class FuncDef implements SyntaxTreeNode {
    public FuncType funcType;
    public Token ident;
    public FuncFParams funcFParams;
    public Block block;

    @Override
    public String printSyntaxTree() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(funcType.printSyntaxTree()).append('\n')
                .append(ident.toString()).append('\n')
                .append(Token.getTokenString("(")).append('\n');
        if (funcFParams != null) {
            stringBuilder.append(funcFParams.printSyntaxTree()).append('\n');
        }
        stringBuilder.append(Token.getTokenString(")")).append('\n')
                .append(block.printSyntaxTree()).append('\n')
                .append("<FuncDef>");
        return stringBuilder.toString();
    }
}
