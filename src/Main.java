import com.heatDifussion.myapp.Chart;
import com.heatDifussion.myapp.ComputationWorker;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CyclicBarrier;

public class Main {

    private static JComboBox<String> comboBox;
    private static JButton startBtn, stopBtn, resetBtn;
    private static double[][] temperature = new double[100][100];

    private static ComputationWorker computationThread;

    public static void main(String[] args) {


        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //frame.setPreferredSize(HeatSimConstants.DEFAULT_WINDOW_DIMENSION);
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


        EventHandler eventHandler = new EventHandler();


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




        //ComputationHandler computationHandler = new ComputationHandler(temperature);


        computationThread = new ComputationWorker(
                0,
                temperature.length,
                temperature[0].length, // assuming temperature[0].length is the correct width
                temperature[0].length, // assuming temperature[0].length is the correct height
                temperature
        );




        // thread responsible for rendering
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


    private static class EventHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(comboBox)) {
                System.out.println(comboBox.getSelectedItem());
            } else if (e.getSource().equals(startBtn)) {
                computationThread.start();
            } else if (e.getSource().equals(stopBtn)) {
                System.out.println("stop");
            } else if (e.getSource().equals(resetBtn)) {
                System.out.println("reset");
            }
        }
    }
}