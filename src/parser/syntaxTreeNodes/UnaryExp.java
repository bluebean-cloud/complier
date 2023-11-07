package parser.syntaxTreeNodes;

import lexer.Token;

public class UnaryExp implements SyntaxTreeNode {
    public SyntaxType type;
    public PrimaryExp primaryExp;
    public Token ident;
    public FuncRParams funcRParams;
    public UnaryOp unaryOp;
    public UnaryExp unaryExp;
    @Override
    public String printSyntaxTree() {
        StringBuilder stringBuilder = new StringBuilder();
        switch (type) {
            case PrimaryExp:
                stringBuilder.append(primaryExp.printSyntaxTree()).append('\n');
                break;
            case FuncCall:
                stringBuilder.append(ident.toString()).append('\n')
                        .append(Token.getTokenString("(")).append('\n');
                if (funcRParams != null) {
                    stringBuilder.append(funcRParams.printSyntaxTree()).append('\n');
                }
                stringBuilder.append(Token.getTokenString(")")).append('\n');
                break;
            case UnaryOp:
                stringBuilder.append(unaryOp.printSyntaxTree()).append('\n')
                        .append(unaryExp.printSyntaxTree()).append('\n');
                break;
        }
        stringBuilder.append("<UnaryExp>");
        return stringBuilder.toString();
    }

    @Override
    public SyntaxTreeNode clone() {
        UnaryExp unaryExp = new UnaryExp();
        unaryExp.type = type;
        if (primaryExp != null) {
            unaryExp.primaryExp = (PrimaryExp) primaryExp.clone();
        }
        if (ident != null) {
            unaryExp.ident = ident;
        }
        if (funcRParams != null) {
            unaryExp.funcRParams = (FuncRParams) funcRParams.clone();
        }
        if (unaryOp != null) {
            unaryExp.unaryOp = (UnaryOp) unaryOp.clone();
        }
        if (this.unaryExp != null) {
            unaryExp.unaryExp = (UnaryExp) this.unaryExp.clone();
        }
        return unaryExp;
    }
}
