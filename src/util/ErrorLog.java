package util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class ErrorLog {
    public static final ErrorLog ERROR_LOGS = new ErrorLog();
    ArrayList<Log> logs = new ArrayList<>();

    private ErrorLog() {}

    public void addErrorLog(int line, String type) {
        logs.add(new Log(line, type));
    }

    public void printErrorLogs() {
        logs.sort(Log::compareTo);
        try (PrintWriter output = new PrintWriter("error.txt")) {
            for (Log log: logs) {
                output.println(log.toString());
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static class Log implements Comparable {
        private int line;
        private String type;

        public Log(int line, String type) {
            this.line = line;
            this.type = type;
        }

        @Override
        public String toString() {
            return line + " " + type;
        }

        @Override
        public int compareTo(Object o) {
            return line - ((Log) o).line;
        }
    }






}
