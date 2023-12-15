package parser.syntaxTreeNodes;

import lexer.Token;
import util.ErrorLog;

public class FuncDef implements SyntaxTreeNode {
    public FuncType funcType;
    public Token ident;
    public FuncFParams funcFParams;
    public Block block;

    public boolean isSimple = true;

    public String getFuncName() {
        return ident.content;
    }

    public String getFuncType() {
        return funcType.type.content;
    }

    public void checkReturn(int line) { // 有返回值的函数最后一句必须是return
        if (getFuncType().equals("void")) {
            return;
        }
        checkReturn(line, block);
    }

    static void checkReturn(int line, Block block) {
        if (block.items.isEmpty()) {
            ErrorLog.ERROR_LOGS.addErrorLog(line, "g");
            return;
        }
        if (block.items.get(block.items.size() - 1).type.equals(SyntaxType.Stmt)) {
            if (!block.items.get(block.items.size() - 1).stmt.type.equals(SyntaxType.Return)) {
                ErrorLog.ERROR_LOGS.addErrorLog(line, "g"); // 若最后一条语句不是 return
            }
        } else {
            ErrorLog.ERROR_LOGS.addErrorLog(line, "g");
        }
    }

    @Override
    public String printSyntaxTree() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(funcType.printSyntaxTree()).append('\n')
                .append(ident.toString()).append('\n')
                .append(Token.getTokenString("(")).append('\n');
        if (funcFParams != null) {
            stringBuilder.append(funcFParams.printSyntaxTree()).append('\n');
        }
        stringBuilder.append(Token.getTokenString(")")).append('\n')
                .append(block.printSyntaxTree()).append('\n')
                .append("<FuncDef>");
        return stringBuilder.toString();
    }

    @Override
    public SyntaxTreeNode clone() {
        FuncDef funcDef = new FuncDef();
        funcDef.funcType = funcType;
        funcDef.ident = ident;
        if (funcFParams != null) {
            funcDef.funcFParams = (FuncFParams) funcFParams.clone();
        }
        funcDef.block = (Block) block.clone();
        return funcDef;
    }

}
