/**
 * Represents a task that takes place between two times.
 */
public class Event extends Task {
    private final Object from;
    private final Object to;

    /**
     * Creates an event, parsing times written as {@code yyyy-MM-dd HHmm}.
     *
     * @param title event description
     * @param from event start date-time or free-form text
     * @param to event end date-time or free-form text
     */
    public Event(String title, String from, String to) {
        this(title, Parser.parseDateTime(from), Parser.parseDateTime(to));
    }

    /**
     * Creates an event from values that have already been interpreted.
     *
     * @param title event description
     * @param from parsed start date-time or free-form text
     * @param to parsed end date-time or free-form text
     */
    Event(String title, Object from, Object to) {
        super(title);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + Parser.formatDateTimeForDisplay(this.from)
                + " to: " + Parser.formatDateTimeForDisplay(this.to) + ")";
    }

    @Override
    public String toStorageFormat() {
        String mark = this.isDone ? "1" : "0";
        return "E | " + mark + " | " + this.description + " | "
                + Parser.formatDateTimeForStorage(this.from) + " | "
                + Parser.formatDateTimeForStorage(this.to);
    }
}
