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

    @Test
    public void constructor_noInitialTasks_emptyListCreated() {
        TaskList taskList = new TaskList();

        assertEquals(0, taskList.size());
        assertEquals(List.of(), taskList.asList());
    }

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

    @Test
    public void add_task_taskAppendedAndSizeIncreased() {
        Task firstTask = new ToDo("read book");
        Task secondTask = new ToDo("return book");
        TaskList taskList = new TaskList(List.of(firstTask));

        taskList.add(secondTask);

        assertEquals(2, taskList.size());
        assertIterableEquals(List.of(firstTask, secondTask), taskList.asList());
    }

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

    @Test
    public void delete_indexOutsideList_exceptionThrown() {
        TaskList taskList = new TaskList(List.of(new ToDo("read book")));

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.delete(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.delete(1));
    }

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

    @Test
    public void mark_indexOutsideList_exceptionThrown() {
        TaskList taskList = new TaskList(List.of(new ToDo("read book")));

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.mark(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.mark(1));
    }

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

    @Test
    public void unmark_indexOutsideList_exceptionThrown() {
        TaskList taskList = new TaskList(List.of(new ToDo("read book")));

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.unmark(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.unmark(1));
    }

    @Test
    public void asList_taskAddedAfterViewCreated_viewUpdated() {
        TaskList taskList = new TaskList();
        List<Task> taskView = taskList.asList();
        Task task = new ToDo("read book");

        taskList.add(task);

        assertIterableEquals(List.of(task), taskView);
    }

    @Test
    public void asList_modificationAttempt_exceptionThrown() {
        TaskList taskList = new TaskList(List.of(new ToDo("read book")));
        List<Task> taskView = taskList.asList();

        assertThrows(UnsupportedOperationException.class,
                () -> taskView.add(new ToDo("return book")));
        assertThrows(UnsupportedOperationException.class, () -> taskView.remove(0));
    }
}
