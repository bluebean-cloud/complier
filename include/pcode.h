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

typedef struct P_INS P_INS;

// PCode-visitor
typedef struct P_FUNC P_FUNC;
typedef struct P_SCOPE P_SCOPE;
typedef struct P_VAR P_VAR;

struct P_INS {
    P_INS_TYPE insType;
    int v;
    char* s;
};

struct P_FUNC {
    TypeBorFuncType funcType;
    P_SCOPE* scope;
    // P_VAR*
    Vector* params;
    // P_VAR*
    Vector* vars;
    char* funcName;
    // 为函数内局部变量编号计数
    int varCnt;
};

struct P_SCOPE {
    P_SCOPE* parent;
    // 全局域不存储，非全局域存储 P_SCOPE*
    Vector* sons;
    // 全局域存储 P_FUNC*
    Vector* funcs;
    // 全局域存储 P_VAR*
    Vector* vars;
    // 非全局域存储 name <-> var 映射
    Trie* trieRoot;
    // 是否包含于循环当中，非 0 代表处于循环中
    int isCycle;
    Vector* blockItems;
};

struct P_VAR {
    TypeBorFuncType varType;
    int isArr;
    int isCon;
    // 进行语义分析时根据该 name 查询作用域
    char* name;
    // 对函数体内部变量进行重命名
    // 具体规则为 name%i >> name 为原名称，i 为此函数定义的第 i 个变量
    // 这样做的目的是防止不同域下的变量重名。
    // 并可以通过 %i 取出该变量在函数参数区中的位置
    char* rename;
    int id;
};

#endif