import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import Util.ErrorLog;
import Util.NotMatchException;
import frontend.lexer.Lexer;
import frontend.parser.Parser;
import frontend.visitor.Visitor;


public class Compiler {
    public static void main(String[] args) {
        try (PrintWriter output = new PrintWriter("output.txt")) {
            String content = new String(Files.readAllBytes(Paths.get("testfile.txt")), StandardCharsets.UTF_8);
            Lexer.LEXER.run(content);
            Parser.PARSER.run();
            if (!ErrorLog.ERRORLIST.isEmpty()) {
                try (PrintWriter errPut = new PrintWriter("error.txt")) {
                    for (ErrorLog errorLog : ErrorLog.ERRORLIST) {
                        errPut.println(errorLog.toString());
                    }
                }
                return;
            }
            // output.print(Parser.PARSER.root);
            Visitor.VISITOR.run();
        } catch (IOException | NotMatchException e) {
            throw new RuntimeException(e);
        }
    }

}
