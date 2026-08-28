package bos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests the collection operations provided by {@link TaskList}.
 */
public class TaskListTest {

    /**
     * Verifies that the no-argument constructor creates an empty task list.
     */
    @Test
    public void constructor_noInitialTasks_emptyListCreated() {
        TaskList taskList = new TaskList();

        assertEquals(0, taskList.size());
        assertEquals(List.of(), taskList.asList());
    }

    /**
     * Verifies that changing the source list does not change the constructed task list.
     */
    @Test
    public void constructor_initialTasks_sourceListChanged_taskListUnaffected() {
        Task task = new ToDo("read book");
        List<Task> initialTasks = new ArrayList<>();
        initialTasks.add(task);
        TaskList taskList = new TaskList(initialTasks);

        initialTasks.clear();

        assertEquals(1, taskList.size());
        assertSame(task, taskList.asList().get(0));
    }

    /**
     * Verifies that adding a task appends it and increases the list size.
     */
    @Test
    public void add_task_taskAppendedAndSizeIncreased() {
        Task firstTask = new ToDo("read book");
        Task secondTask = new ToDo("return book");
        TaskList taskList = new TaskList(List.of(firstTask));

        taskList.add(secondTask);

        assertEquals(2, taskList.size());
        assertIterableEquals(List.of(firstTask, secondTask), taskList.asList());
    }

    /**
     * Verifies that deleting a valid index removes and returns the selected task.
     */
    @Test
    public void delete_validIndex_taskRemovedAndReturned() {
        Task firstTask = new ToDo("read book");
        Task taskToDelete = new ToDo("return book");
        Task lastTask = new ToDo("buy book");
        TaskList taskList = new TaskList(List.of(firstTask, taskToDelete, lastTask));

        Task deletedTask = taskList.delete(1);

        assertSame(taskToDelete, deletedTask);
        assertEquals(2, taskList.size());
        assertIterableEquals(List.of(firstTask, lastTask), taskList.asList());
    }

    /**
     * Verifies that deleting an index outside the list fails.
     */
    @Test
    public void delete_indexOutsideList_exceptionThrown() {
        TaskList taskList = new TaskList(List.of(new ToDo("read book")));

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.delete(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.delete(1));
    }

    /**
     * Verifies that marking a valid index completes and returns the selected task.
     */
    @Test
    public void mark_validIndex_selectedTaskMarkedAndReturned() {
        Task taskToMark = new ToDo("read book");
        Task otherTask = new ToDo("return book");
        TaskList taskList = new TaskList(List.of(taskToMark, otherTask));

        Task markedTask = taskList.mark(0);

        assertSame(taskToMark, markedTask);
        assertEquals("X", taskToMark.getStatusIcon());
        assertEquals(" ", otherTask.getStatusIcon());
    }

    /**
     * Verifies that marking an index outside the list fails.
     */
    @Test
    public void mark_indexOutsideList_exceptionThrown() {
        TaskList taskList = new TaskList(List.of(new ToDo("read book")));

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.mark(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.mark(1));
    }

    /**
     * Verifies that unmarking a valid index resets and returns the selected task.
     */
    @Test
    public void unmark_validIndex_selectedTaskUnmarkedAndReturned() {
        Task taskToUnmark = new ToDo("read book");
        Task otherTask = new ToDo("return book");
        taskToUnmark.markAsDone();
        otherTask.markAsDone();
        TaskList taskList = new TaskList(List.of(taskToUnmark, otherTask));

        Task unmarkedTask = taskList.unmark(0);

        assertSame(taskToUnmark, unmarkedTask);
        assertEquals(" ", taskToUnmark.getStatusIcon());
        assertEquals("X", otherTask.getStatusIcon());
    }

    /**
     * Verifies that unmarking an index outside the list fails.
     */
    @Test
    public void unmark_indexOutsideList_exceptionThrown() {
        TaskList taskList = new TaskList(List.of(new ToDo("read book")));

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.unmark(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.unmark(1));
    }

    /**
     * Verifies that a previously obtained view reflects tasks added later.
     */
    @Test
    public void asList_taskAddedAfterViewCreated_viewUpdated() {
        TaskList taskList = new TaskList();
        List<Task> taskView = taskList.asList();
        Task task = new ToDo("read book");

        taskList.add(task);

        assertIterableEquals(List.of(task), taskView);
    }

    /**
     * Verifies that callers cannot modify the task list through its public view.
     */
    @Test
    public void asList_modificationAttempt_exceptionThrown() {
        TaskList taskList = new TaskList(List.of(new ToDo("read book")));
        List<Task> taskView = taskList.asList();

        assertThrows(UnsupportedOperationException.class,
                () -> taskView.add(new ToDo("return book")));
        assertThrows(UnsupportedOperationException.class, () -> taskView.remove(0));
    }
}
