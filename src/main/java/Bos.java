import java.util.Scanner;

/**
 * Runs the Bos chatbot and stores tasks entered during the current session.
 */
public class Bos {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String INDENT = "     ";
    private static final int MAX_TASKS = 100;
    private static final String BANNER = " ____            \n"
                + "| __ )  ___  ___ \n"
                + "|  _ \\ / _ \\/ __|\n"
                + "| |_) | (_) \\__ \\\n"
                + "|____/ \\___/|___/";
    private static final String BYE_COMMAND = "bye";
    private static final String LIST_COMMAND = "list";
    private static final String MARK_COMMAND = "mark ";
    private static final String UNMARK_COMMAND = "unmark ";

    /**
     * Starts Bos and processes input until the user enters {@code bye}.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        printGreeting();
        Scanner scanner = new Scanner(System.in);
        processCommands(scanner);
        printExit();
    }

    /**
     * Prints Bos's banner and welcome message.
     */
    private static void printGreeting() {
        System.out.println(DIVIDER);
        System.out.println(BANNER);
        System.out.println(INDENT + "Hello! I'm Bos.");
        System.out.println(INDENT + "What can I do for you?");
        System.out.println(DIVIDER);
    }

    /**
     * Prints Bos's farewell message.
     */
    private static void printExit() {
        System.out.println(INDENT + "Bye. Hope to see you again soon!");
        System.out.println(DIVIDER);
    }

    /**
     * Stores ordinary input as tasks and displays stored tasks for {@code list}.
     *
     * @param scanner scanner used to read commands from standard input
     */
    private static void processCommands(Scanner scanner) {
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            System.out.println(DIVIDER);

            if (BYE_COMMAND.equals(input)) {
                return;
            } else if (LIST_COMMAND.equals(input)) {
                System.out.println(INDENT + "Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(INDENT + (i + 1) + ".[" + tasks[i].getStatusIcon() + "] " + tasks[i]);
                }
            } else if (input.startsWith(MARK_COMMAND)) {
                int taskIndex = getTaskIndex(input, MARK_COMMAND);
                tasks[taskIndex].markAsDone();
                System.out.println(INDENT + "Nice! I've marked this task as done:");
                printTask(tasks[taskIndex]);
            } else if (input.startsWith(UNMARK_COMMAND)) {
                int taskIndex = getTaskIndex(input, UNMARK_COMMAND);
                tasks[taskIndex].markAsNotDone();
                System.out.println(INDENT + "OK, I've marked this task as not done yet:");
                printTask(tasks[taskIndex]);
            } else {
                tasks[taskCount++] = new Task(input);
                System.out.println(INDENT + "added: " + input);
            }

            System.out.println(DIVIDER);
        }
    }

    /**
     * Converts the user-facing task number in a command into an array index.
     *
     * @param input full command entered by the user
     * @param commandPrefix command text before the task number
     * @return zero-based index of the selected task
     */
    private static int getTaskIndex(String input, String commandPrefix) {
        return Integer.parseInt(input.substring(commandPrefix.length())) - 1;
    }

    /**
     * Prints a task with extra indentation for a command confirmation.
     *
     * @param task task to print
     */
    private static void printTask(Task task) {
        System.out.println(INDENT + "  [" + task.getStatusIcon() + "] " + task);
    }
}
