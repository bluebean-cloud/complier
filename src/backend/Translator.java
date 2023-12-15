package backend;

import mir.Instruction;
import mir.Manager;
import mir.Value;
import mir.ValueType;
import mir.derivedValue.BasicBlock;
import mir.derivedValue.Function;
import mir.derivedValue.GlobalVar;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class Translator {
    private Translator() {}
    public static final Translator TRANSLATOR = new Translator();
    public ArrayList<MIPSInstruction> instructions = new ArrayList<>();
    Manager MANAGER = Manager.MANAGER;
    private final HashMap<String, Integer> varOffset = new HashMap<>();
    int sum = 0;
    public int getVarOffset(String name) {
        return sum - varOffset.get(name);
    }
    int v0 = 0;
    LFunction curLFunction;
    private ArrayList<LFunction> lFunctions = new ArrayList<>();

    public LFunction findLFunction(String name) {
        for (LFunction function: lFunctions) {
            if (function.name.equals(name)) {
                return function;
            }
        }
        return null;
    }

    public void addInstruction(MIPSInstruction mipsInstruction) {
        instructions.add(mipsInstruction);
        curLFunction.instructions.add(mipsInstruction);
        mipsInstruction.lFunction = curLFunction;
    }

    public void run() {
        // addInstruction(new MIPSInstruction(MIPSInstruction.Type.PSEUDO, ".data"));
        // addInstruction(new MIPSInstruction(MIPSInstruction.Type.PSEUDO, ".text"));
        for (Function function: MANAGER.functions) {
            curFunction = function;
            transFunction();
        }
        RegAlloca.REG_ALLOCA.run();
        for (LFunction lFunction: lFunctions) {
            lFunction.reAlloc();
        }
        printCodes();
    }

    public void printCodes() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(".data\n");
        for (GlobalVar globalVar: MANAGER.globalVars.values()) {
            stringBuilder.append(globalVar.name.substring(1)).append(": ");
            if (globalVar.mirVar.useZeroInit) {
                stringBuilder.append(".space ").append(globalVar.type.pointTo.getTypeSize()).append('\n');
            } else {
                stringBuilder.append(".word ").append(globalVar.mirVar.printIntegerLiterals()).append('\n');
            }
        }
        stringBuilder.append(".text\n");
        stringBuilder.append("j main\n");

        for (MIPSInstruction instruction: instructions) {
            stringBuilder.append(instruction.printCodes()).append('\n');
        }

//        for (LFunction lFunction: lFunctions) {
//            for (MIPSInstruction instruction: lFunction.instructions) {
//                stringBuilder.append(instruction.printCodes()).append('\n');
//            }
//        }

        try (PrintWriter output = new PrintWriter("mips.txt")) {
            output.println(stringBuilder);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    Function curFunction;
    BasicBlock curBlock;

    private void saveRegs() {
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, -4));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.SW, PhysicalReg.A0, PhysicalReg.SP, 0));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, -4));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.SW, PhysicalReg.A1, PhysicalReg.SP, 0));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, -4));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.SW, PhysicalReg.A2, PhysicalReg.SP, 0));
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, -4));
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.SW, PhysicalReg.T1, PhysicalReg.SP, 0));
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, -4));
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.SW, PhysicalReg.T2, PhysicalReg.SP, 0));
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, -4));
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.SW, PhysicalReg.T3, PhysicalReg.SP, 0));
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, -4));
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.SW, PhysicalReg.T4, PhysicalReg.SP, 0));
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, -4));
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.SW, PhysicalReg.T5, PhysicalReg.SP, 0));
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, -4));
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.SW, PhysicalReg.T6, PhysicalReg.SP, 0));
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, -4));
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.SW, PhysicalReg.T7, PhysicalReg.SP, 0));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, -4));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.SW, PhysicalReg.T8, PhysicalReg.SP, 0));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, -4));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.SW, PhysicalReg.T9, PhysicalReg.SP, 0));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, -4));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.SW, PhysicalReg.S1, PhysicalReg.SP, 0));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, -4));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.SW, PhysicalReg.S2, PhysicalReg.SP, 0));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, -4));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.SW, PhysicalReg.S3, PhysicalReg.SP, 0));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, -4));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.SW, PhysicalReg.S4, PhysicalReg.SP, 0));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, -4));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.SW, PhysicalReg.S5, PhysicalReg.SP, 0));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, -4));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.SW, PhysicalReg.S6, PhysicalReg.SP, 0));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, -4));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.SW, PhysicalReg.S7, PhysicalReg.SP, 0));
    }

    private void recoverRegs() {
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.LW, PhysicalReg.S7, PhysicalReg.SP, 0));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, 4));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.LW, PhysicalReg.S6, PhysicalReg.SP, 0));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, 4));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.LW, PhysicalReg.S5, PhysicalReg.SP, 0));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, 4));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.LW, PhysicalReg.S4, PhysicalReg.SP, 0));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, 4));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.LW, PhysicalReg.S3, PhysicalReg.SP, 0));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, 4));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.LW, PhysicalReg.S2, PhysicalReg.SP, 0));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, 4));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.LW, PhysicalReg.S1, PhysicalReg.SP, 0));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, 4));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.LW, PhysicalReg.T9, PhysicalReg.SP, 0));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, 4));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.LW, PhysicalReg.T8, PhysicalReg.SP, 0));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, 4));
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.LW, PhysicalReg.T7, PhysicalReg.SP, 0));
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, 4));
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.LW, PhysicalReg.T6, PhysicalReg.SP, 0));
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, 4));
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.LW, PhysicalReg.T5, PhysicalReg.SP, 0));
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, 4));
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.LW, PhysicalReg.T4, PhysicalReg.SP, 0));
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, 4));
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.LW, PhysicalReg.T3, PhysicalReg.SP, 0));
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, 4));
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.LW, PhysicalReg.T2, PhysicalReg.SP, 0));
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, 4));
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.LW, PhysicalReg.T1, PhysicalReg.SP, 0));
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, 4));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.LW, PhysicalReg.A2, PhysicalReg.SP, 0));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, 4));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.LW, PhysicalReg.A1, PhysicalReg.SP, 0));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, 4));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.LW, PhysicalReg.A0, PhysicalReg.SP, 0));
//        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, 4));
    }

    private void transFunction() {
        v0 = 0;
        curLFunction = new LFunction(curFunction.beginName());
        lFunctions.add(curLFunction);
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.LABEL, curFunction.beginName()));
        sum = 0;
        varOffset.clear();
        for (Instruction decl: curFunction.decls) {
            int size = decl.type.getPointToByte();
            varOffset.put(decl.name, sum + size);
            sum += size;
        }
        // 分配栈空间。函数参数在调用时已经分配好，故减去
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, -(sum - curFunction.params.size() * 4), true, -1));
        saveRegs();

        for (BasicBlock block: curFunction.blocks) {
            curBlock = block;
            transBlock();
        }
        if (curFunction.beginName().equals("main")) {
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.LABEL, curFunction.endName()));
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.LI, PhysicalReg.V0, 10));

            v0 = 10;
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.SYSCALL));
        } else {
            // 结束函数，回收空间
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.LABEL, curFunction.endName()));
            recoverRegs();
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, sum));
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.JR, PhysicalReg.RA));
        }
    }

    private void transBlock() {
        if (!curBlock.name.isEmpty()) {
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.LABEL,
                    curFunction.beginName() + "_" + curBlock.name));
        }
        for (Instruction instruction: curBlock.instructions) {
            transInstruction(instruction);
        }
    }

    private void transInstruction(Instruction instruction) {
        switch (instruction.insType) {
            case add:
                transAdd(instruction);
                break;
            case sub:
                transSub(instruction);
                break;
            case mul:
                transMul(instruction);
                break;
            case sdiv:
                transDiv(instruction);
                break;
            case srem:
                transRem(instruction);
                break;
            case icmp:
                transICmp(instruction);
                break;
            case call:
                transCall(instruction);
                break;
            case load:
                transLoad(instruction);
                break;
            case store:
                transStore(instruction);
                break;
            case getelementptr:
                transGetElementPtr(instruction);
                break;
            case zext:
                // 不需要转换，只需要设置 instruction 的虚拟寄存器即可
                // 又因中端的 zext 是紧挨着上一条指令的，所以直接设置上一条指令的虚拟寄存器即可
                instruction.setVirtualReg(virtualReg);
                break;
            case br:
                transBr(instruction);
                break;
            case ret:
                transRet(instruction);
                break;
            case label:
                transLabel(instruction);
                break;
        }
    }

    VirtualReg virtualReg;

    VirtualReg virtualReg1;
    VirtualReg virtualReg2;
    Integer imm;

    private boolean prepareForCal(Instruction instruction, String op) {
        virtualReg = new VirtualReg();
        curLFunction.addVirtualReg(virtualReg);
        instruction.setVirtualReg(virtualReg);
        if (instruction.isValuesConst()) {
            int a = instruction.values.get(0).constValue;
            int b = instruction.values.get(1).constValue;
            switch (op) {
                case "+":
                    imm = a + b;
                    break;
                case "-":
                    imm = a - b;
                    break;
                case "*":
                    imm = a * b;
                    break;
                case "/":
                    imm = a / b;
                    break;
                case "%":
                    imm = a % b;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + op);
            }
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.LI, virtualReg, imm));
            return true;
        }
        virtualReg1 = null;
        virtualReg2 = null;
        imm = null;
        if (!instruction.values.get(0).isConst) {
            virtualReg1 = instruction.values.get(0).virtualReg;
        } else {
            imm = instruction.values.get(0).constValue;
        }
        if (!instruction.values.get(1).isConst) {
            virtualReg2 = instruction.values.get(1).virtualReg;
        } else {
            imm = instruction.values.get(1).constValue;
        }
        if (virtualReg1 == null) {
            virtualReg1 = virtualReg2;
        }
        return false;
    }

    private void transAdd(Instruction instruction) {
        if (prepareForCal(instruction, "+")) {
            return;
        }
        if (imm == null) {
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDU, virtualReg, virtualReg1, virtualReg2));
        } else {
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, virtualReg, virtualReg1, imm));
        }
    }

    private void transSub(Instruction instruction) {
        virtualReg = new VirtualReg();
        curLFunction.addVirtualReg(virtualReg);
        instruction.setVirtualReg(virtualReg);
        if (instruction.isValuesConst()) {
            int a = instruction.values.get(0).constValue;
            int b = instruction.values.get(1).constValue;
            imm = a - b;
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.LI, virtualReg, imm));
            return;
        }
        virtualReg1 = null;
        virtualReg2 = null;
        imm = null;
        if (!instruction.values.get(0).isConst) {
            virtualReg1 = instruction.values.get(0).virtualReg;
        } else {
            virtualReg1 = new VirtualReg();
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.LI, virtualReg1, instruction.values.get(0).constValue));
        }
        if (!instruction.values.get(1).isConst) {
            virtualReg2 = instruction.values.get(1).virtualReg;
        } else {
            imm = instruction.values.get(1).constValue;
        }
        if (imm == null) {
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.SUBU, virtualReg, virtualReg1, virtualReg2));
        } else {
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.SUBU, virtualReg, virtualReg1, imm));
        }
    }

    private void transMul(Instruction instruction) {
        if (prepareForCal(instruction, "*")) {
            return;
        }
        if (imm == null) {
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.MUL, virtualReg, virtualReg1, virtualReg2));
        } else {
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.MUL, virtualReg, virtualReg1, imm));
        }
    }

    private void transDiv(Instruction instruction) {
        virtualReg = new VirtualReg();
        curLFunction.addVirtualReg(virtualReg);
        instruction.setVirtualReg(virtualReg);
        if (instruction.isValuesConst()) {
            int a = instruction.values.get(0).constValue;
            int b = instruction.values.get(1).constValue;
            imm = a / b;
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.LI, virtualReg, imm));
            return;
        }
        virtualReg1 = null;
        virtualReg2 = null;
        imm = null;
        if (!instruction.values.get(0).isConst) {
            virtualReg1 = instruction.values.get(0).virtualReg;
        } else {
            virtualReg1 = new VirtualReg();
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.LI, virtualReg1, instruction.values.get(0).constValue));
        }
        if (!instruction.values.get(1).isConst) {
            virtualReg2 = instruction.values.get(1).virtualReg;
        } else {
            imm = instruction.values.get(1).constValue;
        }
        if (imm == null) {
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.DIV, virtualReg, virtualReg1, virtualReg2));
        } else {
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.DIV, virtualReg, virtualReg1, imm));
        }
    }

    private void transRem(Instruction instruction) {
        virtualReg = new VirtualReg();
        curLFunction.addVirtualReg(virtualReg);
        instruction.setVirtualReg(virtualReg);
        if (instruction.isValuesConst()) {
            int a = instruction.values.get(0).constValue;
            int b = instruction.values.get(1).constValue;
            imm = a % b;
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.LI, virtualReg, imm));
            return;
        }
        virtualReg1 = null;
        virtualReg2 = null;
        imm = null;
        if (!instruction.values.get(0).isConst) {
            virtualReg1 = instruction.values.get(0).virtualReg;
        } else {
            virtualReg1 = new VirtualReg();
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.LI, virtualReg1, instruction.values.get(0).constValue));
        }
        if (!instruction.values.get(1).isConst) {
            virtualReg2 = instruction.values.get(1).virtualReg;
        } else {
            imm = instruction.values.get(1).constValue;
        }
        if (imm == null) {
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.REM, virtualReg, virtualReg1, virtualReg2));
        } else {
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.REM, virtualReg, virtualReg1, imm));
        }
    }

    private void transICmp(Instruction instruction) {
        virtualReg = new VirtualReg();
        curLFunction.addVirtualReg(virtualReg);
        instruction.setVirtualReg(virtualReg);
        if (instruction.isValuesConst()) {
            int a = instruction.values.get(0).constValue;
            int b = instruction.values.get(1).constValue;
            switch (instruction.cmpType) {
                case "eq":
                    addInstruction(new MIPSInstruction(MIPSInstruction.Type.LI, virtualReg, a == b ? 1 : 0));
                    break;
                case "ne":
                    addInstruction(new MIPSInstruction(MIPSInstruction.Type.LI, virtualReg, a != b ? 1 : 0));
                    break;
                case "sle":
                    addInstruction(new MIPSInstruction(MIPSInstruction.Type.LI, virtualReg, a <= b ? 1 : 0));
                    break;
                case "slt":
                    addInstruction(new MIPSInstruction(MIPSInstruction.Type.LI, virtualReg, a < b ? 1 : 0));
                    break;
                case "sge":
                    addInstruction(new MIPSInstruction(MIPSInstruction.Type.LI, virtualReg, a >= b ? 1 : 0));
                    break;
                case "sgt":
                    addInstruction(new MIPSInstruction(MIPSInstruction.Type.LI, virtualReg, a > b ? 1 : 0));
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + instruction.cmpType);
            }
            return;
        }
        virtualReg1 = null;
        virtualReg2 = null;
        imm = null;
        if (!instruction.values.get(0).isConst) {
            virtualReg1 = instruction.values.get(0).virtualReg;
        } else {
            virtualReg1 = new VirtualReg();
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.LI, virtualReg1, instruction.values.get(0).constValue));
        }
        if (!instruction.values.get(1).isConst) {
            virtualReg2 = instruction.values.get(1).virtualReg;
        } else {
            imm = instruction.values.get(1).constValue;
        }
        switch (instruction.cmpType) {
            case "eq":
                if (imm == null) {
                    addInstruction(new MIPSInstruction(MIPSInstruction.Type.SEQ, virtualReg, virtualReg1, virtualReg2));
                } else {
                    addInstruction(new MIPSInstruction(MIPSInstruction.Type.SEQ, virtualReg, virtualReg1, imm));
                }
                break;
            case "ne":
                if (imm == null) {
                    addInstruction(new MIPSInstruction(MIPSInstruction.Type.SNE, virtualReg, virtualReg1, virtualReg2));
                } else {
                    addInstruction(new MIPSInstruction(MIPSInstruction.Type.SNE, virtualReg, virtualReg1, imm));
                }
                break;
            case "sle":
                if (imm == null) {
                    addInstruction(new MIPSInstruction(MIPSInstruction.Type.SLE, virtualReg, virtualReg1, virtualReg2));
                } else {
                    addInstruction(new MIPSInstruction(MIPSInstruction.Type.SLE, virtualReg, virtualReg1, imm));
                }
                break;
            case "slt":
                if (imm == null) {
                    addInstruction(new MIPSInstruction(MIPSInstruction.Type.SLT, virtualReg, virtualReg1, virtualReg2));
                } else {
                    addInstruction(new MIPSInstruction(MIPSInstruction.Type.LI, PhysicalReg.A3, imm));
                    addInstruction(new MIPSInstruction(MIPSInstruction.Type.SLT, virtualReg, virtualReg1, PhysicalReg.A3));
                }
                break;
            case "sge":
                if (imm == null) {
                    addInstruction(new MIPSInstruction(MIPSInstruction.Type.SGE, virtualReg, virtualReg1, virtualReg2));
                } else {
                    addInstruction(new MIPSInstruction(MIPSInstruction.Type.SGE, virtualReg, virtualReg1, imm));
                }
                break;
            case "sgt":
                if (imm == null) {
                    addInstruction(new MIPSInstruction(MIPSInstruction.Type.SGT, virtualReg, virtualReg1, virtualReg2));
                } else {
                    addInstruction(new MIPSInstruction(MIPSInstruction.Type.SGT, virtualReg, virtualReg1, imm));
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + instruction.cmpType);
        }

    }

    private void transCall(Instruction instruction) {
        if (instruction.funcName.equals("@getint")) {
            virtualReg = new VirtualReg();
            curLFunction.addVirtualReg(virtualReg);
            instruction.virtualReg = virtualReg;
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.LI, PhysicalReg.V0, 5));
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.SYSCALL));
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, virtualReg, PhysicalReg.V0, 0));
            v0 = 0;
            return;
        } else if (instruction.funcName.equals("@putch")) {
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.LI, PhysicalReg.V0, 11));

            v0 = 11;
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.LI, PhysicalReg.A0, instruction.values.get(0).constValue));
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.SYSCALL));
            return;
        } else if (instruction.funcName.equals("@putint")) {

            addInstruction(new MIPSInstruction(MIPSInstruction.Type.LI, PhysicalReg.V0, 1));

            v0 = 1;
            if (instruction.values.get(0).isConst) {
                addInstruction(new MIPSInstruction(MIPSInstruction.Type.LI, PhysicalReg.A0, instruction.values.get(0).constValue));
            } else {
                addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.A0, instruction.values.get(0).virtualReg, 0));
            }
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.SYSCALL));
            return;
        }

        // 保存现场，参数压栈，跳转
        // 但是好像不用特意保存现场，因为该保存的都已经保存完了？除了应当保存 $ra
        Function function = MANAGER.findFunction(instruction.funcName);
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, -4));
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.SW, PhysicalReg.RA, PhysicalReg.SP, 0));

        // 这里分配的空间在函数结束时统一回收。故在结束函数后只需要恢复 $ra
        for (Value param: instruction.values) {
            if (param.isConst) {
                VirtualReg temReg = new VirtualReg();
                curLFunction.addVirtualReg(temReg);
                addInstruction(new MIPSInstruction(MIPSInstruction.Type.LI, temReg, param.constValue));
                addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, -4));
                addInstruction(new MIPSInstruction(MIPSInstruction.Type.SW, temReg, PhysicalReg.SP, 0));
            } else {
                addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, -4));
                addInstruction(new MIPSInstruction(MIPSInstruction.Type.SW, param.virtualReg, PhysicalReg.SP, 0));
            }
        }
        // 跳转
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.JAL, instruction.funcName.substring(1)));
        // 恢复 $ra
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.LW, PhysicalReg.RA, PhysicalReg.SP, 0));
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, 4));
        // 获取返回值
        if (!function.retType.equals(ValueType.VOID)) {
            virtualReg = new VirtualReg();
            curLFunction.addVirtualReg(virtualReg);
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, virtualReg, PhysicalReg.V0, 0)); // 这是一条 move 指令
            v0 = 0;
            instruction.virtualReg = virtualReg;
        }
    }

    private void transRet(Instruction instruction) {
        if (!instruction.values.isEmpty()) {
            if (instruction.values.get(0).isConst) {
                addInstruction(new MIPSInstruction(MIPSInstruction.Type.LI, PhysicalReg.V0, instruction.values.get(0).constValue));
                v0 = 0;
            } else {
                addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.V0, instruction.values.get(0).virtualReg, 0));
                v0 = 0;
            }
        }
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.J, curFunction.endName()));
    }

    private void transBr(Instruction instruction) {
        if (instruction.values.size() == 1) {
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.J,
                    curFunction.beginName() + '_' + instruction.values.get(0).name.substring(1)));
        } else {
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.BNEZ, instruction.values.get(0).virtualReg,
                    curFunction.beginName() + '_' + instruction.values.get(1).name.substring(1)));  // 若为真，跳转至第一个 label
            // 否则跳转至第二个 label
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.J,
                    curFunction.beginName() + '_' + instruction.values.get(2).name.substring(1)));
        }
    }

    private void transLabel(Instruction instruction) {
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.LABEL,
                curFunction.beginName() + '_' + instruction.name.substring(1)));
    }

    private void transLoad(Instruction instruction) {
        virtualReg = new VirtualReg();
        curLFunction.addVirtualReg(virtualReg);
        instruction.setVirtualReg(virtualReg);
        Value from = instruction.values.get(0);
        if (from.name.charAt(0) == '@') {   // 全局变量
            String name = from.name.substring(1);
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.LW, virtualReg, name));
        } else {
            if (from.virtualReg != null) {
                addInstruction(new MIPSInstruction(MIPSInstruction.Type.LW, virtualReg, from.virtualReg, 0));
            } else {
                int offset = getVarOffset(from.name);
                virtualReg1 = new VirtualReg();
                curLFunction.addVirtualReg(virtualReg1);
                instruction.setVirtualReg(virtualReg1);
                addInstruction(new MIPSInstruction(MIPSInstruction.Type.LA, virtualReg, PhysicalReg.SP, offset, true));
                addInstruction(new MIPSInstruction(MIPSInstruction.Type.LW, virtualReg1, virtualReg, 0));
                virtualReg1 = null;
            }
        }
    }

    private void transStore(Instruction instruction) {
        if (curFunction.isParam(instruction.values.get(0).name)) {
            return; // 函数形参已经压过栈了
        }
        Value value = instruction.values.get(0);
        Value storeTo = instruction.values.get(1);
        if (storeTo.name.charAt(0) == '@') {
            String name = storeTo.name.substring(1);
            if (value.isConst) {
                VirtualReg temVirtualReg = new VirtualReg();
                curLFunction.addVirtualReg(temVirtualReg);
                addInstruction(new MIPSInstruction(MIPSInstruction.Type.LI, temVirtualReg, value.constValue));
                addInstruction(new MIPSInstruction(MIPSInstruction.Type.SW, temVirtualReg, name));
            } else {
                addInstruction(new MIPSInstruction(MIPSInstruction.Type.SW, value.virtualReg, name));
            }
        } else {
            if (storeTo.virtualReg != null) {
                if (value.isConst) {
                    VirtualReg temVirtualReg = new VirtualReg();
                    curLFunction.addVirtualReg(temVirtualReg);
                    addInstruction(new MIPSInstruction(MIPSInstruction.Type.LI, temVirtualReg, value.constValue));
                    addInstruction(new MIPSInstruction(MIPSInstruction.Type.SW, temVirtualReg, storeTo.virtualReg, 0));
                } else {
                    addInstruction(new MIPSInstruction(MIPSInstruction.Type.SW, value.virtualReg, storeTo.virtualReg, 0));
                }
            } else {
                int offset = getVarOffset(storeTo.name);
                virtualReg1 = new VirtualReg();
                curLFunction.addVirtualReg(virtualReg1);
                addInstruction(new MIPSInstruction(MIPSInstruction.Type.LA, virtualReg1, PhysicalReg.SP, offset, true));
                if (value.isConst) {
                    VirtualReg temVirtualReg = new VirtualReg();
                    curLFunction.addVirtualReg(temVirtualReg);
                    addInstruction(new MIPSInstruction(MIPSInstruction.Type.LI, temVirtualReg, value.constValue));
                    addInstruction(new MIPSInstruction(MIPSInstruction.Type.SW, temVirtualReg, virtualReg1, 0));
                } else {
                    addInstruction(new MIPSInstruction(MIPSInstruction.Type.SW, value.virtualReg, virtualReg1, 0));
                }
                virtualReg1 = null;
            }
        }
    }

    private void transGetElementPtr(Instruction instruction) {
        VirtualReg temVirtualReg1 = new VirtualReg();
        instruction.setVirtualReg(temVirtualReg1);
        VirtualReg temVirtualReg2;
        Value base = instruction.values.get(0);
        ValueType valueType = instruction.getEleType.pointTo;
        if (base.name.charAt(0) == '@') { // 全局变量
            curLFunction.addVirtualReg(temVirtualReg1);
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.LA, temVirtualReg1, base.name.substring(1)));
            for (int i = 2; i < instruction.values.size(); i++) {
                if (instruction.values.get(i).isConst) {
                    temVirtualReg2 = new VirtualReg();
                    curLFunction.addVirtualReg(temVirtualReg2);
                    addInstruction(new MIPSInstruction(MIPSInstruction.Type.LI, temVirtualReg2, instruction.values.get(i).constValue));
                    addInstruction(new MIPSInstruction(MIPSInstruction.Type.MUL, temVirtualReg2, temVirtualReg2, valueType.elementType.size * 4));
                    addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDU, temVirtualReg1, temVirtualReg1, temVirtualReg2));
                } else {
                    temVirtualReg2 = new VirtualReg();
                    curLFunction.addVirtualReg(temVirtualReg2);
                    addInstruction(new MIPSInstruction(MIPSInstruction.Type.MUL, temVirtualReg2, instruction.values.get(i).virtualReg, valueType.elementType.size * 4));
                    addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDU, temVirtualReg1, temVirtualReg1, temVirtualReg2));
                }
                valueType = valueType.elementType;
            }
        } else {    // 局部变量
            if (base.virtualReg == null) {  // 存在于栈中
                curLFunction.addVirtualReg(temVirtualReg1);
                int offset = getVarOffset(base.name);
                addInstruction(new MIPSInstruction(MIPSInstruction.Type.LA, temVirtualReg1, PhysicalReg.SP, offset, true));
                for (int i = 2; i < instruction.values.size(); i++) {
                    if (instruction.values.get(i).isConst) {
                        temVirtualReg2 = new VirtualReg();
                        curLFunction.addVirtualReg(temVirtualReg2);
                        addInstruction(new MIPSInstruction(MIPSInstruction.Type.LI, temVirtualReg2, instruction.values.get(i).constValue));
                        addInstruction(new MIPSInstruction(MIPSInstruction.Type.MUL, temVirtualReg2, temVirtualReg2, valueType.elementType.size * 4));
                        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDU, temVirtualReg1, temVirtualReg1, temVirtualReg2));
                    } else {
                        temVirtualReg2 = new VirtualReg();
                        curLFunction.addVirtualReg(temVirtualReg2);
                        addInstruction(new MIPSInstruction(MIPSInstruction.Type.MUL, temVirtualReg2, instruction.values.get(i).virtualReg, valueType.elementType.size * 4));
                        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDU, temVirtualReg1, temVirtualReg1, temVirtualReg2));
                    }
                    valueType = valueType.elementType;
                }
            } else {
                temVirtualReg1 = base.virtualReg;
                curLFunction.addVirtualReg(temVirtualReg1);
                instruction.setVirtualReg(temVirtualReg1);
                for (int i = 1; i < instruction.values.size(); i++) {
                    if (instruction.values.get(i).isConst) {
                        temVirtualReg2 = new VirtualReg();
                        curLFunction.addVirtualReg(temVirtualReg2);
                        addInstruction(new MIPSInstruction(MIPSInstruction.Type.LI, temVirtualReg2, instruction.values.get(i).constValue));
                        addInstruction(new MIPSInstruction(MIPSInstruction.Type.MUL, temVirtualReg2, temVirtualReg2, valueType.size * 4));
                        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDU, temVirtualReg1, temVirtualReg1, temVirtualReg2));
                    } else {
                        temVirtualReg2 = new VirtualReg();
                        curLFunction.addVirtualReg(temVirtualReg2);
                        addInstruction(new MIPSInstruction(MIPSInstruction.Type.MUL, temVirtualReg2, instruction.values.get(i).virtualReg, valueType.size * 4));
                        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDU, temVirtualReg1, temVirtualReg1, temVirtualReg2));
                    }
                    valueType = valueType.elementType;
                }
            }
        }
    }

}
