package DoAnCoSo1;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import com.toedter.calendar.JDateChooser;

import DoAnCoSo1.FiancialManagement.DatabaseConnection;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date; 

public class AddBudgetForm extends JPanel {
    private static final long serialVersionUID = 1L;
    private static JTextField amountField;
    private static JComboBox<String> categoryComboBox;
    private static JTextField userIdField;
    private static JDateChooser startDateChooser;
    private static JDateChooser endDateChooser;
    private JButton saveButton;
    private JButton editButton;
    private JButton deleteButton;
    public AddBudgetForm() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1300, 600));

        JPanel formPanel = new JPanel();
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel amountLabel = new JLabel("Số tiền:");
        amountLabel.setBounds(35, 9, 195, 52);
        amountField = new JTextField();
        amountField.setBounds(176, 10, 195, 52);
        JLabel categoryLabel = new JLabel("Danh mục:");
        categoryLabel.setBounds(35, 80, 195, 52);
        categoryComboBox = new JComboBox<>(getCategories());
        categoryComboBox.setBounds(176, 80, 195, 52);
        JLabel UserIDLabel = new JLabel("User ID:");
        UserIDLabel.setBounds(35, 149, 195, 52);
        userIdField = new JTextField();
        userIdField.setBounds(176, 150, 195, 52);
        JLabel startDateLabel = new JLabel("Ngày bắt đầu:");
        startDateLabel.setBounds(35, 220, 195, 52);
        startDateChooser = new JDateChooser();
        startDateChooser.setBounds(176, 220, 195, 52);
        JLabel endDateLabel = new JLabel("Ngày kết thúc:");
        endDateLabel.setBounds(35, 290, 195, 52);
        endDateChooser = new JDateChooser();
        endDateChooser.setBounds(176, 290, 195, 52);

        saveButton = new JButton("Lưu");
        saveButton.setBounds(35, 355, 155, 52);
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveBudget();
            }
        });

        editButton = new JButton("Sửa");
        editButton.setBounds(216, 355, 155, 52);
        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editBudget();
            }
        });

        deleteButton = new JButton("Xóa");
        deleteButton.setBounds(137, 427, 155, 52);
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteBudget();
            }
        });

        formPanel.setLayout(null);

        formPanel.add(amountLabel);
        formPanel.add(amountField);
        formPanel.add(categoryLabel);
        formPanel.add(categoryComboBox);
        formPanel.add(UserIDLabel);
        formPanel.add(userIdField);
        formPanel.add(startDateLabel);
        formPanel.add(startDateChooser);
        formPanel.add(endDateLabel);
        formPanel.add(endDateChooser);
        formPanel.add(saveButton);
        formPanel.add(editButton);
        formPanel.add(deleteButton);

        add(formPanel, BorderLayout.CENTER);

        JPanel listNS = new JPanel();
      listNS.setBounds(380, 10, 909, 590);
      formPanel.add(listNS);

      ViewBudgetsForm viewBudgetsForm = new ViewBudgetsForm();
      listNS.setLayout(new BorderLayout()); // Set layout to BorderLayout
      listNS.add(viewBudgetsForm, BorderLayout.CENTER); // Add ViewBudgetsForm to CENTER
    }
    public static void updateFields(String budgetId, String amount, String category, Date parsedStartDate, Date parsedEndDate) {
        // Cập nhật các textfields với dữ liệu từ hàng được chọn trong bảng
        userIdField.setText(budgetId);
        amountField.setText(amount);
        categoryComboBox.setSelectedItem(category);
        startDateChooser.setDate(parsedStartDate);
        endDateChooser.setDate(parsedEndDate);
    }
    private String[] getCategories() {
           // Lấy danh mục từ cơ sở dữ liệu
           List<String> categories = new ArrayList<>();
           try (Connection connection = DatabaseConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT CategoryName FROM Categories")) {
               while (resultSet.next()) {
                   categories.add(resultSet.getString("CategoryName"));
               }
           } catch (SQLException e) {
               e.printStackTrace();
           }
           return categories.toArray(new String[0]);
       }

       private void saveBudget() {
           // Code để lưu ngân sách
           // Tương tự như phương thức saveBudget() hiện tại của bạn
    	   String amount = amountField.getText();
         String category = (String) categoryComboBox.getSelectedItem();
         String userId = userIdField.getText();
         java.util.Date startDate = startDateChooser.getDate();
         java.util.Date endDate = endDateChooser.getDate();
 
         if (amount.isEmpty() || category.isEmpty() || userId.isEmpty() || startDate == null || endDate == null) {
             JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
             return;
         }
 
         java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
         java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());
 
         try (Connection connection = DatabaseConnection.getConnection()) {
             String sql = "INSERT INTO Budgets (Amount, CategoryID, UserID, StartDate, EndDate) VALUES (?, ?, ?, ?, ?)";
             PreparedStatement statement = connection.prepareStatement(sql);
             statement.setBigDecimal(1, new java.math.BigDecimal(amount));
             statement.setInt(2, getCategoryId(category));
             statement.setInt(3, Integer.parseInt(userId));
             statement.setDate(4, sqlStartDate);
             statement.setDate(5, sqlEndDate);
 
             int rowsInserted = statement.executeUpdate();
             if (rowsInserted > 0) {
                 JOptionPane.showMessageDialog(this, "Ngân sách đã được thêm thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
             }
         } catch (SQLException e) {
             e.printStackTrace();
             JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi thêm ngân sách.", "Lỗi", JOptionPane.ERROR_MESSAGE);
         }
       }

       private void editBudget() {
           // Code để chỉnh sửa ngân sách
           // Tương tự như phương thức editBudget() hiện tại của bạn
    	   String amount = amountField.getText();
         String category = (String) categoryComboBox.getSelectedItem();
         String userId = userIdField.getText();
         java.util.Date startDate = startDateChooser.getDate();
         java.util.Date endDate = endDateChooser.getDate();
 
         if (amount.isEmpty() || category.isEmpty() || userId.isEmpty() || startDate == null || endDate == null) {
             JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
             return;
         }
 
         java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
         java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());
 
         try (Connection connection = DatabaseConnection.getConnection()) {
             String sql = "UPDATE Budgets SET Amount = ?, CategoryID = ?, StartDate = ?, EndDate = ? WHERE UserID = ? AND CategoryID = ?";
             PreparedStatement statement = connection.prepareStatement(sql);
             statement.setBigDecimal(1, new java.math.BigDecimal(amount));
             statement.setInt(2, getCategoryId(category));
             statement.setDate(3, sqlStartDate);
             statement.setDate(4, sqlEndDate);
             statement.setInt(5, Integer.parseInt(userId));
             statement.setInt(6, getCategoryId(category));
 
             int rowsUpdated = statement.executeUpdate();
             if (rowsUpdated > 0) {
                 JOptionPane.showMessageDialog(this, "Ngân sách đã được cập nhật thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
             }
         } catch (SQLException e) {
             e.printStackTrace();
             JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi cập nhật ngân sách.", "Lỗi", JOptionPane.ERROR_MESSAGE);
         }
       }

       private void deleteBudget() {
           // Code để xóa ngân sách
           // Tương tự như phương thức deleteBudget() hiện tại của bạn
    	   String userId = userIdField.getText();
         String category = (String) categoryComboBox.getSelectedItem();
 
         if (userId.isEmpty() || category.isEmpty()) {
             JOptionPane.showMessageDialog(this, "Vui lòng chọn ngân sách để xóa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
             return;
         }
 
         int categoryId = getCategoryId(category);
         if (categoryId == 0) {
             JOptionPane.showMessageDialog(this, "Không tìm thấy danh mục.", "Lỗi", JOptionPane.ERROR_MESSAGE);
             return;
         }
 
         int confirmed = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa ngân sách này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
         if (confirmed == JOptionPane.YES_OPTION) {
             try (Connection connection = DatabaseConnection.getConnection()) {
                 String sql = "DELETE FROM Budgets WHERE UserID = ? AND CategoryID = ?";
                 PreparedStatement statement = connection.prepareStatement(sql);
                 statement.setInt(1, Integer.parseInt(userId));
                 statement.setInt(2, categoryId);
 
                 int rowsDeleted = statement.executeUpdate();
                 if (rowsDeleted > 0) {
                     JOptionPane.showMessageDialog(this, "Ngân sách đã được xóa thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                 } else {
                     JOptionPane.showMessageDialog(this, "Không tìm thấy ngân sách để xóa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                 }
             } catch (SQLException e) {
                 e.printStackTrace();
                 JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi xóa ngân sách.", "Lỗi", JOptionPane.ERROR_MESSAGE);
             }
         }
       }

       private int getCategoryId(String categoryName) {
           // Code để lấy ID của danh mục từ tên danh mục
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
            return 0; // Return 0 or handle the error appropriately
       }

       public static void main(String[] args) {
           SwingUtilities.invokeLater(() -> {
               JFrame frame = new JFrame();
               frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
               frame.getContentPane().add(new AddBudgetForm());
               frame.pack();
               frame.setLocationRelativeTo(null);
               frame.setVisible(true);
           });
       }
   }

