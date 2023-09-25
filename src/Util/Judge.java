package Util;

public class Judge {

    public static boolean isOf(String test, String... contents) {
        for (String content: contents) {
            if (test.equals(content)) {
                return true;
            }
        }
        return false;
    }
}
