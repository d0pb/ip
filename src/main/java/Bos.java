import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Runs the Bos chatbot and stores tasks entered during the current session.
 */
public class Bos {
    private static final String INDENT = "     ";
    private static final String DIVIDER = INDENT + "____________________________________________________________";
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
    private static final String TODO_COMMAND = "todo ";
    private static final String DEADLINE_COMMAND = "deadline ";
    private static final String EVENT_COMMAND = "event ";
    private static final Pattern MARK_PATTERN = Pattern.compile(MARK_COMMAND + "(\\d+)");
    private static final Pattern UNMARK_PATTERN = Pattern.compile(UNMARK_COMMAND + "(\\d+)");
    private static final Pattern TODO_PATTERN = Pattern.compile(TODO_COMMAND + "(.*)");
    private static final Pattern DEADLINE_PATTERN = Pattern.compile(DEADLINE_COMMAND + "(?<title>.*?)\\s+/by\\s+(?<date>.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern EVENT_PATTERN = Pattern.compile(EVENT_COMMAND + "(?<title>.*?)\\s+/from\\s+(?<from>.*?)\\s+/to\\s+(?<to>.*)$");

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
                    System.out.println(INDENT + (i + 1) + "." + tasks[i]);
                }

            } else if (input.startsWith(MARK_COMMAND)) {
                Matcher markMatcher = MARK_PATTERN.matcher(input);
                markMatcher.find();
                int taskIndex = Integer.parseInt(markMatcher.group(1)) - 1;
                tasks[taskIndex].markAsDone();
                System.out.println(INDENT + "Nice! I've marked this task as done:");
                System.out.println(INDENT + tasks[taskIndex]);

            } else if (input.startsWith(UNMARK_COMMAND)) {
                Matcher unmarkMatcher = UNMARK_PATTERN.matcher(input);
                unmarkMatcher.find();
                int taskIndex = Integer.parseInt(unmarkMatcher.group(1)) - 1;
                tasks[taskIndex].markAsNotDone();
                System.out.println(INDENT + "OK, I've marked this task as not done yet:");
                System.out.println(INDENT + tasks[taskIndex]);

            } else if (input.startsWith(TODO_COMMAND)) {
                Matcher todoMatcher = TODO_PATTERN.matcher(input);
                todoMatcher.find();
                Task task = new ToDo(todoMatcher.group(1));
                tasks[taskCount++] = task;
                System.out.println(INDENT + "Got it. I've added this task:");
                System.out.println(INDENT + "  " + task);
                System.out.println(INDENT + "Now you have " + taskCount + " tasks in the list.");
            
            } else if (input.startsWith(DEADLINE_COMMAND)) {
                Matcher deadlineMatcher = DEADLINE_PATTERN.matcher(input);
                deadlineMatcher.find();
                Task task = new Deadline(deadlineMatcher.group("title"), deadlineMatcher.group("date"));
                tasks[taskCount++] = task;
                System.out.println(INDENT + "Got it. I've added this task:");
                System.out.println(INDENT + "  " + task);
                System.out.println(INDENT + "Now you have " + taskCount + " tasks in the list.");
                
            } else if (input.startsWith(EVENT_COMMAND)) {
                Matcher eventMatcher = EVENT_PATTERN.matcher(input);
                eventMatcher.find();
                Task task = new Event(eventMatcher.group("title"), eventMatcher.group("from"), eventMatcher.group("to"));
                tasks[taskCount++] = task;
                System.out.println(INDENT + "Got it. I've added this task:");
                System.out.println(INDENT + "  " + task);
                System.out.println(INDENT + "Now you have " + taskCount + " tasks in the list.");
            } else {
                tasks[taskCount++] = new Task(input);
                System.out.println(INDENT + "added: " + input);
            }

            System.out.println(DIVIDER);
        }
    }
}
