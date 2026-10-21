import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class ExpenseGUI extends JFrame {
    private final ExpenseManager expenseManager;
    private final DefaultTableModel tableModel;
    private final JTable expenseTable;
    private JLabel totalLabel;
    private ArrayList<Expense> displayedExpenses;

    public ExpenseGUI() {
        super("Personal Expense Tracker");
        expenseManager = new ExpenseManager();
        displayedExpenses = expenseManager.getExpenses();

        tableModel = new DefaultTableModel(new Object[]{"Date", "Category", "Description", "Amount"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        expenseTable = new JTable(tableModel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(createInputPanel(), BorderLayout.NORTH);
        mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
        mainPanel.add(createSummaryPanel(), BorderLayout.SOUTH);

        setContentPane(mainPanel);
        refreshTable(expenseManager.getExpenses());
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Add Expense"));

        JLabel dateLabel = new JLabel("Date:", SwingConstants.RIGHT);
        JTextField dateField = new JTextField();

        JLabel categoryLabel = new JLabel("Category:", SwingConstants.RIGHT);
        JComboBox<String> categoryCombo = new JComboBox<>(Expense.getCategories());

        JLabel descriptionLabel = new JLabel("Description:", SwingConstants.RIGHT);
        JTextField descriptionField = new JTextField();

        JLabel amountLabel = new JLabel("Amount:", SwingConstants.RIGHT);
        JTextField amountField = new JTextField();

        JButton addButton = new JButton("Add");
        JButton clearButton = new JButton("Clear");

        panel.add(dateLabel);
        panel.add(dateField);
        panel.add(categoryLabel);
        panel.add(categoryCombo);
        panel.add(descriptionLabel);
        panel.add(descriptionField);
        panel.add(amountLabel);
        panel.add(amountField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addButton);
        buttonPanel.add(clearButton);

        addButton.addActionListener(e -> {
            try {
                String date = dateField.getText().trim();
                String category = (String) categoryCombo.getSelectedItem();
                String description = descriptionField.getText().trim();
                double amount = Double.parseDouble(amountField.getText().trim());

                Expense expense = new Expense(date, category, description, amount);
                expenseManager.addExpense(expense);
                clearFields(dateField, descriptionField, amountField);
                refreshTable(expenseManager.getExpenses());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (InvalidExpenseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid Expense", JOptionPane.ERROR_MESSAGE);
            }
        });

        clearButton.addActionListener(e -> clearFields(dateField, descriptionField, amountField));

        JPanel container = new JPanel(new BorderLayout());
        container.add(panel, BorderLayout.CENTER);
        container.add(buttonPanel, BorderLayout.SOUTH);
        return container;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel searchPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Expenses"));

        JLabel searchCategoryLabel = new JLabel("Category:", SwingConstants.RIGHT);
        JComboBox<String> searchCategoryCombo = new JComboBox<>(Expense.getSearchCategories());

        JLabel searchDescriptionLabel = new JLabel("Description:", SwingConstants.RIGHT);
        JTextField searchDescriptionField = new JTextField();

        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");

        searchPanel.add(searchCategoryLabel);
        searchPanel.add(searchCategoryCombo);
        searchPanel.add(searchDescriptionLabel);
        searchPanel.add(searchDescriptionField);

        JPanel searchActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchActionPanel.add(searchButton);
        searchActionPanel.add(refreshButton);

        JPanel searchContainer = new JPanel(new BorderLayout(10, 10));
        searchContainer.add(searchPanel, BorderLayout.CENTER);
        searchContainer.add(searchActionPanel, BorderLayout.EAST);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Expenses"));
        tablePanel.add(new JScrollPane(expenseTable), BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton deleteButton = new JButton("Delete Selected");
        buttonsPanel.add(deleteButton);

        panel.add(searchContainer, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> {
            String category = (String) searchCategoryCombo.getSelectedItem();
            String description = searchDescriptionField.getText().trim();
            ArrayList<Expense> result = expenseManager.searchExpenses(category, description);
            refreshTable(result);
        });

        refreshButton.addActionListener(e -> {
            searchDescriptionField.setText("");
            searchCategoryCombo.setSelectedIndex(0);
            refreshTable(expenseManager.getExpenses());
        });

        deleteButton.addActionListener(e -> deleteSelectedExpense());

        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalLabel = new JLabel("Total Amount: 0.00");
        panel.add(totalLabel);
        return panel;
    }

    private void refreshTable(ArrayList<Expense> expenses) {
        displayedExpenses = expenses;
        tableModel.setRowCount(0);

        for (Expense expense : expenses) {
            tableModel.addRow(new Object[]{
                expense.getDate(),
                expense.getCategory(),
                expense.getDescription(),
                String.format("%.2f", expense.getAmount())
            });
        }

        totalLabel.setText("Total Amount: " + String.format("%.2f", expenseManager.getTotalAmount()));
    }

    private void deleteSelectedExpense() {
        int selectedRow = expenseTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an expense to delete.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Expense selectedExpense = displayedExpenses.get(selectedRow);
        expenseManager.removeExpense(selectedExpense);
        refreshTable(expenseManager.getExpenses());
    }

    private void clearFields(JTextField dateField, JTextField descriptionField, JTextField amountField) {
        dateField.setText("");
        descriptionField.setText("");
        amountField.setText("");
    }
}
