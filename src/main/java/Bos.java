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
        String[] tasks = new String[MAX_TASKS];
        int taskCount = 0;

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            System.out.println(DIVIDER);

            if (BYE_COMMAND.equals(input)) {
                return;
            } else if (LIST_COMMAND.equals(input)) {
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(INDENT + (i + 1) + ". " + tasks[i]);
                }
            } else {
                tasks[taskCount++] = input;
                System.out.println(INDENT + "added: " + input);
            }

            System.out.println(DIVIDER);
        }
    }
}
