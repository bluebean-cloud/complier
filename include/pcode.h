#ifndef _PCODE
#define _PCODE

/*
对于 PCode 活动记录的设计参考课程组

仅对函数分配活动记录
将函数中的所有变量存储至 Local Var 区，在编译期可以确定变量的地址

*****高地址*****
|--------------| <- SP
|  Temp  Var   |
|--------------|
|  Local Var   |
|--------------|
|  Func Param  |
|--------------|
| Dynamic Link |
|--------------|
|   Recv Addr  |
|--------------|
|   Recv Val   | <- MP
|--------------|
*****低地址*****
*/

#define RV MP
#define RA (MP + 1)
#define DL (MP + 2)
#define STACK_SIZE 1000000

// %n 代表栈顶第 n 个元素（一个元素 4 字节）
// 运算指定均弹出栈顶的两个元素，进行运算后将结果存回栈顶
typedef enum P_INS_TYPE {
    // P_AS val
    // Alloca Stack 分配栈空间
    P_AS,
    // P_CALL funcName
    // 调用函数
    P_CALL,
    // P_RET
    // 结束当前函数 <--> J RA
    P_RET,
    // P_LI val
    // 加载立即数到栈顶
    P_LI,
    // P_J label
    // 跳转至 label
    P_J,
    // P_JIT label
    // 如果栈顶元素非零，则跳转至label
    P_JIT,
    // P_PUSH val
    // 将参数区第 val 个元素加载至栈顶
    P_PUSH,
    // P_POP val
    // 将栈顶存储至参数区第 val 个元素
    P_POP,
    // P_STORE
    // 将 %0 按绝对地址解读，将地址上的值存入栈顶
    P_STORE,
    // P_WRITE
    // 将 %1 的值写入 %0 所代表的绝对地址中
    P_WRITE,
    // P_LABEL label
    // 声明一个标签
    P_LABEL,
    // P_READ
    // 从标准输入读入一个数，存入栈顶
    P_READ,
    // P_PUTS STR
    // 打印字符串
    P_PUTS,
    // P_PUTC
    // 打印栈顶字符
    P_PUTC,
    // P_PUTI
    // 打印栈顶数字
    P_PUTI,

    // %1 + %0 -> %0
    P_ADD,
    // %1 - %0 -> %0
    P_SUB,
    // %1 * %0 -> %0
    P_MUL,
    // %1 / %0 -> %0
    P_DIV,
    // %1 % %0 -> %0
    P_MOD,
    // %1 > %0 ? 1 : 0 -> %0
    P_GRE,
    // %1 >= %0 ? 1 : 0 -> %0
    P_GEQ,
    // %1 < %0 ? 1 : 0 -> %0
    P_LSS,
    // %1 <= %0 ? 1 : 0 -> %0
    P_LEQ,
    // %1 == %0 ? 1 : 0 -> %0
    P_EQ,
    // %1 != %0 ? 1 : 0 -> %0
    P_NEQ,
} P_INS_TYPE;

/*
    在函数调用时，分配活动记录：分配三个单位的栈空间分别给 RV, RA, DL
    
    这样在函数结束返回时当前栈顶就是函数的返回值。（对于 VOID 函数，返回值为 0）

    随后依次分配函数参数

    在函数结束时，SP <- MP, MP <- DL，PC <- RA
*/

typedef struct P_INS {
    P_INS_TYPE insType;
    int v;
    char* s;
} P_INS;


void pCodeRun();
int execPCode();
void pCodeVisit();
void pCodeTrans();

#endif