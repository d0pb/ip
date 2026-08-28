package bos;

/**
 * Represents a task without an associated date or time.
 */
public class ToDo extends Task {

    /**
     * Creates an uncompleted todo with the given description.
     *
     * @param title task description
     */
    public ToDo(String title) {
        super(title);
    }

    /**
     * Returns a readable representation of this todo.
     *
     * @return formatted todo for display
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /**
     * Converts this todo into the text-file storage format.
     *
     * @return storage representation of this todo
     */
    @Override
    public String toStorageFormat() {
        String mark = this.isDone ? "1" : "0";
        return "T | " + mark + " | " + this.description;
    }
}
