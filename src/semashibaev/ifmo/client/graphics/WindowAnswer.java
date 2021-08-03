package semashibaev.ifmo.client.graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WindowAnswer extends JFrame {
    private JTextArea answer = new JTextArea();
    private JButton ok = new JButton("OK.");
    private JPanel panel = new JPanel();

    public WindowAnswer(String string) throws Exception {
        answer.setText(string);
        this.setBounds(400, 250, 500, 300);
           BorderLayout gbl = new BorderLayout();
        setLayout(gbl);

        panel.add(ok);;
        add(answer,BorderLayout.CENTER);
        add(panel,BorderLayout.SOUTH);

        ActionListener ans = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                dispose();

            }
        };
        ok.addActionListener(ans);
    }


}