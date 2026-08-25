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
    private static final String MARK_COMMAND = "mark";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final Pattern MARK_PATTERN = Pattern.compile("^mark\\s+(\\d+)\\s*$");
    private static final Pattern UNMARK_PATTERN = Pattern.compile("^unmark\\s+(\\d+)\\s*$");
    private static final Pattern TODO_PATTERN = Pattern.compile("^todo\\s+(.*)$");
    private static final Pattern DEADLINE_PATTERN = Pattern.compile(
            "^deadline\\s*(?<title>.*?)\\s*/by\\s*(?<date>.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern EVENT_PATTERN = Pattern.compile(
            "^event\\s*(?<title>.*?)\\s*/from\\s*(?<from>.*?)\\s*/to\\s*(?<to>.*)$",
            Pattern.CASE_INSENSITIVE);

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

            try {
                if (BYE_COMMAND.equals(input)) {
                    return;

                } else if (LIST_COMMAND.equals(input)) {
                    System.out.println(INDENT + "Here are the tasks in your list:");
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println(INDENT + (i + 1) + "." + tasks[i]);
                    }

                } else if (isCommand(input, MARK_COMMAND)) {
                    int taskIndex = getTaskIndex(input, MARK_PATTERN, taskCount);
                    tasks[taskIndex].markAsDone();
                    System.out.println(INDENT + "Nice! I've marked this task as done:");
                    System.out.println(INDENT + tasks[taskIndex]);

                } else if (isCommand(input, UNMARK_COMMAND)) {
                    int taskIndex = getTaskIndex(input, UNMARK_PATTERN, taskCount);
                    tasks[taskIndex].markAsNotDone();
                    System.out.println(INDENT + "OK, I've marked this task as not done yet:");
                    System.out.println(INDENT + tasks[taskIndex]);

                } else if (isCommand(input, TODO_COMMAND)) {
                    Matcher todoMatcher = TODO_PATTERN.matcher(input);
                    if (!todoMatcher.matches() || todoMatcher.group(1).isBlank()) {
                        throw BosException.emptyDescription(TODO_COMMAND);
                    }
                    ensureTaskListHasSpace(taskCount);
                    Task task = new ToDo(todoMatcher.group(1).trim());
                    tasks[taskCount++] = task;
                    printTaskAdded(task, taskCount);

                } else if (isCommand(input, DEADLINE_COMMAND)) {
                    Matcher deadlineMatcher = DEADLINE_PATTERN.matcher(input);
                    if (input.trim().equals(DEADLINE_COMMAND)) {
                        throw BosException.emptyDescription(DEADLINE_COMMAND);
                    }
                    if (!deadlineMatcher.matches()) {
                        throw BosException.invalidFormat("deadline DESCRIPTION /by DATE");
                    }
                    if (deadlineMatcher.group("title").isBlank()) {
                        throw BosException.emptyDescription(DEADLINE_COMMAND);
                    }
                    if (deadlineMatcher.group("date").isBlank()) {
                        throw BosException.invalidFormat("deadline DESCRIPTION /by DATE");
                    }
                    ensureTaskListHasSpace(taskCount);
                    Task task = new Deadline(deadlineMatcher.group("title").trim(),
                            deadlineMatcher.group("date").trim());
                    tasks[taskCount++] = task;
                    printTaskAdded(task, taskCount);

                } else if (isCommand(input, EVENT_COMMAND)) {
                    Matcher eventMatcher = EVENT_PATTERN.matcher(input);
                    if (input.trim().equals(EVENT_COMMAND)) {
                        throw BosException.emptyDescription(EVENT_COMMAND);
                    }
                    if (!eventMatcher.matches()) {
                        throw BosException.invalidFormat("event DESCRIPTION /from START /to END");
                    }
                    if (eventMatcher.group("title").isBlank()) {
                        throw BosException.emptyDescription(EVENT_COMMAND);
                    }
                    if (eventMatcher.group("from").isBlank() || eventMatcher.group("to").isBlank()) {
                        throw BosException.invalidFormat("event DESCRIPTION /from START /to END");
                    }
                    ensureTaskListHasSpace(taskCount);
                    Task task = new Event(eventMatcher.group("title").trim(),
                            eventMatcher.group("from").trim(), eventMatcher.group("to").trim());
                    tasks[taskCount++] = task;
                    printTaskAdded(task, taskCount);
                } else {
                    throw BosException.unknownCommand();
                }
            } catch (BosException exception) {
                System.out.println(INDENT + "OOPS!!! " + exception.getMessage());
            }

            System.out.println(DIVIDER);
        }
    }

    /**
     * Checks whether the input starts with a complete command word.
     */
    private static boolean isCommand(String input, String command) {
        return input.equals(command) || input.startsWith(command + " ");
    }

    /**
     * Reads and validates the task number in a mark or unmark command.
     */
    private static int getTaskIndex(String input, Pattern pattern, int taskCount) throws BosException {
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            throw BosException.invalidTaskNumber();
        }

        int taskIndex;
        try {
            taskIndex = Integer.parseInt(matcher.group(1)) - 1;
        } catch (NumberFormatException exception) {
            throw BosException.invalidTaskNumber();
        }

        if (taskIndex < 0 || taskIndex >= taskCount) {
            throw BosException.taskNotFound();
        }
        return taskIndex;
    }

    /**
     * Ensures that adding another task will not overflow the fixed-size array.
     */
    private static void ensureTaskListHasSpace(int taskCount) throws BosException {
        if (taskCount >= MAX_TASKS) {
            throw BosException.taskListFull();
        }
    }

    /**
     * Prints the standard confirmation after a task is added.
     */
    private static void printTaskAdded(Task task, int taskCount) {
        System.out.println(INDENT + "Got it. I've added this task:");
        System.out.println(INDENT + "  " + task);
        System.out.println(INDENT + "Now you have " + taskCount + " tasks in the list.");
    }
}
