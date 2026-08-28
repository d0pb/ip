package bos;

import java.io.IOException;

/**
 * Runs the Bos chatbot and stores tasks entered during the current session.
 */
public class Bos {
    private static final String TEXT_FILE_PATH = "data/tasks.txt";
    private static final Storage STORAGE = new Storage(TEXT_FILE_PATH);

    /**
     * Starts Bos and processes input until the user enters {@code bye}.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showGreeting();
        processCommands(ui);
        ui.showExit();
    }

    /**
     * Stores ordinary input as tasks and displays stored tasks for {@code list}.
     *
     * @param ui user interface used to read commands and show responses.
     */
    private static void processCommands(Ui ui) {
        TaskList tasks = new TaskList();

        try {
            tasks = new TaskList(STORAGE.loadTasks());
        } catch (BosException exception) {
            ui.showLoadingError(exception.getMessage());
        } catch (IOException | SecurityException exception) {
            ui.showFileAccessError();
        }

        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            ui.showDivider();

            try {
                CommandType commandType = Parser.parseCommandType(input);
                switch (commandType) {
                    case BYE:
                        saveTasksToFile(tasks, ui);
                        return;
                    case LIST:
                        ui.showTaskList(tasks.getTasks());
                        break;
                    case FIND: {
                        String keyword = Parser.parseFindKeyword(input);
                        ui.showMatchingTasks(tasks.find(keyword));
                        break;
                    }
                    case MARK: {
                        int taskIndex = Parser.parseTaskIndex(input, commandType, tasks.getSize());
                        Task task = tasks.mark(taskIndex);
                        saveTasksToFile(tasks, ui);
                        ui.showTaskMarked(task);
                        break;
                    }
                    case UNMARK: {
                        int taskIndex = Parser.parseTaskIndex(input, commandType, tasks.getSize());
                        Task task = tasks.unmark(taskIndex);
                        saveTasksToFile(tasks, ui);
                        ui.showTaskUnmarked(task);
                        break;
                    }
                    case DELETE: {
                        int taskIndex = Parser.parseTaskIndex(input, commandType, tasks.getSize());
                        Task removedTask = tasks.delete(taskIndex);
                        saveTasksToFile(tasks, ui);
                        ui.showTaskDeleted(removedTask, tasks.getSize());
                        break;
                    }
                    case TODO, DEADLINE, EVENT: {
                        Task task = Parser.parseTask(input, commandType);
                        tasks.add(task);
                        saveTasksToFile(tasks, ui);
                        ui.showTaskAdded(task, tasks.getSize());
                        break;
                    }
                    case UNKNOWN:
                        throw BosException.createUnknownCommandException();
                }
            } catch (BosException exception) {
                ui.showError(exception.getMessage());
            }

            ui.showDivider();
        }

        saveTasksToFile(tasks, ui);
    }

    /**
     * Saves the current task list and reports failures without stopping Bos.
     */
    private static void saveTasksToFile(TaskList tasks, Ui ui) {
        try {
            STORAGE.saveTasks(tasks.getTasks());
        } catch (IOException | SecurityException exception) {
            ui.showSavingError();
        }
    }

}
