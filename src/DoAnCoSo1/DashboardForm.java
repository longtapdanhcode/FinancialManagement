package DoAnCoSo1;

import com.formdev.flatlaf.FlatClientProperties;

import DoAnCoSo1.FiancialManagement.DatabaseConnection;

import java.awt.Color;
import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import raven.chart.ChartLegendRenderer;
import raven.chart.data.category.DefaultCategoryDataset;
import raven.chart.data.pie.DefaultPieDataset;
import raven.chart.line.LineChart;
import raven.chart.pie.PieChart;

public class DashboardForm extends JPanel {
    private static final long serialVersionUID = 1L;
    private LineChart lineChart;
    private PieChart pieChart1;
    private PieChart pieChart2;

   
    public DashboardForm() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("wrap,fill,gap 10", "fill"));
        createPieChart();
        createLineChart();
    }

    private void createPieChart() {
        // Tạo các đồ thị Pie Chart và thêm vào Panel
        pieChart1 = new PieChart();
        double totalIncome = FinancialUtils.calculateTotalIncome();
        String formattedTotalIncome = formatCurrency(totalIncome);
        JLabel header1 = new JLabel("Thu Nhập: "+formattedTotalIncome+"VND");
        header1.putClientProperty(FlatClientProperties.STYLE, "font:+1");
        pieChart1.setHeader(header1);
        pieChart1.getChartColor().addColor(
            Color.decode("#f87171"), Color.decode("#fb923c"), Color.decode("#fbbf24"), 
            Color.decode("#a3e635"), Color.decode("#34d399"), Color.decode("#22d3ee"), 
            Color.decode("#818cf8"), Color.decode("#c084fc")
        );
        pieChart1.putClientProperty(FlatClientProperties.STYLE, "border:5,5,5,5,$Component.borderColor,,20");
        pieChart1.setDataset(createIncomeDataset()); // Lấy dữ liệu từ cơ sở dữ liệu
        add(pieChart1, "split 2,height 350");

        pieChart2 = new PieChart();
        double totalIncome1 = FinancialUtils.calculateTotalExpense();
        String formattedTotalIncome1 = formatCurrency(totalIncome1);
        JLabel header2 = new JLabel("Chi Phí Sản Phẩm: "+formattedTotalIncome1+"VND");
        header2.putClientProperty(FlatClientProperties.STYLE, "font:+1");
        pieChart2.setHeader(header2);
        pieChart2.getChartColor().addColor(
            Color.decode("#f87171"), Color.decode("#fb923c"), Color.decode("#fbbf24"), 
            Color.decode("#a3e635"), Color.decode("#34d399"), Color.decode("#22d3ee"), 
            Color.decode("#818cf8"), Color.decode("#c084fc")
        );
        pieChart2.putClientProperty(FlatClientProperties.STYLE, "border:5,5,5,5,$Component.borderColor,,20");
        pieChart2.setDataset(createDataset("expense")); // Lấy dữ liệu từ cơ sở dữ liệu
        add(pieChart2, "height 350");

    }
    private void createLineChart() {
        // Tạo đồ thị Line Chart và thêm vào Panel
        lineChart = new LineChart();
        lineChart.setChartType(LineChart.ChartType.CURVE);
        lineChart.putClientProperty(FlatClientProperties.STYLE, "border:5,5,5,5,$Component.borderColor,,20");
        add(lineChart);
        createLineChartData();
    }
    
    public static String formatCurrency(double amount) {
        DecimalFormat formatter = new DecimalFormat("###,###.##");
        return formatter.format(amount);
    }
    public class FinancialUtils {

        public static double calculateTotalIncome() {
            double totalIncome = 0;
            String query = "SELECT SUM(b.Amount) AS TotalAmount " +
                           "FROM Budgets b " +
                           "JOIN Categories c ON b.CategoryID = c.CategoryID " +
                           "WHERE c.CategoryName IN ('Lương', 'Thuong')";

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    totalIncome = resultSet.getDouble("TotalAmount");
                    System.out.println(totalIncome);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return totalIncome;
        }

        public static double calculateTotalExpense() {
            double totalExpense = 0;
            String query = "SELECT SUM(Amount) AS TotalExpense FROM Transactions WHERE Type = 'expense'";
            
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    totalExpense = resultSet.getDouble("TotalExpense");
                    System.out.println(totalExpense);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return totalExpense;
        }

        public static double calculateTotalSavings() {
            double totalIncome = calculateTotalIncome();
            double totalExpense = calculateTotalExpense();
            
            return totalIncome - totalExpense;
        }
        public static String calculateTotalSavingsAsString() {
            double totalSavings = calculateTotalSavings();
            return formatCurrency(totalSavings);
        }

    }
    private DefaultPieDataset createIncomeDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        String query = "SELECT c.CategoryName, SUM(b.Amount) as TotalAmount " +
                       "FROM Budgets b " +
                       "JOIN Categories c ON b.CategoryID = c.CategoryID " +
                       "WHERE c.CategoryName IN ('Lương', 'Thuong') " +
                       "GROUP BY c.CategoryName";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String category = resultSet.getString("CategoryName");
                double amount = resultSet.getDouble("TotalAmount");
                dataset.setValue(category, amount);
            }

            // In ra số lượng mục trong dataset để kiểm tra
            System.out.println("Dataset for Income has " + dataset.getItemCount() + " items.");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi truy vấn cơ sở dữ liệu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        return dataset;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    
	private DefaultPieDataset createDataset(String type) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        String query = "SELECT c.CategoryName, SUM(t.Amount) as TotalAmount " +
                       "FROM Transactions t " +
                       "JOIN Categories c ON t.CategoryID = c.CategoryID " +
                       "WHERE t.Type = ? " +
                       "GROUP BY c.CategoryName";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, type);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String category = resultSet.getString("CategoryName");
                double amount = resultSet.getDouble("TotalAmount");
                dataset.setValue(category, amount);
            }

            // In ra số lượng mục trong dataset để kiểm tra
            System.out.println("Dataset for " + type + " has " + dataset.getItemCount() + " items.");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi truy vấn cơ sở dữ liệu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        return dataset;
    }



    private void createLineChartData() {
        DefaultCategoryDataset<String, String> categoryDataset = new DefaultCategoryDataset<>();
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");

        String expenseQuery = "SELECT c.CategoryName, t.TransactionDate, SUM(t.Amount) as TotalAmount " +
                              "FROM Transactions t " +
                              "JOIN Categories c ON t.CategoryID = c.CategoryID " +
                              "WHERE t.Type = 'Expense' AND c.CategoryName IN ('an uong', 'mua sam', 'giai tri', 'van chuyen') " +
                              "GROUP BY c.CategoryName, t.TransactionDate";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement expenseStatement = connection.prepareStatement(expenseQuery)) {

            ResultSet expenseResultSet = expenseStatement.executeQuery();

            // Add expense data to the dataset
            while (expenseResultSet.next()) {
                String categoryName = expenseResultSet.getString("CategoryName");
                Date transactionDate = expenseResultSet.getDate("TransactionDate");
                double amount = expenseResultSet.getDouble("TotalAmount");
                String formattedDate = df.format(transactionDate);
                categoryDataset.addValue(amount, categoryName, formattedDate);
            }

            // Ensure that the dataset is not empty before proceeding
            if (!categoryDataset.getColumnKeys().isEmpty()) {
                Date date = df.parse(categoryDataset.getColumnKey(0));
                Date dateEnd = df.parse(categoryDataset.getColumnKey(categoryDataset.getColumnCount() - 1));
                DateCalculator dcal = new DateCalculator(date, dateEnd);
                long diff = dcal.getDifferenceDays();
                double d = Math.ceil((diff / 10f));
                lineChart.setLegendRenderer(new ChartLegendRenderer() {
                    @Override
                    public Component getLegendComponent(Object legend, int index) {
                        if (index % d == 0) {
                            return super.getLegendComponent(legend, index);
                        } else {
                            return null;
                        }
                    }
                });
            } else {
                // Handle the case where categoryDataset is empty
                JOptionPane.showMessageDialog(this, "No data available to display on the chart.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while querying the database.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ParseException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while parsing dates.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Set the dataset to the chart
        lineChart.setCategoryDataset(categoryDataset);
        lineChart.getChartColor().addColor(
            Color.decode("#38bdf8"), // Color for "an uong"
            Color.decode("#fb7185"), // Color for "mua sam"
            Color.decode("#34d399"), // Color for "giai tri"
            Color.decode("#a3e635")  // Color for "van chuyen"
        );
        double total = FinancialUtils.calculateTotalSavings();
        String formattedTotal= formatCurrency(total);
        JLabel header = new JLabel("Theo Dõi Chi Tiêu: "+formattedTotal+"VND");
        header.putClientProperty(FlatClientProperties.STYLE, "font:+1; border:0,0,5,0");
        lineChart.setHeader(header);
    }

}
