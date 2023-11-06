package pCode;

import java.util.ArrayList;
import java.util.Stack;

public class ActivityRecord {
    public int addr;                    // 基地址
    public Integer ret;                 // 返回值
    public Integer retAddr;             // 返回地址（instrs）
    public ActivityRecord staticLink;   // 静态链地址  函数调用时候的静态链是：全局变量->函数形参->函数内部...
    public ActivityRecord dynamicLink;  // 动态链
    public ArrayList<Integer> localVar; // 局部变量
    public Stack<Integer> temVar;       // 临时变量


}
