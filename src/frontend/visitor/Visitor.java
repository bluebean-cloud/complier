package frontend.visitor;

import util.Assert;
import util.ErrorLog;
import util.Judge;
import util.NotMatchException;
import frontend.parser.GrammarNode;
import frontend.parser.Parser;
import frontend.parser.Type;

import java.util.ArrayList;

public class Visitor {
    public static Visitor VISITOR = new Visitor();
    private Visitor() {}

    SymTable curTable = SymTable.SYMTABLE;
    Function curFunc;   // 在定义函数的过程中通过 curFunc 来访问当前函数

    private boolean isFun = false;
    private boolean isDecl = false;
    private boolean isFor = false;

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
                    visitDecl(node);
                    break;
                default:
                    System.exit(-1);
            }
        }
    }

    private boolean checkReDefine(String name) {
        for (Symbol symbol: curTable.symbols) {
            if (symbol.name.equals(name)) {
                return true;
            }
        }
        return false;
    }

    private void visitDecl(GrammarNode node) throws NotMatchException {
        Assert.isOf(node.grammarType, "Decl");
        if (node.getTokenValue().equals("const")) {
            visitConstDecl(node.getChild());
            return;
        }
        visitVarDecl(node.getChild());
    }

    private void visitConstDecl(GrammarNode node) throws NotMatchException {
        Assert.isOf(node.grammarType, "ConstDecl");
        isDecl = true;
        node.nextChild();   // 通过 const
        node.nextChild();   // 通过 BType
        Symbol symbol = visitConstDef(node.getChild());
        if (checkReDefine(symbol.name)) {
            ErrorLog.ERRORLIST.add(new ErrorLog(node.getTokenLine(), 'b'));
        } else {
            curTable.addSymbol(symbol);
        }
        node.nextChild();
        while (node.getTokenValue().equals(",")) {
            node.nextChild();
            symbol = visitConstDef(node.getChild());
            if (checkReDefine(symbol.name)) {
                ErrorLog.ERRORLIST.add(new ErrorLog(node.getTokenLine(), 'b'));
            } else {
                curTable.addSymbol(symbol);
            }
            node.nextChild();
        }
        isDecl = false;
    }

    private Symbol visitConstDef(GrammarNode node) throws NotMatchException {
        Assert.isOf(node.grammarType, "ConstDef");
        Symbol symbol = new Symbol();
        symbol.setConst(true);
        symbol.setType(Type.CONSTVAR);
        symbol.setName(node.getTokenValue());
        node.nextChild();   // 通过变量名
        symbol.dataType = new DataType(Type.INT);
        while (node.getTokenValue().equals("[")) {
            node.nextChild();   // 通过 '['
            DataType new_dataType = new DataType(Type.ARRAY);
            new_dataType.capacity = visitConstExp(node.getChild());
            node.nextChild();   // 通过常数
            node.nextChild();   // 通过 ']'
            new_dataType.content = symbol.dataType;
            symbol.dataType = new_dataType;
        }
        node.nextChild();
        symbol.dataType.values = visitConstInitVal(node.getChild());
        symbol.dataType.padding(); // 填零

        return symbol;
    }

    private ArrayList<Integer> visitConstInitVal(GrammarNode node) {
        ArrayList<Integer> array = new ArrayList<>();
        if (node.getTokenValue().equals("{")) {
            node.nextChild();
            if (!node.getTokenValue().equals("}")) {
                array.addAll(visitConstInitVal(node.getChild()));
                while (node.getTokenValue().equals(",")) {
                    node.nextChild();
                    array.addAll(visitConstInitVal(node.getChild()));
                    node.nextChild();
                }
            }
        } else {
            array.add(visitConstExp(node.getChild()));
        }
        return array;
    }

    private int visitConstExp(GrammarNode node) {
        int a;
        switch (node.grammarType) {
            case "ConstExp":
                return visitConstExp(node.getChild());
            case "AddExp":
                a = visitConstExp(node.getChild());
                while (node.hasNextChild()) {
                    node.nextChild();
                    switch (node.getTokenValue()) {
                        case "+":
                            node.nextChild();
                            a += visitConstExp(node.getChild());
                            break;
                        case "-":
                            node.nextChild();
                            a -= visitConstExp(node.getChild());
                            break;
                        default:
                            throw new RuntimeException();
                    }
                }
                return a;
            case "MulExp":
                a = visitConstExp(node.getChild());
                while (node.hasNextChild()) {
                    node.nextChild();
                    switch (node.getTokenValue()) {
                        case "*":
                            node.nextChild();
                            a *= visitConstExp(node.getChild());
                            break;
                        case "/":
                            node.nextChild();
                            a /= visitConstExp(node.getChild());
                            break;
                        case "%":
                            node.nextChild();
                            a %= visitConstExp(node.getChild());
                            break;
                        default:
                            throw new RuntimeException();
                    }
                }
                return a;
            case "UnaryExp":
                switch (node.type) {
                    case OP_EXP:
                        int i = node.getFirstTokenValue().equals("+") ? 1 : -1;
                        node.nextChild();
                        return i * visitConstExp(node.getChild());
                    case PRIMARY_EXP:
                        return visitConstExp(node.getChild());
                    default:
                        throw new RuntimeException();
                }
            case "PrimaryExp":
                switch (node.type) {
                    case WITH_BRACKET:
                        node.nextChild();
                        return visitConstExp(node.getChild());
                    case IDENFR:
                        return getIDENFRValue(node.getChild());
                    case INTCON:
                        return Integer.parseInt(node.getFirstTokenValue());
                }
            case "Exp":
                return visitConstExp(node.getChild());
        }
        return 0;
    }

    private int getIDENFRValue(GrammarNode node) {  // 类型为 LVAL，且必须保证有值
        Symbol symbol = findSym(node.getFirstTokenValue());
        DataType dataType0 = symbol.dataType;
        if (dataType0.type.equals(Type.INT)) {
            return dataType0.values.get(0);
        } else if (dataType0.content.type.equals(Type.INT)) {
            node.nextChild();
            int index = visitConstExp(node.getChild());
            return dataType0.values.get(index);
        } else {
            node.nextChild();
            int index0 = visitConstExp(node.getChild());
            node.nextChild();
            int index1 = visitConstExp(node.getChild());
            return dataType0.values.get(index0 * dataType0.capacity + index1);
        }
    }

    private Symbol findSym(String name) {
        SymTable table = curTable;
        do {
            if (!table.symbols.isEmpty()) {
                for (Symbol symbol : table.symbols) {
                    if (symbol.name.equals(name))
                        return symbol;
                }
            }
            table = table.parent;
        } while (table != null);
        return null;
    }

    private void visitVarDecl(GrammarNode node) throws NotMatchException {
        Assert.isOf(node.grammarType, "VarDecl");
        isDecl = true;
        node.nextChild();   // 通过 BType
        Symbol symbol = visitVarDef(node.getChild());
        if (checkReDefine(symbol.name)) {
            ErrorLog.ERRORLIST.add(new ErrorLog(node.getTokenLine(), 'b'));
        } else {
            curTable.addSymbol(symbol);
        }
        node.nextChild();
        while (node.getTokenValue().equals(",")) {
            node.nextChild();
            symbol = visitVarDef(node.getChild());
            if (checkReDefine(symbol.name)) {
                ErrorLog.ERRORLIST.add(new ErrorLog(node.getTokenLine(), 'b'));
            } else {
                curTable.addSymbol(symbol);
            }
            node.nextChild();
        }
        isDecl = false;
    }

    private Symbol visitVarDef(GrammarNode node) throws NotMatchException {
        Symbol symbol = new Symbol();
        symbol.setType(Type.VAR);
        symbol.setName(node.getTokenValue());
        node.nextChild();   // 通过变量名
        symbol.dataType = new DataType(Type.INT);
        if (node.hasNextChild()) {
            while (node.hasNextChild() && node.getTokenValue().equals("[")) {
                node.nextChild();   // 通过 '['
                DataType new_dataType = new DataType(Type.ARRAY);
                new_dataType.capacity = visitConstExp(node.getChild());
                node.nextChild();   // 通过常数
                node.nextChild();   // 通过 ']'
                new_dataType.content = symbol.dataType;
                symbol.dataType = new_dataType;
            }
            if (node.hasNextChild() && node.getTokenValue().equals("=")) {
                node.nextChild();
                visitInitVal(node.getChild());
            }
        }
        return symbol;
    }

    private void visitInitVal(GrammarNode node) throws NotMatchException {
        switch (node.type) {
            case ARRAYINIT:
                node.nextChild();
                if (!node.getTokenValue().equals("}")) {
                    visitInitVal(node.getChild());
                    node.nextChild();
                    while (node.getTokenValue().equals(",")) {
                        node.nextChild();
                        visitInitVal(node.getChild());
                        node.nextChild();
                    }
                }
                break;
            case EXP:
                visitExp(node.getChild());
                break;
            default:
                throw new RuntimeException("Error In Visit InitVal");
        }
    }


    private void visitFunc(GrammarNode node) throws NotMatchException {
        isFun = true;

        curTable = new SymTable(curTable);  // 构造子符号表
        Symbol funcSymbol = new Symbol();

        funcSymbol.setFunc(true);

        Function function = new Function();
        curFunc = function;
        funcSymbol.function = function;
        curTable.addSymbol(funcSymbol);
        // 处理函数头
        if (node.type.equals(Type.MAIN_FUNC_DEF)) {
            function.retType = new DataType(Type.INT);
            node.nextChild();
            function.name = "main";
            funcSymbol.name = "main";
        } else {
            function.retType = new DataType(Type.valueOf(node.getFirstTokenValue().toUpperCase())); // VOID 或 INT
            node.nextChild();   // Ident
            function.name = node.getTokenValue();
            funcSymbol.name = node.getTokenValue();
        }
        funcSymbol.dataType = new DataType(function.retType.type);

        node.nextChild(); // 通过函数名
        node.nextChild(); // 通过 '('
        if (!Judge.isOf(node.getTokenValue(), ")")) { // 存在参数
            function.funcFParams = visitFParams(node.getChild());
            node.nextChild();
        }
        node.nextChild(); // 通过 ')'
        checkReturn(node.getChild());   // 函数有无return判断
        visitBlock(node.getChild(), false); // 分析函数体
        curTable = curTable.parent;         // 返回上一级符号表
        if (checkReDefine(funcSymbol.name)) {
            ErrorLog.ERRORLIST.add(new ErrorLog(node.getTokenLine(), 'b'));
        } else {
            funcSymbol.setType(Type.FUNC);
            curTable.addSymbol(funcSymbol);
        }
        isFun = false;
        curFunc = null;
    }

    private void checkReturn(GrammarNode node) {    // 接受 block node
        if (curFunc.retType.type.equals(Type.VOID)) {
            return;
        }
        ArrayList<GrammarNode> list = node.childs;
        if (!list.get(list.size() - 2).getFirstTokenValue().equals("return")) {
            ErrorLog.ERRORLIST.add(new ErrorLog(list.get(list.size() - 1).getTokenLine(), 'g'));
        }
    }

    private ArrayList<FuncFParam> visitFParams(GrammarNode node) throws NotMatchException {
        Assert.isOf(node.grammarType, "FuncFParams");
        ArrayList<FuncFParam> funcFParams = new ArrayList<>();
        for (GrammarNode child: node.childs) {
            if (child.getFirstTokenValue().equals(",")) {
                continue;
            }
            FuncFParam funcFParam = visitFParam(child);
            Symbol symbol = new Symbol(funcFParam);
            if (checkReDefine(symbol.name)) {
                ErrorLog.ERRORLIST.add(new ErrorLog(node.getTokenLine(), 'b'));
            } else {
                curTable.addSymbol(symbol);
            }
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
        switch (node.type) {
            case DECL:
                visitDecl(node.getChild());
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
                visitFor(node);
                break;
            case WHILE_STMT:    // 无需实现
                break;
            case BREAK_STMT:
                if (!isFor) {
                    ErrorLog.ERRORLIST.add(new ErrorLog(node.getTokenLine(), 'm'));
                }
                break;
            case CONTINUE_STMT:
                if (!isFor) {
                    ErrorLog.ERRORLIST.add(new ErrorLog(node.getTokenLine(), 'm'));
                }
                break;
            case RETURN_STMT:
                visitReturn(node);
                break;
            case PRINTF_STMT:
                visitPrintf(node);
                break;
            case GETINT_STMT:
                assertLValIsNotCon(node.getChild());
                break;
            case ASSIGN_STMT:
                assertLValIsNotCon(node.getChild());
                visitLVal(node.getChild());
                node.nextChild();
                node.nextChild();   // 通过 '='
                visitExp(node.getChild());
                break;
            case EXP_STMT:
                visitExp(node.getChild());
                break;
            case EMPTY_STMT:
                break;
        }
    }

    private void assertLValIsNotCon(GrammarNode node) {
        if (!node.grammarType.equals("LVal")) {
            return;
        }
        String name = node.getFirstTokenValue();
        Symbol symbol = findSym(name);
        if (symbol != null && symbol.isConst()) {
            ErrorLog.ERRORLIST.add(new ErrorLog(node.getTokenLine(), 'h'));
        }
    }

    private void visitPrintf(GrammarNode node) throws NotMatchException {
        node.nextChild();   // printf
        node.nextChild();   // (
        int num = getFormatNum(node.getTokenValue());
        node.nextChild();   // format string
        int cnt = 0;
        while (node.getTokenValue().equals(",")) {
            cnt++;
            node.nextChild();   // ,
            visitExp(node.getChild());
            node.nextChild();
        }
        if (cnt != num) {
            ErrorLog.ERRORLIST.add(new ErrorLog(node.getTokenLine(), 'l'));
        }
    }

    private int getFormatNum(String str) {
        int cnt = 0;
        while (str.contains("%d")) {
            cnt++;
            str = str.substring(str.indexOf("%d") + 2);
        }
        return cnt;
    }

    private void visitReturn(GrammarNode node) throws NotMatchException {
        node.nextChild();
        if (!node.getTokenValue().equals(";") && curFunc.retType.type.equals(Type.VOID)) {  // void 函数有返回值
            ErrorLog.ERRORLIST.add(new ErrorLog(node.getTokenLine(), 'f'));
        }
        if (!node.getTokenValue().equals(";")) {
            visitExp(node.getChild());
        }
    }

    private void visitFor(GrammarNode node) throws NotMatchException {
        boolean t = isFor;
        isFor = true;
        node.nextChild();   // 通过 for
        node.nextChild();   // 通过 (
        if (!node.getTokenValue().equals(";")) {
            visitForStmt(node.getChild());
            node.nextChild();
        }
        node.nextChild();   // 通过 ;
        if (!node.getTokenValue().equals(";")) {
            visitCond(node.getChild());
            node.nextChild();
        }
        node.nextChild();   // 通过 ;
        if (!node.getTokenValue().equals(")")) {
            visitForStmt(node.getChild());
            node.nextChild();
        }
        node.nextChild();   // 通过 )
        curTable = new SymTable(curTable);
        visitStmt(node.getChild());
        curTable = curTable.parent;
        isFor = t;
    }

    private void visitForStmt(GrammarNode node) throws NotMatchException {
        assertLValIsNotCon(node.getChild());
        visitLVal(node.getChild());
        node.nextChild();
        node.nextChild();
        visitExp(node.getChild());
    }

    private void visitIfStmt(GrammarNode node) throws NotMatchException {
        node.nextChild();   // 通过 if
        node.nextChild();   // 通过 (
        visitCond(node.getChild());
        node.nextChild();
        node.nextChild();   // 通过 )

        curTable = new SymTable(curTable);
        visitStmt(node.getChild());
        node.nextChild();
        curTable = curTable.parent;

        if (node.hasNextChild() && node.getTokenValue().equals("else")) {
            node.nextChild();

            curTable = new SymTable(curTable);
            visitStmt(node.getChild());
            node.nextChild();
            curTable = curTable.parent;
        }
    }

    private void visitCond(GrammarNode node) throws NotMatchException {
        Assert.isOf(node.grammarType, "Cond");
        visitLOrExp(node.getChild());
    }

    private void visitLOrExp(GrammarNode node) throws NotMatchException {
        Assert.isOf(node.grammarType, "LOrExp");
        visitLAndExp(node.getChild());
        node.nextChild();
        while (node.hasNextChild()) {
            node.nextChild();   // 通过 ||
            visitLAndExp(node.getChild());
            node.nextChild();
        }
    }

    private void visitLAndExp(GrammarNode node) throws NotMatchException {
        Assert.isOf(node.grammarType, "LAndExp");
        visitEqExp(node.getChild());
        node.nextChild();
        while (node.hasNextChild()) {
            node.nextChild();
            visitEqExp(node.getChild());
            node.nextChild();
        }
    }

    private void visitEqExp(GrammarNode node) throws NotMatchException {
        Assert.isOf(node.grammarType, "EqExp");
        visitRelExp(node.getChild());
        node.nextChild();
        while (node.hasNextChild()) {
            node.nextChild();
            visitRelExp(node.getChild());
            node.nextChild();
        }
    }

    private void visitRelExp(GrammarNode node) throws NotMatchException {
        Assert.isOf(node.grammarType, "RelExp");
        visitAddExp(node.getChild());
        node.nextChild();
        while (node.hasNextChild()) {
            node.nextChild();
            visitAddExp(node.getChild());
            node.nextChild();
        }
    }

    private void visitExp(GrammarNode node) throws NotMatchException {
        Assert.isOf(node.grammarType, "Exp");
        visitAddExp(node.getChild());
    }

    private void visitAddExp(GrammarNode node) throws NotMatchException {
        Assert.isOf(node.grammarType, "AddExp");
        visitMulExp(node.getChild());
        node.nextChild();
        while (node.hasNextChild()) {
            // 应该对运算符号做一些处理，先不管了（
            node.nextChild();   // 通过 '+', '-'
            visitMulExp(node.getChild());   // BNF
            node.nextChild();
        }
    }

    private void visitMulExp(GrammarNode node) throws NotMatchException {
        Assert.isOf(node.grammarType, "MulExp");
        visitUnaryExp(node.getChild());
        node.nextChild();
        while (node.hasNextChild()) {
            node.nextChild();   // 通过 '*' '/'
            visitUnaryExp(node.getChild());
            node.nextChild();
        }
    }

    private void visitUnaryExp(GrammarNode node) throws NotMatchException {
        Assert.isOf(node.grammarType, "UnaryExp");
        String funcName;
        char type = 'z';
        boolean isErr = false;
        switch (node.type) {
            case OP_EXP:
                node.nextChild();   // 符号
                visitUnaryExp(node.getChild());
                break;
            case FUNC_CALL:
                if (findSym(node.getTokenValue()) == null) {
                    ErrorLog.ERRORLIST.add(new ErrorLog(node.getTokenLine(), 'c'));
                }
                funcName = node.getTokenValue();
                node.nextChild();   // 通过函数名
                
                node.nextChild();   // 通过 '('
                if (node.getChild().grammarType.equals("FuncRParams")) {
                    try {
                        visitFuncRParams(node.getChild(), funcName);
                    } catch (NotMatchException e) {
                        type = e.type.charAt(0);
                        isErr = true;
                    }
                    node.nextChild();
                }
                if (isErr) {
                    ErrorLog.ERRORLIST.add(new ErrorLog(node.getTokenLine(), type));
                }
                node.nextChild();   // 通过 ')'
                break;
            case PRIMARY_EXP:
                visitPrimaryExp(node.getChild());
                break;
        }
    }

    private void visitPrimaryExp(GrammarNode node) throws NotMatchException {
        Assert.isOf(node.grammarType, "PrimaryExp");
        switch (node.type) {
            case WITH_BRACKET:  // '(' Exp ')'
                node.nextChild();   // 通过 '('
                visitExp(node.getChild());
                break;
            case IDENFR:
                visitLVal(node.getChild());
                break;
            case INTCON:    // 错误处理不做处理
                break;

        }
    }

    private void visitLVal(GrammarNode node) throws NotMatchException {
        Assert.isOf(node.grammarType, "LVal");
        // 检查名字是否存在于符号表中
        if (!checkLValIsSymbol(node.getFirstTokenValue())) { // c 类错误
            ErrorLog.ERRORLIST.add(new ErrorLog(
                    node.getFirstTokenLine(),
                    'c'
            ));
        }
    }

    private boolean checkLValIsSymbol(String name) {
        SymTable table = curTable;

        do {
            if (!table.symbols.isEmpty()) {
                for (Symbol symbol : table.symbols) {
                    if (!symbol.isFunc() && symbol.name.equals(name))
                        return true;
                }
            }
            table = table.parent;
        } while (table != null);

        return false;
    }

    //
    private void visitFuncRParams(GrammarNode node, String name) throws NotMatchException {
        Symbol funcSym = findSym(name);
        if (funcSym == null) {
            return;
        }
        Function function = funcSym.function;
        if (function.funcFParams.size() != (node.childs == null ? 0 : node.childs.size() + 1) / 2) {
            throw new NotMatchException("d");
        }
        int cnt = 0;
        while (cnt < function.funcFParams.size()) {
            Symbol fParam = new Symbol(function.funcFParams.get(cnt));
            cnt++;
            checkFuncRParam(fParam, node.getChild());
            visitExp(node.getChild());
            node.nextChild();
            node.nextChild();
        }
    }

    private void checkFuncRParam(Symbol symbol, GrammarNode expNode) {  // symbol 为函数形参类型
        ArrayList<GrammarNode> unaryList = flatUnaryExp(expNode);
        DataType dataType = getUnaryDataType(unaryList.get(0));
        if (!dataType.equalsIgnoreCapacity(symbol.dataType)) {
            ErrorLog.ERRORLIST.add(new ErrorLog(expNode.getTokenLine(), 'e'));
        }
    }

    private DataType getUnaryDataType(GrammarNode node) {
        switch (node.type) {
            case FUNC_CALL:
                return findSym(node.getFirstTokenValue()).dataType;
            case INTCON:
                return new DataType(Type.INT);
            case LVAL:
                DataType type = findSym(node.getFirstTokenValue()).dataType;
                int cnt = node.childs.size() - 1;
                while (cnt > 0) {
                    type = type.content;
                    cnt -= 3;
                }
                return type;
        }
        return null;
    }

    private ArrayList<GrammarNode> flatUnaryExp(GrammarNode node) { // 扁平化Exp
        ArrayList<GrammarNode> list = new ArrayList<>();

        int cnt = 0;
        switch (node.grammarType) {
            case "Exp":
                list.addAll(flatUnaryExp(node.getChild()));
                return list;
            case "AddExp":
            case "MulExp":
                while (cnt < node.childs.size()) {
                    list.addAll(flatUnaryExp(node.childs.get(cnt)));
                    cnt += 2;
                }
                return list;
            case "UnaryExp":
                switch (node.type) {
                    case FUNC_CALL:
                        list.add(node);
                        return list;
                    case PRIMARY_EXP:
                        list.addAll(flatUnaryExp(node.childs.get(0)));
                        return list;
                    case OP_EXP:
                        list.addAll(flatUnaryExp(node.childs.get(1)));
                        return list;
                }
            case "PrimaryExp":
                switch (node.type) {
                    case WITH_BRACKET:
                        list.addAll(flatUnaryExp(node.childs.get(1)));
                        return list;
                    case IDENFR:
                    case INTCON:
                        list.add(node.childs.get(0));
                        return list;
                }

        }

        return list;
    }

}
