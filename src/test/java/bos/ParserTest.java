package bos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests the date-time parsing behavior of {@link Parser}.
 */
public class ParserTest {

    @Test
    public void parseCommandType_findCommand_findReturned() {
        assertEquals(CommandType.FIND, Parser.parseCommandType("find book"));
    }

    @Test
    public void parseFindKeyword_validCommand_trimmedKeywordReturned() throws BosException {
        assertEquals("read book", Parser.parseFindKeyword("find   read book  "));
    }

    @Test
    public void parseFindKeyword_missingKeyword_exceptionThrown() {
        assertThrows(BosException.class, () -> Parser.parseFindKeyword("find"));
        assertThrows(BosException.class, () -> Parser.parseFindKeyword("find   "));
    }

    @Test
    public void parseDateTime_validDateTime_localDateTimeReturned() {
        Object result = Parser.parseDateTime("2026-08-28 1430");

        assertEquals(LocalDateTime.of(2026, 8, 28, 14, 30), result);
    }

    @Test
    public void parseDateTime_validLeapDay_localDateTimeReturned() {
        Object result = Parser.parseDateTime("2024-02-29 0000");

        assertEquals(LocalDateTime.of(2024, 2, 29, 0, 0), result);
    }

    @Test
    public void parseDateTime_invalidCalendarDate_originalTextReturned() {
        String input = "2023-02-29 1200";

        assertEquals(input, Parser.parseDateTime(input));
    }

    @Test
    public void parseDateTime_invalidTime_originalTextReturned() {
        String input = "2026-08-28 2400";

        assertEquals(input, Parser.parseDateTime(input));
    }

    @Test
    public void parseDateTime_incorrectFormat_originalTextReturned() {
        String input = "28-08-2026 1430";

        assertEquals(input, Parser.parseDateTime(input));
    }

    @Test
    public void parseDateTime_freeFormText_originalTextReturned() {
        String input = "tomorrow evening";

        assertEquals(input, Parser.parseDateTime(input));
    }
}
