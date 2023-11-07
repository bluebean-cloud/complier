package parser.syntaxTreeNodes;

import lexer.Token;

import java.util.ArrayList;

public class ConstInitVal implements SyntaxTreeNode {
    public ConstExp constExp;
    public ArrayList<ConstInitVal> constInitVals = new ArrayList<>();

    @Override
    public String printSyntaxTree() {
        StringBuilder stringBuilder = new StringBuilder();
        if (constExp != null) {
            stringBuilder.append(constExp.printSyntaxTree()).append('\n');
        } else {
            stringBuilder.append(Token.getTokenString("{")).append('\n');
            if (!constInitVals.isEmpty()) {
                stringBuilder.append(constInitVals.get(0).printSyntaxTree()).append('\n');
            }
            for (int i = 1; i < constInitVals.size(); i++) {
                stringBuilder.append(Token.getTokenString(",")).append('\n')
                        .append(constInitVals.get(i).printSyntaxTree()).append('\n');
            }
            stringBuilder.append(Token.getTokenString("}")).append('\n');
        }
        stringBuilder.append("<ConstInitVal>");
        return stringBuilder.toString();
    }
}
