import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import frontend.lexer.Lexer;
import frontend.lexer.Token;

public class Compiler {
    public static void main(String[] args) {
        try (PrintWriter output = new PrintWriter("output.txt")) {
            String content = new String(Files.readAllBytes(Paths.get("testfile.txt")), StandardCharsets.UTF_8);
            Lexer.LEXER.run(content);
            for (Token token : Lexer.tokens) {
                output.println(token.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
