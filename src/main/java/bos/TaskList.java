package bos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Owns the collection of tasks and provides operations that modify it.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * <p>The tasks are copied so callers cannot change this task list by
     * retaining and modifying the original list.</p>
     *
     * @param tasks initial tasks
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given zero-based index.
     *
     * @param taskIndex index of the task to remove
     * @return removed task
     */
    public Task delete(int taskIndex) {
        return tasks.remove(taskIndex);
    }

    /**
     * Marks and returns the task at the given zero-based index.
     *
     * @param taskIndex index of the task to mark
     * @return marked task
     */
    public Task mark(int taskIndex) {
        Task task = tasks.get(taskIndex);
        task.markAsDone();
        return task;
    }

    /**
     * Unmarks and returns the task at the given zero-based index.
     *
     * @param taskIndex index of the task to unmark
     * @return unmarked task
     */
    public Task unmark(int taskIndex) {
        Task task = tasks.get(taskIndex);
        task.markAsNotDone();
        return task;
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Provides a read-only view for displaying or saving the tasks.
     *
     * @return unmodifiable view of the tasks
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }
}
