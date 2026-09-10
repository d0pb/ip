package bos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests command and date-time parsing behavior provided by {@link Parser}.
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
    public void formatDateTimeForDisplay_validDateTime_formattedTextReturned() {
        assertEquals("Aug 28 2026, 2:30 PM", Parser.formatDateTimeForDisplay("2026-08-28 1430"));
    }

    @Test
    public void formatDateTimeForDisplay_validLeapDay_formattedTextReturned() {
        assertEquals("Feb 29 2024, 12:00 AM", Parser.formatDateTimeForDisplay("2024-02-29 0000"));
    }

    @Test
    public void formatDateTimeForDisplay_invalidCalendarDate_originalTextReturned() {
        String input = "2023-02-29 1200";

        assertEquals(input, Parser.formatDateTimeForDisplay(input));
    }

    @Test
    public void formatDateTimeForDisplay_invalidTime_originalTextReturned() {
        String input = "2026-08-28 2400";

        assertEquals(input, Parser.formatDateTimeForDisplay(input));
    }

    @Test
    public void formatDateTimeForDisplay_incorrectFormat_originalTextReturned() {
        String input = "28-08-2026 1430";

        assertEquals(input, Parser.formatDateTimeForDisplay(input));
    }

    @Test
    public void formatDateTimeForDisplay_freeFormText_originalTextReturned() {
        String input = "tomorrow evening";

        assertEquals(input, Parser.formatDateTimeForDisplay(input));
    }
}
