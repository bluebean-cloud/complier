package parser.syntaxTreeNodes;

import lexer.Token;

import java.util.ArrayList;

public class ConstDecl implements SyntaxTreeNode {
    public ArrayList<ConstDef> constDefs = new ArrayList<>();


    @Override
    public String printSyntaxTree() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Token.getTokenString("const")).append('\n')
                .append(Token.getTokenString("int")).append('\n');
        stringBuilder.append(constDefs.get(0).printSyntaxTree()).append('\n');
        for (int i = 1; i < constDefs.size(); i++) {
            stringBuilder.append(Token.getTokenString(",")).append('\n')
                    .append(constDefs.get(i).printSyntaxTree()).append('\n');
        }
        stringBuilder.append(Token.getTokenString(";")).append('\n')
                .append("<ConstDecl>");
        return stringBuilder.toString();
    }
}
