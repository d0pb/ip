import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Parses and formats the optional date-time details used by deadlines and events.
 */
public final class DateTimeParser {
    private static final String DATE_TIME_PATTERN = "uuuu-MM-dd HHmm";
    private static final String DISPLAY_DATE_TIME_PATTERN = "MMM d uuuu, h:mm a";
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern(DISPLAY_DATE_TIME_PATTERN);

    private DateTimeParser() {
    }

    /**
     * Converts a date-time in {@code yyyy-MM-dd HHmm} format into a
     * {@link LocalDateTime}. Text in any other format is retained unchanged.
     *
     * @param text user-entered or stored date-time text
     * @return a {@code LocalDateTime} when parsing succeeds, or the original string otherwise
     */
    public static Object parse(String text) {
        try {
            return LocalDateTime.parse(text, DATE_TIME_FORMAT);
        } catch (DateTimeParseException exception) {
            return text;
        }
    }

    /**
     * Converts a date-time value to the consistent format used in {@code tasks.txt}.
     * Plain text is returned unchanged so that descriptions such as "tomorrow evening"
     * remain supported.
     *
     * @param value a parsed date-time or plain text
     * @return value suitable for saving in the data file
     */
    public static String formatForStorage(Object value) {
        if (value instanceof LocalDateTime dateTime) {
            return dateTime.format(DATE_TIME_FORMAT);
        }
        return value.toString();
    }

    /**
     * Formats a parsed value for a readable chatbot response.
     *
     * @param value a parsed date-time or plain text
     * @return readable date-time text
     */
    public static String formatForDisplay(Object value) {
        if (value instanceof LocalDateTime dateTime) {
            return dateTime.format(DISPLAY_DATE_TIME_FORMAT);
        }
        return value.toString();
    }
}
