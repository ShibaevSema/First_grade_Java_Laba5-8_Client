package semashibaev.ifmo.client.graphics;

import semashibaev.ifmo.cfs.CommandForServer;
import semashibaev.ifmo.client.ClientConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import static semashibaev.ifmo.client.ClientConnection.*;
import static semashibaev.ifmo.client.Serialization.serialize;

public class WindowRegistration extends JFrame {
    static WindowMain wm;
    JButton reg = new JButton("Send");
    Label login = new Label("Login");
    public static JTextField loginF = new JTextField(16);
    Label password = new Label("Password");
    public static JTextField passwordF = new JTextField(16);
//    JTextArea info = new JTextArea("Login or Register type username and password.\n" +
//            "If username does not exists you will be registered.");

    private static String getFastAnswer(CommandForServer commandForServer) throws IOException, ClassNotFoundException {
        Socket outcoming = new Socket(HOST, PORT);
        OutputStream writer = outcoming.getOutputStream();
        writer.write(serialize(commandForServer));

        InputStreamReader inputStreamReader = new InputStreamReader(outcoming.getInputStream());
        BufferedReader reader = new BufferedReader(inputStreamReader);
        char readChar = (char) reader.read();
        StringBuilder builder = new StringBuilder();
        builder.append(readChar);
        while (reader.ready()) {
            readChar = (char) reader.read();
            builder.append(readChar);
        }
        outcoming.close();
        String answer = builder.toString();
        return answer;
    }

    private void registration(ActionEvent e) {


    }

    public WindowRegistration() throws Exception {
        super("Authorization");

        this.setBounds(250, 200, 500, 250);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);

        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.NONE;//при изменении размера - поля не меняются
        c.gridheight = 1;//занимают 1 ячейк
        c.gridwidth = 1;
        c.gridx = GridBagConstraints.RELATIVE;
        c.gridy = GridBagConstraints.RELATIVE;
        c.insets = new Insets(10, 8, 0, 0);

        GridBagConstraints c2 = new GridBagConstraints();
        c2.anchor = GridBagConstraints.CENTER;
        c2.fill = GridBagConstraints.NONE;
        c2.gridheight = 1;
        c2.gridwidth = 1;
        c2.gridx = GridBagConstraints.RELATIVE;
        c2.gridy = GridBagConstraints.RELATIVE;
        c2.insets = new Insets(10, 10, 0, 0);
        c2.gridwidth = GridBagConstraints.REMAINDER;
        c2.ipadx = 25;

        login.setFont(new Font("Arial", Font.PLAIN, 15));
        gbl.setConstraints(login, c);
        add(login);

        gbl.setConstraints(loginF, c2);
        add(loginF);

        password.setFont(new Font("Arial", Font.PLAIN, 15));
        gbl.setConstraints(password, c);
        add(password);

        gbl.setConstraints(passwordF, c2);
        add(passwordF);

        gbl.setConstraints(reg, c2);
        add(reg);

//        info.setFont(new Font("Arial", Font.PLAIN, 15));
//        gbl.setConstraints(info, c2);
//        add(info);

        ActionListener br = new ButtonReg();
        reg.addActionListener(br);

    }

    class ButtonReg implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            try {
                username = loginF.getText();
                ClientConnection.password = passwordF.getText();
                CommandForServer cfs = new CommandForServer("help", new ArrayList<>(), username, ClientConnection.password);
                String answerR = getFastAnswer(cfs);
                if (answerR.equals(notReg)) {
                    System.out.println("**" + answerR);
                } else {
                    System.out.println("List of commands:");
                    wm = new WindowMain();
                    wm.setVisible(true);
                    dispose();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
