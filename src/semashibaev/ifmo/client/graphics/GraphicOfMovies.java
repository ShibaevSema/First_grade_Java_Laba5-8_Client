package semashibaev.ifmo.client.graphics;

import semashibaev.ifmo.cfs.CommandForServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static semashibaev.ifmo.client.ClientConnection.password;
import static semashibaev.ifmo.client.ClientConnection.username;
import static semashibaev.ifmo.client.graphics.WindowMain.getFastAnswer;
import static semashibaev.ifmo.client.graphics.WindowMain.modelForGraphic;
import static semashibaev.ifmo.client.graphics.WindowRegistration.wm;

public class GraphicOfMovies extends JPanel {
    public static volatile java.util.List<Shape> shapes;

    public static void createAndShowGUI() {
        JFrame f = new JFrame();
//        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(new ShapeClickTestPanel());
        f.setSize(400, 400);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    static class Animator implements Runnable {
        private Ellipse2D ellipse2D;
        private ShapeClickTestPanel mvs;
        public Animator(Ellipse2D ellipse2D, ShapeClickTestPanel mvs) {
            this.ellipse2D = ellipse2D;
            this.mvs = mvs;
        }

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                try {
                    Random rnd = new Random();
                    this.ellipse2D.setFrame(
                            this.ellipse2D.getX() + rnd.nextInt(3) - 1,
                            this.ellipse2D.getY() + rnd.nextInt(3) - 1,
                            (30-i/10), (30+i/30));
                    mvs.repaint();
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    static class ShapeClickTestPanel extends JPanel implements MouseListener {
        public ShapeClickTestPanel() {
            addMouseListener(this);
            shapes = new ArrayList<Shape>();
            int elements = modelForGraphic.getRowCount();

            for (int r = 0; r < elements; r++) {
                Ellipse2D el1 = new Ellipse2D.Double(new Integer(wm.getMovie(r, 2)), new Integer(wm.getMovie(r, 3)), 30, 30);
                shapes.add(el1);
            }
        }

        @Override
        protected void paintComponent(Graphics gr) {
            super.paintComponent(gr);
            Graphics2D graphics2D = (Graphics2D) gr;
            int elements = shapes.size();
            for (int i = 0; i < elements; i++) {
                Ellipse2D el1 = (Ellipse2D) shapes.get(i);
                String login = wm.getMovie(i, 10);
                String id = wm.getMovie(i, 0);
                int r = 0;
                int g = 0;
                int b = 0;
                for (char c : login.toCharArray()) {
                    r = (r + (c & 0xf2374) + 120) % 256;
                    g = (g + ((2 * c & 0xf1454) + 120) >> 2) % 256;
                    b = (b + ((3 * c & 0xf234) << 1) + 120) % 256;
                }
                float[] clr = new float[3];
                Color.RGBtoHSB(r, g, b, clr);
                Color color = Color.getHSBColor(clr[0], clr[1], clr[2]);
                graphics2D.draw(el1);
                graphics2D.setColor(color);
                graphics2D.fill(el1);
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            for (int i = 0; i < shapes.size(); i++) {
                Shape shape = shapes.get(i);
                if (shape.contains(e.getPoint())) {
                    Animator anm = new Animator((Ellipse2D) shape, this);
                    Thread animatorWorker = new Thread(anm);
                    animatorWorker.start();
                    String login = wm.getMovie(i, 10);
                    String info = "You clicked on element with id=" + wm.getMovie(i, 0) + ",x=" +
                            wm.getMovie(i, 2) + ",y=" + wm.getMovie(i, 3) + ",login=" + wm.getMovie(i, 10);
                    ActiveObject ao = new ActiveObject(wm.getMovie(i, 0), info, login, i);
                    ao.setVisible(true);
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }

    public Ellipse2D showAnime(Ellipse2D ellipse2D) {
        double x0 = ellipse2D.getX();
        double y0 = ellipse2D.getY();
        x0 += 1;
        y0 += 1;
        //ellipse2D.setFrame();
        ellipse2D = new Ellipse2D.Double(x0, y0, 30, 30);
        return ellipse2D;

    }

    static class ActiveObject extends JFrame {
        JButton remove = new JButton("Remove");
        JButton update = new JButton("Update");
        JPanel mainPanel = new JPanel();
        JLabel infoL;

        public ActiveObject(String id, String info, String login, int i) {
            super("Info about this element");
            infoL = new JLabel(info);
            this.setBounds(300, 200, 300, 200);
            BorderLayout gbl = new BorderLayout();
            mainPanel.add(remove);
            mainPanel.add(update);
            add(mainPanel, BorderLayout.SOUTH);
            add(infoL, BorderLayout.CENTER);
            this.setDefaultCloseOperation(2);

            ActionListener removeButton = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ArrayList<String> arg = new ArrayList<>();
                    arg.add(id);
                    String command = "remove_by_id";
                    if (username.equals(login)) {
                        CommandForServer cfs = new CommandForServer(command, arg, username, password);
                        try {
                            getFastAnswer(cfs);
                        } catch (IOException | ClassNotFoundException ex) {
                            ex.printStackTrace();
                        }
                        shapes.remove(i);
                        repaint();
                        dispose();
                    } else {
                        infoL.setText("You don't have permission to modify this object.");
                        infoL.setForeground(Color.red);
                    }

                }

            };
            remove.addActionListener(removeButton);

            ActionListener updateL = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ArrayList<String> arg = new ArrayList<>();
                    arg.add(id);
                    String command = "update";
                    if (username.equals(login)) {
                        CommandForServer cfs = new CommandForServer(command, arg, username, password);
                        try {
                            WindowMain.WindowUpdate wa = new WindowMain.WindowUpdate(command);
                            wa.setVisible(true);
                            getFastAnswer(cfs);
                        } catch (IOException | ClassNotFoundException ex) {
                            ex.printStackTrace();
                        }
                        dispose();
                    } else {
                        infoL = new JLabel("You don't have permission to modify this object.");
                        infoL.setForeground(Color.red);
                    }

                }
            };
            update.addActionListener(updateL);
        }

    }

}

