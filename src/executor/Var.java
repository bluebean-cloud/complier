package executor;

import java.util.ArrayList;

public class Var {
    public final RuntimeType runtimeType;
    public ArrayList<Var> arrayList;
    public Integer value;

    public Var(RuntimeType runtimeType) {
        this.runtimeType = runtimeType;
    }

    public Var(RuntimeType runtimeType, Integer value) {
        this.runtimeType = runtimeType;
        this.value = value;
    }

    public Var(RuntimeType runtimeType, ArrayList<Var> arrayList) {
        this.runtimeType = runtimeType;
        this.arrayList = arrayList;
    }

    public void addValue(Var var) {
        arrayList.add(var);
    }

    public int compareTo(Var var) {
        return this.value - var.value;
    }

    public boolean equals(Var var) {
        return this.value.equals(var.value);
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Var getVar(int x) {
        return arrayList.get(x);
    }

    public Var getVar(int x, int y) {
        return arrayList.get(x).getVar(y);  // 访问二维数组
    }

}
