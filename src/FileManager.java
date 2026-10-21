import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FileManager {
    private static final String FILE_NAME = "expenses.txt";

    public static ArrayList<Expense> loadExpenses() throws IOException, InvalidExpenseException {
        ArrayList<Expense> expenses = new ArrayList<>();
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            file.createNewFile();
            return expenses;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                expenses.add(Expense.fromLine(line));
            }
        }

        return expenses;
    }

    public static void saveExpenses(ArrayList<Expense> expenses) throws IOException {
        File file = new File(FILE_NAME);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Expense expense : expenses) {
                writer.write(expense.toFileLine());
                writer.newLine();
            }
        }
    }
}
