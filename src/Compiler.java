import lexer.Lexer;
import parser.Parser;
import util.ErrorLog;
import util.GlobalConfigure;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Compiler {
    public static void main(String[] args) {
        try (PrintWriter output = new PrintWriter("output.txt")) {
            String content = new String(Files.readAllBytes(Paths.get("testfile.txt")), StandardCharsets.UTF_8);
            Lexer.LEXER.run(content);
            // output.print(Lexer.LEXER.printThis());
            Parser.PARSER.run();
            if (GlobalConfigure.ERROR && !ErrorLog.ERROR_LOGS.isEmpty()) {
                ErrorLog.ERROR_LOGS.printErrorLogs();
                return;
            }
            output.print(Parser.PARSER.root.printSyntaxTree());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
