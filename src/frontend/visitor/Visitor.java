package frontend.visitor;

import Util.Assert;
import Util.Judge;
import Util.NotMatchException;
import frontend.parser.GrammarNode;
import frontend.parser.Parser;
import frontend.parser.Type;

import java.util.ArrayList;

public class Visitor {
    public static Visitor VISITOR = new Visitor();
    private Visitor() {}

    SymTable curTable = SymTable.SYMTABLE;

    public void run() throws NotMatchException {
        visitCompUnit();
    }

    private void visitCompUnit() throws NotMatchException {
        for (GrammarNode node: Parser.PARSER.root.childs) {
            switch (node.type) {
                case FUNC_DEF:
                case MAIN_FUNC_DEF:
                    visitFunc(node);
                    break;
                case DECL:
                    break;
                default:
                    System.exit(-1);
            }
        }
    }



    private void visitFunc(GrammarNode node) throws NotMatchException {
        curTable = new SymTable(curTable);  // 构造子符号表
        Symbol funcSymbol = new Symbol();
        curTable.addSymbol(funcSymbol);

        funcSymbol.isFunc = true;

        Function function = new Function();
        funcSymbol.function = function;

        // 处理函数头
        if (node.type.equals(Type.MAIN_FUNC_DEF)) {
            function.retType = new DataType(Type.INT);
            node.nextChild();
            function.name = "main";
        } else {
            function.retType = new DataType(Type.valueOf(node.getFirstTokenValue().toUpperCase()));
            node.nextChild();   // Ident
            function.name = node.getTokenValue();
        }
        node.nextChild(); // 通过函数名
        node.nextChild(); // 通过 '('
        if (!Judge.isOf(node.getTokenValue(), ")")) { // 存在参数
            function.funcFParams = visitFParams(node.getChild());
            node.nextChild();
        }
        node.nextChild(); // 通过 ')'
        visitBlock(node.getChild(), false); // 分析函数体

        curTable = curTable.parent;         // 返回上一级符号表
    }

    private ArrayList<FuncFParam> visitFParams(GrammarNode node) throws NotMatchException {
        Assert.isOf(node.grammarType, "FuncFParams");
        ArrayList<FuncFParam> funcFParams = new ArrayList<>();
        for (GrammarNode child: node.childs) {
            if (child.getFirstTokenValue().equals(",")) {
                continue;
            }
            FuncFParam funcFParam = visitFParam(child);
            curTable.addSymbol(new Symbol(funcFParam));
            funcFParams.add(funcFParam);
        }
        return funcFParams;
    }

    private FuncFParam visitFParam(GrammarNode node) throws NotMatchException {
        Assert.isOf(node.grammarType, "FuncFParam");
        FuncFParam funcFParam = new FuncFParam();
        funcFParam.dataType = new DataType(Type.INT);
        node.nextChild();
        funcFParam.name = node.getTokenValue();
        node.nextChild();
        if (node.childs.size() >= 4) {
            funcFParam.dataType.type = Type.ARRAY;
            funcFParam.dataType.content = new DataType(Type.INT);
            node.nextChild();
            node.nextChild();
        }
        if (node.childs.size() == 7) { // 至多只有二维数组
            node.nextChild();
            DataType type = new DataType(Type.ARRAY);
            type.capacity = Integer.parseInt(node.getTokenValue());
            type.content = new DataType(Type.INT);
            funcFParam.dataType.content = type;
        }
        return funcFParam;
    }

    private void visitBlock(GrammarNode node, boolean sym) throws NotMatchException {
        Assert.isOf(node.grammarType, "Block");
        if (sym) {
            curTable = new SymTable(curTable);
        }
        node.nextChild();   // 通过 '{'
        while (!node.getTokenValue().equals("}")) {
            visitBlockItem(node.getChild());
            node.nextChild();
        }
        if (sym) {
            curTable = curTable.parent;
        }
    }

    private void visitBlockItem(GrammarNode node) throws NotMatchException { // 分发 BlockItem
        Assert.isOf(node.grammarType, "BlockItem");
        switch (node.getChild().type) {
            case DECL:
                break;
            case STMT:
                visitStmt(node.getChild());
                break;
            default:
                System.exit(-2);
        }
    }

    private void visitStmt(GrammarNode node) throws NotMatchException {
        Assert.isOf(node.grammarType, "Stmt");
        switch (node.type) {
            case BLOCK_STMT:
                visitBlock(node.getChild(), true);
                break;
            case IF_STMT:
                visitIfStmt(node);
                break;
            case FOR_STMT:
                break;
            case WHILE_STMT:
                break;
            case BREAK_STMT:
                break;
            case CONTINUE_STMT:
                break;
            case RETURN_STMT:
                break;
            case PRINTF_STMT:
                break;
            case GETINT_STMT:
                break;
            case ASSIGN_STMT:
                break;
            case EXP_STMT:
                break;
            case EMPTY_STMT:
                break;
        }
    }

    private void visitIfStmt(GrammarNode node) {
        node.nextChild();   // 通过 if
        node.nextChild();   // 通过 (
    }

    private void visitCond(GrammarNode node) {

    }

    private void visitLOrExp(GrammarNode node) {

    }

    private void visitLAndExp(GrammarNode node) {

    }

    private void visitEqExp(GrammarNode node) {

    }

    private void visitRelExp(GrammarNode node) throws NotMatchException {
        Assert.isOf(node.grammarType, "RelExp");
        
    }

    private void visitExp(GrammarNode node) throws NotMatchException {
        Assert.isOf(node.grammarType, "Exp");
        visitAddExp(node.getChild());
    }

    private void visitAddExp(GrammarNode node) throws NotMatchException {
        Assert.isOf(node.grammarType, "AddExp");
        visitMulExp(node.getChild());
        node.nextChild();
        if (node.hasNextChild()) {
            // 应该对运算符号做一些处理，先不管了（
            node.nextChild();   // 通过 '+', '-'
            visitAddExp(node.getChild());   // 右递归
            node.nextChild();
        }
    }

    private void visitMulExp(GrammarNode node) throws NotMatchException {
        Assert.isOf(node.grammarType, "MulExp");
        visitUnaryExp(node.getChild());
        node.nextChild();
        if (node.hasNextChild()) {
            node.nextChild();   // 通过 '*' '/'
            visitMulExp(node.getChild());
            node.nextChild();
        }
    }

    private void visitUnaryExp(GrammarNode node) throws NotMatchException {
        Assert.isOf(node.grammarType, "UnaryExp");
        switch (node.type) {
            case OP_EXP:
                node.nextChild();   // 符号
                visitUnaryExp(node.getChild());
                break;
            case FUNC_CALL:
                node.nextChild();   // 通过函数名
                node.nextChild();   // 通过 '('
                if (node.getChild().grammarType.equals("FuncRParams")) {
                    visitFuncRParams(node.getChild());
                    node.nextChild();
                }
                node.nextChild();   // 通过 ')'
                break;
            case PRIMARY_EXP:
                visitPrimaryExp(node.getChild());
                break;
        }
    }

    private void visitPrimaryExp(GrammarNode node) {
        switch (node.type) {
            case WITH_BRACKET:
                break;
            case INTCON:
                break;
            case IDENFR:
                break;
        }
    }

    private void visitLVal(GrammarNode node) {

    }

    private void visitFuncRParams(GrammarNode node) {

    }

}
