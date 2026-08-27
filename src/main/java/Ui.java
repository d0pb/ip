import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

/**
 * Handles text input from and output to the user.
 */
public class Ui {
    private static final String INDENT = "     ";
    private static final String DIVIDER = INDENT + "____________________________________________________________";
    private static final String BANNER = " ____            \n"
            + "| __ )  ___  ___ \n"
            + "|  _ \\ / _ \\/ __|\n"
            + "| |_) | (_) \\__ \\\n"
            + "|____/ \\___/|___/";

    private final Scanner scanner;
    private final PrintStream output;

    /**
     * Creates a UI connected to standard input and output.
     */
    public Ui() {
        this(System.in, System.out);
    }

    /**
     * Creates a UI using the supplied streams.
     *
     * @param input source of user commands
     * @param output destination for user-facing messages
     */
    Ui(InputStream input, PrintStream output) {
        this.scanner = new Scanner(input);
        this.output = output;
    }

    /**
     * Returns whether another command is available to read.
     *
     * @return true when another input line is available
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return the next line of input
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Shows Bos's banner and welcome message.
     */
    public void showGreeting() {
        output.println(DIVIDER);
        output.println(BANNER);
        output.println(INDENT + "Hello! I'm Bos.");
        output.println(INDENT + "What can I do for you?");
        output.println(DIVIDER);
    }

    /**
     * Shows Bos's farewell message.
     */
    public void showExit() {
        output.println(INDENT + "Bye. Hope to see you again soon!");
        output.println(DIVIDER);
    }

    /**
     * Separates consecutive command responses.
     */
    public void showDivider() {
        output.println(DIVIDER);
    }

    /**
     * Shows all tasks with their one-based list numbers.
     *
     * @param tasks tasks to display
     */
    public void showTaskList(List<Task> tasks) {
        output.println(INDENT + "Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            output.println(INDENT + (i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Confirms that a task was marked as done.
     *
     * @param task task that was marked
     */
    public void showTaskMarked(Task task) {
        output.println(INDENT + "Nice! I've marked this task as done:");
        output.println(INDENT + task);
    }

    /**
     * Confirms that a task was marked as not done.
     *
     * @param task task that was unmarked
     */
    public void showTaskUnmarked(Task task) {
        output.println(INDENT + "OK, I've marked this task as not done yet:");
        output.println(INDENT + task);
    }

    /**
     * Confirms that a task was deleted and reports the remaining count.
     *
     * @param task deleted task
     * @param taskCount number of tasks remaining
     */
    public void showTaskDeleted(Task task, int taskCount) {
        output.println(INDENT + "Noted. I've removed this task:");
        output.println(INDENT + "  " + task);
        output.println(INDENT + "Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Confirms that a task was added and reports the new count.
     *
     * @param task added task
     * @param taskCount number of tasks after adding
     */
    public void showTaskAdded(Task task, int taskCount) {
        output.println(INDENT + "Got it. I've added this task:");
        output.println(INDENT + "  " + task);
        output.println(INDENT + "Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Shows an invalid-command error.
     *
     * @param message explanation of the error
     */
    public void showError(String message) {
        output.println(INDENT + "OOPS!!! " + message);
    }

    /**
     * Warns that corrupted saved data could not be loaded.
     *
     * @param message explanation of the corrupted data
     */
    public void showLoadingError(String message) {
        output.println("tasks.txt is corrupted (" + message + "), resetting...");
    }

    /**
     * Warns that the task file could not be accessed.
     */
    public void showFileAccessError() {
        output.println("Cannot access the task file, recording from scratch...");
    }

    /**
     * Warns that the current task list could not be saved.
     */
    public void showSavingError() {
        showError("Unable to save tasks onto the hard disk.");
    }
}
