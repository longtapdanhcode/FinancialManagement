package DoAnCoSo1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;

import DoAnCoSo1.FiancialManagement.DatabaseConnection;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddTransactionForm extends JPanel {
    private static final long serialVersionUID = 1L;

    private JTextField amountField;
    private JComboBox<String> categoryComboBox;
    private JDateChooser dateChooser;
    private JTextArea descriptionArea;
    private JTextField transactionIdField; // Field to store Transaction ID for update/delete
    private DefaultTableModel tableModel;
    private JComboBox<String> typeComboBox;
    public AddTransactionForm() {
        setLayout(new BorderLayout());
        setSize(1300, 600);

        JLabel amountLabel = new JLabel("Số tiền:");
        amountLabel.setBounds(10, 0, 195, 52);
        amountField = new JTextField();
        amountField.setBounds(205, 0, 195, 52);
        
        JLabel categoryLabel = new JLabel("Danh mục:");
        categoryLabel.setBounds(10, 62, 195, 52);
        categoryComboBox = new JComboBox<>(getCategories());
        categoryComboBox.setBounds(205, 62, 195, 52);
        
        JLabel dateLabel = new JLabel("Ngày:");
        dateLabel.setBounds(10, 124, 195, 52);	
        dateChooser = new JDateChooser();
        dateChooser.setBounds(205, 124, 195, 52);
        
        JLabel descriptionLabel = new JLabel("Mô tả:");
        descriptionLabel.setBounds(10, 186, 195, 52);
        descriptionArea = new JTextArea();
        
        JLabel transactionIdLabel = new JLabel("Transaction ID:");
        transactionIdLabel.setBounds(10, 248, 195, 52);
        transactionIdField = new JTextField();
        transactionIdField.setBounds(205, 248, 195, 52);

        JButton saveButton = new JButton("Lưu");
        saveButton.setBounds(0, 352, 195, 52);
        saveButton.addActionListener(e -> saveTransaction());

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.add(amountLabel);
        panel.add(amountField);
        panel.add(categoryLabel);
        panel.add(categoryComboBox);
        panel.add(dateLabel);
        panel.add(dateChooser);
        panel.add(descriptionLabel);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setBounds(205, 186, 195, 52);
        panel.add(scrollPane);
        panel.add(saveButton);
        panel.add(transactionIdLabel);
        panel.add(transactionIdField);
        
        JButton btnUpdate = new JButton("Sửa");
        btnUpdate.addActionListener(e -> updateTransaction());
        btnUpdate.setBounds(205, 352, 195, 52);
        panel.add(btnUpdate);
        
        JButton btnDelete = new JButton("Xóa");
        btnDelete.addActionListener(e -> deleteTransaction());
        btnDelete.setBounds(98, 428, 195, 52);
        panel.add(btnDelete);
        
        
        // Trong constructor của AddTransactionForm:
        JLabel typeLabel = new JLabel("Loại:");
        typeLabel.setBounds(10, 301, 195, 52);
        typeComboBox = new JComboBox<>(new String[]{"Income", "Expense"});
        typeComboBox.setBounds(205, 301, 195, 52);
        panel.add(typeLabel);
        panel.add(typeComboBox);

        add(panel, BorderLayout.CENTER);
        
        JPanel list = new JPanel();
        list.setBackground(Color.LIGHT_GRAY);
        list.setBounds(400, -10, 856, 600);
        panel.add(list);
//        ==============================================================================================================
        String[] columnNames = {"ID", "Số tiền", "Danh mục", "Ngày", "Mô tả", "Loại"};
        this.tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);

        // Kết nối đến cơ sở dữ liệu và lấy dữ liệu giao dịch
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT t.TransactionID, t.Amount, c.CategoryName, t.TransactionDate, t.Description, t.Type " +
                     "FROM Transactions t " +
                     "JOIN Categories c ON t.CategoryID = c.CategoryID")) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("TransactionID");
                double amount = resultSet.getDouble("Amount");
                String category = resultSet.getString("CategoryName");
                String date = resultSet.getString("TransactionDate");
                String description = resultSet.getString("Description");
                String type = resultSet.getString("Type"); // Lấy dữ liệu từ cột "Type"
                tableModel.addRow(new Object[]{id, amount, category, date, description, type});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi truy vấn cơ sở dữ liệu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }


        JScrollPane scrollPane1 = new JScrollPane(table);
        scrollPane1.setPreferredSize(new Dimension(856, 600));
        list.add(scrollPane1, BorderLayout.CENTER);
        
    }
    private String[] getCategories() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT CategoryName FROM Categories")) {

            List<String> categories = new ArrayList<>();
            while (resultSet.next()) {
                categories.add(resultSet.getString("CategoryName"));
            }
            return categories.toArray(new String[0]);
        } catch (SQLException e) {
            e.printStackTrace();
            return new String[]{};
        }
    }
    private void saveTransaction() {
        String amount = amountField.getText();
        String category = (String) categoryComboBox.getSelectedItem();
        java.util.Date date = dateChooser.getDate();
        String description = descriptionArea.getText();
        String type = (String) typeComboBox.getSelectedItem();

        if (amount.isEmpty() || category.isEmpty() || date == null || description.isEmpty() || type.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        java.sql.Date sqlDate = new java.sql.Date(date.getTime());

        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO Transactions (UserID, CategoryID, Amount, TransactionDate, Description, Type) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            // Assuming a default UserID of 1 for now, you can adjust this as needed.
            statement.setInt(1, 1);
            statement.setInt(2, getCategoryId(category));
            statement.setBigDecimal(3, new java.math.BigDecimal(amount));
            statement.setDate(4, sqlDate);
            statement.setString(5, description);
            statement.setString(6, type);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Giao dịch đã được thêm thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi thêm giao dịch.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }



    private int getCategoryId(String categoryName) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT CategoryID FROM Categories WHERE CategoryName = ?")) {
            statement.setString(1, categoryName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("CategoryID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void updateTransaction() {
        String transactionId = transactionIdField.getText();
        String amount = amountField.getText();
        String category = (String) categoryComboBox.getSelectedItem();
        java.util.Date date = dateChooser.getDate();
        String description = descriptionArea.getText();

        if (transactionId.isEmpty() || amount.isEmpty() || category.isEmpty() || date == null || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        java.sql.Date sqlDate = new java.sql.Date(date.getTime());

        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "UPDATE Transactions SET CategoryID = ?, Amount = ?, TransactionDate = ?, Description = ? WHERE TransactionID = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, getCategoryId(category));
            statement.setBigDecimal(2, new java.math.BigDecimal(amount));
            statement.setDate(3, sqlDate);
            statement.setString(4, description);
            statement.setInt(5, Integer.parseInt(transactionId));

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Giao dịch đã được cập nhật thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi cập nhật giao dịch.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteTransaction() {
        String transactionId = transactionIdField.getText();

        if (transactionId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Transaction ID.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Xóa giao dịch từ cơ sở dữ liệu
            String deleteSQL = "DELETE FROM Transactions WHERE TransactionID = ?";
            PreparedStatement deleteStatement = connection.prepareStatement(deleteSQL);
            deleteStatement.setInt(1, Integer.parseInt(transactionId));
            int rowsDeleted = deleteStatement.executeUpdate();
            
            if (rowsDeleted > 0) {
                // Nếu xóa thành công, cập nhật lại TransactionID trong table
                deleteTransactionFromTable(Integer.parseInt(transactionId));
                JOptionPane.showMessageDialog(this, "Giao dịch đã được xóa thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi xóa giao dịch.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
 // Phương thức để xóa giao dịch từ table và cập nhật lại TransactionID
    private void deleteTransactionFromTable(int deletedTransactionId) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Xóa giao dịch từ cơ sở dữ liệu
            String deleteSQL = "DELETE FROM Transactions WHERE TransactionID = ?";
            PreparedStatement deleteStatement = connection.prepareStatement(deleteSQL);
            deleteStatement.setInt(1, deletedTransactionId);
            int rowsDeleted = deleteStatement.executeUpdate();

            if (rowsDeleted > 0) {
                // Lấy dữ liệu từ hàng xóa đến cuối và cập nhật lại TransactionID
                String updateSQL = "UPDATE Transactions SET TransactionID = ? WHERE TransactionID = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateSQL);
                for (int i = deletedTransactionId + 1; i <= tableModel.getRowCount(); i++) {
                    updateStatement.setInt(1, i - 1); // Giả sử TransactionID bắt đầu từ 1
                    updateStatement.setInt(2, i);
                    updateStatement.addBatch();
                }
                updateStatement.executeBatch();

                // Xóa hàng từ tableModel
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    int currentTransactionId = (int) tableModel.getValueAt(i, 0); // Giả sử cột TransactionID ở cột 0
                    if (currentTransactionId == deletedTransactionId) {
                        tableModel.removeRow(i); // Xóa hàng chứa TransactionID cần xóa
                        break;
                    }
                }
                JOptionPane.showMessageDialog(this, "Giao dịch đã được xóa thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi xóa giao dịch.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    
}
