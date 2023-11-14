package visitor;

import lexer.TokenType;
import mir.Instruction;
import mir.Manager;
import mir.Use;
import mir.Value;
import mir.derivedValue.BasicBlock;
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


    public void run() {
        CompUnit root = Parser.PARSER.root;
        for (ConstDef constDef: Parser.PARSER.curScope.constDefs) {
            visitConstDef(constDef);
        }
        for (VarDef varDef: Parser.PARSER.curScope.varDefs) {
            visitGlobalVarDef(varDef);
        }

        return;
    }


    private void visitFuncDef(FuncDef funcDef) {

    }

    private void visitMainFuncDef(MainFuncDef mainFuncDef) {

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
        globalHeap.addVar(constDef.getName(), mirVar);
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
        GlobalVar globalVar = new GlobalVar(Value.Type.POINTER, varDef.getName());
        if (varDef.constExp1 == null) { // 为普通变量
            mirVar = new MirVar(MirVar.Type.GLOBAL_VAL, varDef.getName());
            if (varDef.initVal != null) {
                mirVar.integerLiteral = visitConstAddExp(varDef.initVal.exp.addExp);
            } else {
                mirVar.integerLiteral = 0;
            }
        } else {
            mirVar = new MirVar(MirVar.Type.GLOBAL_ARRAY, varDef.getName());
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
        globalVar.use = new Use();
        globalVar.mirVar = mirVar;

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
            switch (addExp.ops.get(i).tokenType) {
                case PLUS:
                    instruction = new Instruction(Instruction.InsType.add, temValue, newValue);
                    curBlock.addInstruction(instruction);
                    break;
                case MINU:
                    instruction = new Instruction(Instruction.InsType.sub, temValue, newValue);
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
            switch (mulExp.ops.get(i).tokenType) {
                case MULT:
                    instruction = new Instruction(Instruction.InsType.mul, temValue, newValue);
                    curBlock.addInstruction(instruction);
                    break;
                case DIV:
                    instruction = new Instruction(Instruction.InsType.sdiv, temValue, newValue);
                    curBlock.addInstruction(instruction);
                    break;
                case MOD:
                    instruction = new Instruction(Instruction.InsType.srem, temValue, newValue);
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
                Value value = visitUnaryExp(unaryExp.unaryExp);
                if (unaryExp.unaryOp.op.tokenType.equals(TokenType.MINU)) {
                    Instruction instruction = new Instruction(Instruction.InsType.mul, value, new Value(-1, true));
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
