package semashibaev.ifmo.client;


import semashibaev.ifmo.cfs.CommandForServer;
import semashibaev.ifmo.client.graphics.WindowMain;
import semashibaev.ifmo.client.graphics.WindowRegistration;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import static semashibaev.ifmo.client.Serialization.serialize;

public class ClientConnection {
    public static int PORT = 6000 ;
    public static String HOST = "localhost";
    public static volatile String exit = "exit";
    public final static PrintStream sink = new PrintStream(System.out);
    private static Scanner fromKeyboard = new Scanner(System.in);
    public static volatile String username;
    public static volatile String password;
    static boolean reg = true;
    public static String notReg = "Invalid Credentials, register or type valid password";
    public static WindowRegistration wr;
    static WindowMain instance;


    public static void connect() throws Exception {
        try (Socket client = new Socket(HOST, PORT)) {
            work(client);
        } catch (IOException e) {
            System.err.println("No connection to server.");
        }
    }


    public static void work(Socket socket) throws Exception {
        try {
            wr = new WindowRegistration();
            wr.setVisible(true);
            while (true) {
                if (username == null)
                    continue;
                User user = new User(username, password);
                CommandForServer command = new CommandForServer("help", new ArrayList<>(), username, password);
                sendCommand(command, socket);
                String answerR = getAnswer(socket);
                if (answerR.equals(notReg)) {
                    System.out.println("**" + answerR);
                } else {
                    System.out.println("List of commands:");
                    System.out.println(answerR);
                    wr.setVisible(false);
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("No connection to server");
        }

    }

    public static String getAnswer(Socket socket) throws Exception {

        InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuilder builder = new StringBuilder();
        char readChar = (char) reader.read();
        builder.append(readChar);
        while (reader.ready()) {
            readChar = (char) reader.read();
            builder.append(readChar);
        }
        return builder.toString();
    }


    public static void sendCommand(CommandForServer commandForServer, Socket socket) throws IOException, ClassNotFoundException {
        OutputStream writer = socket.getOutputStream();
        writer.write(serialize(commandForServer));
    }

    public static void close() throws IOException {
        System.out.println("Completion of the program.");
        System.exit(0);
    }

    static class User {
        String Username;
        String password;

        User(String Username, String password) {
            this.password = password;
            this.Username = Username;
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

        public void setUsername(String username) {
            this.Username = username;
        }
    }

}




