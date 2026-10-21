import java.util.Arrays;

public class Expense {
    public static final String[] VALID_CATEGORIES = {
        "Food",
        "Transport",
        "Shopping",
        "Education",
        "Bills",
        "Other"
    };

    private String date;
    private String category;
    private String description;
    private double amount;

    public Expense(String date, String category, String description, double amount) throws InvalidExpenseException {
        setDate(date);
        setCategory(category);
        setDescription(description);
        setAmount(amount);
    }

    public static String[] getCategories() {
        return VALID_CATEGORIES.clone();
    }

    public static String[] getSearchCategories() {
        String[] categories = new String[VALID_CATEGORIES.length + 1];
        categories[0] = "All";
        System.arraycopy(VALID_CATEGORIES, 0, categories, 1, VALID_CATEGORIES.length);
        return categories;
    }

    public static Expense fromLine(String line) throws InvalidExpenseException {
        if (line == null || line.trim().isEmpty()) {
            throw new InvalidExpenseException("Expense line is empty.");
        }

        String[] parts = line.split("\\|", -1);
        if (parts.length != 4) {
            throw new InvalidExpenseException("Invalid expense format in file.");
        }

        String date = parts[0].trim();
        String category = parts[1].trim();
        String description = parts[2].trim();
        double amount;

        try {
            amount = Double.parseDouble(parts[3].trim());
        } catch (NumberFormatException e) {
            throw new InvalidExpenseException("Invalid amount in file.");
        }

        return new Expense(date, category, description, amount);
    }

    public String toFileLine() {
        return date + "|" + category + "|" + description + "|" + amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) throws InvalidExpenseException {
        if (date == null || date.trim().isEmpty()) {
            throw new InvalidExpenseException("Date cannot be empty.");
        }
        this.date = date.trim();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) throws InvalidExpenseException {
        if (category == null || category.trim().isEmpty()) {
            throw new InvalidExpenseException("Category cannot be empty.");
        }

        boolean validCategory = Arrays.stream(VALID_CATEGORIES)
                .anyMatch(item -> item.equalsIgnoreCase(category.trim()));

        if (!validCategory) {
            throw new InvalidExpenseException("Category is invalid.");
        }

        this.category = category.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) throws InvalidExpenseException {
        if (description == null || description.trim().isEmpty()) {
            throw new InvalidExpenseException("Description cannot be empty.");
        }
        this.description = description.trim();
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) throws InvalidExpenseException {
        if (amount <= 0) {
            throw new InvalidExpenseException("Amount must be greater than zero.");
        }
        this.amount = amount;
    }

    @Override
    public String toString() {
        return date + " | " + category + " | " + description + " | " + amount;
    }
}
