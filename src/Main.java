import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ExpenseGUI gui = new ExpenseGUI();
            gui.setVisible(true);
        });
    }
}

 // Run:
//   d:; cd 'd:\CODE\JAVA\PersonalExpenseTracker'; & 'C:\Users\Dharm Maniya\.vscode\extensions\redhat.java-1.58.0-win32-x64\jre\25.0.4.1-win32-x86_64\bin\java.exe' '-XX:+ShowCodeDetailsInExceptionMessages' '-cp' 'C:\Users\Dharm Maniya\AppData\Roaming\Code\User\workspaceStorage\b45b7867cace60053edd0443335d5973\redhat.java\jdt_ws\PersonalExpenseTracker_3ea9ea24\bin' 'Main'