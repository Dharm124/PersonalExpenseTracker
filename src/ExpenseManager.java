import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class ExpenseManager {
    private ArrayList<Expense> expenses;

    public ExpenseManager() {
        expenses = new ArrayList<>();
        try {
            expenses.addAll(FileManager.loadExpenses());
        } catch (IOException | InvalidExpenseException e) {
            JOptionPane.showMessageDialog(null,
                    "Unable to load expenses file. A new file will be created.\n" + e.getMessage(),
                    "File Error",
                    JOptionPane.WARNING_MESSAGE);
            expenses = new ArrayList<>();
        }
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
        saveToFile();
    }

    public void removeExpense(Expense expense) {
        expenses.remove(expense);
        saveToFile();
    }

    public ArrayList<Expense> getExpenses() {
        return new ArrayList<>(expenses);
    }

    public ArrayList<Expense> searchExpenses(String category, String description) {
        ArrayList<Expense> result = new ArrayList<>();

        for (Expense expense : expenses) {
            boolean categoryMatches = true;
            boolean descriptionMatches = true;

            if (category != null && !category.equals("All")) {
                categoryMatches = expense.getCategory().equalsIgnoreCase(category);
            }

            if (description != null && !description.trim().isEmpty()) {
                descriptionMatches = expense.getDescription().toLowerCase().contains(description.toLowerCase());
            }

            if (categoryMatches && descriptionMatches) {
                result.add(expense);
            }
        }

        return result;
    }

    public double getTotalAmount() {
        double total = 0;
        for (Expense expense : expenses) {
            total += expense.getAmount();
        }
        return total;
    }

    public void saveToFile() {
        try {
            FileManager.saveExpenses(expenses);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Could not save to expenses.txt\n" + e.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
