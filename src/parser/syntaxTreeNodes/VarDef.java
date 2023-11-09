package parser.syntaxTreeNodes;

import lexer.Token;

public class VarDef implements SyntaxTreeNode{
    public Token ident;
    public ConstExp constExp1;
    public ConstExp constExp2;
    public InitVal initVal;

    public String getName() {
        return ident.content;
    }

    public VarDef() {}

    public VarDef(FuncFParam funcFParam) {
        if (funcFParam.deep >= 1) {
            constExp1 = new ConstExp();
        }
        if (funcFParam.deep == 2) {
            constExp2 = new ConstExp();
        }
        ident = funcFParam.ident;
    }

    @Override
    public String printSyntaxTree() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ident.toString()).append('\n');
        if (constExp1 != null) {
            stringBuilder.append(Token.getTokenString("[")).append('\n')
                    .append(constExp1.printSyntaxTree()).append('\n')
                    .append(Token.getTokenString("]")).append('\n');
        }
        if (constExp2 != null) {
            stringBuilder.append(Token.getTokenString("[")).append('\n')
                    .append(constExp2.printSyntaxTree()).append('\n')
                    .append(Token.getTokenString("]")).append('\n');
        }
        if (initVal != null) {
            stringBuilder.append(Token.getTokenString("=")).append('\n')
                    .append(initVal.printSyntaxTree()).append('\n');
        }
        stringBuilder.append("<VarDef>");
        return stringBuilder.toString();
    }

    @Override
    public SyntaxTreeNode clone() {
        VarDef varDef = new VarDef();
        varDef.ident = ident;
        if (constExp1 != null) {
            varDef.constExp1 = (ConstExp) constExp1.clone();
        }
        if (constExp2 != null) {
            varDef.constExp2 = (ConstExp) constExp2.clone();
        }
        if (initVal != null) {
            varDef.initVal = (InitVal) initVal.clone();
        }
        return initVal;
    }

}
