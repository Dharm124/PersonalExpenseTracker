import java.util.Arrays;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class Expense {
    private static final DateTimeFormatter FILE_DATE_FORMAT =
            DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT);

    public static final String[] VALID_CATEGORIES = {
        "Food",
        "Transport",
        "Shopping",
        "Education",
        "Bills",
        "Other"
    };

    private LocalDate date;
    private String category;
    private String description;
    private double amount;

    public Expense(LocalDate date, String category, String description, double amount) throws InvalidExpenseException {
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

        LocalDate date;
        String category = parts[1].trim();
        String description = parts[2].trim();
        double amount;

        try {
            String dateText = parts[0].trim();
            try {
                date = LocalDate.parse(dateText, FILE_DATE_FORMAT);
            } catch (DateTimeParseException e) {
                date = LocalDate.parse(dateText, DateTimeFormatter.ISO_LOCAL_DATE);
            }
        } catch (DateTimeParseException e) {
            throw new InvalidExpenseException("Invalid date in file. Use day/month/year format.");
        }

        try {
            amount = Double.parseDouble(parts[3].trim());
        } catch (NumberFormatException e) {
            throw new InvalidExpenseException("Invalid amount in file.");
        }

        return new Expense(date, category, description, amount);
    }

    public String toFileLine() {
        return date.format(FILE_DATE_FORMAT) + "|" + category + "|" + description + "|" + amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) throws InvalidExpenseException {
        if (date == null) {
            throw new InvalidExpenseException("Please select a valid date.");
        }
        this.date = date;
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
        return date.format(FILE_DATE_FORMAT) + " | " + category + " | " + description + " | " + amount;
    }
}
