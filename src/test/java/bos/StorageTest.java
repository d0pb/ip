package bos;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests conversion between tasks and their text-file storage records.
 */
public class StorageTest {

    @TempDir
    private Path temporaryDirectory;

    @Test
    public void saveTasks_allTaskTypes_expectedStorageRecords() throws IOException {
        Task todo = new TodoTask("read book");
        todo.markAsDone();
        Task deadline = new Deadline("return book", "2026-09-12 1800");
        Task event = new Event("project meeting", "Monday", "Tuesday");
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(taskFile.toString());

        storage.saveTasks(List.of(todo, deadline, event));

        assertIterableEquals(
                List.of(
                        "T | 1 | read book",
                        "D | 0 | return book | 2026-09-12 1800",
                        "E | 0 | project meeting | Monday | Tuesday"),
                Files.readAllLines(taskFile));
    }

    @Test
    public void loadTasks_validRecords_equivalentTasksReturned() throws IOException, BosException {
        List<String> storedRecords = List.of(
                "T | 1 | read book",
                "D | 0 | return book | 2026-09-12 1800",
                "E | 0 | project meeting | Monday | Tuesday");
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Files.write(taskFile, storedRecords);
        Storage storage = new Storage(taskFile.toString());

        List<String> reloadedRecords = storage.loadTasks().stream()
                .map(Task::formatForStorage)
                .toList();

        assertIterableEquals(storedRecords, reloadedRecords);
    }
}
