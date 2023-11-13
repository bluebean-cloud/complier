package visitor;

import mir.Value;

public class MirVar {
    enum Type {
        GLOBAL_VAL, LOCAL_VAL
    }

    public Value value;    // 值
    public String name;


}
