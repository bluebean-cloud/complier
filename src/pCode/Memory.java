package pCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class Memory {
    public static final Memory MEMORY = new Memory();
    private Memory() {}
    /* 模拟内存行为
    * 使用 ArrayList 模拟内存
    *
    */
    ArrayList<Integer> memory = new ArrayList<>();

    public int load(int addr) {
        if (addr >= memory.size()) {
            memory.addAll(Collections.nCopies(addr - memory.size() + 1, 0));
        }
        return memory.get(addr);
    }

    public void save(int addr, int val) {
        if (addr >= memory.size()) {
            memory.addAll(Collections.nCopies(addr - memory.size() + 1, 0));
        }
        memory.set(addr, val);
    }


}
