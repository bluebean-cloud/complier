package mir;

import java.util.ArrayList;

public class Use {
    public ArrayList<Instruction> users = new ArrayList<>();
    public Value value;

    public void addUser(Instruction instruction) {
        users.add(instruction);
    }

}