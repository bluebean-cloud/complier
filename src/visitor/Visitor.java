package visitor;

import lexer.TokenType;
import mir.Instruction;
import mir.Manager;
import mir.Value;
import mir.ValueType;
import mir.derivedValue.BasicBlock;
import mir.derivedValue.Function;
import mir.derivedValue.GlobalVar;
import parser.Parser;
import parser.syntaxTreeNodes.*;

import java.util.ArrayList;

public class Visitor {
    public static final Visitor VISITOR = new Visitor();
    private Visitor() {}
    private MIRStack globalHeap = new MIRStack();
    private MIRStack curStack = globalHeap;
    private BasicBlock curBlock = null;
    private Function curFunction = null;


    public void run() {
        CompUnit root = Parser.PARSER.root;
        for (ConstDef constDef: Parser.PARSER.curScope.constDefs) {
            visitConstDef(constDef);
        }
        for (VarDef varDef: Parser.PARSER.curScope.varDefs) {
            visitGlobalVarDef(varDef);
        }

        for (FuncDef funcDef: root.funcDefs) {
            visitFuncDef(funcDef);
        }

        visitMainFuncDef(root.mainFuncDef);
    }


    private void visitFuncDef(FuncDef funcDef) {
        curBlock = new BasicBlock(new ValueType(ValueType.Type.BLOCK), "__" + funcDef.getFuncName() + "__");
        curFunction = new Function(new ValueType(ValueType.Type.FUNCTION), "@" + funcDef.getFuncName());
        curFunction.addBlock(curBlock);
        if (funcDef.funcType.type.tokenType.equals(TokenType.VOIDTK)) {
            curFunction.retType = new ValueType(ValueType.Type.VOID);
        } else {
            curFunction.retType = new ValueType(ValueType.Type.I32);
        }

        curStack = new MIRStack(curStack);
        if (funcDef.funcFParams != null) {
            for (FuncFParam funcFParam: funcDef.funcFParams.funcFParams) {
                visitFuncFParam(funcFParam);
            }
        }

        for (BlockItem item: funcDef.block.items) {
            switch (item.type) {
                case Decl:
                    visitDecl(item.decl);
                    break;
                case Stmt:
                    visitStmt(item.stmt);
                    break;
            }
        }
        Manager.MANAGER.addFunction(curFunction);
        curStack = curStack.parent;
    }

    private void visitMainFuncDef(MainFuncDef mainFuncDef) {
        curBlock = new BasicBlock(new ValueType(ValueType.Type.BLOCK), "__main__");
        curFunction = new Function(new ValueType(ValueType.Type.FUNCTION), "@main");
        curFunction.addBlock(curBlock);
        curFunction.retType = new ValueType(ValueType.Type.I32);
        curStack = new MIRStack(curStack);

        for (BlockItem item: mainFuncDef.block.items) {
            switch (item.type) {
                case Decl:
                    visitDecl(item.decl);
                    break;
                case Stmt:
                    visitStmt(item.stmt);
                    break;
            }
        }

        Manager.MANAGER.addFunction(curFunction);
        curStack = curStack.parent;
    }

    private void visitFuncFParam(FuncFParam funcFParam) {
        MirVar mirVar;
        Value value;
        switch (funcFParam.deep) {
            case 0:
                mirVar = new MirVar(MirVar.Type.LOCAL_VAL, funcFParam.getName());
                value = new Value(new ValueType(ValueType.Type.I32), "%" + funcFParam.getName() + "_" + curFunction.cnt);
                curFunction.cnt++;
                mirVar.funcFParam = value;
                break;
            case 1:
                mirVar = new MirVar(MirVar.Type.LOCAL_ARRAY, funcFParam.getName());
                break;
            case 2:
                mirVar = new MirVar(MirVar.Type.LOCAL_ARRAY, funcFParam.getName());
                break;
            default:
                throw new RuntimeException("FuncFParam: deep is " + funcFParam.deep);
        }
    }

    private void visitDecl(Decl decl) {
        if (decl.constDecl != null) {   // 为常量
            for (ConstDef constDef: decl.constDecl.constDefs) {
                visitConstDef(constDef);
            }
        } else {    // 不为常量
            for (VarDef varDef: decl.varDecl.varDefs) {
                visitVarDef(varDef);
            }
        }
    }

