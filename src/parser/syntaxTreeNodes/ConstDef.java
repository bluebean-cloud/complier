package parser.syntaxTreeNodes;

import lexer.Token;

public class ConstDef implements SyntaxTreeNode {
    public Token ident;
    public ConstExp constExp1;
    public ConstExp constExp2;
    public ConstInitVal constInitVal;

    public String getName() {
        return ident.content;
    }

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
        stringBuilder.append(Token.getTokenString("=")).append('\n')
                .append(constInitVal.printSyntaxTree()).append('\n').append("<ConstDef>");
        return stringBuilder.toString();
    }

    @Override
    public SyntaxTreeNode clone() {
        ConstDef constDef = new ConstDef();
        constDef.ident = ident;
        if (constExp1 != null) {
            constDef.constExp1 = (ConstExp) constExp1.clone();
        }
        if (constExp2 != null) {
            constDef.constExp2 = (ConstExp) constExp2.clone();
        }
        constDef.constInitVal = (ConstInitVal) constInitVal.clone();
        return constDef;
    }
}
