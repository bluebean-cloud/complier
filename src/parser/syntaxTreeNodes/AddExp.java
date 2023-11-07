package parser.syntaxTreeNodes;

import lexer.Token;

import java.util.ArrayList;

public class AddExp implements SyntaxTreeNode {
    public ArrayList<MulExp> mulExps = new ArrayList<>();
    public ArrayList<Token> ops = new ArrayList<>();
    @Override
    public String printSyntaxTree() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(mulExps.get(0).printSyntaxTree()).append('\n')
                .append("<AddExp>\n");
        for (int i = 0; i < ops.size(); i++) {
            stringBuilder.append(ops.get(i).toString()).append('\n')
                    .append(mulExps.get(i + 1).printSyntaxTree()).append('\n')
                    .append("<AddExp>\n");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }
}