    private void visitStmt(Stmt stmt) {
        switch (stmt.type) {
            case Assign:

            case Exp:

            case Block:

            case If:

            case For:

            case Break:

            case Continue:

            case Return:
                visitReturn(stmt);
                break;
            case GetInt:

            case Printf:

            case Empty:

            default:
                throw new RuntimeException("Unknown type: " + stmt.type);
        }
    }

    private void visitReturn(Stmt stmt) {
        Instruction instruction;
        if (stmt.exp == null) { // ret void;
            instruction = new Instruction(Instruction.InsType.ret, "", new ValueType(ValueType.Type.INS));
        } else {    // ret sth
            Value value = visitExp(stmt.exp);
            instruction = new Instruction(Instruction.InsType.ret, "", new ValueType(ValueType.Type.INS), value);
        }
        curBlock.addInstruction(instruction);
    }

    private void visitVarDef(VarDef varDef) {
        String name = "%" + varDef.getName() + "_" + curFunction.cnt;
        curFunction.cnt++;
        MirVar mirVar;
        ValueType type = new ValueType(ValueType.Type.POINTER);
        ValueType type1 = new ValueType(ValueType.Type.ARRAY);
        ValueType type2 = new ValueType(ValueType.Type.ARRAY);
        Instruction allocAddr;
        if (varDef.constExp1 == null) { // 为 int
            mirVar = new MirVar(MirVar.Type.LOCAL_VAL, varDef.getName());

            type.pointTo = new ValueType(ValueType.Type.I32);
            allocAddr = new Instruction(Instruction.InsType.alloca, name, type);

            if (varDef.initVal != null) {
                Value value = visitExp(varDef.initVal.exp);
                Instruction instruction1 = new Instruction(Instruction.InsType.store, "", null, value, allocAddr);
                curBlock.addInstruction(instruction1);
            }
        } else if (varDef.constExp2 == null) {  // 为 int[]
            mirVar = new MirVar(MirVar.Type.LOCAL_ARRAY, varDef.getName());

            type.pointTo = type1;
            type1.elementType = new ValueType(ValueType.Type.I32);
            type1.size = visitConstExp(varDef.constExp1);
            allocAddr = new Instruction(Instruction.InsType.alloca, name, type);

            if (varDef.initVal != null) {   // 一维数组初始化
                String initName = "%" + varDef.getName() + "_init_" + curFunction.cnt;
                curFunction.cnt++;
                ValueType ptrType = new ValueType(ValueType.Type.POINTER);
                ptrType.pointTo = new ValueType(ValueType.Type.I32);
                Instruction ptr = new Instruction(Instruction.InsType.getelementptr, initName, ptrType, allocAddr, new Value(0, true), new Value(0, true));
                ptr.getEleType = type;
                curBlock.addInstruction(ptr);
                for (InitVal initVal: varDef.initVal.initVals) {
                    Value value = visitExp(initVal.exp);
                    Instruction store = new Instruction(Instruction.InsType.store, "", null, value, ptr);
                    curBlock.addInstruction(store);
                    initName = "%" + varDef.getName() + "_init_" + curFunction.cnt;
                    curFunction.cnt++;
                    // 指针前移
                    ptr = new Instruction(Instruction.InsType.getelementptr, initName, ptrType, ptr, new Value(1, true));
                    ptr.getEleType = ptrType;
                    curBlock.addInstruction(ptr);
                }
            }
        } else {
            mirVar = new MirVar(MirVar.Type.LOCAL_ARRAY, varDef.getName());

            type.pointTo = type1;
            type1.elementType = type2;
            type2.elementType = new ValueType(ValueType.Type.I32);
            type1.size = visitConstExp(varDef.constExp1);
            type2.size = visitConstExp(varDef.constExp2);
            allocAddr = new Instruction(Instruction.InsType.alloca, name, type);

            if (varDef.initVal != null) {   // 二维数组初始化
                String initName = "%" + varDef.getName() + "_init_" + curFunction.cnt;
                curFunction.cnt++;
                int x = visitConstAddExp(varDef.constExp1.addExp);
                int y = visitConstAddExp(varDef.constExp2.addExp);
                ValueType ptrType = new ValueType(ValueType.Type.POINTER);
                ptrType.pointTo = new ValueType(ValueType.Type.I32);
                for (int i = 0; i < x; i++) {
                    for (int j = 0; j < y; j++) {
                        Instruction ptr = new Instruction(Instruction.InsType.getelementptr, initName, ptrType, allocAddr, new Value(x, true), new Value(y, true));
                        ptr.getEleType = type;
                        curBlock.addInstruction(ptr);
                        Value value = visitExp(varDef.initVal.initVals.get(i).initVals.get(j).exp);
                        Instruction store = new Instruction(Instruction.InsType.store, "", null, value, ptr);
                        curBlock.addInstruction(store);

                        initName = "%" + varDef.getName() + "_init_" + curFunction.cnt;
                        curFunction.cnt++;
                    }
                }

            }
        }
        curFunction.addDecl(allocAddr);
        mirVar.addr = allocAddr;
        curStack.addVar(varDef.getName(), mirVar);

    }

