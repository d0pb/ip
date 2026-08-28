package bos;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Interprets user input and converts it into values that Bos can execute.
 */
public final class Parser {
    private static final String DATE_TIME_PATTERN = "uuuu-MM-dd HHmm";
    private static final String DISPLAY_DATE_TIME_PATTERN = "MMM d uuuu, h:mm a";
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern(DISPLAY_DATE_TIME_PATTERN);
    private static final Pattern MARK_PATTERN = Pattern.compile("^mark\\s+(\\d+)\\s*$");
    private static final Pattern UNMARK_PATTERN = Pattern.compile("^unmark\\s+(\\d+)\\s*$");
    private static final Pattern DELETE_PATTERN = Pattern.compile("^delete\\s+(\\d+)\\s*$");
    private static final Pattern TODO_PATTERN = Pattern.compile("^todo\\s+(.*)$");
    private static final Pattern DEADLINE_PATTERN = Pattern.compile(
            "^deadline\\s*(?<title>.*?)\\s*/by\\s*(?<date>.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern EVENT_PATTERN = Pattern.compile(
            "^event\\s*(?<title>.*?)\\s*/from\\s*(?<from>.*?)\\s*/to\\s*(?<to>.*)$",
            Pattern.CASE_INSENSITIVE);

    private Parser() {
    }

    /**
     * Identifies the command keyword at the beginning of an input line.
     *
     * @param input complete line entered by the user.
     * @return matching command type, or {@link CommandType#UNKNOWN} when there is no match.
     */
    public static CommandType parseCommandType(String input) {
        for (CommandType commandType : CommandType.values()) {
            String keyword = commandType.getKeyword();
            if (commandType != CommandType.UNKNOWN
                    && (input.equals(keyword) || input.startsWith(keyword + " "))) {
                return commandType;
            }
        }
        return CommandType.UNKNOWN;
    }

    /**
     * Reads a one-based task number and converts it to a valid list index.
     *
     * @param input complete mark, unmark, or delete command.
     * @param commandType type of task-number command being parsed.
     * @param taskCount number of tasks currently available.
     * @return zero-based index of the selected task.
     * @throws BosException if the number is invalid or does not identify a task.
     */
    public static int parseTaskIndex(String input, CommandType commandType, int taskCount)
            throws BosException {
        Pattern pattern = getTaskIndexPattern(commandType);
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            throw BosException.createInvalidTaskNumberException();
        }

        int taskIndex;
        try {
            taskIndex = Integer.parseInt(matcher.group(1)) - 1;
        } catch (NumberFormatException exception) {
            throw BosException.createInvalidTaskNumberException();
        }

