import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ExpenseGUI gui = new ExpenseGUI();
            gui.setVisible(true);
        });
    }
}

// Open PowerShell

// Run:
// cd "D:\CODE\JAVA\PersonalExpenseTracker"
// & "C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot\bin\javac.exe" .\src*.java -d .\out
// & "C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot\bin\java.exe" -cp .\out Main