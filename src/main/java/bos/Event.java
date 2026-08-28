package bos;

/**
 * Represents a task that takes place between two times.
 */
public class Event extends Task {
    private final Object startTime;
    private final Object endTime;

    /**
     * Creates an event, parsing times written as {@code yyyy-MM-dd HHmm}.
     *
     * @param title event description.
     * @param startTime event start date-time or free-form text.
     * @param endTime event end date-time or free-form text.
     */
    public Event(String title, String startTime, String endTime) {
        this(title, Parser.parseDateTime(startTime), Parser.parseDateTime(endTime));
    }

    /**
     * Creates an event from values that have already been interpreted.
     *
     * @param title event description.
     * @param startTime parsed start date-time or free-form text.
     * @param endTime parsed end date-time or free-form text.
     */
    Event(String title, Object startTime, Object endTime) {
        super(title);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Returns a readable representation of this event and its time range.
     *
     * @return formatted event for display
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + Parser.formatDateTimeForDisplay(this.startTime)
                + " to: " + Parser.formatDateTimeForDisplay(this.endTime) + ")";
    }

    /**
     * Converts this event into the text-file storage format.
     *
     * @return storage representation of this event
     */
    @Override
    public String formatForStorage() {
        String mark = this.isDone ? "1" : "0";
        return "E | " + mark + " | " + this.description + " | "
                + Parser.formatDateTimeForStorage(this.startTime) + " | "
                + Parser.formatDateTimeForStorage(this.endTime);
    }
}
