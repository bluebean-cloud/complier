package Util;

import java.util.ArrayList;

public class ErrorLog implements Comparable {
    public static ArrayList<ErrorLog> ERRORLIST = new ArrayList<>();

    private final int line;
    private final char type;

    public ErrorLog(int line, char type) {
        this.line = line;
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("%d %c", line, type);
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof ErrorLog)) {
            return 0;
        }
        return this.line - ((ErrorLog) o).line;
    }
}
