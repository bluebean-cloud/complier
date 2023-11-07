package parser.syntaxTreeNodes;

import lexer.Token;

import java.util.ArrayList;

public class EqExp implements SyntaxTreeNode {
    public ArrayList<RelExp> relExps = new ArrayList<>();
    public ArrayList<Token> ops = new ArrayList<>();

    @Override
    public String printSyntaxTree() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(relExps.get(0).printSyntaxTree()).append('\n')
                .append("<EqExp>\n");;
        for (int i = 0; i < ops.size(); i++) {
            stringBuilder.append(ops.get(i).toString()).append('\n')
                    .append(relExps.get(i + 1).printSyntaxTree()).append('\n')
                    .append("<EqExp>\n");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }
}