    private void visitConstDef(ConstDef constDef) {
        // 构造常量 M_Var
        MirVar mirVar;
        if (constDef.constExp1 == null) {   // 为 int类型
            mirVar = new MirVar(MirVar.Type.CONST_VAR, constDef.getName());
            if (constDef.constInitVal != null) {
                mirVar.integerLiteral = visitConstExp(constDef.constInitVal.constExp);
            } else {
                mirVar.integerLiteral = 0;
            }
        } else {    // 为数组类型，常量一定拥有初始值
            mirVar = new MirVar(MirVar.Type.CONST_ARRAY, constDef.getName());
            mirVar.compoundLiteral = visitConstInitVals(constDef.constInitVal.constInitVals);
        }
        curStack.addVar(constDef.getName(), mirVar);
    }

    private ArrayList<MirVar> visitConstInitVals(ArrayList<ConstInitVal> constInitVals) {
        ArrayList<MirVar> vars = new ArrayList<>();
        for (ConstInitVal constInitVal: constInitVals) {
            MirVar var;
            if (constInitVal.constExp == null) {    // 为数组
                var = new MirVar(MirVar.Type.CONST_ARRAY, "");  // 匿名子数组
                var.compoundLiteral = visitConstInitVals(constInitVal.constInitVals);
            } else {
                var = new MirVar(MirVar.Type.CONST_VAR, visitConstExp(constInitVal.constExp));
            }
            vars.add(var);
        }
        return vars;
    }

    private ArrayList<MirVar> visitGlobalInitVals(ArrayList<InitVal> initVals) {
        // 求全局变量的初始值
        ArrayList<MirVar> vars = new ArrayList<>();
        for (InitVal initVal: initVals) {
            MirVar var;
            if (initVal.exp == null) {  // 为数组
                var = new MirVar(MirVar.Type.GLOBAL_ARRAY, "");
                var.compoundLiteral = visitGlobalInitVals(initVal.initVals);
            } else {
                var = new MirVar(MirVar.Type.GLOBAL_VAL, visitConstAddExp(initVal.exp.addExp));
            }
            vars.add(var);
        }
        return vars;
    }

    private int visitConstExp(ConstExp constExp) {
        return visitConstAddExp(constExp.addExp);
    }

    private int visitConstAddExp(AddExp addExp) {
        int result = visitConstMulExp(addExp.mulExps.get(0));
        for (int i = 0; i < addExp.ops.size(); i++) {
            switch (addExp.ops.get(i).tokenType) {
                case PLUS:
                    result += visitConstMulExp(addExp.mulExps.get(i + 1));
                    break;
                case MINU:
                    result -= visitConstMulExp(addExp.mulExps.get(i + 1));
                    break;
            }
        }
        return result;
    }

    private int visitConstMulExp(MulExp mulExp) {
        int result = visitConstUnaryExp(mulExp.unaryExps.get(0));
        for (int i = 0; i < mulExp.ops.size(); i++) {
            switch (mulExp.ops.get(i).tokenType) {
                case MULT:
                    result *= visitConstUnaryExp(mulExp.unaryExps.get(i + 1));
                    break;
                case DIV:
                    result /= visitConstUnaryExp(mulExp.unaryExps.get(i + 1));
                    break;
                case MOD:
                    result %= visitConstUnaryExp(mulExp.unaryExps.get(i + 1));
                    break;
            }
        }
        return result;
    }

