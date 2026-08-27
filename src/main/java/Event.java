/**
 * Represents a task that takes place between two times.
 */
public class Event extends Task {
    Object from;
    Object to;

    /**
     * Creates an event, parsing times written as {@code yyyy-MM-dd HHmm}.
     *
     * @param title event description
     * @param from event start date-time or free-form text
     * @param to event end date-time or free-form text
     */
    public Event(String title, String from, String to) {
        super(title);
        this.from = DateTimeParser.parse(from);
        this.to = DateTimeParser.parse(to);
    }

    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + DateTimeParser.formatForDisplay(this.from)
                + " to: " + DateTimeParser.formatForDisplay(this.to) + ")";
    }

    @Override
    public String toStorageFormat() {
        String mark = this.isDone ? "1" : "0";
        return "E | " + mark + " | " + this.description + " | "
                + DateTimeParser.formatForStorage(this.from) + " | "
                + DateTimeParser.formatForStorage(this.to);
    }
}
