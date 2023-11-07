package parser.syntaxTreeNodes;

import lexer.Token;

import java.util.ArrayList;

public class VarDecl implements SyntaxTreeNode {
    public ArrayList<VarDef> varDefs = new ArrayList<>();

    @Override
    public String printSyntaxTree() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Token.getTokenString("int")).append('\n')
                .append(varDefs.get(0).printSyntaxTree()).append('\n');
        for (int i = 1; i < varDefs.size(); i++) {
            stringBuilder.append(Token.getTokenString(",")).append('\n')
                    .append(varDefs.get(i).printSyntaxTree()).append('\n');
        }
        stringBuilder.append(Token.getTokenString(";")).append('\n')
                .append("<VarDecl>");
        return stringBuilder.toString();
    }

    @Override
    public SyntaxTreeNode clone() {
        VarDecl varDecl = new VarDecl();
        varDefs.forEach(varDef -> varDecl.varDefs.add((VarDef) varDef.clone()));
        return varDecl;
    }

}
