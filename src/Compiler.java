import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import Util.NotMatchException;
import frontend.lexer.Lexer;
import frontend.parser.Parser;

public class Compiler {
    public static void main(String[] args) {
        try (PrintWriter output = new PrintWriter("output.txt")) {
            String content = new String(Files.readAllBytes(Paths.get("testfile.txt")), StandardCharsets.UTF_8);
            Lexer.LEXER.run(content);
            Parser.PARSER.run();
            output.print(Parser.PARSER.root);
        } catch (IOException | NotMatchException e) {
            throw new RuntimeException(e);
        }
    }

}
