package parser.syntaxTreeNodes;

import lexer.Token;

public class PrimaryExp implements SyntaxTreeNode{
    public SyntaxType type;
    public Exp exp;
    public LVal lVal;
    public Number number;
    @Override
    public String printSyntaxTree() {
        StringBuilder stringBuilder = new StringBuilder();
        switch (type) {
            case Exp:
                stringBuilder.append(Token.getTokenString("(")).append('\n')
                        .append(exp.printSyntaxTree()).append('\n')
                        .append(Token.getTokenString(")")).append('\n');
                break;
            case LVal:
                stringBuilder.append(lVal.printSyntaxTree()).append('\n');
                break;
            case Number:
                stringBuilder.append(number.printSyntaxTree()).append('\n');
                break;
        }
        stringBuilder.append("<PrimaryExp>");
        return stringBuilder.toString();
    }
}
