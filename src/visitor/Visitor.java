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
            visitConstDef(constDef, true);
        }
        for (VarDef varDef: Parser.PARSER.curScope.varDefs) {
            visitGlobalVarDef(varDef);
        }

        for (FuncDef funcDef: root.funcDefs) {
            visitFuncDef(funcDef);
        }

        visitMainFuncDef(root.mainFuncDef);

        Manager.MANAGER.renameRegs();
    }


    private void visitFuncDef(FuncDef funcDef) {
        curBlock = new BasicBlock(ValueType.BLOCK, "");
        curFunction = new Function(ValueType.FUNCTION, "@" + funcDef.getFuncName());
        Manager.MANAGER.addFunction(curFunction);
        curFunction.addBlock(curBlock);
        if (funcDef.funcType.type.tokenType.equals(TokenType.VOIDTK)) {
            curFunction.retType = ValueType.VOID;
        } else {
            curFunction.retType = ValueType.I32;
        }

        curStack = new MIRStack(curStack);
        if (funcDef.funcFParams != null) {
            for (FuncFParam funcFParam: funcDef.funcFParams.funcFParams) {
                visitFuncFParam(funcFParam);
            }
            for (int i = 0; i < curFunction.params.size(); i++) {
                allocFuncParam(curFunction.params.get(i), funcDef.funcFParams.funcFParams.get(i).getName());
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
        if (curFunction.retType.isVoid()) {
            Instruction ret = new Instruction(Instruction.InsType.ret, "", new ValueType(ValueType.Type.INS));
            curBlock.addInstruction(ret);
        }
        curStack = curStack.parent;
    }

    private void visitMainFuncDef(MainFuncDef mainFuncDef) {
        curBlock = new BasicBlock(ValueType.BLOCK, "");
        curFunction = new Function(ValueType.FUNCTION, "@main");
        curFunction.addBlock(curBlock);
        curFunction.retType = ValueType.I32;
        curStack = new MIRStack(curStack);

        Manager.MANAGER.addFunction(curFunction);

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

        curStack = curStack.parent;
    }

    private void visitFuncFParam(FuncFParam funcFParam) {
        MirVar mirVar;
        Value value;
        ValueType ptrType;
        ValueType arrPtrType;
        int size;
        String name = "%" + funcFParam.getName() + "_" + curFunction.cnt;
        curFunction.cnt++;

        switch (funcFParam.deep) {
            case 0:
                mirVar = new MirVar(MirVar.Type.LOCAL_VAL, funcFParam.getName());
                value = new Value(ValueType.I32, name);
                curFunction.addParam(value);
                mirVar.funcFParam = value;
                break;
            case 1:
                mirVar = new MirVar(MirVar.Type.LOCAL_ARRAY, funcFParam.getName());
                ptrType = new ValueType(ValueType.Type.POINTER, ValueType.I32);
                value = new Value(ptrType, name);
                curFunction.addParam(value);
                mirVar.funcFParam = value;
                break;
            case 2:
                mirVar = new MirVar(MirVar.Type.LOCAL_ARRAY, funcFParam.getName());
                size = visitConstExp(funcFParam.constExp);
                arrPtrType = new ValueType(ValueType.Type.POINTER, new ValueType(ValueType.Type.ARRAY, ValueType.I32, size));
                value = new Value(arrPtrType, name);
                curFunction.addParam(value);
                mirVar.funcFParam = value;
                break;
            default:
                throw new RuntimeException("FuncFParam: deep is " + funcFParam.deep);
        }
    }

    private void allocFuncParam(Value param, String funcName) {  // 传入的是一个函数形参
        String name = param.name + "_" + curFunction.cnt;
        curFunction.cnt++;
        Instruction alloca = new Instruction(Instruction.InsType.alloca, name, param.type.getPointType());
        curFunction.addDecl(alloca);
        Instruction store = new Instruction(Instruction.InsType.store, "", null, param, alloca);
        curBlock.addInstruction(store);
        MirVar mirVar;
        if (param.type.isI32()) {
            mirVar = new MirVar(MirVar.Type.LOCAL_VAL, funcName);
        } else {
            mirVar = new MirVar(MirVar.Type.LOCAL_POINTER, funcName);
        }
        mirVar.addr = alloca;
        curStack.addVar(funcName, mirVar);
    }

    private void visitDecl(Decl decl) {
        if (decl.constDecl != null) {   // 为常量
            for (ConstDef constDef: decl.constDecl.constDefs) {
                visitConstDef(constDef, false);
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
                visitAssign(stmt);
                break;
            case Exp:
                visitExp(stmt.exp);
                break;
            case Block:
                visitBlock(stmt);
                break;
            case If:
                visitIf(stmt);
                break;
            case For:
                visitFor(stmt);
                break;
            case Break:
                visitBreak();
                break;
            case Continue:
                visitContinue();
                break;
            case Return:
                visitReturn(stmt);
                break;
            case GetInt:
                visitGetInt(stmt);
                break;
            case Printf:
                visitPrintf(stmt);
                break;
            case Empty:
                break;
            default:
                throw new RuntimeException("Unknown type: " + stmt.type);
        }
    }

    private void visitContinue() {
        Instruction br = new Instruction(Instruction.InsType.br, "", null, curContinue);
        curBlock.addInstruction(br);
    }

    private void visitBreak() {
        Instruction br = new Instruction(Instruction.InsType.br, "", null, curBreak);
        curBlock.addInstruction(br);
    }

    Instruction curBreak;
    Instruction curContinue;

    private void visitFor(Stmt stmt) {
        if (stmt.forStmt1 != null) {
            Stmt forStmt = new Stmt(stmt.forStmt1);
            visitStmt(forStmt);
        }
        String condName = "%cond_" + curFunction.cnt;
        curFunction.cnt++;
        String forBegin = "%for_begin_" + curFunction.cnt;
        curFunction.cnt++;
        String forEnd = "%for_end_" + curFunction.cnt;
        curFunction.cnt++;
        String forStmtName = "%for_stmt" + curFunction.cnt;
        curFunction.cnt++;
        Instruction condLabel = new Instruction(Instruction.InsType.label, condName, null);
        Instruction forBeginLabel = new Instruction(Instruction.InsType.label, forBegin, null);
        Instruction forEndLabel = new Instruction(Instruction.InsType.label, forEnd, null);
        Instruction forStmt2 = new Instruction(Instruction.InsType.label, forStmtName, null);

        Instruction temBreak = curBreak;
        Instruction temContinue = curContinue;
        curBreak = forEndLabel;
        curContinue = forStmt2;

        Instruction br = new Instruction(Instruction.InsType.br, "", null, condLabel);
        curBlock.addInstruction(br);

        curBlock.addInstruction(condLabel);

        if (stmt.cond != null) {
            visitCond(stmt.cond, forBeginLabel, forEndLabel, null);
        } else {
            br = new Instruction(Instruction.InsType.br, "", null, forBeginLabel);
            curBlock.addInstruction(br);
        }
        curBlock.addInstruction(forBeginLabel);
        visitStmt(stmt.stmt1);
        br = new Instruction(Instruction.InsType.br, "", null, forStmt2);
        curBlock.addInstruction(br);

        curBlock.addInstruction(forStmt2);
        if (stmt.forStmt2 != null) {
            Stmt forStmt = new Stmt(stmt.forStmt2);
            visitStmt(forStmt);
        }
        br = new Instruction(Instruction.InsType.br, "", null, condLabel);
        curBlock.addInstruction(br);
        curBlock.addInstruction(forEndLabel);
        curBreak = temBreak;
        curContinue = temContinue;
    }

//    private Instruction endLabel; // if_end / else_block / for_end
//    private Instruction beginLabel;   // if_ / for_
//    private Instruction elseLabel;

    private void visitIf(Stmt stmt) {
        String name1 = "%label" + curFunction.cnt;
        curFunction.cnt++;

        Instruction beginLabel = new Instruction(Instruction.InsType.label, name1, null);
        Instruction elseLabel;
        Instruction endLabel;
        String name2;
        String name3 = "";
        if (stmt.stmt2 != null) {
            name2 = "%label" + curFunction.cnt;
            curFunction.cnt++;
            elseLabel = new Instruction(Instruction.InsType.label, name2, null);
            name3 = "%label" + curFunction.cnt;   // end
            curFunction.cnt++;
            endLabel = new Instruction(Instruction.InsType.label, name3, null);
        } else {
            name2 = "%label" + curFunction.cnt;
            curFunction.cnt++;
            endLabel = new Instruction(Instruction.InsType.label, name2, null);
            elseLabel = null;   // 修复if else if 的 bug。纯屎山
        }

        visitCond(stmt.cond, beginLabel, endLabel, elseLabel);

        // curBlock = new BasicBlock(ValueType.BLOCK, name1.substring(1));
        // curFunction.addBlock(curBlock);
        curBlock.addInstruction(beginLabel);

        visitBlock(stmt.stmt1);

        curBlock.addInstruction(new Instruction(Instruction.InsType.br, "", null, endLabel));

        if (stmt.stmt2 != null) {
            // curBlock = new BasicBlock(ValueType.BLOCK, name2.substring(1));
            // curFunction.addBlock(curBlock);
            curBlock.addInstruction(elseLabel);
            visitBlock(stmt.stmt2);
            curBlock.addInstruction(new Instruction(Instruction.InsType.br, "", null, endLabel));

            // curBlock = new BasicBlock(ValueType.BLOCK, name3.substring(1));
            // curFunction.addBlock(curBlock);

        } else {
//            curBlock = new BasicBlock(ValueType.BLOCK, name2.substring(1));
            // curFunction.addBlock(curBlock);
        }
        curBlock.addInstruction(endLabel);

    }

    private void visitCond(Cond cond, Instruction beginLabel, Instruction endLabel, Instruction elseLabel) {
        visitLOrExp(cond.lOrExp, beginLabel, endLabel, elseLabel);
    }


    private void visitLOrExp(LOrExp lOrExp, Instruction beginLabel, Instruction endLabel, Instruction elseLabel) {
        Instruction lOrNext;
        for (int i = 0; i < lOrExp.andExps.size(); i++) {
            if (i < lOrExp.andExps.size() - 1) {
                String name = "%label" + curFunction.cnt;
                curFunction.cnt++;
                lOrNext = new Instruction(Instruction.InsType.label, name, null);
                visitLAndExp(lOrExp.andExps.get(i), lOrNext, beginLabel);   // true->beginLabel false->lOrNext
                // 若为真则跳调走
                // Instruction br = new Instruction(Instruction.InsType.br, "", null, cond, beginLabel, lOrNext);
                // curBlock.addInstruction(br);
                curBlock.addInstruction(lOrNext);
            } else {
                visitLAndExp(lOrExp.andExps.get(i), elseLabel != null ? elseLabel : endLabel, beginLabel);
                // 最后一个块可以不加跳转
            }
        }
    }

    Instruction lAndNext;

    private void visitLAndExp(LAndExp lAndExp, Instruction orNext, Instruction orEnd) {
        for (int i = 0; i < lAndExp.eqExps.size(); i++) {
            if (i < lAndExp.eqExps.size() - 1) {
                String name = "%label" + curFunction.cnt;
                curFunction.cnt++;
                lAndNext = new Instruction(Instruction.InsType.label, name, null);
                visitEqExp(lAndExp.eqExps.get(i), lAndNext, orNext);  // True->lAndNext False->orNext
                curBlock.addInstruction(lAndNext);
            } else {
                lAndNext = orNext;
                visitEqExp(lAndExp.eqExps.get(i), orEnd, orNext); // True->orEnd False->orNext
            }
        }
    }

    private void visitEqExp(EqExp eqExp, Instruction andNext, Instruction andEnd) {
        Value value = visitRelExp(eqExp.relExps.get(0));
        for (int i = 0; i < eqExp.ops.size(); i++) {
            Value value1 = visitRelExp(eqExp.relExps.get(i + 1));
            Instruction icmp;
            String name = "%tem_" + curFunction.cnt;
            curFunction.cnt++;
            switch (eqExp.ops.get(i).tokenType) {
                case EQL:
                    icmp = new Instruction(Instruction.InsType.icmp, name, ValueType.I1, value, value1);
                    icmp.cmpType = "eq";
                    curBlock.addInstruction(icmp);
                    break;
                case NEQ:
                    icmp = new Instruction(Instruction.InsType.icmp, name, ValueType.I1, value, value1);
                    icmp.cmpType = "ne";
                    curBlock.addInstruction(icmp);
                    break;
                default:
                    throw new RuntimeException();
            }
            name = "%tem_" + curFunction.cnt;
            curFunction.cnt++;
            value = new Instruction(Instruction.InsType.zext, name, ValueType.I32, icmp);   // 将 i1 扩展至 i32
            curBlock.addInstruction((Instruction) value);
        }
        String name = "%tem_" + curFunction.cnt;
        curFunction.cnt++;
        Instruction cmpz = new Instruction(Instruction.InsType.icmp, name, ValueType.I1, value, new Value(0, true));
        cmpz.cmpType = "ne";
        curBlock.addInstruction(cmpz);
        Instruction br = new Instruction(Instruction.InsType.br, "", null, cmpz, andNext, andEnd);
        curBlock.addInstruction(br);
    }

    private Value visitRelExp(RelExp relExp) {
        Value value = visitAddExp(relExp.addExps.get(0));
        for (int i = 0; i < relExp.ops.size(); i++) {
            Value value1 = visitAddExp(relExp.addExps.get(i + 1));
            Instruction icmp;
            String name = "%tem_" + curFunction.cnt;
            curFunction.cnt++;
            switch (relExp.ops.get(i).tokenType) {
                case GRE:   // >
                    icmp = new Instruction(Instruction.InsType.icmp, name, ValueType.I1, value, value1);
                    icmp.cmpType = "sgt";
                    curBlock.addInstruction(icmp);
                    break;
                case GEQ:   // >=
                    icmp = new Instruction(Instruction.InsType.icmp, name, ValueType.I1, value, value1);
                    icmp.cmpType = "sge";
                    curBlock.addInstruction(icmp);
                    break;
                case LSS:   // <
                    icmp = new Instruction(Instruction.InsType.icmp, name, ValueType.I1, value, value1);
                    icmp.cmpType = "slt";
                    curBlock.addInstruction(icmp);
                    break;
                case LEQ:   // <=
                    icmp = new Instruction(Instruction.InsType.icmp, name, ValueType.I1, value, value1);
                    icmp.cmpType = "sle";
                    curBlock.addInstruction(icmp);
                    break;
                default:
                    throw new RuntimeException();
            }
            name = "%tem_" + curFunction.cnt;
            curFunction.cnt++;
            value = new Instruction(Instruction.InsType.zext, name, ValueType.I32, icmp);   // 将 i1 扩展至 i32
            curBlock.addInstruction((Instruction) value);
        }
        return value;
    }


    private void visitPrintf(Stmt stmt) {
        String str = stmt.formatString.content;
        int cnt = 0;
        for (int i = 1; i < str.length() - 1; i++) {
            Instruction put;
            if (str.charAt(i) == '\\') {
                put = new Instruction(Instruction.InsType.call, "", ValueType.VOID, new Value('\n', true));
                put.funcName = "@putch";
                curBlock.addInstruction(put);
                i++;
            } else if (str.charAt(i) == '%') {
                Value value = visitExp(stmt.exps.get(cnt));
                put = new Instruction(Instruction.InsType.call, "", ValueType.VOID, value);
                put.funcName = "@putint";
                curBlock.addInstruction(put);
                cnt++;
                i++;
            } else {
                put = new Instruction(Instruction.InsType.call, "", ValueType.VOID, new Value(str.charAt(i), true));
                put.funcName = "@putch";
                curBlock.addInstruction(put);
            }
        }
    }

    private void visitGetInt(Stmt stmt) {
        Value lVal = visitLVal(stmt.lVal, false);   // 向此地址写入
        String name = "%tem_" + curFunction.cnt;
        curFunction.cnt++;
        Instruction getInt = new Instruction(Instruction.InsType.call, name, ValueType.I32);
        getInt.funcName = "@getint";
        curBlock.addInstruction(getInt);
        Instruction store = new Instruction(Instruction.InsType.store, "", null, getInt, lVal);
        curBlock.addInstruction(store);
    }

    private void visitBlock(Stmt stmt) {
        curStack = new MIRStack(curStack);
        if (stmt.block != null) {
            for (BlockItem item : stmt.block.items) {
                switch (item.type) {
                    case Decl:
                        visitDecl(item.decl);
                        break;
                    case Stmt:
                        visitStmt(item.stmt);
                        break;
                }
            }
        } else {
            visitStmt(stmt);
        }
        curStack = curStack.parent;
    }

    private void visitAssign(Stmt stmt) {
        Value lVal = visitLVal(stmt.lVal, false);  // 向此地址写入
        Value value = visitExp(stmt.exp);
        Instruction store = new Instruction(Instruction.InsType.store, "", null, value, lVal);
        curBlock.addInstruction(store);
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

            type.pointTo = ValueType.I32;
            allocAddr = new Instruction(Instruction.InsType.alloca, name, type);

            if (varDef.initVal != null) {
                Value value = visitExp(varDef.initVal.exp);
                Instruction instruction1 = new Instruction(Instruction.InsType.store, "", null, value, allocAddr);
                curBlock.addInstruction(instruction1);
            }
        } else if (varDef.constExp2 == null) {  // 为 int[]
            mirVar = new MirVar(MirVar.Type.LOCAL_ARRAY, varDef.getName());

            type.pointTo = type1;
            type1.elementType = ValueType.I32;
            type1.size = visitConstExp(varDef.constExp1);
            allocAddr = new Instruction(Instruction.InsType.alloca, name, type);

            if (varDef.initVal != null) {   // 一维数组初始化
                String initName = "%" + varDef.getName() + "_init_" + curFunction.cnt;
                curFunction.cnt++;
                ValueType ptrType = new ValueType(ValueType.Type.POINTER);
                ptrType.pointTo = ValueType.I32;
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
            type2.elementType = ValueType.I32;
            type1.size = visitConstExp(varDef.constExp1);
            type2.size = visitConstExp(varDef.constExp2);
            allocAddr = new Instruction(Instruction.InsType.alloca, name, type);

            if (varDef.initVal != null) {   // 二维数组初始化
                String initName = "%" + varDef.getName() + "_init_" + curFunction.cnt;
                curFunction.cnt++;
                int x = visitConstAddExp(varDef.constExp1.addExp);
                int y = visitConstAddExp(varDef.constExp2.addExp);
                ValueType ptrType = new ValueType(ValueType.Type.POINTER);
                ptrType.pointTo = ValueType.I32;
                for (int i = 0; i < x; i++) {
                    for (int j = 0; j < y; j++) {
                        Instruction ptr = new Instruction(Instruction.InsType.getelementptr, initName, ptrType, allocAddr, new Value(0, true), new Value(i, true), new Value(j, true));
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

    private void visitConstDef(ConstDef constDef, boolean isGlobal) {   //
        // 构造常量 M_Var
        MirVar mirVar;
        if (constDef.constExp1 == null) {   // 为 int类型
            mirVar = new MirVar(MirVar.Type.CONST_VAR, constDef.getName());
            if (constDef.constInitVal != null) {
                mirVar.integerLiteral = visitConstExp(constDef.constInitVal.constExp);
            } else {
                mirVar.integerLiteral = 0;
            }
            curStack.addVar(constDef.getName(), mirVar);
        } else {    // 数组类型应该置入内存。。。。
            if (isGlobal) {
                mirVar = new MirVar(MirVar.Type.GLOBAL_ARRAY, constDef.getName());
                ValueType type = new ValueType(ValueType.Type.POINTER);
                GlobalVar globalVar = new GlobalVar(type, "@" + constDef.getName());
                if (constDef.constExp2 == null) {
                    ValueType type1 = new ValueType(ValueType.Type.ARRAY);
                    type.pointTo = type1;
                    type1.elementType = ValueType.I32;
                    type1.size = visitConstExp(constDef.constExp1);
                } else {
                    ValueType type1 = new ValueType(ValueType.Type.ARRAY);
                    ValueType type2 = new ValueType(ValueType.Type.ARRAY);
                    type1.size = visitConstExp(constDef.constExp1);
                    type2.size = visitConstExp(constDef.constExp2);
                    type.pointTo = type1;
                    type1.elementType = type2;
                    type2.elementType = ValueType.I32;
                }
                mirVar.compoundLiteral = visitConstInitVals(constDef.constInitVal.constInitVals);
                globalVar.mirVar = mirVar;
                mirVar.addr = globalVar;

                globalHeap.addVar(constDef.getName(), mirVar);
                Manager.MANAGER.addGlobalVar(constDef.getName(), globalVar);

            } else {
                String name = "%" + constDef.getName() + "_" + curFunction.cnt;
                curFunction.cnt++;
                ValueType type = new ValueType(ValueType.Type.POINTER);
                ValueType type1 = new ValueType(ValueType.Type.ARRAY);
                ValueType type2 = new ValueType(ValueType.Type.ARRAY);
                Instruction allocAddr;
                mirVar = new MirVar(MirVar.Type.LOCAL_ARRAY, constDef.getName());
                mirVar.compoundLiteral = visitConstInitVals(constDef.constInitVal.constInitVals);
                if (constDef.constExp2 == null) {
                    type.pointTo = type1;
                    type1.elementType = ValueType.I32;
                    type1.size = visitConstExp(constDef.constExp1);
                    allocAddr = new Instruction(Instruction.InsType.alloca, name, type);
                    String initName = "%" + constDef.getName() + "_init_" + curFunction.cnt;
                    curFunction.cnt++;
                    ValueType ptrType = new ValueType(ValueType.Type.POINTER);
                    ptrType.pointTo = ValueType.I32;
                    Instruction ptr = new Instruction(Instruction.InsType.getelementptr, initName, ptrType, allocAddr, new Value(0, true), new Value(0, true));
                    ptr.getEleType = type;
                    curBlock.addInstruction(ptr);
                    for (ConstInitVal initVal: constDef.constInitVal.constInitVals) {
                        int x = visitConstExp(initVal.constExp);
                        Value value = new Value(x, true);
                        Instruction store = new Instruction(Instruction.InsType.store, "", null, value, ptr);
                        curBlock.addInstruction(store);
                        initName = "%" + constDef.getName() + "_init_" + curFunction.cnt;
                        curFunction.cnt++;
                        // 指针前移
                        ptr = new Instruction(Instruction.InsType.getelementptr, initName, ptrType, ptr, new Value(1, true));
                        ptr.getEleType = ptrType;
                        curBlock.addInstruction(ptr);
                    }
                } else {
                    type.pointTo = type1;
                    type1.elementType = type2;
                    type2.elementType = ValueType.I32;
                    type1.size = visitConstExp(constDef.constExp1);
                    type2.size = visitConstExp(constDef.constExp2);
                    allocAddr = new Instruction(Instruction.InsType.alloca, name, type);

                    String initName = "%" + constDef.getName() + "_init_" + curFunction.cnt;
                    curFunction.cnt++;
                    int x = visitConstAddExp(constDef.constExp1.addExp);
                    int y = visitConstAddExp(constDef.constExp2.addExp);
                    ValueType ptrType = new ValueType(ValueType.Type.POINTER);
                    ptrType.pointTo = ValueType.I32;
                    for (int i = 0; i < x; i++) {
                        for (int j = 0; j < y; j++) {
                            Instruction ptr = new Instruction(Instruction.InsType.getelementptr, initName, ptrType, allocAddr, new Value(0, true), new Value(i, true), new Value(j, true));
                            ptr.getEleType = type;
                            curBlock.addInstruction(ptr);
                            int v = visitConstExp(constDef.constInitVal.constInitVals.get(i).constInitVals.get(j).constExp);
                            Value value = new Value(v, true);
                            Instruction store = new Instruction(Instruction.InsType.store, "", null, value, ptr);
                            curBlock.addInstruction(store);

                            initName = "%" + constDef.getName() + "_init_" + curFunction.cnt;
                            curFunction.cnt++;
                        }
                    }
                }

                curFunction.addDecl(allocAddr);
                mirVar.addr = allocAddr;
                curStack.addVar(constDef.getName(), mirVar);
            }

        }
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
        GlobalVar globalVar = new GlobalVar(type, "@" + varDef.getName());
        if (varDef.constExp1 == null) { // 为普通变量
            mirVar = new MirVar(MirVar.Type.GLOBAL_VAL, varDef.getName());
            type.pointTo = ValueType.I32;
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
                type1.elementType = ValueType.I32;
                type1.size = visitConstExp(varDef.constExp1);
            } else {
                ValueType type1 = new ValueType(ValueType.Type.ARRAY);
                ValueType type2 = new ValueType(ValueType.Type.ARRAY);
                type1.size = visitConstExp(varDef.constExp1);
                type2.size = visitConstExp(varDef.constExp2);
                type.pointTo = type1;
                type1.elementType = type2;
                type2.elementType = ValueType.I32;
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
        if (exp.isConst) {
            return new Value(getConstExpValue(exp), true);
        }
        return visitAddExp(exp.addExp);
    }

    private int getConstExpValue(SyntaxTreeNode node) {
        int tem;
        if (node instanceof Exp) {
            return getConstExpValue(((Exp) node).addExp);
        } else if (node instanceof AddExp) {
            tem = getConstExpValue(((AddExp) node).mulExps.get(0));
            for (int i = 0; i < ((AddExp) node).ops.size(); i++) {
                switch (((AddExp) node).ops.get(i).tokenType) {
                    case PLUS:
                        tem += getConstExpValue(((AddExp) node).mulExps.get(i + 1));
                        break;
                    case MINU:
                        tem -= getConstExpValue(((AddExp) node).mulExps.get(i + 1));
                        break;
                }
            }
            return tem;
        } else if (node instanceof MulExp) {
            tem = getConstExpValue(((MulExp) node).unaryExps.get(0));
            for (int i = 0; i < ((MulExp) node).ops.size(); i++) {
                switch (((MulExp) node).ops.get(i).tokenType) {
                    case MULT:
                        tem *= getConstExpValue(((MulExp) node).unaryExps.get(i + 1));
                        break;
                    case DIV:
                        tem /= getConstExpValue(((MulExp) node).unaryExps.get(i + 1));
                        break;
                    case MOD:
                        tem %= getConstExpValue(((MulExp) node).unaryExps.get(i + 1));
                        break;
                }
            }
            return tem;
        } else if (node instanceof UnaryExp) {
            switch (((UnaryExp) node).type) {
                case UnaryOp:
                    switch (((UnaryExp) node).unaryOp.op.tokenType) {
                        case PLUS:
                            tem = getConstExpValue(((UnaryExp) node).unaryExp);
                            break;
                        case MINU:
                            tem = -getConstExpValue(((UnaryExp) node).unaryExp);
                            break;
                        case NOT:
                            tem = getConstExpValue(((UnaryExp) node).unaryExp) == 0 ? 1 : 0;
                            break;
                        default:
                            throw new RuntimeException();
                    }
                    break;
                case PrimaryExp:
                    tem = getConstExpValue(((UnaryExp) node).primaryExp);
                    break;
                default:
                    throw new RuntimeException();
            }
            return tem;
        } else if (node instanceof PrimaryExp) {
            switch (((PrimaryExp) node).type) {
                case Number:
                    tem = ((PrimaryExp) node).number.getNumber();
                    break;
                case LVal:
                    tem = getConstExpValue(((PrimaryExp) node).lVal);
                    break;
                case Exp:
                    tem = getConstExpValue(((PrimaryExp) node).exp);
                    break;
                default:
                    throw new RuntimeException();
            }
            return tem;
        } else if (node instanceof LVal) {
            MirVar var = findVarValue(((LVal) node).getName());
            if (((LVal) node).exp1 == null) {   //
                tem = var.integerLiteral;
            } else if (((LVal) node).exp2 == null) {
                tem = var.getVar(getConstExpValue(((LVal) node).exp1)).integerLiteral;
            } else {
                tem = var.getVar(getConstExpValue(((LVal) node).exp1), getConstExpValue(((LVal) node).exp2)).integerLiteral;
            }
            return tem;
        } else {
            throw new RuntimeException();
        }
    }

    private Value visitAddExp(AddExp addExp) {
        Value temValue = visitMulExp(addExp.mulExps.get(0));
        for (int i = 0; i < addExp.ops.size(); i++) {
            Value newValue = visitMulExp(addExp.mulExps.get(i + 1));
            Instruction instruction;
            String name = "%tem_" + curFunction.cnt;
            curFunction.cnt++;
            switch (addExp.ops.get(i).tokenType) {
                case PLUS:
                    instruction = new Instruction(Instruction.InsType.add, name, ValueType.I32,  temValue, newValue);
                    curBlock.addInstruction(instruction);
                    break;
                case MINU:
                    instruction = new Instruction(Instruction.InsType.sub, name, ValueType.I32, temValue, newValue);
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
            String name = "%tem_" + curFunction.cnt;
            curFunction.cnt++;
            switch (mulExp.ops.get(i).tokenType) {
                case MULT:
                    instruction = new Instruction(Instruction.InsType.mul, name, ValueType.I32, temValue, newValue);
                    curBlock.addInstruction(instruction);
                    break;
                case DIV:
                    instruction = new Instruction(Instruction.InsType.sdiv, name, ValueType.I32, temValue, newValue);
                    curBlock.addInstruction(instruction);
                    break;
                case MOD:
                    instruction = new Instruction(Instruction.InsType.srem, name, ValueType.I32, temValue, newValue);
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
                return FuncCall(unaryExp);
            case UnaryOp:
                Value value = visitUnaryExp(unaryExp.unaryExp);
                if (unaryExp.unaryOp.op.tokenType.equals(TokenType.MINU)) {
                    String name = "%tem_" + curFunction.cnt;
                    curFunction.cnt++;
                    Instruction instruction = new Instruction(Instruction.InsType.mul, name, ValueType.I32, new Value(-1, true), value);
                    curBlock.addInstruction(instruction);
                    value = instruction;
                } else if (unaryExp.unaryOp.op.tokenType.equals(TokenType.NOT)) {
                    String name = "%tem_" + curFunction.cnt;
                    curFunction.cnt++;
                    Instruction icmp = new Instruction(Instruction.InsType.icmp, name, ValueType.I1, value, new Value(0, true));
                    icmp.cmpType = "eq";
                    curBlock.addInstruction(icmp);
                    name = "%tem_" + curFunction.cnt;
                    curFunction.cnt++;
                    Instruction instruction = new Instruction(Instruction.InsType.zext, name, ValueType.I32, icmp);
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
                return visitLVal(primaryExp.lVal, true);
            case Number:
                return new Value(primaryExp.number.getNumber(), true);
            default:
                throw new RuntimeException("Unknown type: " + primaryExp.type);
        }
    }

    private Value FuncCall(UnaryExp unaryExp) {
        Function function = Manager.MANAGER.findFunction(unaryExp.ident.content);
        String name;
        if (curFunction.name.equals(function.name)) {
            function.isRecur = true;    // 存在自我调用的递归情况
        }
        if (true) {//function.isRecur) {
            if (function.retType.isVoid()) {
                name = "";
            } else {
                name = "%call_" + curFunction.cnt;
                curFunction.cnt++;
            }
            Instruction call = new Instruction(Instruction.InsType.call, name, function.retType);
            call.funcName = function.name;
            if (unaryExp.funcRParams != null) {
                for (Exp exp : unaryExp.funcRParams.exps) {
                    call.addValue(visitExp(exp));
                }
            }
            curBlock.addInstruction(call);
            if (function.retType.isVoid()) {
                return null;
            } else {
                return call;
            }
        } else {    // 可以内联
            if (!curFunction.functions.contains(function)) {
                curFunction.functions.add(function);
                curFunction.decls.addAll(function.decls);
            }

        }
        return null;
    }

    private Value visitLVal(LVal lVal, boolean needLoad) {
        MirVar var = findVarValue(lVal.getName());
        Instruction load;
        Instruction ptr;
        String name;
        Value x;
        Value y;
        switch (var.type) {
            case LOCAL_VAL:
            case GLOBAL_VAL:
                if (!needLoad) {
                    return var.addr;
                } else {
                    name = "%tem_" + curFunction.cnt;
                    curFunction.cnt++;
                    load = new Instruction(Instruction.InsType.load, name, ValueType.I32, var.addr);
                    curBlock.addInstruction(load);
                    return load;
                }
            case LOCAL_ARRAY:
            case GLOBAL_ARRAY:
                name = "%tem_" + curFunction.cnt;
                curFunction.cnt++;
                if (lVal.exp1 == null) {    // 不寻址，返回指针
                    ValueType ptrType = new ValueType(ValueType.Type.POINTER);
                    ptrType.pointTo = var.addr.type.pointTo.elementType;    // 什么歃畀的疯狂指
                    ptr = new Instruction(Instruction.InsType.getelementptr, name, ptrType, var.addr, new Value(0, true), new Value(0, true));
                    ptr.getEleType = var.addr.type;
                    curBlock.addInstruction(ptr);
                    return ptr;
                } else if (lVal.exp2 == null) {    // 寻址一次，可能寻址到地址

                    x = visitExp(lVal.exp1);
                    ValueType ptrType = new ValueType(ValueType.Type.POINTER);
                    ptrType.pointTo = var.addr.type.pointTo.elementType;    // 什么歃畀的疯狂指
                    ptr = new Instruction(Instruction.InsType.getelementptr, name, ptrType, var.addr, new Value(0, true), x);
                    ptr.getEleType = var.addr.type;
                    curBlock.addInstruction(ptr);
                    if (ptrType.pointTo.isI32()) {  // 一维数组
                        if (needLoad) {
                            name = "%tem_" + curFunction.cnt;
                            curFunction.cnt++;
                            load = new Instruction(Instruction.InsType.load, name, ValueType.I32, ptr);
                            curBlock.addInstruction(load);
                            return load;
                        } else {
                            return ptr;
                        }
                    } else {                         // 二维数组寻址一次，一定是作为数组传参，返回一个int*的指针
                        ptrType.pointTo = ValueType.I32;    //  Int*
                        ptr.values.add(new Value(0, true));
                        return ptr;
                    }
                } else {                    // 寻址两次，一定能寻址到 I32
                    x = visitExp(lVal.exp1);
                    y = visitExp(lVal.exp2);
                    ValueType ptrType = new ValueType(ValueType.Type.POINTER);
                    ptrType.pointTo = var.addr.type.pointTo ;    // 什么歃畀的疯狂指
                    ptr = new Instruction(Instruction.InsType.getelementptr, name, ValueType.I32.getPointType(), var.addr, new Value(0, true), x, y);
                    ptr.getEleType = ptrType;
                    curBlock.addInstruction(ptr);
                    if (needLoad) {
                        name = "%tem_" + curFunction.cnt;
                        curFunction.cnt++;
                        load = new Instruction(Instruction.InsType.load, name, ValueType.I32, ptr);
                        curBlock.addInstruction(load);
                        return load;
                    } else {
                        return ptr;
                    }
                }
            case LOCAL_POINTER:
                name = "%tem_" + curFunction.cnt;
                curFunction.cnt++;
                if (lVal.exp1 == null) {    // 返回数字或数组地址
                    ValueType ptrType = new ValueType(ValueType.Type.POINTER);
                    ptrType.pointTo = var.addr.type.pointTo.elementType;    // 什么歃畀的疯狂指
                    ptr = new Instruction(Instruction.InsType.load, name, var.addr.type.pointTo, var.addr);
                    curBlock.addInstruction(ptr);
                    return ptr;
                } else if (lVal.exp2 == null) {    // 寻址一次，可能寻址到地址
                    x = visitExp(lVal.exp1);
                    load = new Instruction(Instruction.InsType.load, name, var.addr.type.pointTo, var.addr);    // 先将地址 load 下来
                    curBlock.addInstruction(load);
                    name = "%tem_" + curFunction.cnt;
                    curFunction.cnt++;
                    ptr = new Instruction(Instruction.InsType.getelementptr, name, var.addr.type.pointTo, load, x);
                    ptr.getEleType = var.addr.type.pointTo;
                    curBlock.addInstruction(ptr);
                    if (!var.addr.type.pointTo.pointTo.isI32()) {   // 传指针
                        name = "%tem_" + curFunction.cnt;
                        curFunction.cnt++;
                        ptr = new Instruction(Instruction.InsType.getelementptr, name, ValueType.I32.getPointType(), ptr, new Value(0, true), new Value(0, true));
                        ptr.getEleType = var.addr.type.pointTo;
                        curBlock.addInstruction(ptr);
                        return ptr;
                    } else if (needLoad) {
                        name = "%tem_" + curFunction.cnt;
                        curFunction.cnt++;
                        load = new Instruction(Instruction.InsType.load, name, load.type.pointTo, ptr);
                        curBlock.addInstruction(load);
                        return load;
                    } else {
                        return ptr;
                    }
                } else {                    // 寻址两次，一定能寻址到 I32
                    x = visitExp(lVal.exp1);
                    y = visitExp(lVal.exp2);
                    load = new Instruction(Instruction.InsType.load, name, var.addr.type.pointTo, var.addr);    // 先将地址 load 下来
                    curBlock.addInstruction(load);
                    name = "%tem_" + curFunction.cnt;
                    curFunction.cnt++;
                    ptr = new Instruction(Instruction.InsType.getelementptr, name, var.addr.type.pointTo, load, x);
                    ptr.getEleType = var.addr.type.pointTo;
                    curBlock.addInstruction(ptr);
                    name = "%tem_" + curFunction.cnt;
                    curFunction.cnt++;
                    ptr = new Instruction(Instruction.InsType.getelementptr, name, ValueType.I32.getPointType(), ptr, new Value(0, true), y);
                    ptr.getEleType = var.addr.type.pointTo;
                    curBlock.addInstruction(ptr);

                    if (needLoad) {
                        name = "%tem_" + curFunction.cnt;
                        curFunction.cnt++;
                        load = new Instruction(Instruction.InsType.load, name, ValueType.I32, ptr);
                        curBlock.addInstruction(load);
                        return load;
                    } else {
                        return ptr;
                    }
                }
            case CONST_VAR:
                return new Value(var.integerLiteral, true);
            case CONST_ARRAY:
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
