# Personal Expense Tracker

A simple beginner-friendly Java desktop application for tracking personal expenses. It uses Java Swing and stores data in a local text file named `expenses.txt`.

## Features

- Add an expense with a calendar-selected date, category, description, and amount
- View all expenses in a table
- Delete a selected expense
- Refresh the table
- Search expenses by category or description
- Display total expenses
- Automatically load and save data from `expenses.txt`

## Project Structure

- `src/Expense.java` - Represents one expense record
- `src/InvalidExpenseException.java` - Custom exception for invalid input
- `src/ExpenseManager.java` - Manages all expense operations
- `src/FileManager.java` - Reads and writes `expenses.txt`
- `src/ExpenseGUI.java` - Swing user interface
- `src/Main.java` - Launches the application

## How to Run

1. Open a terminal in the project folder.
2. Compile the project:

   ```bash
   javac src/*.java -d out
   ```

3. Run the application:

   ```bash
   java -cp out Main
   ```

4. The program will create and use an `expenses.txt` file in the project folder.

## Class Explanation

### Expense
Stores one expense and validates its fields such as date, category, description, and amount. Dates are represented as `LocalDate`; existing `day/month/year` expense files remain supported.

### InvalidExpenseException
A custom exception used when the user enters invalid data.

### ExpenseManager
Keeps the list of expenses, performs searches, calculates totals, and calls file operations.

### FileManager
Loads and saves expense data to the local text file `expenses.txt`.

### ExpenseGUI
Creates the Swing interface with text fields, buttons, table, and search features.

### Main
Starts the application by creating the GUI window.

## Notes

This project is designed for a simple college Java assignment and viva. It uses only Core Java and Swing, with no database or web technology.
