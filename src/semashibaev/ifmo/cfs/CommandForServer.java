package semashibaev.ifmo.cfs;

import java.io.Serializable;
import java.util.ArrayList;

public class CommandForServer implements Serializable {

    private static final long serialVersionUID = 10L;
    public String command;
    public ArrayList<String> args;
    String Username;
    String password;

    public CommandForServer(String command, ArrayList<String> args, String Username, String password) {
        this.command = command;
        this.args = args;
        this.Username = Username;
        this.password = password;
    }

    public String getCommand() {
        return command;
    }

    public ArrayList<String> getArgs() {
        return args;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setArguments(ArrayList<String> arg) {
        this.args = arg;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return Username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}


