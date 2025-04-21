import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class MetroRailGUI extends JFrame {
    // Database connection constants
    private static final String DB_URL = "jdbc:mysql://localhost:3306/metro";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    private Connection conn;

    // For station dropdowns, cache
    private Map<Integer, String> stationMap = new HashMap<>();

    public MetroRailGUI() {
        setTitle("Dhaka Metro Rail Ticketing System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 400);
        setLocationRelativeTo(null);

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            loadStations();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection failed!\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        setContentPane(mainMenuPanel());
    }

    // Load stations into map
    private void loadStations() throws SQLException {
        stationMap.clear();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT station_id, station_name FROM stations");
        while (rs.next()) {
            stationMap.put(rs.getInt(1), rs.getString(2));
        }
    }

    // Main menu panel
    private JPanel mainMenuPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBorder(new EmptyBorder(30, 100, 30, 100));

        JLabel title = new JLabel("Dhaka Metro Rail Ticketing System", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(title);

        JButton btnOneTime = new JButton("Buy One-Time Pass");
        btnOneTime.addActionListener(e -> setContentPane(oneTimePanel()));
        panel.add(btnOneTime);

        JButton btnApplyPass = new JButton("Apply for MRT/RAPID Pass");
        btnApplyPass.addActionListener(e -> setContentPane(applyPassPanel()));
        panel.add(btnApplyPass);

        JButton btnPass = new JButton("Use MRT/Rapid Pass");
        btnPass.addActionListener(e -> setContentPane(passPanel()));
        panel.add(btnPass);

        JButton btnRecharge = new JButton("Recharge MRT/Rapid Pass");
        btnRecharge.addActionListener(e -> setContentPane(rechargePanel()));
        panel.add(btnRecharge);

        // --- New "Calculate Fare" button ---
        JButton btnFareCalc = new JButton("Calculate Fare");
        btnFareCalc.addActionListener(e -> setContentPane(calculateFarePanel()));
        panel.add(btnFareCalc);
        // -----------------------------------

        JButton btnExit = new JButton("Exit");
        btnExit.addActionListener(e -> System.exit(0));
        panel.add(btnExit);

        return panel;
    }

    // Panel to apply for MRT/RAPID pass (with NID)
    private JPanel applyPassPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel lbl = new JLabel("Apply for MRT/RAPID Pass", SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(lbl, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(0, 2, 10, 10));
        center.add(new JLabel("Full Name:"));
        JTextField tfName = new JTextField();
        center.add(tfName);

        center.add(new JLabel("NID (National ID):"));
        JTextField tfNid = new JTextField();
        center.add(tfNid);

        center.add(new JLabel("Select Card Type:"));
        JComboBox<String> cbType = new JComboBox<>(new String[]{"MRT Pass (500 Tk)", "Rapid Pass (400 Tk)"});
        center.add(cbType);

        center.add(new JLabel("Amount Paid:"));
        JTextField tfAmount = new JTextField();
        center.add(tfAmount);

        panel.add(center, BorderLayout.CENTER);

        JPanel south = new JPanel();
        JButton btnApply = new JButton("Apply");
        JButton btnBack = new JButton("Back");
        south.add(btnApply);
        south.add(btnBack);
        panel.add(south, BorderLayout.SOUTH);

        btnBack.addActionListener(e -> setContentPane(mainMenuPanel()));

        btnApply.addActionListener(e -> {
            String name = tfName.getText().trim();
            String nid = tfNid.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your name.");
                return;
            }
            if (nid.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your NID.");
                return;
            }
            String cardType = (cbType.getSelectedIndex() == 0) ? "MRT" : "RAPID";
            int cardPrice = cardType.equals("MRT") ? 500 : 400;
            double paid;
            try {
                paid = Double.parseDouble(tfAmount.getText().trim());
                if (paid < cardPrice) throw new Exception();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid amount. Minimum: " + cardPrice + " Tk");

                return;
            }
            double change = paid - cardPrice;
            Map<Integer, Integer> notes = getChangeNotes(change);
            String cardNum = (cardType.equals("MRT") ? "MRT" : "RAP") + System.currentTimeMillis();
            double startBalance = 0 + 250.0; // Bonus 250

            try {
                PreparedStatement pst = conn.prepareStatement(
                        "INSERT INTO mrt_rapid_pass_users (card_number, name, nid, card_type, balance) VALUES (?, ?, ?, ?, ?)");
                pst.setString(1, cardNum);
                pst.setString(2, name);
                pst.setString(3, nid);
                pst.setString(4, cardType);
                pst.setDouble(5, startBalance);
                pst.executeUpdate();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
                return;
            }
            StringBuilder msg = new StringBuilder();
            msg.append("Pass issued successfully!\n");
            msg.append("Name: ").append(name).append("\n");
            msg.append("NID: ").append(nid).append("\n");
            msg.append("Card Type: ").append(cardType).append("\n");
            msg.append("Card Number: ").append(cardNum).append("\n");
            msg.append("Price: ").append(cardPrice).append(" Tk\n");
            msg.append("Bonus added: 250 Tk\n");
            msg.append("Current Balance: ").append(String.format("%.2f", startBalance)).append(" Tk\n");
            msg.append("Paid: ").append(paid).append(" Tk\n");
            msg.append("Change: ").append(String.format("%.2f", change)).append(" Tk\n");
            if (change > 0.0 && !notes.isEmpty()) {
                msg.append("Change given as:\n");
                for (int note : Arrays.asList(1000, 500, 200, 100, 50, 20, 10, 2, 1)) {
                    if (notes.containsKey(note)) {
                        msg.append(note).append(" x ").append(notes.get(note)).append("\n");
                    }
                }
            }
            JOptionPane.showMessageDialog(this, msg.toString());
            setContentPane(mainMenuPanel());
        });

        return panel;
    }

    // Panel for one-time pass
    private JPanel oneTimePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel lbl = new JLabel("Buy One-Time Pass", SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(lbl, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(0, 2, 10, 10));
        JComboBox<String> cbSource = new JComboBox<>();
        JComboBox<String> cbDest = new JComboBox<>();
        for (Map.Entry<Integer, String> e : stationMap.entrySet()) {
            cbSource.addItem(e.getKey() + ": " + e.getValue());
            cbDest.addItem(e.getKey() + ": " + e.getValue());
        }
        center.add(new JLabel("Source:"));
        center.add(cbSource);
        center.add(new JLabel("Destination:"));
        center.add(cbDest);

        center.add(new JLabel("Enter Amount Paid:"));
        JTextField tfAmountPaid = new JTextField();
        center.add(tfAmountPaid);

        panel.add(center, BorderLayout.CENTER);

        JPanel south = new JPanel();
        JButton btnBuy = new JButton("Buy Ticket");
        JButton btnBack = new JButton("Back");
        south.add(btnBuy);
        south.add(btnBack);
        panel.add(south, BorderLayout.SOUTH);

        btnBack.addActionListener(e -> setContentPane(mainMenuPanel()));

        btnBuy.addActionListener(e -> {
            int src = Integer.parseInt(cbSource.getSelectedItem().toString().split(":")[0]);
            int dst = Integer.parseInt(cbDest.getSelectedItem().toString().split(":")[0]);
            if (src == dst) {
                JOptionPane.showMessageDialog(this, "Source and destination cannot be the same.");
                return;
            }
            double fare;
            try {
                fare = getFare(src, dst);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Fare not found for this route.");
                return;
            }
            double paid;
            try {
                paid = Double.parseDouble(tfAmountPaid.getText().trim());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid amount.");
                return;
            }
            if (paid < fare) {
                JOptionPane.showMessageDialog(this, "Insufficient amount. Fare: " + fare);
                return;
            }
            double change = paid - fare;
            Map<Integer, Integer> notes = getChangeNotes(change);
            String ticketNum = "OT" + System.currentTimeMillis();

            try {
                PreparedStatement pst = conn.prepareStatement(
                        "INSERT INTO one_time_pass VALUES (?, ?, ?, ?, NOW())");
                pst.setString(1, ticketNum);
                pst.setInt(2, src);
                pst.setInt(3, dst);
                pst.setDouble(4, fare);
                pst.executeUpdate();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
                return;
            }
            StringBuilder msg = new StringBuilder();
            msg.append("One Time Pass issued!\n");
            msg.append("Ticket: ").append(ticketNum).append("\n");
            msg.append("Fare: ").append(fare).append(" Tk\n");
            msg.append("Paid: ").append(paid).append(" Tk\n");
            msg.append("Change: ").append(String.format("%.2f", change)).append(" Tk\n");
            if (change > 0.0 && !notes.isEmpty()) {
                msg.append("Change given as:\n");
                for (int note : Arrays.asList(1000, 500, 200, 100, 50, 20, 10, 2, 1)) {
                    if (notes.containsKey(note)) {
                        msg.append(note).append(" x ").append(notes.get(note)).append("\n");
                    }
                }
            }
            msg.append("From: ").append(stationMap.get(src)).append("\nTo: ").append(stationMap.get(dst));
            JOptionPane.showMessageDialog(this, msg.toString());
            setContentPane(mainMenuPanel());
        });

        return panel;
    }

    // Panel for MRT/Rapid pass usage
    private JPanel passPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        JLabel lbl = new JLabel("Use MRT/Rapid Pass", SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(lbl, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(0, 2, 10, 10));
        center.add(new JLabel("Enter Card Number:"));
        JTextField tfCard = new JTextField();
        center.add(tfCard);

        JComboBox<String> cbSource = new JComboBox<>();
        JComboBox<String> cbDest = new JComboBox<>();
        for (Map.Entry<Integer, String> e : stationMap.entrySet()) {
            cbSource.addItem(e.getKey() + ": " + e.getValue());
            cbDest.addItem(e.getKey() + ": " + e.getValue());
        }
        center.add(new JLabel("Source:"));
        center.add(cbSource);
        center.add(new JLabel("Destination:"));
        center.add(cbDest);

        JButton btnCheck = new JButton("Make Journey");
        center.add(btnCheck);

        panel.add(center, BorderLayout.CENTER);

        JPanel south = new JPanel();
        JButton btnBack = new JButton("Back");
        south.add(btnBack);
        panel.add(south, BorderLayout.SOUTH);

        btnBack.addActionListener(e -> setContentPane(mainMenuPanel()));

        btnCheck.addActionListener(e -> {
            String cardNum = tfCard.getText().trim();
            int src = Integer.parseInt(cbSource.getSelectedItem().toString().split(":")[0]);
            int dst = Integer.parseInt(cbDest.getSelectedItem().toString().split(":")[0]);
            if (src == dst) {
                JOptionPane.showMessageDialog(this, "Source and destination cannot be the same.");
                return;
            }
            try {
                // Get user
                PreparedStatement pst = conn.prepareStatement(
                        "SELECT * FROM mrt_rapid_pass_users WHERE card_number=?");
                pst.setString(1, cardNum);
                ResultSet rs = pst.executeQuery();
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "Card not found.");
                    return;
                }
                String name = rs.getString("name");
                String nid = rs.getString("nid");
                String type = rs.getString("card_type");
                double balance = rs.getDouble("balance");
                double fare = getFare(src, dst);
                double discounted = fare * 0.9;
                if (balance < discounted) {
                    JOptionPane.showMessageDialog(this, "Insufficient Balance! Required: " + discounted + ", Your Balance: " + balance);
                    return;
                }
                balance -= discounted;
                PreparedStatement updt = conn.prepareStatement(
                        "UPDATE mrt_rapid_pass_users SET balance=? WHERE card_number=?");
                updt.setDouble(1, balance);
                updt.setString(2, cardNum);
                updt.executeUpdate();
                StringBuilder msg = new StringBuilder();
                msg.append("Journey Successful!\n");
                msg.append("Name: ").append(name).append(" [").append(type).append(" Pass]\n");
                msg.append("NID: ").append(nid).append("\n");
                msg.append("Original Fare: ").append(fare).append(" Tk\n");
                msg.append("Discounted Fare: ").append(String.format("%.2f", discounted)).append(" Tk\n");
                msg.append("New Balance: ").append(String.format("%.2f", balance)).append(" Tk\n");
                msg.append("Route: ").append(stationMap.get(src)).append(" -> ").append(stationMap.get(dst));
                JOptionPane.showMessageDialog(this, msg.toString());
                setContentPane(mainMenuPanel());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
            }
        });

        return panel;
    }

    // Panel for recharge
    private JPanel rechargePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        JLabel lbl = new JLabel("Recharge MRT/Rapid Pass", SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(lbl, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(0, 2, 10, 10));
        center.add(new JLabel("Enter Card Number:"));
        JTextField tfCard = new JTextField();
        center.add(tfCard);

        center.add(new JLabel("Enter Amount:"));
        JTextField tfAmt = new JTextField();
        center.add(tfAmt);

        JButton btnRecharge = new JButton("Recharge");
        center.add(btnRecharge);

        panel.add(center, BorderLayout.CENTER);

        JPanel south = new JPanel();
        JButton btnBack = new JButton("Back");
        south.add(btnBack);
        panel.add(south, BorderLayout.SOUTH);

        btnBack.addActionListener(e -> setContentPane(mainMenuPanel()));

        btnRecharge.addActionListener(e -> {
            String cardNum = tfCard.getText().trim();
            double amt;
            try {
                amt = Double.parseDouble(tfAmt.getText().trim());
                if (amt <= 0) throw new Exception();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid amount.");
                return;
            }
            try {
                PreparedStatement pst = conn.prepareStatement(
                        "SELECT balance FROM mrt_rapid_pass_users WHERE card_number=?");
                pst.setString(1, cardNum);
                ResultSet rs = pst.executeQuery();
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "Card not found.");
                    return;
                }
                double bal = rs.getDouble("balance") + amt;
                PreparedStatement updt = conn.prepareStatement(
                        "UPDATE mrt_rapid_pass_users SET balance=? WHERE card_number=?");
                updt.setDouble(1, bal);
                updt.setString(2, cardNum);
                updt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Recharge successful!\nNew balance: " + bal + " Tk");
                setContentPane(mainMenuPanel());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
            }
        });

        return panel;
    }

    // ------------------------- NEW: Calculate Fare Panel -------------------------
    private JPanel calculateFarePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        JLabel lbl = new JLabel("Calculate Fare", SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(lbl, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(0, 2, 10, 10));
        JComboBox<String> cbSource = new JComboBox<>();
        JComboBox<String> cbDest = new JComboBox<>();
        for (Map.Entry<Integer, String> e : stationMap.entrySet()) {
            cbSource.addItem(e.getKey() + ": " + e.getValue());
            cbDest.addItem(e.getKey() + ": " + e.getValue());
        }
        center.add(new JLabel("Source:"));
        center.add(cbSource);
        center.add(new JLabel("Destination:"));
        center.add(cbDest);

        panel.add(center, BorderLayout.CENTER);

        JPanel south = new JPanel();
        JButton btnCalc = new JButton("Calculate");
        JButton btnBack = new JButton("Back");
        south.add(btnCalc);
        south.add(btnBack);
        panel.add(south, BorderLayout.SOUTH);

        btnBack.addActionListener(e -> setContentPane(mainMenuPanel()));

        btnCalc.addActionListener(e -> {
            int src = Integer.parseInt(cbSource.getSelectedItem().toString().split(":")[0]);
            int dst = Integer.parseInt(cbDest.getSelectedItem().toString().split(":")[0]);
            if (src == dst) {
                JOptionPane.showMessageDialog(this, "Source and destination cannot be the same.");
                return;
            }
            double fare;
            try {
                fare = getFare(src, dst);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Fare not found for this route.");
                return;
            }
            StringBuilder msg = new StringBuilder();
            msg.append("From: ").append(stationMap.get(src)).append("\n");
            msg.append("To: ").append(stationMap.get(dst)).append("\n");
            msg.append("Regular Fare: ").append(fare).append(" Tk\n");
            msg.append("MRT/Rapid Pass Fare (10% discount): ").append(String.format("%.2f", fare * 0.9)).append(" Tk");
            JOptionPane.showMessageDialog(this, msg.toString());
        });

        return panel;
    }
    // ---------------------------------------------------------------------------

    // Set the panel and refresh
    private void setContentPane(JPanel panel) {
        getContentPane().removeAll();
        getContentPane().add(panel);
        revalidate();
        repaint();
    }

    // Fare from DB
    private double getFare(int src, int dst) throws SQLException {
        PreparedStatement pst = conn.prepareStatement(
                "SELECT fare FROM fares WHERE source_id=? AND dest_id=?");
        pst.setInt(1, Math.min(src, dst));
        pst.setInt(2, Math.max(src, dst));
        ResultSet rs = pst.executeQuery();
        if (rs.next()) return rs.getDouble(1);
        throw new SQLException("Fare not found for this route");
    }

    // Compute change notes
    private Map<Integer, Integer> getChangeNotes(double change) {
        int[] notes = {1000, 500, 200, 100, 50, 20, 10, 2, 1};
        Map<Integer, Integer> res = new LinkedHashMap<>();
        int remaining = (int)Math.round(change); // Avoid floating point issues
        for (int note : notes) {
            int count = remaining / note;
            if (count > 0) {
                res.put(note, count);
                remaining -= count * note;
            }
        }
        return res;
    }

    public static void main(String[] args) {
        // Use system look and feel for a better appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new MetroRailGUI().setVisible(true));
    }
}