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

    /**
     * Finds the command at the beginning of the given input.
     *
     * @param input complete line entered by the user
     * @return matching command type, or {@link #UNKNOWN} if there is no match
     */
    public static CommandType fromInput(String input) {
        for (CommandType commandType : values()) {
            if (commandType != UNKNOWN && commandType.matches(input)) {
                return commandType;
            }
        }
        return UNKNOWN;
    }

    private boolean matches(String input) {
        return input.equals(keyword) || input.startsWith(keyword + " ");
    }
}
