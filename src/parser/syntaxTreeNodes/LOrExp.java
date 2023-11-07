package parser.syntaxTreeNodes;

import lexer.Token;

import java.util.ArrayList;

public class LOrExp implements SyntaxTreeNode {
    public ArrayList<LAndExp> andExps = new ArrayList<>();

    @Override
    public String printSyntaxTree() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(andExps.get(0).printSyntaxTree()).append('\n')
                .append("<LOrExp>\n");
        for (int i = 1; i < andExps.size(); i++) {
            stringBuilder.append(Token.getTokenString("||")).append('\n')
                    .append(andExps.get(i).printSyntaxTree()).append('\n')
                    .append("<LOrExp>\n");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    @Override
    public SyntaxTreeNode clone() {
        LOrExp lOrExp = new LOrExp();
        andExps.forEach(lAndExp -> lOrExp.andExps.add((LAndExp) lAndExp.clone()));
        return lOrExp;
    }

}
