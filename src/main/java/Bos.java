import java.io.IOException;
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

    private static final String TEXT_FILE_PATH = "data/tasks.txt";
    private static final Storage STORAGE = new Storage(TEXT_FILE_PATH);

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

        try {
            tasks.addAll(STORAGE.loadTasks());
        } catch (BosException exception) {
            System.out.println("tasks.txt is corrupted (" + exception.getMessage() + "), resetting...");
        } catch (IOException | SecurityException exception) {
            System.out.println("Cannot access the task file, recording from scratch...");
        }

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            System.out.println(DIVIDER);

            try {
                CommandType commandType = CommandType.fromInput(input);
                switch (commandType) {
                case BYE:
                    saveTasksToFile(tasks);
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
                    saveTasksToFile(tasks);
                    System.out.println(INDENT + "Nice! I've marked this task as done:");
                    System.out.println(INDENT + task);
                    break;
                }
                case UNMARK: {
                    int taskIndex = getTaskIndex(input, UNMARK_PATTERN, tasks.size());
                    Task task = tasks.get(taskIndex);
                    task.markAsNotDone();
                    saveTasksToFile(tasks);
                    System.out.println(INDENT + "OK, I've marked this task as not done yet:");
                    System.out.println(INDENT + task);
                    break;
                }
                case DELETE: {
                    int taskIndex = getTaskIndex(input, DELETE_PATTERN, tasks.size());
                    Task removedTask = tasks.remove(taskIndex);
                    saveTasksToFile(tasks);
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
                    String title = todoMatcher.group(1).trim();
                    validateStorageFields(title);
                    Task task = new ToDo(title);
                    tasks.add(task);
                    saveTasksToFile(tasks);
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
                    String title = deadlineMatcher.group("title").trim();
                    String date = deadlineMatcher.group("date").trim();
                    validateStorageFields(title, date);
                    Task task = new Deadline(title, date);
                    tasks.add(task);
                    saveTasksToFile(tasks);
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
                    String title = eventMatcher.group("title").trim();
                    String from = eventMatcher.group("from").trim();
                    String to = eventMatcher.group("to").trim();
                    validateStorageFields(title, from, to);
                    Task task = new Event(title, from, to);
                    tasks.add(task);
                    saveTasksToFile(tasks);
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

        saveTasksToFile(tasks);

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
     * Saves the current task list and reports failures without stopping Bos.
     */
    private static void saveTasksToFile(ArrayList<Task> tasks) {
        try {
            STORAGE.saveTasks(tasks);
        } catch (IOException | SecurityException exception) {
            System.out.println(INDENT + "OOPS!!! Unable to save tasks onto the hard disk.");
        }
    }

    /**
     * Rejects the storage delimiter because it cannot be represented unambiguously.
     */
    private static void validateStorageFields(String... fields) throws BosException {
        for (String field : fields) {
            if (field.contains("|")) {
                throw new BosException("Task details cannot contain the | character.");
            }
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
