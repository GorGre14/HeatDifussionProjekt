import com.heatDifussion.myapp.Chart;
import com.heatDifussion.myapp.ComputationHandler;
import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {

    private JComboBox<String> comboBox;
    private JButton startBtn, stopBtn, resetBtn;
    private double[][] temperature = new double[100][100];

    private ComputationHandler computationHandler;
    private String selectedMode;

    public static void main(String[] args) {
        Main mainApp = new Main();
        mainApp.createAndShowGUI();
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);

        for (int i = 0; i < temperature.length; i++) {
            temperature[i][99] = 1;
        }

        Chart chart = new Chart(temperature);
        frame.add(chart, BorderLayout.CENTER);

        JPanel controls = new JPanel();
        GridLayout gridLayout = new GridLayout();
        gridLayout.setRows(10);
        gridLayout.setColumns(1);
        controls.setLayout(gridLayout);
        controls.setBackground(new Color(211, 211, 211));
        controls.setPreferredSize(new Dimension(200, 500));

        JLabel modeLabel = new JLabel("PROGRAM MODE");
        modeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        controls.add(modeLabel);

        EventHandler eventHandler = new EventHandler(this);

        class MyComboBoxRenderer extends BasicComboBoxRenderer {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setHorizontalAlignment(CENTER);
                return this;
            }
        }
        String[] programModes = {"sequential", "parallel", "distributed"};
        comboBox = new JComboBox<>(programModes);
        comboBox.setRenderer(new MyComboBoxRenderer());
        comboBox.addActionListener(eventHandler);
        controls.add(comboBox);

        startBtn = new JButton("Start");
        startBtn.addActionListener(eventHandler);
        controls.add(startBtn);

        stopBtn = new JButton("Stop");
        stopBtn.addActionListener(eventHandler);
        controls.add(stopBtn);

        resetBtn = new JButton("Reset");
        resetBtn.addActionListener(eventHandler);
        controls.add(resetBtn);

        frame.add(controls, BorderLayout.EAST);

        frame.pack();
        frame.setVisible(true);

        SwingWorker<Void, Void> chartWorker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                Timer timer = new Timer((int) 0, e -> chart.repaint());
                timer.start();
                return null;
            }
        };
        chartWorker.execute();
    }

    private class EventHandler implements ActionListener {
        private Main mainApp;

        public EventHandler(Main mainApp) {
            this.mainApp = mainApp;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(comboBox)) {
                mainApp.selectedMode = (String) comboBox.getSelectedItem();
                System.out.println(mainApp.selectedMode);
            } else if (e.getSource().equals(startBtn)) {
                if (mainApp.selectedMode != null) {
                    if (mainApp.selectedMode.equals("parallel")) {
                        mainApp.computationHandler = new ComputationHandler(mainApp.temperature, Runtime.getRuntime().availableProcessors());
                    } else if (mainApp.selectedMode.equals("sequential")) {
                        mainApp.computationHandler = new ComputationHandler(mainApp.temperature);
                    } else if (mainApp.selectedMode.equals("distributed")) {
                        // Add distributed logic if necessary
                    }
                } else {
                    System.out.println("Please select a mode before starting the computation.");
                }
            } else if (e.getSource().equals(stopBtn)) {
                if (mainApp.computationHandler != null) {
                    mainApp.computationHandler.shutdown();
                    System.out.println("Computation stopped.");
                } else {
                    System.out.println("ComputationHandler is not initialized.");
                }
            } else if (e.getSource().equals(resetBtn)) {
                if (mainApp.computationHandler != null) {
                    mainApp.computationHandler.shutdown();
                }
                mainApp.resetTemperature();
                System.out.println("Temperature grid reset.");
            }
        }
    }

    private void resetTemperature() {
        for (int i = 0; i < temperature.length; i++) {
            for (int j = 0; j < temperature[i].length; j++) {
                temperature[i][j] = 0;
            }
            temperature[i][99] = 1;
        }
    }
}
