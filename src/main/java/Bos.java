import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Runs the Bos chatbot and stores tasks entered during the current session.
 */
public class Bos {
    private static final String INDENT = "     ";
    private static final String DIVIDER = INDENT + "____________________________________________________________";
    private static final String BANNER = " ____            \n"
                + "| __ )  ___  ___ \n"
                + "|  _ \\ / _ \\/ __|\n"
                + "| |_) | (_) \\__ \\\n"
                + "|____/ \\___/|___/";
    private static final Pattern MARK_PATTERN = Pattern.compile("^mark\\s+(\\d+)\\s*$");
    private static final Pattern UNMARK_PATTERN = Pattern.compile("^unmark\\s+(\\d+)\\s*$");
    private static final Pattern DELETE_PATTERN = Pattern.compile("^delete\\s+(\\d+)\\s*$");
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
        ArrayList<Task> tasks = new ArrayList<>();

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            System.out.println(DIVIDER);

            try {
                CommandType commandType = CommandType.fromInput(input);
                switch (commandType) {
                case BYE:
                    return;
                case LIST:
                    System.out.println(INDENT + "Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println(INDENT + (i + 1) + "." + tasks.get(i));
                    }
                    break;
                case MARK: {
                    int taskIndex = getTaskIndex(input, MARK_PATTERN, tasks.size());
                    Task task = tasks.get(taskIndex);
                    task.markAsDone();
                    System.out.println(INDENT + "Nice! I've marked this task as done:");
                    System.out.println(INDENT + task);
                    break;
                }
                case UNMARK: {
                    int taskIndex = getTaskIndex(input, UNMARK_PATTERN, tasks.size());
                    Task task = tasks.get(taskIndex);
                    task.markAsNotDone();
                    System.out.println(INDENT + "OK, I've marked this task as not done yet:");
                    System.out.println(INDENT + task);
                    break;
                }
                case DELETE: {
                    int taskIndex = getTaskIndex(input, DELETE_PATTERN, tasks.size());
                    Task removedTask = tasks.remove(taskIndex);
                    System.out.println(INDENT + "Noted. I've removed this task:");
                    System.out.println(INDENT + "  " + removedTask);
                    System.out.println(INDENT + "Now you have " + tasks.size() + " tasks in the list.");
                    break;
                }
                case TODO: {
                    Matcher todoMatcher = TODO_PATTERN.matcher(input);
                    if (!todoMatcher.matches() || todoMatcher.group(1).isBlank()) {
                        throw BosException.emptyDescription(CommandType.TODO);
                    }
                    Task task = new ToDo(todoMatcher.group(1).trim());
                    tasks.add(task);
                    printTaskAdded(task, tasks.size());
                    break;
                }
                case DEADLINE: {
                    Matcher deadlineMatcher = DEADLINE_PATTERN.matcher(input);
                    if (input.trim().equals(CommandType.DEADLINE.getKeyword())) {
                        throw BosException.emptyDescription(CommandType.DEADLINE);
                    }
                    if (!deadlineMatcher.matches()) {
                        throw BosException.invalidFormat("deadline DESCRIPTION /by DATE");
                    }
                    if (deadlineMatcher.group("title").isBlank()) {
                        throw BosException.emptyDescription(CommandType.DEADLINE);
                    }
                    if (deadlineMatcher.group("date").isBlank()) {
                        throw BosException.invalidFormat("deadline DESCRIPTION /by DATE");
                    }
                    Task task = new Deadline(deadlineMatcher.group("title").trim(),
                            deadlineMatcher.group("date").trim());
                    tasks.add(task);
                    printTaskAdded(task, tasks.size());
                    break;
                }
                case EVENT: {
                    Matcher eventMatcher = EVENT_PATTERN.matcher(input);
                    if (input.trim().equals(CommandType.EVENT.getKeyword())) {
                        throw BosException.emptyDescription(CommandType.EVENT);
                    }
                    if (!eventMatcher.matches()) {
                        throw BosException.invalidFormat("event DESCRIPTION /from START /to END");
                    }
                    if (eventMatcher.group("title").isBlank()) {
                        throw BosException.emptyDescription(CommandType.EVENT);
                    }
                    if (eventMatcher.group("from").isBlank() || eventMatcher.group("to").isBlank()) {
                        throw BosException.invalidFormat("event DESCRIPTION /from START /to END");
                    }
                    Task task = new Event(eventMatcher.group("title").trim(),
                            eventMatcher.group("from").trim(), eventMatcher.group("to").trim());
                    tasks.add(task);
                    printTaskAdded(task, tasks.size());
                    break;
                }
                case UNKNOWN:
                    throw BosException.unknownCommand();
                }
            } catch (BosException exception) {
                System.out.println(INDENT + "OOPS!!! " + exception.getMessage());
            }

            System.out.println(DIVIDER);
        }
    }

    /**
     * Reads and validates a task number from a command.
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
     * Prints the standard confirmation after a task is added.
     */
    private static void printTaskAdded(Task task, int taskCount) {
        System.out.println(INDENT + "Got it. I've added this task:");
        System.out.println(INDENT + "  " + task);
        System.out.println(INDENT + "Now you have " + taskCount + " tasks in the list.");
    }
}
