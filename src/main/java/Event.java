public class Event extends Task {
    String from;
    String to;

    public Event(String title, String from, String to) {
        super(title);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + this.from + " to: " + this.to + ")";
    }

    @Override
    public String toStorageFormat() {
        String mark = this.isDone ? "1" : "0";
        return "E | " + mark + " | " + this.description + " | " + this.from + " | " + this.to;
    }
}