package pCode;

public class Instr {
    public enum Type {
        LIT,    // LIT imm              加载立即数imm到栈顶
        OPR,    // OPR opcode           进行栈顶与次栈顶（或者单独栈顶）的运算，并将结果压入栈顶；opcode决定运算类型
        LOD,    // LOD level, addr      以栈顶元素为动态偏移，加载内存中(level, addr)+动态偏移处的变量值到栈顶
        STO,    // STO level, addr      以栈顶元素为动态偏移，把次栈顶的元素写入内存中(level, addr)+动态偏移处的变量
        CAL,    // CAL label            函数调用（分配AR、设置SL、DL、参数等）
        BLKS,   // BLKS                 开启新的block（分配新的AR，设置SL、DL等）
        BLKE,   // BLKE level           结束当前block（回收当前AR）
        JMP,    // JMP label            无条件跳转到label
        JPC,    // JPC label            栈顶为0时跳转到label，否则顺序执行
        JPT,    // JPT label	        栈顶不为0时跳转到label，否则顺序执行
        INT,    // INT imm	            栈顶寄存器加imm，用于控制栈顶指针的移动
        RED,    // RED                  读入数字到栈顶
        WRT,    // WRT                  输出栈顶数字
        WRTS,   // WRTS str             输出字符串
        LABLE,  // label: str           标识label，用于跳转
        RET,    // RET                  返回调用者并回收AR
        LEA,    // LEA level, addr      加载(level, addr)处变量的绝对地址到栈顶
    }
    public enum Opcode {
        Add, Sub, Mul, Div, Mod, And, Or, Not
    }


    Type type;
    Opcode opcode;
    int imm;
    int level;
    int addr;
    String str;
    String label;

    public Instr(Type type) {
        this.type = type;
    }

    public Instr(Type type, Opcode opcode) {
        this.type = type;
        this.opcode = opcode;
    }

    public Instr(Type type, int level, int addr) {
        this.type = type;
        this.level = level;
        this.addr = addr;
    }

    public Instr(Type type, int imm) {
        this.type = type;
        this.level = imm;
        this.imm = imm;
    }

    public Instr(Type type, String label) {
        this.type = type;
        this.label = label;
        this.str = label;
    }

    @Override
    public String toString() {
        switch (type) {
            case LIT:
            case INT:
                return type + " " + imm;
            case OPR:
                return type + " " + opcode;
            case LOD:
            case STO:
            case LEA:
                return type + " " + level + ", " + addr;
            case CAL:
            case JMP:
            case JPC:
            case JPT:
                return type + " " + label;
            case BLKS:
            case RED:
            case WRT:
            case RET:
                return type.toString();
            case BLKE:
                return type + " " + level;
            case WRTS:
                return type + " " + str;
            case LABLE:
                return "label: " + str;
            default:
                return "UNKNOWN INSTR TYPE";
        }
    }
}
