/**
 * Represents an error caused by an invalid command entered by the user.
 */
public class BosException extends Exception {
    /**
     * Creates an exception with the message that Bos should show to the user.
     *
     * @param message explanation of the input error
     */
    public BosException(String message) {
        super(message);
    }

    /**
     * Creates an error for a task command with no description.
     *
     * @param commandType task-creation command with no description
     * @return exception describing the missing description
     */
    public static BosException emptyDescription(CommandType commandType) {
        String taskType = commandType.getKeyword();
        String article = commandType == CommandType.EVENT ? "an" : "a";
        return new BosException("The description of " + article + " " + taskType + " cannot be empty.");
    }

    /**
     * Creates an error for a command that does not follow its required format.
     *
     * @param usage example of the correct command format
     * @return exception describing the correct format
     */
    public static BosException invalidFormat(String usage) {
        return new BosException("Please use this command format: " + usage);
    }

    /**
     * Creates an error for a task number that is missing or is not an integer.
     *
     * @return exception describing the invalid task number
     */
    public static BosException invalidTaskNumber() {
        return new BosException("The task number must be a whole number.");
    }

    /**
     * Creates an error for a task number outside the current task list.
     *
     * @return exception describing the missing task
     */
    public static BosException taskNotFound() {
        return new BosException("There is no task with that number.");
    }

    /**
     * Creates an error for an unrecognised command.
     *
     * @return exception describing the unknown command
     */
    public static BosException unknownCommand() {
        return new BosException("I'm sorry, but I don't know what that means :-(");
    }

}
