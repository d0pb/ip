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
}