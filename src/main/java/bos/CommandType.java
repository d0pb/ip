package bos;

/**
 * Represents a command that Bos can recognise.
 */
public enum CommandType {
    BYE("bye"),
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    UNKNOWN("");

    private final String keyword;

    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the word used to enter this command.
     *
     * @return command keyword
     */
    public String getKeyword() {
        return keyword;
    }

}
