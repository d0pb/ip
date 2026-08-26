public class ToDo extends Task {
    public ToDo(String title) {
        super(title);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public String toStorageFormat() {
        String mark = this.isDone ? "1" : "0";
        return "T | " + mark + " | " + this.description;
    }
}