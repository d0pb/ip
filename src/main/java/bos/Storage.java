package bos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads tasks from a text file on the hard disk. Writes tasks to the text file.
 */
public class Storage {
    private final File taskFile;

    /**
     * Creates storage that uses the file at the given path.
     *
     * @param filePath path of the task data file.
     */
    public Storage(String filePath) {
        this.taskFile = new File(filePath);
    }

    /**
     * Loads all tasks from the data file.
     *
     * <p>If the file or its parent directory does not exist, this method creates
     * them and returns an empty task list.</p>
     *
     * @return tasks stored in the data file.
     * @throws IOException if the file cannot be created or read.
     * @throws BosException if a line does not follow the storage format.
     */
    public ArrayList<Task> loadTasks() throws IOException, BosException {
        createFileIfMissing();
        ArrayList<Task> tasks = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(taskFile))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                tasks.add(parseTask(line, lineNumber));
            }
        }

        return tasks;
    }

    /**
     * Replaces the data file contents with the current task list.
     *
     * @param tasks tasks to save.
     * @throws IOException if the file cannot be created or written.
     */
    public void saveTasks(List<Task> tasks) throws IOException {
        createFileIfMissing();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(taskFile))) {
            for (Task task : tasks) {
                writer.write(task.formatForStorage());
                writer.newLine();
            }
        }
    }

    /**
     * Creates the parent directory and data file when they do not exist.
     */
    private void createFileIfMissing() throws IOException {
        File parentDirectory = taskFile.getParentFile();
        if (parentDirectory != null
                && !parentDirectory.exists()
                && !parentDirectory.mkdirs()) {
            throw new IOException("Cannot create directory: " + parentDirectory);
        }

        if (!taskFile.exists()) {
            taskFile.createNewFile();
        }
    }

    /**
     * Converts one validated storage line into a task.
     *
     * @param line stored task record.
     * @param lineNumber line number used in error messages.
     * @return task represented by the line.
     * @throws BosException if the record has invalid or missing fields.
     */
    private Task parseTask(String line, int lineNumber) throws BosException {
        String[] elements = line.split("\\|", -1);
        if (elements.length < 3) {
            throw createInvalidDataException(lineNumber, "too few fields");
        }

        String taskType = elements[0].trim();
        String status = elements[1].trim();
        String title = elements[2].trim();

        if (!status.equals("0") && !status.equals("1")) {
            throw createInvalidDataException(lineNumber, "status must be 0 or 1");
        }
        if (title.isBlank()) {
            throw createInvalidDataException(lineNumber, "task description is empty");
        }

        Task task;
        switch (taskType) {
            case "T":
                requireFieldCount(elements, 3, lineNumber);
                task = new TodoTask(title);
                break;
            case "D":
                requireFieldCount(elements, 4, lineNumber);
                String deadline = elements[3].trim();
                if (deadline.isBlank()) {
                    throw createInvalidDataException(lineNumber, "deadline is empty");
                }
                task = new Deadline(title, deadline);
                break;
            case "E":
                requireFieldCount(elements, 5, lineNumber);
                String startTime = elements[3].trim();
                String endTime = elements[4].trim();
                if (startTime.isBlank() || endTime.isBlank()) {
                    throw createInvalidDataException(lineNumber, "event time is empty");
                }
                task = new Event(title, startTime, endTime);
                break;
            default:
                throw createInvalidDataException(lineNumber, "unknown task type");
        }

        if (status.equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Checks that a record contains exactly the expected number of fields.
     */
    private void requireFieldCount(String[] elements, int expectedCount, int lineNumber)
            throws BosException {
        if (elements.length != expectedCount) {
            throw createInvalidDataException(lineNumber, "expected " + expectedCount + " fields");
        }
    }

    /**
     * Creates a consistent exception for malformed storage data.
     */
    private BosException createInvalidDataException(int lineNumber, String reason) {
        return new BosException("invalid data on line " + lineNumber + ": " + reason);
    }
}