    private int visitConstUnaryExp(UnaryExp unaryExp) {
        int sign = 1;
        while (unaryExp.type.equals(SyntaxType.UnaryOp)) {
            if (unaryExp.unaryOp.op.tokenType.equals(TokenType.MINU)) {
                sign *= -1;
            } else if (unaryExp.unaryOp.op.tokenType.equals(TokenType.NOT)) {
                throw new RuntimeException("Negation is not allowed while defining constants");
            }
            unaryExp = unaryExp.unaryExp;
        }
        switch (unaryExp.type) {
            case PrimaryExp:
                return sign * visitConstPrimaryExp(unaryExp.primaryExp);
            case FuncCall:
                throw new RuntimeException("Functions are not allowed while defining constants");
            default:
                throw new RuntimeException("Unknown type: " + unaryExp.type);
        }
    }

    private int visitConstPrimaryExp(PrimaryExp primaryExp) {
        switch (primaryExp.type) {
            case Exp:
                return visitConstAddExp(primaryExp.exp.addExp);
            case Number:
                return primaryExp.number.getNumber();
            case LVal:
                return visitConstLVal(primaryExp.lVal);
            default:
                throw new RuntimeException("Unknown type: " + primaryExp.type);
        }
    }

    private int visitConstLVal(LVal lVal) {
        MirVar mirVar = findVarValue(lVal.getName());
        if (lVal.exp1 == null) {    // 为变量
            return mirVar.integerLiteral;
        }
        int x = visitConstAddExp(lVal.exp1.addExp);
        if (lVal.exp2 == null) {    // 一维数组
            return mirVar.getVar(x).integerLiteral;
        }
        int y = visitConstAddExp(lVal.exp2.addExp);
        return mirVar.getVar(x, y).integerLiteral;
    }

    private void visitGlobalVarDef(VarDef varDef) {
        // 生成分配指令，并添加到符号表中
        MirVar mirVar;
        ValueType type = new ValueType(ValueType.Type.POINTER);
        GlobalVar globalVar = new GlobalVar(type, varDef.getName());
        if (varDef.constExp1 == null) { // 为普通变量
            mirVar = new MirVar(MirVar.Type.GLOBAL_VAL, varDef.getName());
            type.pointTo = new ValueType(ValueType.Type.I32);
            if (varDef.initVal != null) {
                mirVar.integerLiteral = visitConstAddExp(varDef.initVal.exp.addExp);
            } else {
                mirVar.integerLiteral = 0;
            }
        } else {
            mirVar = new MirVar(MirVar.Type.GLOBAL_ARRAY, varDef.getName());

            if (varDef.constExp2 == null) {
                ValueType type1 = new ValueType(ValueType.Type.ARRAY);
                type.pointTo = type1;
                type1.elementType = new ValueType(ValueType.Type.I32);
                type1.size = visitConstExp(varDef.constExp1);
            } else {
                ValueType type1 = new ValueType(ValueType.Type.ARRAY);
                ValueType type2 = new ValueType(ValueType.Type.ARRAY);
                type1.size = visitConstExp(varDef.constExp1);
                type2.size = visitConstExp(varDef.constExp2);
                type.pointTo = type1;
                type1.elementType = type2;
                type2.elementType = new ValueType(ValueType.Type.I32);
            }

            if (varDef.initVal != null) {
                mirVar.compoundLiteral = visitGlobalInitVals(varDef.initVal.initVals);
            } else {
                int x = visitConstExp(varDef.constExp1);
                ArrayList<MirVar> vars = new ArrayList<>();
                if (varDef.constExp2 != null) { // 二维数组
                    int y = visitConstExp(varDef.constExp2);
                    for (int i = 0; i < x; i++) {
                        MirVar varSon = new MirVar(MirVar.Type.GLOBAL_ARRAY, "");
                        varSon.compoundLiteral = new ArrayList<>();
                        for (int j = 0; j < y; j++) {
                            varSon.compoundLiteral.add(new MirVar(MirVar.Type.GLOBAL_VAL, 0));
                        }
                        vars.add(varSon);
                    }
                } else { // 一维数组
                    for (int i = 0; i < x; i++) {
                        vars.add(new MirVar(MirVar.Type.GLOBAL_VAL, 0));
                    }
                }
                mirVar.compoundLiteral = vars;
                mirVar.useZeroInit = true;
            }
        }
        globalVar.mirVar = mirVar;
        mirVar.addr = globalVar;

        globalHeap.addVar(varDef.getName(), mirVar);
        Manager.MANAGER.addGlobalVar(varDef.getName(), globalVar);
    }


