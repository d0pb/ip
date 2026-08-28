package bos;

/**
 * Represents a task without a deadline or scheduled time.
 */
public class TodoTask extends Task {
    /**
     * Creates a todo task with the given description.
     *
     * @param title task description.
     */
    public TodoTask(String title) {
        super(title);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public String formatForStorage() {
        String mark = this.isDone ? "1" : "0";
        return "T | " + mark + " | " + this.description;
    }
}
