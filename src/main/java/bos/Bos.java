package bos;

import java.io.IOException;
import java.util.List;

/**
 * Interprets commands for the Bos chatbot and stores its tasks.
 */
public class Bos {
    private static final String DEFAULT_FILE_PATH = "data/tasks.txt";
    private static final String GREETING = "Hello! I'm Bos.\nWhat can I do for you?";
    private static final String FAREWELL = "Bye. Hope to see you again soon!";

    private final Storage storage;
    private final TaskList tasks;
    private final String loadingMessage;

    /**
     * Creates Bos using the default task storage file.
     */
    public Bos() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Creates Bos using the specified task storage file.
     *
     * @param filePath path of the task storage file.
     */
    Bos(String filePath) {
        storage = new Storage(filePath);

        TaskList loadedTasks;
        String loadResult = "";
        try {
            loadedTasks = new TaskList(storage.loadTasks());
        } catch (BosException exception) {
            loadedTasks = new TaskList();
            loadResult = "tasks.txt is corrupted (" + exception.getMessage() + "), resetting...";
        } catch (IOException | SecurityException exception) {
            loadedTasks = new TaskList();
            loadResult = "Cannot access the task file, recording from scratch...";
        }
        tasks = loadedTasks;
        loadingMessage = loadResult;
    }

    /**
     * Starts Bos and processes input until the user enters {@code bye}.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        Bos bos = new Bos();
        Ui ui = new Ui();
        ui.showGreeting(bos.getGreeting());
        processCommands(bos, ui);
    }

    /**
     * Returns Bos's greeting and any warning raised while loading saved tasks.
     *
     * @return greeting to show when the application starts.
     */
    public String getGreeting() {
        if (loadingMessage.isEmpty()) {
            return GREETING;
        }
        return GREETING + "\n" + loadingMessage;
    }

    /**
     * Executes one user command and returns the response to display.
     *
     * @param input command entered by the user.
     * @return Bos's response to the command.
     */
    public String getResponse(String input) {
        try {
            CommandType commandType = Parser.parseCommandType(input);
            return switch (commandType) {
                case BYE -> prependSavingError(FAREWELL);
                case LIST -> formatTaskList("Here are the tasks in your list:", tasks.getTasks());
                case FIND -> formatTaskList(
                        "Here are the matching tasks in your list:",
                        tasks.find(Parser.parseFindKeyword(input)));
                case MARK -> markTask(input, commandType);
                case UNMARK -> unmarkTask(input, commandType);
                case DELETE -> deleteTask(input, commandType);
                case TODO, DEADLINE, EVENT -> addTask(input, commandType);
                case UNKNOWN -> throw BosException.createUnknownCommandException();
            };
        } catch (BosException exception) {
            return "OOPS!!! " + exception.getMessage();
        }
    }

    /**
     * Reads commands from the terminal and displays Bos's responses.
     */
    private static void processCommands(Bos bos, Ui ui) {
        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            ui.showDivider();
            ui.showResponse(bos.getResponse(input));
            ui.showDivider();

            if (Parser.parseCommandType(input) == CommandType.BYE) {
                return;
            }
        }

        ui.showResponse(bos.getResponse(CommandType.BYE.getKeyword()));
        ui.showDivider();
    }

    /**
     * Adds a task described by the command.
     */
    private String addTask(String input, CommandType commandType) throws BosException {
        Task task = Parser.parseTask(input, commandType);
        tasks.add(task);
        String response = "Got it. I've added this task:\n  " + task
                + "\nNow you have " + tasks.getSize() + " tasks in the list.";
        return prependSavingError(response);
    }

    /**
     * Marks the task selected by the command.
     */
    private String markTask(String input, CommandType commandType) throws BosException {
        int taskIndex = Parser.parseTaskIndex(input, commandType, tasks.getSize());
        Task task = tasks.mark(taskIndex);
        return prependSavingError("Nice! I've marked this task as done:\n" + task);
    }

    /**
     * Unmarks the task selected by the command.
     */
    private String unmarkTask(String input, CommandType commandType) throws BosException {
        int taskIndex = Parser.parseTaskIndex(input, commandType, tasks.getSize());
        Task task = tasks.unmark(taskIndex);
        return prependSavingError("OK, I've marked this task as not done yet:\n" + task);
    }

    /**
     * Deletes the task selected by the command.
     */
    private String deleteTask(String input, CommandType commandType) throws BosException {
        int taskIndex = Parser.parseTaskIndex(input, commandType, tasks.getSize());
        Task task = tasks.delete(taskIndex);
        String response = "Noted. I've removed this task:\n  " + task
                + "\nNow you have " + tasks.getSize() + " tasks in the list.";
        return prependSavingError(response);
    }

    /**
     * Formats tasks as a heading followed by a one-based numbered list.
     */
    private String formatTaskList(String heading, List<Task> displayedTasks) {
        StringBuilder response = new StringBuilder(heading);
        for (int i = 0; i < displayedTasks.size(); i++) {
            response.append('\n')
                    .append(i + 1)
                    .append('.')
                    .append(displayedTasks.get(i));
        }
        return response.toString();
    }

    /**
     * Saves all tasks and prepends a warning to the response if saving fails.
     */
    private String prependSavingError(String response) {
        try {
            storage.saveTasks(tasks.getTasks());
            return response;
        } catch (IOException | SecurityException exception) {
            return "OOPS!!! Unable to save tasks onto the hard disk.\n" + response;
        }
    }
}
