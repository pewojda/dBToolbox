import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.Random;

public class Main {

    private double currentLinear = randomLinear();
    private double currentLinear1 = randomLinear();

    private double currentDecibel = randomDecibel();
    private double currentDecibel1 = randomDecibel();

    private DecimalFormat df = new DecimalFormat("#.###");
    private DecimalFormat df1 = new DecimalFormat("#.##################");

    private String siPrint(double val) {
        int[] prefix = {-18, -15, -12, -9, -6, -3, 0, 3, 6, 9, 12, 15, 18};
        String[] prefixLetter = {"a", "f", "p", "n", "u", "m", " ", "k", "M", "G", "T", "P", "E"};

        int step = (int) Math.floor(Math.log10(val) / 3 + 6);

        if (step > 9) step = 9;
        if (step < 0) step = 0;

        double valNew = val / Math.pow(10, prefix[step]);
        //return String.format("%.3f %s", valNew, prefixLetter[step]);
        return String.format("%s %s", df.format(valNew), prefixLetter[step]);
    }

    private double randomDecibel() {
        Random rand = new Random();
        double temp;

        int[] div = {1, 3, 6, 9, 10};
        temp = (float)div[rand.nextInt(5)]*div[rand.nextInt(5)];

        if (rand.nextInt(2) == 1) temp+=60;

        //do {
        //    temp = rand.nextInt(150);
        //} while (temp % 3 != 0 | temp == 0);

        if (rand.nextInt(2) == 1) return temp;
        else return -temp;
    }

    private double randomLinear() {
        int[] prefix = {-15, -12, -9, -6, -3, 0, 3, 6};
        Random rand = new Random();
        double temp;

        int[] div = {1, 2, 4, 8, 16, 32};
        int[] div1 = {1, 10, 100, 1000};
        temp = (float) div1[rand.nextInt(3)] / div[rand.nextInt(5)];

        //do {
        //    temp = rand.nextInt(200);
        //} while (temp % 2 != 0 | temp == 0);

        return temp * Math.pow(10, prefix[rand.nextInt(8)]);
    }

    private double voltToDecibel(double val) {
        return 20 * Math.log10(val / 1e-6);
    }

    private double powerToDecibel(double val) {
        return 10 * Math.log10(val / 1e-3);
    }

    private double decibelToVolt(double val) {
        return Math.pow(10, val / 20) * 1e-6;
    }

    private double decibelToPower(double val) {
        return Math.pow(10, val / 10) * 1e-3;
    }

