package parser.syntaxTreeNodes;

import lexer.Token;

import java.util.ArrayList;

public class FuncFParams implements SyntaxTreeNode{
    public ArrayList<FuncFParam> funcFParams = new ArrayList<>();

    @Override
    public String printSyntaxTree() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(funcFParams.get(0).printSyntaxTree()).append('\n');
        for (int i = 1; i < funcFParams.size(); i++) {
            stringBuilder.append(Token.getTokenString(",")).append('\n')
                    .append(funcFParams.get(i).printSyntaxTree()).append('\n');
        }
        stringBuilder.append("<FuncFParams>");
        return stringBuilder.toString();
    }
}
