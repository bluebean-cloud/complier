package mir;

public class Value {
    Type type;
    public Use use; // 谁使用了我

    public String name;

    public enum Type {
        BASIC_BLOCK, FUNCTION, FUNCTION_PARAM, GLOBAL_VALUE, INSTRUCTION
    }


}
