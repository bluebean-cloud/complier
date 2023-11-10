package executor;

import lexer.TokenType;
import parser.Parser;
import parser.syntaxTreeNodes.*;
import util.GlobalConfigure;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Executor {
    public static final Executor EXECUTOR = new Executor();
    private Executor() {}

    RuntimeStack globalHeap = new RuntimeStack();
    RuntimeStack curStack = globalHeap;
    Scanner scanner;
    StringBuilder stringBuilder = new StringBuilder();
    Random random = new Random();

    public void run() {
        if (GlobalConfigure.DEBUG) {
            try {
                scanner = new Scanner(new File("inputfile.txt"));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            scanner = new Scanner(System.in);
        }
        CompUnit root = Parser.PARSER.root;
        for (ConstDef constDef: Parser.PARSER.curScope.constDefs) {
            curStack.addVar(constDef);
        }   // 添加全局变量
        for (VarDef varDef: Parser.PARSER.curScope.varDefs) {
            curStack.addVar(varDef);
        }
        int mainValue = interpretMainFunc(root.mainFuncDef);
        try (PrintWriter output = new PrintWriter("pcoderesult.txt")) {
            output.print(stringBuilder.toString());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println(stringBuilder.toString());
    }

    public int interpretMainFunc(MainFuncDef mainFuncDef) {
        int returnValue = 0;
        curStack = new RuntimeStack(curStack);  // 创建新的函数栈
        try {   // 接收到返回值
            for (BlockItem blockItem : mainFuncDef.block.items) {
                switch (blockItem.type) {
                    case Decl:
                        interpretDecl(blockItem.decl);
                        break;
                    case Stmt:
                        interpretStmt(blockItem.stmt);
                        break;
                    default:
                        throw new RuntimeException("Unknown type: " + blockItem.type);
                }
            }
        } catch (ReturnException value) {
            returnValue = value.value;
        } finally {
            curStack = curStack.parent;
        }
        return returnValue;
    }

    public void interpretDecl(Decl decl) {
        if (decl.varDecl != null) {
            for (VarDef varDef: decl.varDecl.varDefs) {
                interpretVarDef(varDef);
            }
        } else {
            for (ConstDef constDef: decl.constDecl.constDefs) {
                interpretConstDef(constDef);
            }
        }
    }

    public void interpretVarDef(VarDef varDef) {
        Var var;
        if (varDef.constExp1 == null) {
            var = new Var(RuntimeType.SIMPLE_VAR);
            if (varDef.initVal != null) {
                var.value = interpretExp(varDef.initVal.exp);
            } else {
                var.value = random.nextInt();
            }
        } else {    // 数组
            var = new Var(RuntimeType.ARRAY);
            if (varDef.initVal != null) {   // 有初始值
                var.arrayList = interpretInitVals(varDef.initVal.initVals);
            } else {                        // 无初始值，全部初始成 0
                int x = interpretConstExp(varDef.constExp1);
                ArrayList<Var> vars = new ArrayList<>();
                if (varDef.constExp2 != null) {
                    int y = interpretConstExp(varDef.constExp2);
                    for (int i = 0; i < x; i++) {
                        Var varSon = new Var(RuntimeType.ARRAY, new ArrayList<>());
                        for (int j = 0; j < y; j++) {
                            varSon.arrayList.add(new Var(RuntimeType.INT_CON, 0));
                        }
                        vars.add(varSon);
                    }
                } else {
                    for (int i = 0; i < x; i++) {
                        vars.add(new Var(RuntimeType.INT_CON, 0));
                    }
                }
                var.arrayList = vars;
            }
        }
        curStack.addVar(varDef.getName(), var);
    }

    public void interpretConstDef(ConstDef constDef) {
        Var var;
        if (constDef.constExp1 == null) {
            var = new Var(RuntimeType.SIMPLE_VAR);
            var.value = interpretConstExp(constDef.constInitVal.constExp);
        } else {    // 数组
            var = new Var(RuntimeType.ARRAY);
            // constVar 一定有初值
            var.arrayList = interpretConstInitVals(constDef.constInitVal.constInitVals);
        }
        curStack.addVar(constDef.getName(), var);
    }

    public ArrayList<Var> interpretInitVals(ArrayList<InitVal> initVals) {
        ArrayList<Var> vars = new ArrayList<>();
        for (InitVal initVal: initVals) {
            Var var;
            if (initVal.exp == null) {
                var = new Var(RuntimeType.ARRAY);
                var.arrayList = interpretInitVals(initVal.initVals);
            } else {
                var = new Var(RuntimeType.INT_CON);
                var.value = interpretExp(initVal.exp);
            }
            vars.add(var);
        }
        return vars;
    }

    public ArrayList<Var> interpretConstInitVals(ArrayList<ConstInitVal> constInitVals) {
        ArrayList<Var> vars = new ArrayList<>();
        for (ConstInitVal constInitVal: constInitVals) {
            Var var;
            if (constInitVal.constExp == null) {
                var = new Var(RuntimeType.ARRAY);
                var.arrayList = interpretConstInitVals(constInitVal.constInitVals);
            } else {
                var = new Var(RuntimeType.INT_CON);
                var.value = interpretConstExp(constInitVal.constExp);
            }
            vars.add(var);
        }
        return vars;
    }

    public int interpretExp(Exp exp) {
        return interpretAddExp(exp.addExp);
    }

    private int interpretAddExp(AddExp addExp) {
        int result = interpretMulExp(addExp.mulExps.get(0));
        for (int i = 0; i < addExp.ops.size(); i++) {
            switch (addExp.ops.get(i).tokenType) {
                case PLUS:
                    result += interpretMulExp(addExp.mulExps.get(i + 1));
                    break;
                case MINU:
                    result -= interpretMulExp(addExp.mulExps.get(i + 1));
                    break;
            }
        }
        return result;
    }

    private int interpretMulExp(MulExp mulExp) {
        int result = interpretUnaryExp(mulExp.unaryExps.get(0));
        for (int i = 0; i < mulExp.ops.size(); i++) {
            switch (mulExp.ops.get(i).tokenType) {
                case MULT:
                    result *= interpretUnaryExp(mulExp.unaryExps.get(i + 1));
                    break;
                case DIV:
                    result /= interpretUnaryExp(mulExp.unaryExps.get(i + 1));
                    break;
                case MOD:
                    result %= interpretUnaryExp(mulExp.unaryExps.get(i + 1));
                    break;
            }
        }
        return result;
    }

    private int interpretUnaryExp(UnaryExp unaryExp) {
        int sign = 1;
        while (unaryExp.type.equals(SyntaxType.UnaryOp)) {
            if (unaryExp.unaryOp.op.tokenType.equals(TokenType.MINU)) {
                sign *= -1;
            } else if (unaryExp.unaryOp.op.tokenType.equals(TokenType.NOT)) {
                sign = 0;
            }
            unaryExp = unaryExp.unaryExp;
        }
        switch (unaryExp.type) {
            case PrimaryExp:
                int value = interpretPrimaryExp(unaryExp.primaryExp);
                if (sign == 0) {
                    return value == 0 ? 1 : 0;  // 取反
                }
                return sign * value;
            case FuncCall:
                RuntimeStack temStack = curStack;   // 保存运行栈
                int returnValue = sign * callFunc(unaryExp.ident.content, unaryExp.funcRParams);
                curStack = temStack;
                return  returnValue;
        }
        return 0;
    }

    private int interpretPrimaryExp(PrimaryExp primaryExp) {
        switch (primaryExp.type) {
            case Exp:
                return interpretExp(primaryExp.exp);
            case Number:
                return primaryExp.number.getNumber();
            case LVal:
                return interpretLVal(primaryExp.lVal);
        }
        return 0;
    }

    private int interpretLVal(LVal lVal) {
        Var var = findVar(lVal.getName());
        if (lVal.exp1 == null) {    // 为变量
            if (var.value == null) {
                System.out.println("NULL: " + lVal.getName());
            }
            return var.value;
        }
        int x = interpretExp(lVal.exp1);
        if (lVal.exp2 == null) {
            return var.getVar(x).value;
        }
        int y = interpretExp(lVal.exp2);
        return var.getVar(x, y).value;
    }

    public int callFunc(String name, FuncRParams funcRParams) {
        RuntimeStack temStack = new RuntimeStack(curStack);
        FuncDef funcDef = Parser.PARSER.findFuncDef(name);
        FuncFParams funcFParams = funcDef.funcFParams;
        if (funcFParams != null) {
            for (int i = 0; i < funcFParams.funcFParams.size(); i++) {
                if (funcFParams.funcFParams.get(i).deep == 0) { // 朴素的 int 类型
                    temStack.addVar(funcFParams.funcFParams.get(i).getName(),
                            new Var(RuntimeType.SIMPLE_VAR, interpretExp(funcRParams.exps.get(i))));
                } else {    // 数组类型
                    temStack.addVar(funcFParams.funcFParams.get(i).getName(),
                            interpretExp(funcRParams.exps.get(i), 0));  // 0 作为占位符
                }
            }
        }
        curStack = temStack;
        try {
            for (BlockItem blockItem : funcDef.block.items) {
                switch (blockItem.type) {
                    case Decl:
                        interpretDecl(blockItem.decl);
                        break;
                    case Stmt:
                        interpretStmt(blockItem.stmt);
                }
            }
        } catch (ReturnException e) {
            curStack = null;
            return e.value;
        }
        curStack = null;
        return 0;
    }

    public void interpretStmt(Stmt stmt) {
        switch (stmt.type) {
            case Empty:
                return;
            case Exp:
                interpretExp(stmt.exp);
                return;
            case Block:
                interpretBlock(stmt);
                return;
            case Assign:
                interpretAssign(stmt);
                return;
            case If:
                interpretIf(stmt);
                break;
            case For:
                interpretFor(stmt);
                break;
            case Break:
                throw new ControlFlowException(ControlFlowException.Type.BREAK);
            case Continue:
                throw new ControlFlowException(ControlFlowException.Type.CONTINUE);
            case Return:
                interpretReturn(stmt);
                break;
            case GetInt:
                interpretGetInt(stmt);
                break;
            case Printf:
                interPrintf(stmt);
        }
    }

    private void interPrintf(Stmt stmt) {
        String string = stmt.formatString.content;
        int cnt = 0;
        for (int i = 1; i < string.length() - 1; i++) {
            if (string.charAt(i) == '%' && string.charAt(i + 1) == 'd') {
                stringBuilder.append(interpretExp(stmt.exps.get(cnt)));
                cnt++;
                i++;
            } else if (string.charAt(i) == '\\' && string.charAt(i + 1) == 'n') {
                stringBuilder.append('\n');
                i++;
            } else {
                stringBuilder.append(string.charAt(i));
            }
        }
    }

    private void interpretGetInt(Stmt stmt) {
        LVal lVal = stmt.lVal;
        Var var = findVar(lVal.getName());
        int number = scanner.nextInt();
        if (lVal.exp1 == null) {    // 普通的变量
            var.setValue(number);
            return;
        }
        int x = interpretExp(lVal.exp1);
        if (lVal.exp2 == null) {    // 一维数组
            var.getVar(x).setValue(number);
            return;
        }
        int y = interpretExp(lVal.exp2);
        var.getVar(x, y).setValue(number);
    }

    public void interpretReturn(Stmt stmt) {
        if (stmt.exp != null) {
            throw new ReturnException(interpretExp(stmt.exp));
        }
        throw new ReturnException(0);
    }

    public void interpretBlock(Stmt stmt) {
        curStack = new RuntimeStack(curStack);
        try {
            for (BlockItem blockItem : stmt.block.items) {
                switch (blockItem.type) {
                    case Decl:
                        interpretDecl(blockItem.decl);
                        break;
                    case Stmt:
                        interpretStmt(blockItem.stmt);
                }
            }
            curStack = curStack.parent;
        } catch (ControlFlowException e) {
            curStack = curStack.parent;
            throw e;
        }
    }

    public void interpretAssign(Stmt stmt) {
        // LVal = exp
        LVal lVal = stmt.lVal;
        Var var = findVar(lVal.getName());
        if (lVal.exp1 == null) {
            var.setValue(interpretExp(stmt.exp));
            return;
        }
        int x = interpretExp(lVal.exp1);
        if (lVal.exp2 == null) {
            var.getVar(x).setValue(interpretExp(stmt.exp));
            return;
        }
        int y = interpretExp(lVal.exp2);
        var.getVar(x, y).setValue(interpretExp(stmt.exp));
    }

    public void interpretIf(Stmt stmt) {
        int cond = interpretCond(stmt.cond);
        if (cond != 0) {
            interpretStmt(stmt.stmt1);
        } else if (stmt.stmt2 != null) {
            interpretStmt(stmt.stmt2);
        }
    }

    public void interpretFor(Stmt stmt) {
        if (stmt.forStmt1 != null) {
            interpretForStmt(stmt.forStmt1);
        }
        while (stmt.cond == null || interpretCond(stmt.cond) != 0) {
            try {
                interpretStmt(stmt.stmt1);
            } catch (ControlFlowException e) {
                if (e.type.equals(ControlFlowException.Type.BREAK)) {
                    return;
                }
            }
            if (stmt.forStmt2 != null) {
                interpretForStmt(stmt.forStmt2);
            }
        }
    }

    private void interpretForStmt(ForStmt forStmt) {
        LVal lVal = forStmt.lVal;
        Var var = findVar(lVal.getName());
        if (lVal.exp1 == null) {
            var.setValue(interpretExp(forStmt.exp));
            return;
        }
        int x = interpretExp(lVal.exp1);
        if (lVal.exp2 == null) {
            var.getVar(x).setValue(interpretExp(forStmt.exp));
            return;
        }
        int y = interpretExp(lVal.exp2);
        var.getVar(x, y).setValue(interpretExp(forStmt.exp));
    }

    private int interpretCond(Cond cond) {
        return interpretLOrExp(cond.lOrExp);
    }

    private int interpretLOrExp(LOrExp lOrExp) {
        for (LAndExp lAndExp: lOrExp.andExps) {
            if (interpretLAndExp(lAndExp) != 0) {
                return 1;
            }
        }
        return 0;
    }

    private int interpretLAndExp(LAndExp lAndExp) {
        for (EqExp eqExp: lAndExp.eqExps) {
            if (interpretEqExp(eqExp) == 0) {
                return 0;
            }
        }
        return 1;
    }

    private int interpretEqExp(EqExp eqExp) {
        int cond = interpretRelExp(eqExp.relExps.get(0));
        for (int i = 0; i < eqExp.ops.size(); i++) {
            switch (eqExp.ops.get(i).tokenType) {
                case EQL:
                    cond = cond == interpretRelExp(eqExp.relExps.get(i + 1)) ? 1 : 0;
                    break;
                case NEQ:
                    cond = cond != interpretRelExp(eqExp.relExps.get(i + 1)) ? 1 : 0;
            }
        }
        return cond;
    }

    private int interpretRelExp(RelExp relExp) {
        int cond = interpretAddExp(relExp.addExps.get(0));
        for (int i = 0; i < relExp.ops.size(); i++) {
            switch (relExp.ops.get(i).tokenType) {
                case LSS:
                    cond = cond < interpretAddExp(relExp.addExps.get(i + 1)) ? 1 : 0;
                    break;
                case LEQ:
                    cond = cond <= interpretAddExp(relExp.addExps.get(i + 1)) ? 1 : 0;
                    break;
                case GRE:
                    cond = cond > interpretAddExp(relExp.addExps.get(i + 1)) ? 1 : 0;
                    break;
                case GEQ:
                    cond = cond >= interpretAddExp(relExp.addExps.get(i + 1)) ? 1 : 0;
                    break;
            }
        }
        return cond;
    }

    public int interpretConstExp(ConstExp constExp) {
        return interpretAddExp(constExp.addExp);
    }

    public Var interpretExp(Exp exp, int i) {   // 获取数组指针
        PrimaryExp primaryExp = exp.getFirstUnaryExp().primaryExp;  // 一定是 PrimaryExp
        while (!primaryExp.type.equals(SyntaxType.LVal)) {
            primaryExp = primaryExp.exp.getFirstUnaryExp().primaryExp;
        }
        LVal lVal = primaryExp.lVal;
        Var var = findVar(lVal.getName());
        if (lVal.exp1 == null) {
            return var;
        }
        int x = interpretExp(lVal.exp1);
        return var.getVar(x);
    }

    public Var findVar(String name) {
        RuntimeStack temStack = curStack;
        while (temStack != null) {
            if (temStack.contains(name)) {
                return temStack.getVar(name);
            }
            temStack = temStack.parent;
        }
        throw new RuntimeException("Not Define: " + name);
    }

}