        if (taskIndex < 0 || taskIndex >= taskCount) {
            throw BosException.createTaskNotFoundException();
        }
        return taskIndex;
    }

    /**
     * Converts a task-creation command into the corresponding task.
     *
     * @param input complete todo, deadline, or event command.
     * @param commandType type of task to create.
     * @return task described by the command.
     * @throws BosException if the command is incomplete or malformed.
     */
    public static Task parseTask(String input, CommandType commandType) throws BosException {
        return switch (commandType) {
            case TODO -> parseTodo(input);
            case DEADLINE -> parseDeadline(input);
            case EVENT -> parseEvent(input);
            default -> throw new IllegalArgumentException(commandType + " is not a task-creation command");
        };
    }

    /**
     * Converts a date-time in {@code yyyy-MM-dd HHmm} format into a
     * {@link LocalDateTime}. Text in any other format is retained unchanged.
     *
     * @param text user-entered or stored date-time text.
     * @return a {@code LocalDateTime} when parsing succeeds, or the original string otherwise.
     */
    public static Object parseDateTime(String text) {
        try {
            return LocalDateTime.parse(text, DATE_TIME_FORMAT);
        } catch (DateTimeParseException exception) {
            return text;
        }
    }

    /**
     * Converts a date-time value to the consistent format used in {@code tasks.txt}.
     * Plain text is returned unchanged so descriptions such as "tomorrow evening"
     * remain supported.
     *
     * @param value parsed date-time or plain text.
     * @return value suitable for saving in the data file.
     */
    public static String formatDateTimeForStorage(Object value) {
        if (value instanceof LocalDateTime dateTime) {
            return dateTime.format(DATE_TIME_FORMAT);
        }
        return value.toString();
    }

    /**
     * Formats a parsed date-time value for a readable chatbot response.
     *
     * @param value parsed date-time or plain text.
     * @return readable date-time text.
     */
    public static String formatDateTimeForDisplay(Object value) {
        if (value instanceof LocalDateTime dateTime) {
            return dateTime.format(DISPLAY_DATE_TIME_FORMAT);
        }
        return value.toString();
    }

    /**
     * Selects the format used by a command that refers to a task number.
     */
    private static Pattern getTaskIndexPattern(CommandType commandType) {
        return switch (commandType) {
            case MARK -> MARK_PATTERN;
            case UNMARK -> UNMARK_PATTERN;
            case DELETE -> DELETE_PATTERN;
            default -> throw new IllegalArgumentException(commandType + " does not use a task number");
        };
    }

    /**
     * Parses a todo command after its command type has been identified.
     */
    private static Task parseTodo(String input) throws BosException {
        Matcher matcher = TODO_PATTERN.matcher(input);
        if (!matcher.matches() || matcher.group(1).isBlank()) {
            throw BosException.createEmptyDescriptionException(CommandType.TODO);
        }

        String title = matcher.group(1).trim();
        validateStorageFields(title);
        return new TodoTask(title);
    }

    /**
     * Parses a deadline command after its command type has been identified.
     */
    private static Task parseDeadline(String input) throws BosException {
        Matcher matcher = DEADLINE_PATTERN.matcher(input);
        if (input.trim().equals(CommandType.DEADLINE.getKeyword())) {
            throw BosException.createEmptyDescriptionException(CommandType.DEADLINE);
        }
        if (!matcher.matches()) {
            throw BosException.createInvalidFormatException("deadline DESCRIPTION /by DATE");
        }
        if (matcher.group("title").isBlank()) {
            throw BosException.createEmptyDescriptionException(CommandType.DEADLINE);
        }
        if (matcher.group("date").isBlank()) {
            throw BosException.createInvalidFormatException("deadline DESCRIPTION /by DATE");
        }

        String title = matcher.group("title").trim();
        String date = matcher.group("date").trim();
        validateStorageFields(title, date);
        Object parsedDate = parseDateTime(date);
        return new Deadline(title, parsedDate);
    }

    /**
     * Parses an event command after its command type has been identified.
     */
    private static Task parseEvent(String input) throws BosException {
        Matcher matcher = EVENT_PATTERN.matcher(input);
        if (input.trim().equals(CommandType.EVENT.getKeyword())) {
            throw BosException.createEmptyDescriptionException(CommandType.EVENT);
        }
        if (!matcher.matches()) {
            throw BosException.createInvalidFormatException("event DESCRIPTION /from START /to END");
        }
        if (matcher.group("title").isBlank()) {
            throw BosException.createEmptyDescriptionException(CommandType.EVENT);
        }
        if (matcher.group("from").isBlank() || matcher.group("to").isBlank()) {
            throw BosException.createInvalidFormatException("event DESCRIPTION /from START /to END");
        }

        String title = matcher.group("title").trim();
        String startTime = matcher.group("from").trim();
        String endTime = matcher.group("to").trim();
        validateStorageFields(title, startTime, endTime);
        Object parsedStartTime = parseDateTime(startTime);
        Object parsedEndTime = parseDateTime(endTime);
        return new Event(title, parsedStartTime, parsedEndTime);
    }

    /**
     * Rejects the storage delimiter because it cannot be represented unambiguously.
     */
    private static void validateStorageFields(String... fields) throws BosException {
        for (String field : fields) {
            if (field.contains("|")) {
                throw new BosException("Task details cannot contain the | character.");
            }
        }
    }
}
