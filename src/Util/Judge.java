package Util;

import frontend.parser.Type;

public class Judge {

    public static boolean isOf(String test, String... contents) { // 测试 test 是否为 contents 中一员
        for (String content: contents) {
            if (test.equals(content)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isOf(Type test, Type... contents) {
        for (Type content: contents) {
            if (test.equals(content)) {
                return true;
            }
        }
        return false;
    }

}
