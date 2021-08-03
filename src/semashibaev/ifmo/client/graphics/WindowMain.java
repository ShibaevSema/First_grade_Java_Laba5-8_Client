package semashibaev.ifmo.client.graphics;

import semashibaev.ifmo.cfs.CommandForServer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import static semashibaev.ifmo.client.ClientConnection.*;
import static semashibaev.ifmo.client.MovieReader.readMovie;
import static semashibaev.ifmo.client.Serialization.serialize;
import static semashibaev.ifmo.client.graphics.GraphicOfMovies.createAndShowGUI;
import static semashibaev.ifmo.client.graphics.WindowRegistration.wm;
// писалось как можно скорее - отсюда столько кода
public class WindowMain extends JFrame {
    private ArrayList<Locale> locale_list = new ArrayList<>();
    private ResourceBundle resourceBundle;
    JButton exit = new JButton("exit");
    JButton show = new JButton("show");
    JButton add = new JButton("add");
    JButton remove_lower = new JButton("remove_lower");
    JButton remove_greater = new JButton("remove_greater");
    JButton update = new JButton("update");
    JButton graphic = new JButton("Graphic");
    private String[] item = {
            "Canada",
            "Россия",
            "Danmark",
            "Ísland"
    };
    JComboBox languageComboBox = new JComboBox(item);
    JButton help = new JButton("help");//без аргументов - обычная форма
    JButton clear = new JButton("clear");//без аргументов - обычная форма
    JButton info = new JButton("info");//без аргументов - обычная форма
    JButton filter_starts_with_name = new JButton("filter_starts_with_name");
    JButton print_unique_mpaa_rating = new JButton("print_unique_mpaa_rating");//без аргументов - обычная форма
    JButton remove_all_by_golden_palm_count = new JButton("remove_all_by_golden_palm_count");
    JButton remove_first = new JButton("remove_first");//без аргументов - обычная форма
    JButton remove_by_id = new JButton("remove_by_id");
    JButton execute_script = new JButton("execute_script");

    JPanel mainPanel = new JPanel();
    JPanel difPanel = new JPanel();
    JLabel user = new JLabel("Your login is ");
    JLabel userS = new JLabel(":" + username);

    static volatile TableModel modelForGraphic;

    int columns = 11;
    private String Id;
    private String Name;
    private String Coordinates_x;
    private String Coordinates_y;
    private String Creation_date;
    private String Oscars_count;
    private String Gold_palm_count;
    private String Total_box_office;
    private String Mpaa_rating;
    private String Screenwriter;
    private String Login;
    String[] columnNames = {
            Id = "Id",
            Name = "Name",
            Coordinates_x = "Coordinates x",
            Coordinates_y = "Coordinates y",
            Creation_date = "Creation date",
            Oscars_count = "Oscars count",
            Gold_palm_count = "Golden palm count",
            Total_box_office = "Total box office",
            Mpaa_rating = "Mpaa rating",
            Screenwriter = "Screenwriter",
            Login = "Login"
    };
    String[][] data = new String[2][];
    JTable table = new JTable();

    static Container c;
    static Container c2;


    public String getMovie(int i, int j) {
        return (String) modelForGraphic.getValueAt(i, j);
    }

