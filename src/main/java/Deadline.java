public class Deadline extends Task {
    String deadline;

    public Deadline(String title, String deadline) {
        super(title);
        this.deadline = deadline;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + this.deadline + ")";
    }

    @Override
    public String toStorageFormat() {
        String mark = this.isDone ? "1" : "0";
        return "D | " + mark + " | " + this.description + " | " + this.deadline;
    }
}