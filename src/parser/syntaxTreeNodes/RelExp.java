package parser.syntaxTreeNodes;

import lexer.Token;

import java.util.ArrayList;

public class RelExp implements SyntaxTreeNode {
    public ArrayList<AddExp> addExps = new ArrayList<>();
    public ArrayList<Token> ops = new ArrayList<>();
    @Override
    public String printSyntaxTree() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(addExps.get(0).printSyntaxTree()).append('\n')
                .append("<RelExp>\n");
        for (int i = 0; i < ops.size(); i++) {
            stringBuilder.append(ops.get(i).toString()).append('\n')
                    .append(addExps.get(i + 1).printSyntaxTree()).append('\n')
                    .append("<RelExp>\n");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    @Override
    public SyntaxTreeNode clone() {
        RelExp relExp = new RelExp();
        addExps.forEach(addExp -> relExp.addExps.add((AddExp) addExp.clone()));
        relExp.ops.addAll(ops);
        return relExp;
    }
}
