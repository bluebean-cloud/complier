package parser.syntaxTreeNodes;

import lexer.Token;

public class LVal implements SyntaxTreeNode{
    public Token ident;
    public Exp exp1;
    public Exp exp2;
    @Override
    public String printSyntaxTree() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ident.toString()).append('\n');
        if (exp1 != null) {
            stringBuilder.append(Token.getTokenString("[")).append('\n')
                    .append(exp1.printSyntaxTree()).append('\n')
                    .append(Token.getTokenString("]")).append('\n');
        }
        if (exp2 != null) {
            stringBuilder.append(Token.getTokenString("[")).append('\n')
                    .append(exp2.printSyntaxTree()).append('\n')
                    .append(Token.getTokenString("]")).append('\n');
        }
        stringBuilder.append("<LVal>");
        return stringBuilder.toString();
    }
}
