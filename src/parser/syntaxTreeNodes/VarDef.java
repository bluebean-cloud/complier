package parser.syntaxTreeNodes;

import lexer.Token;

import java.util.ArrayList;

public class VarDef implements SyntaxTreeNode{
    public Token ident;
    public ConstExp constExp1;
    public ConstExp constExp2;
    public InitVal initVal;

    @Override
    public String printSyntaxTree() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ident.toString()).append('\n');
        if (constExp1 != null) {
            stringBuilder.append(Token.getTokenString("[")).append('\n')
                    .append(constExp1.printSyntaxTree()).append('\n')
                    .append(Token.getTokenString("]")).append('\n');
        }
        if (constExp2 != null) {
            stringBuilder.append(Token.getTokenString("[")).append('\n')
                    .append(constExp2.printSyntaxTree()).append('\n')
                    .append(Token.getTokenString("]")).append('\n');
        }
        if (initVal != null) {
            stringBuilder.append(Token.getTokenString("=")).append('\n')
                    .append(initVal.printSyntaxTree()).append('\n');
        }
        stringBuilder.append("<VarDef>");
        return stringBuilder.toString();
    }

}
