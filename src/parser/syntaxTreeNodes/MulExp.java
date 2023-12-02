package parser.syntaxTreeNodes;

import lexer.Token;

import java.util.ArrayList;

public class MulExp implements SyntaxTreeNode {
    public ArrayList<UnaryExp> unaryExps = new ArrayList<>();
    public ArrayList<Token> ops = new ArrayList<>();    // * / %

    public boolean isConst;

    @Override
    public String printSyntaxTree() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(unaryExps.get(0).printSyntaxTree()).append('\n')
                .append("<MulExp>\n");
        for (int i = 0; i < ops.size(); i++) {
            stringBuilder.append(ops.get(i).toString()).append('\n')
                    .append(unaryExps.get(i + 1).printSyntaxTree()).append('\n')
                    .append("<MulExp>\n");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    @Override
    public SyntaxTreeNode clone() {
        MulExp mulExp = new MulExp();
        unaryExps.forEach(unaryExp -> mulExp.unaryExps.add((UnaryExp) unaryExp.clone()));
        mulExp.ops.addAll(ops);
        return mulExp;
    }

}
