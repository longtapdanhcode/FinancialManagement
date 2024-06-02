package DoAnCoSo1;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.axis.NumberAxis;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.TimingTargetAdapter;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class TransactionChart extends JPanel {

    private static final long serialVersionUID = 1L;

    private final DefaultCategoryDataset dataset;
    private final Animator animator;
    private float animate;
    private final Map<String, Double> originalValues = new HashMap<>();
    private double maxValue = 0;

    public TransactionChart() {
        dataset = createDataset();
        storeOriginalValues(dataset);
        JFreeChart chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        this.setLayout(new BorderLayout());
        this.add(chartPanel, BorderLayout.CENTER);

        TimingTarget target = new TimingTargetAdapter() {
            @Override
            public void timingEvent(float fraction) {
                animate = fraction;
                updateDataset();
            }
        };

        animator = new Animator(2000, target);
        animator.setResolution(0);
        animator.setAcceleration(0.5f);
        animator.setDeceleration(0.5f);
        animator.start();
    }

    private DefaultCategoryDataset createDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Double> categoryAmountMap = new HashMap<>();

        String query = "SELECT t.TransactionID, t.Amount, c.CategoryName, t.TransactionDate, t.Description " +
                       "FROM Transactions t " +
                       "JOIN Categories c ON t.CategoryID = c.CategoryID";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String category = resultSet.getString("CategoryName");
                double amount = resultSet.getDouble("Amount");
                categoryAmountMap.merge(category, amount, Double::sum);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Map.Entry<String, Double> entry : categoryAmountMap.entrySet()) {
            dataset.addValue(entry.getValue(), entry.getKey(), entry.getKey());
            if (entry.getValue() > maxValue) {
                maxValue = entry.getValue();
            }
        }

        return dataset;
    }

    private void storeOriginalValues(DefaultCategoryDataset dataset) {
        for (int row = 0; row < dataset.getRowCount(); row++) {
            for (int column = 0; column < dataset.getColumnCount(); column++) {
                Number value = dataset.getValue(row, column);
                if (value != null) {
                    originalValues.put(dataset.getRowKey(row).toString(), value.doubleValue());
                }
            }
        }
    }

    private void updateDataset() {
        for (Map.Entry<String, Double> entry : originalValues.entrySet()) {
            dataset.setValue(entry.getValue() * animate, entry.getKey(), entry.getKey());
        }
    }

    private JFreeChart createChart(DefaultCategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createBarChart(
                "Transaction Chart",   // Chart title
                "Category",            // Domain axis label
                "Amount",              // Range axis label
                dataset,               // Data
                PlotOrientation.VERTICAL,
                true, true, false);

        // Customizing the chart
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.decode("#edf1f6"));  // Background color of the plot area
        plot.setDomainGridlinePaint(Color.GRAY); // Gridline color
        plot.setRangeGridlinePaint(Color.GRAY); // Gridline color

        // Set background color of the chart using hex color code
        chart.setBackgroundPaint(Color.decode("#FFFFFF")); // Set to white background

        // Set fixed range on Y-axis based on the max value
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setRange(0, maxValue * 1.1); // Set the upper bound slightly higher than the max value for better visuals

        // Customizing the renderer for changing the bar colors
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, Color.decode("#f87171"));
        renderer.setSeriesPaint(1, Color.decode("#fb923c")); // Second bar color
        renderer.setSeriesPaint(2, Color.decode("#fbbf24")); // Third bar color
        renderer.setSeriesPaint(3, Color.decode("#a3e635"));
        renderer.setSeriesPaint(4, Color.decode("#34d399"));
        renderer.setSeriesPaint(5, Color.decode("#22d3ee"));
        // Add more colors if you have more categories

        // Customizing the chart title and axis labels
        chart.getTitle().setPaint(Color.decode("#000000")); // Title color
        plot.getDomainAxis().setLabelPaint(Color.decode("#000000")); // X-axis label color
        plot.getRangeAxis().setLabelPaint(Color.decode("#000000")); // Y-axis label color

        // Customizing the tick labels on axes
        plot.getDomainAxis().setTickLabelPaint(Color.decode("#000000")); // X-axis tick label color
        plot.getRangeAxis().setTickLabelPaint(Color.decode("#000000")); // Y-axis tick label color

        return chart;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Transaction Chart");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(new TransactionChart());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    public static class DatabaseConnection {
        private static final String URL = "jdbc:sqlserver://localhost;databaseName=Data";
        private static final String USER = "sa";
        private static final String PASSWORD = "123456";

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }
    }
}