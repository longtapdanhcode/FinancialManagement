package DoAnCoSo1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.TimingTargetAdapter;

public class BudgetChart extends JPanel {

    private static final long serialVersionUID = 1L;
    private final Animator animator;
    private float animate;
    private DefaultCategoryDataset dataset;
    private double[][] originalValues;
    private double maxValue;

    // Constructor to initialize the panel with the chart
    public BudgetChart() {
        TimingTarget target = new TimingTargetAdapter() {
            @Override
            public void timingEvent(float fraction) {
                animate = fraction;
                updateDataset();
                repaint();
            }
        };

        dataset = createDataset();
        storeOriginalValues(dataset); // Lưu trữ các giá trị gốc
        JFreeChart chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        this.setLayout(new BorderLayout());
        this.add(chartPanel, BorderLayout.CENTER);

        animator = new Animator(2000, target); // Tăng thời gian hoạt hình lên 2000ms để làm cho chuyển động chậm hơn
        animator.setResolution(0);
        animator.setAcceleration(0.5f);
        animator.setDeceleration(0.5f);

        startAnimation();
    }

    // Method to get data from the database and create a dataset for the chart
    private DefaultCategoryDataset createDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        String query = "SELECT B.BudgetID, B.Amount, C.CategoryName, B.StartDate, B.EndDate FROM Budgets B " +
                       "JOIN Categories C ON B.CategoryID = C.CategoryID";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String category = resultSet.getString("CategoryName");
                double amount = resultSet.getDouble("Amount");
                dataset.addValue(amount, category, category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dataset;
    }

    // Method to store the original values of the dataset and find the max value
    private void storeOriginalValues(DefaultCategoryDataset dataset) {
        int rowCount = dataset.getRowCount();
        int columnCount = dataset.getColumnCount();
        originalValues = new double[rowCount][columnCount];

        maxValue = 0; // Initialize max value

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                Number value = dataset.getValue(row, column);
                double val = (value != null) ? value.doubleValue() : 0.0;
                originalValues[row][column] = val;
                if (val > maxValue) {
                    maxValue = val;
                }
            }
        }
    }

    // Method to update the dataset during the animation
    private void updateDataset() {
        for (int row = 0; row < originalValues.length; row++) {
            for (int column = 0; column < originalValues[row].length; column++) {
                double value = originalValues[row][column] * animate;
                dataset.setValue(value, dataset.getRowKey(row), dataset.getColumnKey(column));
            }
        }
    }

    // Method to create a chart
    private JFreeChart createChart(DefaultCategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createBarChart(
                "Budget Chart",
                "Category",
                "Amount",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        // Customizing the chart
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.decode("#edf1f6")); // Background color of the plot area
        plot.setDomainGridlinePaint(Color.GRAY); // Gridline color
        plot.setRangeGridlinePaint(Color.GRAY); // Gridline color

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

        return chart;
    }

    // Method to start the animation
    private void startAnimation() {
        if (!animator.isRunning()) {
            animator.start();
        }
    }

    // Main method to run the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Budget Chart");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(new BudgetChart());
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
