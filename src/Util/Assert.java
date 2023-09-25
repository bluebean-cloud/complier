package Util;

public class Assert {

    public static void isOf(String test, String... contents) throws NotMatchException {
        for (String content: contents) {
            if (test.equals(content)) {
                return;
            }
        }
        throw new NotMatchException();
    }


}
