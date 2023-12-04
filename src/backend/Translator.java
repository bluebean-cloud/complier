package backend;

import mir.Instruction;
import mir.Manager;
import mir.Value;
import mir.ValueType;
import mir.derivedValue.BasicBlock;
import mir.derivedValue.Function;
import mir.derivedValue.GlobalVar;

import java.util.ArrayList;
import java.util.HashMap;

public class Translator {
    private Translator() {}
    public static final Translator TRANSLATOR = new Translator();
    private ArrayList<MIPSInstruction> instructions = new ArrayList<>();
    Manager MANAGER = Manager.MANAGER;
    public HashMap<String, GlobalVar> globalVars;
    private final HashMap<String, Integer> varOffset = new HashMap<>();
    int sum = 0;
    public int getVarOffset(String name) {
        return sum - varOffset.get(name);
    }

    public GlobalVar getGlobalVar(String name) {
        return globalVars.get(name);
    }

    public void addInstruction(MIPSInstruction mipsInstruction) {
        instructions.add(mipsInstruction);
    }

    public void run() {
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.PSEUDO, ".data"));
        allocGlobalMemory();
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.PSEUDO, ".text"));
        for (Function function: MANAGER.functions) {
            curFunction = function;
            transFunction();
        }
    }

    private void allocGlobalMemory() {
        globalVars = MANAGER.globalVars;
    }


    Function curFunction;
    BasicBlock curBlock;
    private void transFunction() {
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.LABEL, curFunction.beginName()));
        sum = 0;
        varOffset.clear();
        for (Instruction decl: curFunction.decls) {
            int size = decl.type.getPointToByte();
            varOffset.put(decl.name, sum + size);
            sum += size;
        }
        // 分配栈空间。函数参数在调用时已经分配好，故减去
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, -(sum - curFunction.params.size() * 4)));

        for (BasicBlock block: curFunction.blocks) {
            curBlock = block;
            transBlock();
        }

        // 结束函数，回收空间
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.LABEL, curFunction.endName()));
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, sum));
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.JR, PhysicalReg.RA));
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

    private void prepareForCal(Instruction instruction, String op) {
        virtualReg = new VirtualReg();
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
            return;
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
    }

    private void transAdd(Instruction instruction) {
        prepareForCal(instruction, "+");
        if (imm == null) {
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDU, virtualReg, virtualReg1, virtualReg2));
        } else {
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, virtualReg, virtualReg1, imm));
        }
    }

    private void transSub(Instruction instruction) {
        prepareForCal(instruction, "-");
        if (imm == null) {
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.SUBU, virtualReg, virtualReg1, virtualReg2));
        } else {
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.SUBU, virtualReg, virtualReg1, imm));
        }
    }

    private void transMul(Instruction instruction) {
        prepareForCal(instruction, "*");
        if (imm == null) {
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.MUL, virtualReg, virtualReg1, virtualReg2));
        } else {
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.MUL, virtualReg, virtualReg1, imm));
        }
    }

    private void transDiv(Instruction instruction) {
        prepareForCal(instruction, "/");
        if (imm == null) {
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.DIV, virtualReg, virtualReg1, virtualReg2));
        } else {
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.DIV, virtualReg, virtualReg1, imm));
        }
    }

    private void transRem(Instruction instruction) {
        prepareForCal(instruction, "%");
        if (imm == null) {
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.REM, virtualReg, virtualReg1, virtualReg2));
        } else {
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.REM, virtualReg, virtualReg1, imm));
        }
    }

    private void transICmp(Instruction instruction) {
        virtualReg = new VirtualReg();
        instruction.setVirtualReg(virtualReg);
        if (instruction.isConst) {
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
                    addInstruction(new MIPSInstruction(MIPSInstruction.Type.SLTI, virtualReg, virtualReg1, imm));
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
        // 保存现场，参数压栈，跳转
        // 但是好像不用特意保存现场，因为该保存的都已经保存完了？除了应当保存 $ra
        Function function = MANAGER.findFunction(instruction.funcName);
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.SP, PhysicalReg.SP, -4));
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.SW, PhysicalReg.RA, PhysicalReg.SP, 0));

        // 这里分配的空间在函数结束时统一回收。故在结束函数后只需要恢复 $ra
        for (Value param: instruction.values) {
            if (param.isConst) {
                VirtualReg temReg = new VirtualReg();
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
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, virtualReg, PhysicalReg.V0, 0)); // 这是一条 move 指令
            instruction.virtualReg = virtualReg;
        }
    }

    private void transRet(Instruction instruction) {
        if (!instruction.values.isEmpty()) {
            if (instruction.values.get(0).isConst) {
                addInstruction(new MIPSInstruction(MIPSInstruction.Type.LI, PhysicalReg.V0, instruction.values.get(0).constValue));
            } else {
                addInstruction(new MIPSInstruction(MIPSInstruction.Type.ADDUI, PhysicalReg.V0, instruction.values.get(0).virtualReg, 0));
            }
        }
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.J, curFunction.endName()));
    }

    private void transBr(Instruction instruction) {
        if (instruction.values.size() == 1) {
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.J, instruction.values.get(0).name.substring(1)));
        } else {
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.BNEZ, instruction.values.get(0).virtualReg,
                    instruction.values.get(1).name.substring(1)));  // 若为真，跳转至第一个 label
            // 否则跳转至第二个 label
            addInstruction(new MIPSInstruction(MIPSInstruction.Type.J, instruction.values.get(2).name.substring(1)));
        }
    }

    private void transLabel(Instruction instruction) {
        addInstruction(new MIPSInstruction(MIPSInstruction.Type.LABEL, instruction.name.substring(1)));
    }

    private void transLoad(Instruction instruction) {
        virtualReg = new VirtualReg();
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
                instruction.setVirtualReg(virtualReg1);
                addInstruction(new MIPSInstruction(MIPSInstruction.Type.LA, virtualReg, PhysicalReg.SP, offset));
                addInstruction(new MIPSInstruction(MIPSInstruction.Type.LW, virtualReg1, virtualReg, 0));
                virtualReg1 = null;
            }
        }
    }

    private void transStore(Instruction instruction) {
        Value value = instruction.values.get(0);
        Value storeTo = instruction.values.get(1);
        if (storeTo.name.charAt(0) == '@') {
            String name = storeTo.name.substring(1);
            if (value.isConst) {
                VirtualReg temVirtualReg = new VirtualReg();
                addInstruction(new MIPSInstruction(MIPSInstruction.Type.LI, temVirtualReg, value.constValue));
                addInstruction(new MIPSInstruction(MIPSInstruction.Type.SW, temVirtualReg, name));
            } else {
                addInstruction(new MIPSInstruction(MIPSInstruction.Type.SW, storeTo.virtualReg, name));
            }
        } else {
            if (storeTo.virtualReg != null) {
                addInstruction(new MIPSInstruction(MIPSInstruction.Type.SW, value.virtualReg, storeTo.virtualReg, 0));
            } else {
                int offset = getVarOffset(storeTo.name);
                virtualReg1 = new VirtualReg();
                addInstruction(new MIPSInstruction(MIPSInstruction.Type.LA, virtualReg1, PhysicalReg.SP, offset));
                addInstruction(new MIPSInstruction(MIPSInstruction.Type.SW, value.virtualReg, virtualReg1, 0));
                virtualReg1 = null;
            }
        }
    }

    private void transGetElementPtr(Instruction instruction) {

    }

}
