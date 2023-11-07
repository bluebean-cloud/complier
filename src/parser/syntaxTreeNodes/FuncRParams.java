package parser.syntaxTreeNodes;

import lexer.Token;

import java.util.ArrayList;

public class FuncRParams implements SyntaxTreeNode {
    public ArrayList<Exp> exps = new ArrayList<>();
    @Override
    public String printSyntaxTree() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(exps.get(0).printSyntaxTree()).append('\n');
        for (int i = 1; i < exps.size(); i++) {
            stringBuilder.append(Token.getTokenString(",")).append('\n')
                    .append(exps.get(i).printSyntaxTree()).append('\n');
        }
        stringBuilder.append("<FuncRParams>");
        return stringBuilder.toString();
    }

    @Override
    public SyntaxTreeNode clone() {
        FuncRParams funcRParams = new FuncRParams();
        exps.forEach(exp -> funcRParams.exps.add((Exp) exp.clone()));
        return funcRParams;
    }

}
