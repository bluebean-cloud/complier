package executor;

import parser.syntaxTreeNodes.ConstDef;
import parser.syntaxTreeNodes.VarDef;

import java.util.ArrayList;
import java.util.HashMap;

public class RuntimeStack {
    public RuntimeStack parent;

    public RuntimeStack() {}

    public RuntimeStack(RuntimeStack parent) {
        this.parent = parent;
    }

    public HashMap<String, Var> varHashMap = new HashMap<>();

    public boolean contains(String name) {
        return varHashMap.containsKey(name);
    }

    public Var getVar(String name) {
        return varHashMap.get(name);
    }

    public void addVar(VarDef varDef) {
        if (varDef.constExp1 == null) {
            Var var = new Var(RuntimeType.SIMPLE_VAR);
            if (varDef.initVal != null) {
                var.value = Executor.EXECUTOR.interpretExp(varDef.initVal.exp);
            } else {
                var.value = 0;
            }
            varHashMap.put(varDef.ident.content, var);
        } else {    // 数组
            Var var = new Var(RuntimeType.ARRAY);
            if (varDef.initVal != null) {   // 有初始值
                var.arrayList = Executor.EXECUTOR.interpretInitVals(varDef.initVal.initVals);
            } else {                        // 无初始值，全部初始成 0
                int x = Executor.EXECUTOR.interpretConstExp(varDef.constExp1);
                ArrayList<Var> vars = new ArrayList<>();
                if (varDef.constExp2 != null) {
                    int y = Executor.EXECUTOR.interpretConstExp(varDef.constExp2);
                    for (int i = 0; i < x; i++) {
                        Var varSon = new Var(RuntimeType.ARRAY, new ArrayList<>());
                        for (int j = 0; j < y; j++) {
                            varSon.arrayList.add(new Var(RuntimeType.INT_CON, 0));
                        }
                        vars.add(varSon);
                    }
                } else {
                    for (int i = 0; i < x; i++) {
                        vars.add(new Var(RuntimeType.INT_CON, 0));
                    }
                }
                var.arrayList = vars;
            }
            varHashMap.put(varDef.ident.content, var);
        }
    }

    public void addVar(ConstDef constDef) {
        if (constDef.constExp1 == null) {
            Var var = new Var(RuntimeType.SIMPLE_VAR);
            var.value = Executor.EXECUTOR.interpretConstExp(constDef.constInitVal.constExp);
            varHashMap.put(constDef.ident.content, var);
        } else {    // 数组
            Var var = new Var(RuntimeType.ARRAY);
            // constVar 一定有初值
            var.arrayList = Executor.EXECUTOR.interpretConstInitVals(constDef.constInitVal.constInitVals);
            varHashMap.put(constDef.ident.content, var);
        }
    }

    public void addVar(String name, Var var) {
        varHashMap.put(name, var);
    }


}