    public WindowMain() throws Exception {
        super("Collection of movies");
        locale_list.add(new Locale("en", "CA"));
        locale_list.add(new Locale("ru", "RU"));
        locale_list.add(new Locale("en", "DK"));
        locale_list.add(new Locale("is", "IS"));
        resourceBundle = ResourceBundle.getBundle("main", locale_list.get(0));
        System.out.println();
        exit.setText(resourceBundle.getString("exit"));
        add.setText(resourceBundle.getString("add"));
        remove_lower.setText(resourceBundle.getString("remove_lower"));
        remove_greater.setText(resourceBundle.getString("remove_greater"));
        update.setText(resourceBundle.getString("update"));
        update.setText(resourceBundle.getString("update"));
        graphic.setText(resourceBundle.getString("graphic"));
        help.setText(resourceBundle.getString("help"));
        clear.setText(resourceBundle.getString("clear"));
        info.setText(resourceBundle.getString("info"));
        filter_starts_with_name.setText(resourceBundle.getString("filter_starts_with_name"));
        print_unique_mpaa_rating.setText(resourceBundle.getString("print_unique_mpaa_rating"));
        remove_all_by_golden_palm_count.setText(resourceBundle.getString("remove_all_by_golden_palm_count"));
        remove_first.setText(resourceBundle.getString("remove_first"));
        remove_by_id.setText(resourceBundle.getString("remove_by_id"));
        execute_script.setText(resourceBundle.getString("execute_script"));
        show.setText(resourceBundle.getString("show"));

        updateTable();

        Thread autoUpdate = new Thread(()-> {
            while(true) {
                updateTable();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        autoUpdate.start();


        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 500);

        BorderLayout gbl = new BorderLayout();

        c = getContentPane();

        mainPanel.add(graphic);
        mainPanel.add(info);
        mainPanel.add(show);
        mainPanel.add(help);
        mainPanel.add(add);
        mainPanel.add(update);
        mainPanel.add(clear);
        exit.setBackground(Color.red);
        mainPanel.add(exit);
        mainPanel.add(user, 5);
        mainPanel.add(userS, 6);
        mainPanel.add(languageComboBox);

        difPanel.add(filter_starts_with_name);
        difPanel.add(print_unique_mpaa_rating);
        difPanel.add(remove_all_by_golden_palm_count);
        difPanel.add(remove_first);
        difPanel.add(remove_lower);
        difPanel.add(remove_greater);
        difPanel.add(remove_by_id);


        add(mainPanel, BorderLayout.NORTH);

        JScrollPane pane = new JScrollPane(table);
        add(pane, BorderLayout.CENTER);

        add(difPanel, BorderLayout.SOUTH);
        languageComboBox.addActionListener(this::languageChangePerformed);

        ActionListener grap = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createAndShowGUI();
            }
        };
        graphic.addActionListener(grap);
        // обработка простых команд без аргументов:
        ActionListener h = new CommandNoArg();
        help.addActionListener(h);

        ActionListener i = new CommandNoArg();
        info.addActionListener(i);

        ActionListener cl = new CommandNoArg();
        clear.addActionListener(cl);

        ActionListener pmpaa = new CommandNoArg();
        print_unique_mpaa_rating.addActionListener(pmpaa);

        ActionListener rfirst = new CommandNoArg();
        remove_first.addActionListener(rfirst);
        //обработка простых команд с аргументами :
        ActionListener withArg = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton btn = (JButton) e.getSource();
                String command = btn.getName();
                try {
                    WindowArg wa = new WindowArg(command, username, password);
                    wa.setVisible(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        filter_starts_with_name.addActionListener(withArg);
        remove_by_id.addActionListener(withArg);
        remove_all_by_golden_palm_count.addActionListener(withArg);
        //команды модификации:
        ActionListener ADD = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton btn = (JButton) e.getSource();
                String command = getButtonText(btn);
                WindowAdd wa = new WindowAdd(command);
                wa.setVisible(true);
            }
        };
        add.addActionListener(ADD);
        remove_greater.addActionListener(ADD);
        remove_lower.addActionListener(ADD);
        ActionListener UPD = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton btn = (JButton) e.getSource();
                String command = getButtonText(btn);
                WindowUpdate wu = new WindowUpdate(command);
                wu.setVisible((true));
            }
        };
        update.addActionListener(UPD);
    }

    private void languageChangePerformed(ActionEvent e) {
        resourceBundle = ResourceBundle.getBundle("main", locale_list.get(languageComboBox.getSelectedIndex()));
        exit.setText(resourceBundle.getString("exit"));
        add.setText(resourceBundle.getString("add"));
        remove_lower.setText(resourceBundle.getString("remove_lower"));
        remove_greater.setText(resourceBundle.getString("remove_greater"));
        update.setText(resourceBundle.getString("update"));
        update.setText(resourceBundle.getString("update"));
        graphic.setText(resourceBundle.getString("graphic"));
        help.setText(resourceBundle.getString("help"));
        clear.setText(resourceBundle.getString("clear"));
        info.setText(resourceBundle.getString("info"));
        filter_starts_with_name.setText(resourceBundle.getString("filter_starts_with_name"));
        print_unique_mpaa_rating.setText(resourceBundle.getString("print_unique_mpaa_rating"));
        remove_all_by_golden_palm_count.setText(resourceBundle.getString("remove_all_by_golden_palm_count"));
        remove_first.setText(resourceBundle.getString("remove_first"));
        remove_by_id.setText(resourceBundle.getString("remove_by_id"));
        execute_script.setText(resourceBundle.getString("execute_script"));
        show.setText(resourceBundle.getString("show"));

    }

    public void updateTable() {
        try {

            String command = "show";
            ArrayList<String> args = new ArrayList<>();
            CommandForServer cfs = new CommandForServer(command, args, username, password);
            String answer = getFastAnswer(cfs);
            System.out.println(answer);
            StringTokenizer tokenizer = new StringTokenizer(answer, "\n");
            ArrayList<String> collection = new ArrayList<>();
            while (tokenizer.hasMoreTokens()) {
                collection.add(tokenizer.nextToken());
            }
            int i;


            data = new String[collection.size()][11];


            for (i = 0; i < collection.size(); ) {
                StringTokenizer tokenizerMovie = new StringTokenizer(collection.get(i), ",");
                while (tokenizerMovie.hasMoreTokens()) {
                    data[i][0] = tokenizerMovie.nextToken();
                    data[i][1] = tokenizerMovie.nextToken();
                    data[i][2] = tokenizerMovie.nextToken();
                    data[i][3] = tokenizerMovie.nextToken();
                    data[i][4] = tokenizerMovie.nextToken();
                    data[i][5] = tokenizerMovie.nextToken();
                    data[i][6] = tokenizerMovie.nextToken();
                    data[i][7] = tokenizerMovie.nextToken();
                    data[i][8] = tokenizerMovie.nextToken();
                    data[i][9] = tokenizerMovie.nextToken();
                    data[i][10] = tokenizerMovie.nextToken();
                    i++;
                }
            }
            table.setModel(new DefaultTableModel(data, columnNames));
            modelForGraphic = table.getModel();
            RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(new DefaultTableModel(data, columnNames));
            table.setRowSorter(sorter);
            table.repaint();
            table.updateUI();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public static String getButtonText(JButton button) {
        return button.getText();
    }

    static class CommandNoArg implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                JButton btn = (JButton) e.getSource();
                String command = getButtonText(btn);
                ArrayList<String> args = new ArrayList<>();
                CommandForServer cfs = new CommandForServer(command, args, username, password);
                String answer = getFastAnswer(cfs);
                CommandForServer Autocfs = new CommandForServer("show", new ArrayList<>(), username, password);
                getFastAnswer(Autocfs);
                wm.updateTable();
                WindowAnswer wa = new WindowAnswer(answer);
                wa.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    class WindowArg extends JFrame {
        JButton send = new JButton("Send");
        Label argu = new Label("Arguments");
        public JTextField arguF = new JTextField(16);

        public WindowArg(String command, String username, String password) throws Exception {
            super("Arguments");
            this.setBounds(250, 200, 500, 250);
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

            argu.setFont(new Font("Arial", Font.PLAIN, 15));
            gbl.setConstraints(argu, c);
            add(argu);

            gbl.setConstraints(arguF, c2);
            add(arguF);

            gbl.setConstraints(send, c2);
            add(send);

            ActionListener sending = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JButton btn = (JButton) e.getSource();
                    String command = getButtonText(btn);
                    String arguments = arguF.getText();
                    StringTokenizer tokenizerArg = new StringTokenizer(arguments, " ");
                    ArrayList<String> args = new ArrayList<>();
                    while (tokenizerArg.hasMoreTokens())
                        args.add(tokenizerArg.nextToken());
                    CommandForServer cfs = new CommandForServer(command, args, username, password);
                    String answer = null;
                    try {
                        answer = getFastAnswer(cfs);
                        wm.updateTable();
                    } catch (IOException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                    WindowAnswer wa = null;
                    try {
                        wa = new WindowAnswer(answer);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    assert wa != null;
                    wa.setVisible(true);

                }
            };
            send.addActionListener(sending);
        }
    }

    static class WindowAdd extends JFrame {
        private String globalCommand;
        JButton send = new JButton("Send");
        Label name = new Label("Name");
        public JTextField nameF = new JTextField(16);
        Label oscar_count = new Label("Oscar count");
        public JTextField oscar_countF = new JTextField(16);
        Label golden_palm_count = new Label("Golden palm count");
        public JTextField golden_palm_countF = new JTextField(16);
        Label total_box_office = new Label("Total box office");
        public JTextField total_box_officeF = new JTextField(16);
        Label x = new Label("Coordinate x");
        public JTextField xF = new JTextField(16);
        Label y = new Label("Coordinate y");
        public JTextField yF = new JTextField(16);
        Label person = new Label("Screenwriter");
        public JTextField personF = new JTextField(16);
        Label mpaaRating = new Label("Screenwriter");
        public
        String[] items = {
                "R",
                "G",
                "PG",
                "NC_17"
        };
        JComboBox mpaaRatingBox = new JComboBox(items);
        JTextArea exception = new JTextArea("");


        public WindowAdd(String command) {
            super("Collection changes");
            this.globalCommand = command;
            this.setBounds(300, 600, 500, 400);
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

            name.setFont(new Font("Arial", Font.PLAIN, 15));
            gbl.setConstraints(name, c);
            add(name);

            gbl.setConstraints(nameF, c2);
            add(nameF);

            oscar_count.setFont(new Font("Arial", Font.PLAIN, 15));
            gbl.setConstraints(oscar_count, c);
            add(oscar_count);

            gbl.setConstraints(oscar_countF, c2);
            add(oscar_countF);

            golden_palm_count.setFont(new Font("Arial", Font.PLAIN, 15));
            gbl.setConstraints(golden_palm_count, c);
            add(golden_palm_count);

            gbl.setConstraints(golden_palm_countF, c2);
            add(golden_palm_countF);

            total_box_office.setFont(new Font("Arial", Font.PLAIN, 15));
            gbl.setConstraints(total_box_office, c);
            add(total_box_office);

            gbl.setConstraints(total_box_officeF, c2);
            add(total_box_officeF);

            x.setFont(new Font("Arial", Font.PLAIN, 15));
            gbl.setConstraints(x, c);
            add(x);

            gbl.setConstraints(xF, c2);
            add(xF);

            y.setFont(new Font("Arial", Font.PLAIN, 15));
            gbl.setConstraints(y, c);
            add(y);

            gbl.setConstraints(yF, c2);
            add(yF);

            person.setFont(new Font("Arial", Font.PLAIN, 15));
            gbl.setConstraints(person, c);
            add(person);

            gbl.setConstraints(personF, c2);
            add(personF);

            mpaaRating.setFont(new Font("Arial", Font.PLAIN, 15));
            gbl.setConstraints(mpaaRating, c);
            add(mpaaRating);

            gbl.setConstraints(mpaaRatingBox, c2);
            add(mpaaRatingBox);

            gbl.setConstraints(send, c2);
            add(send);

            exception.setFont(new Font("Arial", Font.PLAIN, 15));
            exception.setForeground(Color.red);
            gbl.setConstraints(exception, c2);
            add(exception);

            ActionListener sending = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ArrayList<String> form = new ArrayList<>();
                    String name = nameF.getText();
                    form.add(name);
                    String oc = oscar_countF.getText();
                    form.add(oc);
                    String gpc = golden_palm_countF.getText();
                    form.add(gpc);
                    String tbo = total_box_officeF.getText();
                    form.add(tbo);
                    String cx = xF.getText();
                    form.add(cx);
                    String cy = yF.getText();
                    form.add(cy);
                    String person = personF.getText();
                    form.add(person);
                    String mr = (String) mpaaRatingBox.getSelectedItem();
                    form.add(mr);

                    try {
                        form = readMovie(form);
                    } catch (Exception ex) {
                        exception.setText(String.valueOf(ex));
                        return;
                    }
                    CommandForServer cfs = new CommandForServer(command, form, username, password);

                    String answer = null;
                    try {
                        answer = getFastAnswer(cfs);
                        wm.updateTable();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                    WindowAnswer wa = null;
                    try {
                        wa = new WindowAnswer(answer);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    wa.setVisible(true);
                    dispose();
                }
            };
            send.addActionListener(sending);
        }
    }

    static class WindowUpdate extends JFrame {
        private ArrayList<String> arg;
        private String globalCommand;
        Label id = new Label("Id");
        public JTextField idF = new JTextField(16);
        JButton send = new JButton("Send");
        Label name = new Label("Name");
        public JTextField nameF = new JTextField(16);
        Label oscar_count = new Label("Oscar count");
        public JTextField oscar_countF = new JTextField(16);
        Label golden_palm_count = new Label("Golden palm count");
        public JTextField golden_palm_countF = new JTextField(16);
        Label total_box_office = new Label("Total box office");
        public JTextField total_box_officeF = new JTextField(16);
        Label x = new Label("Coordinate x");
        public JTextField xF = new JTextField(16);
        Label y = new Label("Coordinate y");
        public JTextField yF = new JTextField(16);
        Label person = new Label("Screenwriter");
        public JTextField personF = new JTextField(16);
        Label mpaaRating = new Label("Screenwriter");
        public
        String[] items = {
                "R",
                "G",
                "PG",
                "NC_17"
        };
        JComboBox mpaaRatingBox = new JComboBox(items);
        Label exception = new Label("");

        public WindowUpdate(String command) {
            super("Collection changes");
            this.globalCommand = command;
            this.setBounds(300, 600, 500, 400);
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

            id.setFont(new Font("Arial", Font.PLAIN, 15));
            gbl.setConstraints(id, c);
            add(id);

            gbl.setConstraints(idF, c2);
            add(idF);

            name.setFont(new Font("Arial", Font.PLAIN, 15));
            gbl.setConstraints(name, c);
            add(name);

            gbl.setConstraints(nameF, c2);
            add(nameF);

            oscar_count.setFont(new Font("Arial", Font.PLAIN, 15));
            gbl.setConstraints(oscar_count, c);
            add(oscar_count);

            gbl.setConstraints(oscar_countF, c2);
            add(oscar_countF);

            golden_palm_count.setFont(new Font("Arial", Font.PLAIN, 15));
            gbl.setConstraints(golden_palm_count, c);
            add(golden_palm_count);

            gbl.setConstraints(golden_palm_countF, c2);
            add(golden_palm_countF);

            total_box_office.setFont(new Font("Arial", Font.PLAIN, 15));
            gbl.setConstraints(total_box_office, c);
            add(total_box_office);

            gbl.setConstraints(total_box_officeF, c2);
            add(total_box_officeF);

            x.setFont(new Font("Arial", Font.PLAIN, 15));
            gbl.setConstraints(x, c);
            add(x);

            gbl.setConstraints(xF, c2);
            add(xF);

            y.setFont(new Font("Arial", Font.PLAIN, 15));
            gbl.setConstraints(y, c);
            add(y);

            gbl.setConstraints(yF, c2);
            add(yF);

            person.setFont(new Font("Arial", Font.PLAIN, 15));
            gbl.setConstraints(person, c);
            add(person);

            gbl.setConstraints(personF, c2);
            add(personF);

            mpaaRating.setFont(new Font("Arial", Font.PLAIN, 15));
            gbl.setConstraints(mpaaRating, c);
            add(mpaaRating);

            gbl.setConstraints(mpaaRatingBox, c2);
            add(mpaaRatingBox);

            gbl.setConstraints(send, c2);
            add(send);

            exception.setFont(new Font("Arial", Font.PLAIN, 15));
            exception.setForeground(Color.red);
            gbl.setConstraints(exception, c2);
            add(exception);

            ActionListener sending = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ArrayList<String> form = new ArrayList<>();
                    String name = nameF.getText();
                    form.add(name);
                    String oc = oscar_countF.getText();
                    form.add(oc);
                    String gpc = golden_palm_countF.getText();
                    form.add(gpc);
                    String tbo = total_box_officeF.getText();
                    form.add(tbo);
                    String cx = xF.getText();
                    form.add(cx);
                    String cy = yF.getText();
                    form.add(cy);
                    String person = personF.getText();
                    form.add(person);
                    String mr = (String) mpaaRatingBox.getSelectedItem();
                    form.add(mr);
                    ArrayList<String> argId = new ArrayList<>();
                    String id = (String) idF.getText();
                    argId.add(id);
                    try {
                        form = readMovie(form);
                    } catch (Exception ex) {
                        exception.setText(String.valueOf(ex));
                    }
                    argId.addAll(form);
                    CommandForServer cfs = new CommandForServer(command, argId, username, password);

                    String answer = null;
                    try {
                        answer = getFastAnswer(cfs);
                        wm.updateTable();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                    WindowAnswer wa = null;
                    try {
                        wa = new WindowAnswer(answer);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    wa.setVisible(true);
                    dispose();
                }
            };
            send.addActionListener(sending);
        }
    }

    static String getFastAnswer(CommandForServer commandForServer) throws IOException, ClassNotFoundException {
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
}

