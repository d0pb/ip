package bos;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Interprets user input and converts it into values that Bos can execute.
 */
public final class Parser {
    private static final String DEADLINE_COMMAND_FORMAT = "deadline DESCRIPTION /by DATE";
    private static final String EVENT_COMMAND_FORMAT = "event DESCRIPTION /from START /to END";
    private static final String DATE_TIME_PATTERN = "uuuu-MM-dd HHmm";
    private static final String DISPLAY_DATE_TIME_PATTERN = "MMM d uuuu, h:mm a";
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern(DISPLAY_DATE_TIME_PATTERN, Locale.ENGLISH);
    private static final Pattern MARK_PATTERN = Pattern.compile("^mark\\s+(\\d+)\\s*$");
    private static final Pattern UNMARK_PATTERN = Pattern.compile("^unmark\\s+(\\d+)\\s*$");
    private static final Pattern DELETE_PATTERN = Pattern.compile("^delete\\s+(\\d+)\\s*$");
    private static final Pattern FIND_PATTERN = Pattern.compile("^find\\s+(.*)$");
    private static final Pattern TODO_PATTERN = Pattern.compile("^todo\\s+(.*)$");
    private static final Pattern DEADLINE_PATTERN = Pattern.compile(
            "^deadline\\s*(?<description>.*?)\\s*/by\\s*(?<date>.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern EVENT_PATTERN = Pattern.compile(
            "^event\\s*(?<description>.*?)\\s*/from\\s*(?<from>.*?)\\s*/to\\s*(?<to>.*)$",
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
     * Extracts the keyword from a find command.
     *
     * @param input complete find command.
     * @return trimmed keyword to search for.
     * @throws BosException if no keyword was provided.
     */
    public static String parseFindKeyword(String input) throws BosException {
        Matcher matcher = FIND_PATTERN.matcher(input);
        if (!matcher.matches() || matcher.group(1).isBlank()) {
            throw BosException.createInvalidFormatException("find KEYWORD");
        }
        return matcher.group(1).trim();
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
     * Formats a date-time written as {@code yyyy-MM-dd HHmm} for display.
     * Text in any other format is retained unchanged.
     *
     * @param text user-entered or stored date-time text.
     * @return readable date-time text.
     */
    public static String formatDateTimeForDisplay(String text) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(text, DATE_TIME_FORMAT);
            return dateTime.format(DISPLAY_DATE_TIME_FORMAT);
        } catch (DateTimeParseException exception) {
            return text;
        }
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

        String description = matcher.group(1).trim();
        validateStorageFields(description);
        return new TodoTask(description);
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
            throw BosException.createInvalidFormatException(DEADLINE_COMMAND_FORMAT);
        }
        if (matcher.group("description").isBlank()) {
            throw BosException.createEmptyDescriptionException(CommandType.DEADLINE);
        }
        if (matcher.group("date").isBlank()) {
            throw BosException.createInvalidFormatException(DEADLINE_COMMAND_FORMAT);
        }

        String description = matcher.group("description").trim();
        String deadline = matcher.group("date").trim();
        validateStorageFields(description, deadline);
        return new Deadline(description, deadline);
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
            throw BosException.createInvalidFormatException(EVENT_COMMAND_FORMAT);
        }
        if (matcher.group("description").isBlank()) {
            throw BosException.createEmptyDescriptionException(CommandType.EVENT);
        }
        if (matcher.group("from").isBlank() || matcher.group("to").isBlank()) {
            throw BosException.createInvalidFormatException(EVENT_COMMAND_FORMAT);
        }

        String description = matcher.group("description").trim();
        String startTime = matcher.group("from").trim();
        String endTime = matcher.group("to").trim();
        validateStorageFields(description, startTime, endTime);
        return new Event(description, startTime, endTime);
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
