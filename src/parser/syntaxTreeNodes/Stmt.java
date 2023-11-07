package parser.syntaxTreeNodes;

import lexer.Token;

import java.util.ArrayList;

public class Stmt implements SyntaxTreeNode {
    public SyntaxType type;
    public LVal lVal;
    public Exp exp;
    public Block block;
    public Cond cond;
    public Stmt stmt1;
    public Stmt stmt2;
    public ForStmt forStmt1;
    public ForStmt forStmt2;
    public Token formatString;
    public ArrayList<Exp> exps = new ArrayList<>();

    @Override
    public String printSyntaxTree() {
        StringBuilder stringBuilder = new StringBuilder();
        switch (type) {
            case Assign:
                stringBuilder.append(lVal.printSyntaxTree()).append('\n')
                        .append(Token.getTokenString("=")).append('\n')
                        .append(exp.printSyntaxTree()).append('\n')
                        .append(Token.getTokenString(";")).append('\n');
                break;
            case Empty:
                stringBuilder.append(Token.getTokenString(";")).append('\n');
                break;
            case Exp:
                if (exp != null) {
                    stringBuilder.append(exp.printSyntaxTree()).append('\n');
                }
                stringBuilder.append(Token.getTokenString(";")).append('\n');
                break;
            case Block:
                stringBuilder.append(block.printSyntaxTree()).append('\n');
                break;
            case If:
                stringBuilder.append(Token.getTokenString("if")).append('\n')
                        .append(Token.getTokenString("(")).append('\n')
                        .append(cond.printSyntaxTree()).append('\n')
                        .append(Token.getTokenString(")")).append('\n')
                        .append(stmt1.printSyntaxTree()).append('\n');
                if (stmt2 != null) {
                    stringBuilder.append(Token.getTokenString("else")).append('\n')
                            .append(stmt2.printSyntaxTree()).append('\n');
                }
                break;
            case For:
                stringBuilder.append(Token.getTokenString("for")).append('\n')
                        .append(Token.getTokenString("(")).append('\n');
                if (forStmt1 != null) {
                    stringBuilder.append(forStmt1.printSyntaxTree()).append('\n');
                }
                stringBuilder.append(Token.getTokenString(";")).append('\n');
                if (cond != null) {
                    stringBuilder.append(cond.printSyntaxTree()).append('\n');
                }
                stringBuilder.append(Token.getTokenString(";")).append('\n');
                if (forStmt2 != null) {
                    stringBuilder.append(forStmt2.printSyntaxTree()).append('\n');
                }
                stringBuilder.append(Token.getTokenString(")")).append('\n')
                        .append(stmt1.printSyntaxTree()).append('\n');
                break;
            case Break:
                stringBuilder.append(Token.getTokenString("break")).append('\n')
                        .append(Token.getTokenString(";")).append('\n');
                break;
            case Continue:
                stringBuilder.append(Token.getTokenString("continue")).append('\n')
                        .append(Token.getTokenString(";")).append('\n');
                break;
            case Return:
                stringBuilder.append(Token.getTokenString("return")).append('\n');
                if (exp != null) {
                    stringBuilder.append(exp.printSyntaxTree()).append('\n');
                }
                stringBuilder.append(Token.getTokenString(";")).append('\n');
                break;
            case GetInt:
                stringBuilder.append(lVal.printSyntaxTree()).append('\n')
                        .append(Token.getTokenString("=")).append('\n')
                        .append(Token.getTokenString("getint")).append('\n')
                        .append(Token.getTokenString("(")).append('\n')
                        .append(Token.getTokenString(")")).append('\n')
                        .append(Token.getTokenString(";")).append('\n');
                break;
            case Printf:
                stringBuilder.append(Token.getTokenString("printf")).append('\n')
                        .append(Token.getTokenString("(")).append('\n')
                        .append(formatString.toString()).append('\n');
                for (Exp param: exps) {
                    stringBuilder.append(Token.getTokenString(",")).append('\n')
                            .append(param.printSyntaxTree()).append('\n');
                }
                stringBuilder.append(Token.getTokenString(")")).append('\n')
                        .append(Token.getTokenString(";")).append('\n');
                break;
        }
        stringBuilder.append("<Stmt>");
        return stringBuilder.toString();
    }
}
