package DoAnCoSo1;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import DoAnCoSo1.FiancialManagement.DatabaseConnection;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ViewBudgetsForm extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTable table;

    public ViewBudgetsForm() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        String[] columnNames = {"ID", "Số tiền", "Danh mục", "Ngày bắt đầu", "Ngày kết thúc"};
        Object[][] data = getBudgetData();

        table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);

        add(scrollPane, BorderLayout.CENTER);
     // Thêm bộ lắng nghe sự kiện để bắt sự kiện khi một hàng được chọn
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        updateFields(selectedRow);
                    }
                }
            }
        });
    }
    
    private void updateFields(int row) {
        Object budgetId = table.getValueAt(row, 0);
        Object amount = table.getValueAt(row, 1);
        Object category = table.getValueAt(row, 2);
        Object startDate = table.getValueAt(row, 3);
        Object endDate = table.getValueAt(row, 4);

        // Chuyển đổi java.sql.Date thành java.util.Date
        java.util.Date parsedStartDate = new java.util.Date(((java.sql.Date) startDate).getTime());
        java.util.Date parsedEndDate = new java.util.Date(((java.sql.Date) endDate).getTime());

        AddBudgetForm.updateFields(budgetId.toString(), amount.toString(), category.toString(), parsedStartDate, parsedEndDate);
    }

    private Object[][] getBudgetData() {
        List<Object[]> data = new ArrayList<>();

        String query = "SELECT B.BudgetID, B.Amount, C.CategoryName, B.StartDate, B.EndDate FROM Budgets B " +
                       "JOIN Categories C ON B.CategoryID = C.CategoryID";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int budgetId = resultSet.getInt("BudgetID");
                double amount = resultSet.getDouble("Amount");
                String categoryName = resultSet.getString("CategoryName");
                Date startDate = resultSet.getDate("StartDate");
                Date endDate = resultSet.getDate("EndDate");

                data.add(new Object[]{budgetId, amount, categoryName, startDate, endDate});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Thông báo lỗi tại đây hoặc xử lý lỗi theo cách khác
        }

        return data.toArray(new Object[0][]);
    }
}
