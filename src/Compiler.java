import lexer.Lexer;
import mir.Manager;
import parser.Parser;
import util.ErrorLog;
import util.GlobalConfigure;
import visitor.Visitor;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Compiler {
    public static void main(String[] args) throws IOException {
        try (PrintWriter output = new PrintWriter("output.txt")) {
            String content = new String(Files.readAllBytes(Paths.get("testfile.txt")), StandardCharsets.UTF_8);
            Lexer.LEXER.run(content);
            // output.print(Lexer.LEXER.printThis());
            Parser.PARSER.run();
            if (GlobalConfigure.ERROR && !ErrorLog.ERROR_LOGS.isEmpty()) {
                ErrorLog.ERROR_LOGS.printErrorLogs();
                return;
            }
            Visitor.VISITOR.run();
            try (PrintWriter llvm = new PrintWriter("llvm_ir.txt")) {
                llvm.print(Manager.MANAGER.printCodes());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 动态解释执行
        // Executor.EXECUTOR.run();


    }

}
