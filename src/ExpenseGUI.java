import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
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
        dateField.setEditable(false);
        dateField.setToolTipText("Click to choose an expense date");
        JButton datePickerButton = new JButton("Choose");
        JPanel datePanel = new JPanel(new BorderLayout(5, 0));
        datePanel.add(dateField, BorderLayout.CENTER);
        datePanel.add(datePickerButton, BorderLayout.EAST);

        Runnable openDatePicker = () -> {
            LocalDate initialDate = LocalDate.now();
            if (!dateField.getText().isEmpty()) {
                initialDate = LocalDate.parse(dateField.getText(), DateTimeFormatter.ofPattern("dd/MM/uuuu"));
            }
            LocalDate selectedDate = DatePickerDialog.showDialog(this, initialDate);
            if (selectedDate != null) {
                dateField.setText(selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/uuuu")));
            }
        };
        dateField.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent event) {
                openDatePicker.run();
            }
        });
        datePickerButton.addActionListener(event -> openDatePicker.run());

        JLabel categoryLabel = new JLabel("Category:", SwingConstants.RIGHT);
        JComboBox<String> categoryCombo = new JComboBox<>(Expense.getCategories());

        JLabel descriptionLabel = new JLabel("Description:", SwingConstants.RIGHT);
        JTextField descriptionField = new JTextField();

        JLabel amountLabel = new JLabel("Amount:", SwingConstants.RIGHT);
        JTextField amountField = new JTextField();

        JButton addButton = new JButton("Add");
        JButton clearButton = new JButton("Clear");

        panel.add(dateLabel);
        panel.add(datePanel);
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
                if (dateField.getText().isEmpty()) {
                    throw new InvalidExpenseException("Please select an expense date.");
                }
                LocalDate date = LocalDate.parse(dateField.getText(), DateTimeFormatter.ofPattern("dd/MM/uuuu"));
                String category = (String) categoryCombo.getSelectedItem();
                String description = descriptionField.getText().trim();
                double amount = Double.parseDouble(amountField.getText().trim());

                Expense expense = new Expense(date, category, description, amount);
                expenseManager.addExpense(expense);
                clearFields(dateField, descriptionField, amountField);
                refreshTable(expenseManager.getExpenses());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (java.time.format.DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Please select a valid date.", "Input Error", JOptionPane.ERROR_MESSAGE);
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
                expense.getDate().format(DateTimeFormatter.ofPattern("dd/MM/uuuu")),
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

    private static class DatePickerDialog extends JDialog {
        private LocalDate selectedDate;
        private YearMonth displayedMonth;
        private final JLabel monthLabel;
        private final JPanel calendarPanel;

        private DatePickerDialog(Window owner, LocalDate initialDate) {
            super(owner, "Select Expense Date", ModalityType.APPLICATION_MODAL);
            displayedMonth = YearMonth.from(initialDate);
            monthLabel = new JLabel("", SwingConstants.CENTER);
            calendarPanel = new JPanel(new GridLayout(0, 7, 4, 4));

            JPanel navigationPanel = new JPanel(new BorderLayout(8, 0));
            JButton previousButton = new JButton("<");
            JButton nextButton = new JButton(">");
            previousButton.setToolTipText("Previous month");
            nextButton.setToolTipText("Next month");
            previousButton.addActionListener(event -> {
                displayedMonth = displayedMonth.minusMonths(1);
                updateCalendar();
            });
            nextButton.addActionListener(event -> {
                displayedMonth = displayedMonth.plusMonths(1);
                updateCalendar();
            });
            navigationPanel.add(previousButton, BorderLayout.WEST);
            navigationPanel.add(monthLabel, BorderLayout.CENTER);
            navigationPanel.add(nextButton, BorderLayout.EAST);

            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(event -> dispose());
            JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            footerPanel.add(cancelButton);

            JPanel contentPanel = new JPanel(new BorderLayout(8, 8));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            contentPanel.add(navigationPanel, BorderLayout.NORTH);
            contentPanel.add(calendarPanel, BorderLayout.CENTER);
            contentPanel.add(footerPanel, BorderLayout.SOUTH);
            setContentPane(contentPanel);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            updateCalendar();
            pack();
            setResizable(false);
            setLocationRelativeTo(owner);
        }

        private static LocalDate showDialog(Component parent, LocalDate initialDate) {
            Window owner = SwingUtilities.getWindowAncestor(parent);
            DatePickerDialog dialog = new DatePickerDialog(owner, initialDate);
            dialog.setVisible(true);
            return dialog.selectedDate;
        }

        private void updateCalendar() {
            monthLabel.setText(displayedMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault())
                    + " " + displayedMonth.getYear());
            calendarPanel.removeAll();

            for (DayOfWeek day : DayOfWeek.values()) {
                String dayName = day.getDisplayName(TextStyle.SHORT, Locale.getDefault());
                calendarPanel.add(new JLabel(dayName, SwingConstants.CENTER));
            }

            LocalDate firstDay = displayedMonth.atDay(1);
            int leadingDays = firstDay.getDayOfWeek().getValue() - 1;
            for (int i = 0; i < leadingDays; i++) {
                calendarPanel.add(new JLabel(""));
            }

            for (int day = 1; day <= displayedMonth.lengthOfMonth(); day++) {
                LocalDate date = displayedMonth.atDay(day);
                JButton dayButton = new JButton(Integer.toString(day));
                dayButton.addActionListener(event -> {
                    selectedDate = date;
                    dispose();
                });
                calendarPanel.add(dayButton);
            }

            calendarPanel.revalidate();
            calendarPanel.repaint();
            pack();
        }
    }
}