    private Value visitExp(Exp exp) {    // 每个 Exp 最后都应该能用一个 Value 表示出来
        return visitAddExp(exp.addExp);
    }

    private Value visitAddExp(AddExp addExp) {
        Value temValue = visitMulExp(addExp.mulExps.get(0));
        for (int i = 0; i < addExp.ops.size(); i++) {
            Value newValue = visitMulExp(addExp.mulExps.get(i + 1));
            Instruction instruction;
            String name = "@tem" + curFunction.cnt;
            curFunction.cnt++;
            switch (addExp.ops.get(i).tokenType) {
                case PLUS:
                    instruction = new Instruction(Instruction.InsType.add, name, new ValueType(ValueType.Type.I32),  temValue, newValue);
                    curBlock.addInstruction(instruction);
                    break;
                case MINU:
                    instruction = new Instruction(Instruction.InsType.sub, name, new ValueType(ValueType.Type.I32), temValue, newValue);
                    curBlock.addInstruction(instruction);
                    break;
                default:
                    throw new RuntimeException("Unknown type: " + addExp.ops.get(i).tokenType);
            }
            temValue = instruction;
        }
        return temValue;
    }

    private Value visitMulExp(MulExp mulExp) {
        Value temValue = visitUnaryExp(mulExp.unaryExps.get(0));
        for (int i = 0; i < mulExp.ops.size(); i++) {
            Value newValue = visitUnaryExp(mulExp.unaryExps.get(i + 1));
            Instruction instruction;
            String name = "@tem" + curFunction.cnt;
            curFunction.cnt++;
            switch (mulExp.ops.get(i).tokenType) {
                case MULT:
                    instruction = new Instruction(Instruction.InsType.mul, name, new ValueType(ValueType.Type.I32), temValue, newValue);
                    curBlock.addInstruction(instruction);
                    break;
                case DIV:
                    instruction = new Instruction(Instruction.InsType.sdiv, name, new ValueType(ValueType.Type.I32), temValue, newValue);
                    curBlock.addInstruction(instruction);
                    break;
                case MOD:
                    instruction = new Instruction(Instruction.InsType.srem, name, new ValueType(ValueType.Type.I32), temValue, newValue);
                    curBlock.addInstruction(instruction);
                    break;
                default:
                    throw new RuntimeException("Unknown type: " + mulExp.ops.get(i).tokenType);
            }
            temValue = instruction;
        }
        return temValue;
    }

    private Value visitUnaryExp(UnaryExp unaryExp) {
        switch (unaryExp.type) {
            case PrimaryExp:
                return visitPrimaryExp(unaryExp.primaryExp);
            case FuncCall:
                return FuncCall();
            case UnaryOp:
                String name = "@tem" + curFunction.cnt;
                curFunction.cnt++;
                Value value = visitUnaryExp(unaryExp.unaryExp);
                if (unaryExp.unaryOp.op.tokenType.equals(TokenType.MINU)) {
                    Instruction instruction = new Instruction(Instruction.InsType.mul, name, new ValueType(ValueType.Type.I32), value, new Value(-1, true));
                    curBlock.addInstruction(instruction);
                    value = instruction;
                }
                return value;
            default:
                throw new RuntimeException("Unknown type: " + unaryExp.type);
        }
    }

    private Value visitPrimaryExp(PrimaryExp primaryExp) {
        switch (primaryExp.type) {
            case Exp:
                return visitExp(primaryExp.exp);
            case LVal:
                return visitLVal(primaryExp.lVal);
            case Number:
                return new Value(primaryExp.number.getNumber(), true);
            default:
                throw new RuntimeException("Unknown type: " + primaryExp.type);
        }
    }

    private Value FuncCall() {
        return null;
    }

    private Value visitLVal(LVal lVal) {
        MirVar var = findVarValue(lVal.getName());
        switch (var.type) {
            case LOCAL_VAL:
                return var.getLastValue();
            case GLOBAL_VAL:
                return var.globalVar;
            case LOCAL_ARRAY:

            case GLOBAL_ARRAY:

            default:
                throw new RuntimeException("Unknown type: " + var.type);
        }

    }

    private MirVar findVarValue(String name) {
        MIRStack temStack = curStack;
        while (temStack != null) {
            if (temStack.contains(name)) {
                return temStack.get(name);
            }
            temStack = temStack.parent;
        }
        throw new RuntimeException("Not Found Var: " + name);
    }


}
