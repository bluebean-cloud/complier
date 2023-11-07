package parser.syntaxTreeNodes;

import lexer.Token;

import java.util.ArrayList;

public class InitVal implements SyntaxTreeNode {
    public Exp exp;
    public ArrayList<InitVal> initVals = new ArrayList<>();

    @Override
    public String printSyntaxTree() {
        StringBuilder stringBuilder = new StringBuilder();
        if (exp != null) {
            stringBuilder.append(exp.printSyntaxTree()).append('\n');
        } else {
            stringBuilder.append(Token.getTokenString("{")).append('\n');
            if (!initVals.isEmpty()) {
                stringBuilder.append(initVals.get(0).printSyntaxTree()).append('\n');
            }
            for (int i = 1; i < initVals.size(); i++) {
                stringBuilder.append(Token.getTokenString(",")).append('\n')
                        .append(initVals.get(i).printSyntaxTree()).append('\n');
            }
            stringBuilder.append(Token.getTokenString("}")).append('\n');
        }
        stringBuilder.append("<InitVal>");
        return stringBuilder.toString();
    }
}
