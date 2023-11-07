package parser.syntaxTreeNodes;

import lexer.Token;

import java.util.ArrayList;

public class LAndExp implements SyntaxTreeNode {
    public ArrayList<EqExp> eqExps = new ArrayList<>();
    @Override
    public String printSyntaxTree() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(eqExps.get(0).printSyntaxTree()).append('\n')
                .append("<LAndExp>\n");
        for (int i = 1; i < eqExps.size(); i++) {
            stringBuilder.append(Token.getTokenString("&&")).append('\n')
                    .append(eqExps.get(i).printSyntaxTree()).append('\n')
                    .append("<LAndExp>\n");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    @Override
    public SyntaxTreeNode clone() {
        LAndExp lAndExp = new LAndExp();
        eqExps.forEach(eqExp -> lAndExp.eqExps.add((EqExp) eqExp.clone()));
        return lAndExp;
    }

}