    private JPanel convTab(int type) {
        //0: V->dBuV
        //1: dBuV->V
        //2: W->dBm
        //3: dBm->W

        JPanel tempPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        JLabel l = new JLabel();
        JLabel l1 = new JLabel("???");
        JButton b = new JButton("Sprawdź");

        switch (type) {
            case 0:
                l.setText(siPrint(currentLinear) + "V");
                break;
            case 1:
                l.setText(String.format("%.0f dBuV", currentDecibel));
                break;
            case 2:
                l.setText(siPrint(currentLinear1) + "W");
                break;
            case 3:
                l.setText(String.format("%.0f dBm", currentDecibel1));
                break;
        }

        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (b.getText().equals("Sprawdź")) {
                    switch (type) {
                        case 0:
                            l1.setText(String.format("%.0f dBuV", voltToDecibel(currentLinear)));
                            break;
                        case 1:
                            l1.setText(siPrint(decibelToVolt(currentDecibel)) + "V");
                            break;
                        case 2:
                            l1.setText(String.format("%.0f dBm", powerToDecibel(currentLinear1)));
                            break;
                        case 3:
                            l1.setText(siPrint(decibelToPower(currentDecibel1)) + "W");
                            break;
                    }
                    b.setText("Losuj");
                } else {
                    switch (type) {
                        case 0:
                            currentLinear = randomLinear();
                            l.setText(siPrint(currentLinear) + "V");
                            break;
                        case 1:
                            currentDecibel = randomDecibel();
                            l.setText(String.format("%.0f dBuV", currentDecibel));
                            break;
                        case 2:
                            currentLinear1 = randomLinear();
                            l.setText(siPrint(currentLinear1) + "W");
                            break;
                        case 3:
                            currentDecibel1 = randomDecibel();
                            l.setText(String.format("%.0f dBm", currentDecibel1));
                            break;
                    }
                    l1.setText("???");
                    b.setText("Sprawdź");
                }
            }
        });

        l.setHorizontalAlignment(SwingConstants.CENTER);
        l1.setHorizontalAlignment(SwingConstants.CENTER);

        l.setFont(l.getFont().deriveFont(24.0f));
        l1.setFont(l1.getFont().deriveFont(24.0f));

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        tempPanel.add(l, c);

        c.gridx = 0;
        c.gridy = 1;
        tempPanel.add(l1, c);

        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.PAGE_END;
        tempPanel.add(b, c);

        return tempPanel;
    }

    private void calcdB(JComboBox<String> comb, JTextField t, JTextField t1, int type, int direction) {
        //Type
        //0: W
        //1: V

        //Direction
        //0: ->dB
        //1: dB<-

        int[] prefix = {-18, -15, -12, -9, -6, -3, 0, 3, 6, 9, 12, 15, 18};

        double val = Double.parseDouble(t.getText()) * Math.pow(10, prefix[comb.getSelectedIndex()]);
        double db = Double.parseDouble(t1.getText());

        int step;
        double valNew;

        switch (direction) {
            case 0:
                switch (type) {
                    case 0:
                        t1.setText(df.format(powerToDecibel(val)));
                        break;
                    case 1:
                        t1.setText(df.format(voltToDecibel(val)));
                        break;
                }
                break;
            case 1:
                switch (type) {
                    case 0:
                        val = decibelToPower(db);

                        step = (int) Math.floor(Math.log10(val) / 3 + 6);

                        if (step > 9) step = 9;
                        if (step < 0) step = 0;

                        valNew = val / Math.pow(10, prefix[step]);

                        comb.setSelectedIndex(step);
                        t.setText(df.format(valNew));
                        break;
                    case 1:
                        val = decibelToVolt(db);

                        step = (int) Math.floor(Math.log10(val) / 3 + 6);

                        if (step > 9) step = 9;
                        if (step < 0) step = 0;

                        valNew = val / Math.pow(10, prefix[step]);

                        comb.setSelectedIndex(step);
                        t.setText(df.format(valNew));
                        break;
                }
                break;
        }
    }

    private JPanel calcPanel(int type) {
        //0: W
        //1: V

        JPanel temp = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        JTextField t = new JTextField("1", 10);
        JComboBox<String> comb;

        JTextField t1 = new JTextField("0", 10);
        JComboBox<String> comb1;

        switch (type) {
            case 0:
                comb = new JComboBox<>(new String[]{"aW", "fW", "pW", "nW", "uW", "mW", "W", "kW", "MW", "GW", "TW", "PW", "EW"});
                comb.setSelectedIndex(6);
                comb1 = new JComboBox<>(new String[]{"dBm"});
                break;
            case 1:
                comb = new JComboBox<>(new String[]{"aV", "fV", "pV", "nV", "uV", "mV", "V", "kV", "MV", "GV", "TV", "PV", "EV"});
                comb.setSelectedIndex(6);
                comb1 = new JComboBox<>(new String[]{"dBuV"});
                break;
            default:
                comb = new JComboBox<>();
                comb1 = new JComboBox<>();
                break;
        }

        calcdB(comb, t, t1, type, 0);

        comb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (actionEvent.getModifiers() != 0) calcdB(comb, t, t1, type, 0);
            }
        });

        t.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                calcdB(comb, t, t1, type, 0);
            }
        });

        t1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                calcdB(comb, t, t1, type, 1);
            }
        });

        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        c.ipady = 5;
        c.insets = new Insets(5, 5, 5, 5);
        temp.add(t, c);

        c.gridx = 1;
        c.gridy = 0;
        temp.add(comb, c);

        c.gridx = 0;
        c.gridy = 1;
        temp.add(t1, c);

        c.gridx = 1;
        c.gridy = 1;
        temp.add(comb1, c);

        return temp;
    }

    private void calcPrefix(JTextField t, JLabel l, JComboBox<String> comb, JComboBox<String> comb1, JCheckBox autoPrefix) {
        int[] prefix = {-18, -15, -12, -9, -6, -3, 0, 3, 6, 9, 12, 15, 18};
        String[] prefixLetter = {"a", "f", "p", "n", "u", "m", " ", "k", "M", "G", "T", "P", "E"};

        double field = Double.parseDouble(t.getText()) * Math.pow(10, prefix[comb.getSelectedIndex()]);
        boolean auto = autoPrefix.isSelected();

        if (auto) {
            int step = (int) Math.floor(Math.log10(field) / 3 + 6);

            if (step > 9) step = 9;
            if (step < 0) step = 0;

            double fieldNew = field / Math.pow(10, prefix[step]);

            l.setText(df1.format(fieldNew) + " " + prefixLetter[step]);
        } else {
            double fieldNew = field / Math.pow(10, prefix[comb1.getSelectedIndex()]);
            l.setText(df1.format(fieldNew) + " " + prefixLetter[comb1.getSelectedIndex()]);
        }
    }

    private JPanel prefixTab() {
        JPanel temp = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        String[] prefixLetter = {"a", "f", "p", "n", "u", "m", "", "k", "M", "G", "T", "P", "E"};

        JTextField t = new JTextField("1", 10);
        JComboBox<String> comb = new JComboBox<String>(prefixLetter);
        JComboBox<String> comb1 = new JComboBox<String>(prefixLetter);
        JCheckBox autoPrefix = new JCheckBox("Auto");


        JLabel l = new JLabel("");

        l.setHorizontalAlignment(SwingConstants.CENTER);
        l.setFont(l.getFont().deriveFont(24.0f));

        comb.setSelectedIndex(6);
        comb1.setSelectedIndex(6);

        autoPrefix.setSelected(true);

        calcPrefix(t, l, comb, comb1, autoPrefix);

        comb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (actionEvent.getModifiers() != 0) calcPrefix(t, l, comb, comb1, autoPrefix);
            }
        });

        comb1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (actionEvent.getModifiers() != 0) calcPrefix(t, l, comb, comb1, autoPrefix);
            }
        });

        t.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                calcPrefix(t, l, comb, comb1, autoPrefix);
            }
        });

        autoPrefix.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                calcPrefix(t, l, comb, comb1, autoPrefix);
            }
        });

        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        c.ipady = 5;
        c.insets = new Insets(5, 5, 5, 5);
        temp.add(t, c);

        c.weightx = 0;
        c.gridx = 2;
        c.gridy = 0;
        temp.add(comb, c);

        c.gridx = 0;
        c.gridy = 1;
        temp.add(autoPrefix, c);

        c.gridx = 2;
        c.gridy = 1;
        temp.add(comb1, c);

        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 3;
        c.anchor = GridBagConstraints.PAGE_END;
        temp.add(l, c);

        return temp;
    }

    private JTabbedPane calcTab() {
        JTabbedPane temp = new JTabbedPane();

        temp.addTab("Moc", calcPanel(0));
        temp.addTab("Napięcie", calcPanel(1));
        temp.addTab("Prefiks", prefixTab());

        return temp;
    }

    private void run() {
        JFrame f = new JFrame("Decybele");
        f.setSize(600, 400);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabPanel = new JTabbedPane();

        tabPanel.addTab("Kalkulator", calcTab());
        tabPanel.addTab("V -> dBuV", convTab(0));
        tabPanel.addTab("dBuV -> V", convTab(1));
        tabPanel.addTab("W -> dBm", convTab(2));
        tabPanel.addTab("dBm -> W", convTab(3));
        f.add(tabPanel);

        f.setLocationRelativeTo(null);
        f.setResizable(false);
        f.setVisible(true);
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.run();
    }
}
