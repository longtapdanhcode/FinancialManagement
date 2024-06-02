package DoAnCoSo1;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

public class FiancialManagement extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane, panel, panel_1, panel_2, panel_3, panel_4, panel_5;
    private CardLayout cardpanel;

    public FiancialManagement() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1250, 617);
        contentPane = new JPanel();
        contentPane.setBackground(Color.decode("#edf1f6"));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        panel = new JPanel();
        panel.setBounds(0, 70, 1236, 508);
        contentPane.add(panel);
        cardpanel = new CardLayout(0, 0);
        panel.setLayout(cardpanel);

        panel_1 = new JPanel();
        panel_1.setBackground(Color.WHITE);
        panel.add(panel_1, "panel1");
        panel_1.setLayout(null);

        panel_2 = new JPanel();
        panel_2.setBackground(Color.CYAN);
        panel_2.setLayout(new BorderLayout());
        panel.add(panel_2, "panel2");

        panel_3 = new JPanel();
        panel_3.setBackground(Color.LIGHT_GRAY);
        panel.add(panel_3, "panel3");

        panel_4 = new JPanel();
        panel_4.setBackground(Color.DARK_GRAY);
        panel_4.setLayout(new BorderLayout());
        panel.add(panel_4, "panel4");

        panel_5 = new JPanel();
        panel_5.setBackground(SystemColor.activeCaption);
        panel_5.setLayout(new BorderLayout());
        panel.add(panel_5, "panel5");

      
        JButton bt1 = new JButton("Chi Tiêu");
        bt1.setForeground(Color.WHITE);
        bt1.setBounds(21, 20, 150, 40);
        bt1.setBackground(Color.decode("#007aff"));
        contentPane.add(bt1);
        
      
        JButton bt2 = new JButton("Ngân Sách");
        bt2.setForeground(Color.WHITE);
        bt2.setBounds(220, 20, 150, 40);
        bt2.setBackground(Color.decode("#007aff"));
        contentPane.add(bt2);
        
        
        JButton bt3 = new JButton("Xem Ngân Sách");
        bt3.setForeground(Color.WHITE);
        bt3.setBounds(420, 20, 150, 40);
        bt3.setBackground(Color.decode("#007aff"));
        contentPane.add(bt3);
  
       
        JButton bt4 = new JButton("Xem Chi Tiêu");
        bt4.setForeground(Color.WHITE);
        bt4.setBounds(621, 20, 150, 40);
        bt4.setBackground(Color.decode("#007aff"));
        contentPane.add(bt4);

        JButton bt5 = new JButton("Xem Báo Cáo");
        bt5.setForeground(Color.WHITE);
        bt5.setBounds(820, 20, 150, 40);
        bt5.setBackground(Color.decode("#007aff"));
        contentPane.add(bt5);

        bt1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardpanel.show(panel, "panel1");
                
            }
        });

        bt2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardpanel.show(panel, "panel3");
            }
        });

        bt3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardpanel.show(panel, "panel2");
            }
        });

        bt4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardpanel.show(panel, "panel4");
            }
        });

        bt5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardpanel.show(panel, "panel5");
            }
        });
     // Cập nhật các ActionListener
        bt1.addActionListener(e -> openForm(new AddTransactionForm(), "panel1"));
        bt2.addActionListener(e -> openForm(new AddBudgetForm(), "panel3"));
        bt3.addActionListener(e -> openForm(new BudgetChart(), "panel2"));
        bt4.addActionListener(e -> openForm(new TransactionChart(), "panel4"));
        bt5.addActionListener(e -> openForm(new DashboardForm(), "panel5"));
    }

    private void openForm(JPanel form, String panelName) {
        JPanel targetPanel = null;
        // Tìm panel tương ứng với tên được truyền vào
        switch(panelName) {
            case "panel1":
                targetPanel = panel_1;
                break;
            case "panel2":
                targetPanel = panel_2;
                break;
            case "panel3":
                targetPanel = panel_3;
                break;
            case "panel4":
                targetPanel = panel_4;
                break;
            case "panel5":
                targetPanel = panel_5;
                break;
            default:
                break;
        }
        if (targetPanel == null) {
            System.out.println("Panel không tồn tại.");
            return;
        }
        // Hiển thị form con trên panel tương ứng
        targetPanel.removeAll();
        targetPanel.add(form, BorderLayout.CENTER);
        targetPanel.revalidate();
        targetPanel.repaint();
        cardpanel.show(panel, panelName);
    }

    public class DatabaseConnection {
        private static final String URL = "jdbc:sqlserver://localhost;databaseName=Data";
        private static final String USER = "sa";
        private static final String PASSWORD = "123456";

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }
    }
    public static void main(String[] args) {
    	//set look and feel cho giao dien
    	try {
    		UIManager.setLookAndFeel("com.formdev.flatlaf.themes.FlatMacLightLaf");
    	} catch(ClassNotFoundException| InstantiationException| IllegalAccessException| UnsupportedLookAndFeelException ex) {
    		ex.printStackTrace();
    	}
    	
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    FiancialManagement frame = new FiancialManagement();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
