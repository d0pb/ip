package bos;

/**
 * Represents a task that should be completed by a particular time.
 */
public class Deadline extends Task {
    private final Object deadline;

    /**
     * Creates a deadline, parsing date-times written as {@code yyyy-MM-dd HHmm}.
     *
     * @param title task description
     * @param deadline deadline date-time or free-form text
     */
    public Deadline(String title, String deadline) {
        this(title, Parser.parseDateTime(deadline));
    }

    /**
     * Creates a deadline from a value that has already been interpreted.
     *
     * @param title task description
     * @param deadline parsed date-time or free-form text
     */
    Deadline(String title, Object deadline) {
        super(title);
        this.deadline = deadline;
    }

    /**
     * Returns a readable representation of this deadline and its due time.
     *
     * @return formatted deadline for display
     */
    @Override
    public String toString() {
        return "[D]" + super.toString()
                + " (by: " + Parser.formatDateTimeForDisplay(this.deadline) + ")";
    }

    /**
     * Converts this deadline into the text-file storage format.
     *
     * @return storage representation of this deadline
     */
    @Override
    public String toStorageFormat() {
        String mark = this.isDone ? "1" : "0";
        return "D | " + mark + " | " + this.description + " | "
                + Parser.formatDateTimeForStorage(this.deadline);
    }
}
