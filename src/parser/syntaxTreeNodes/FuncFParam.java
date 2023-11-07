package parser.syntaxTreeNodes;

import lexer.Token;

public class FuncFParam  implements SyntaxTreeNode {
    public int deep = 0;    // 0:int 1:int[] 2:int[][ConstExp]
    public Token ident;
    public ConstExp constExp;

    @Override
    public String printSyntaxTree() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Token.getTokenString("int")).append('\n')
                .append(ident.toString()).append('\n');
        if (deep >= 1) {
            stringBuilder.append(Token.getTokenString("[")).append('\n')
                    .append(Token.getTokenString("]")).append('\n');
        }
        if (deep == 2) {
            stringBuilder.append(Token.getTokenString("[")).append('\n')
                    .append(constExp.printSyntaxTree()).append('\n')
                    .append(Token.getTokenString("]")).append('\n');
        }
        stringBuilder.append("<FuncFParam>");
        return stringBuilder.toString();
    }

    @Override
    public SyntaxTreeNode clone() {
        FuncFParam funcFParam = new FuncFParam();
        funcFParam.deep = deep;
        funcFParam.ident = ident;
        if (constExp != null) {
            funcFParam.constExp = (ConstExp) constExp.clone();
        }
        return funcFParam;
    }

}
