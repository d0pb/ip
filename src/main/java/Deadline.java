/**
 * Represents a task that should be completed by a particular time.
 */
public class Deadline extends Task {
    Object deadline;

    /**
     * Creates a deadline, parsing date-times written as {@code yyyy-MM-dd HHmm}.
     *
     * @param title task description
     * @param deadline deadline date-time or free-form text
     */
    public Deadline(String title, String deadline) {
        super(title);
        this.deadline = DateTimeParser.parse(deadline);
    }

    @Override
    public String toString() {
        return "[D]" + super.toString()
                + " (by: " + DateTimeParser.formatForDisplay(this.deadline) + ")";
    }

    @Override
    public String toStorageFormat() {
        String mark = this.isDone ? "1" : "0";
        return "D | " + mark + " | " + this.description + " | "
                + DateTimeParser.formatForStorage(this.deadline);
    }
}
