package bos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests command execution through the interface used by the GUI.
 */
public class BosTest {

    @TempDir
    private Path temporaryDirectory;

    /**
     * Verifies that tasks added through a response are available to later commands.
     */
    @Test
    public void getResponse_addThenList_addedTaskDisplayed() {
        Bos bos = new Bos(temporaryDirectory.resolve("tasks.txt").toString());

        String addResponse = bos.getResponse("todo read book");
        String listResponse = bos.getResponse("list");

        assertTrue(addResponse.contains("[T][ ] read book"));
        assertEquals("Here are the tasks in your list:\n1.[T][ ] read book", listResponse);
    }

    /**
     * Verifies that a new chatbot instance reloads tasks saved by an earlier instance.
     */
    @Test
    public void constructor_savedTaskExists_taskLoaded() {
        String filePath = temporaryDirectory.resolve("tasks.txt").toString();
        Bos firstBos = new Bos(filePath);
        firstBos.getResponse("deadline submit report /by Friday");

        Bos reloadedBos = new Bos(filePath);

        assertTrue(reloadedBos.getResponse("list").contains("submit report"));
    }

    /**
     * Verifies that invalid commands are converted into a response for the GUI.
     */
    @Test
    public void getResponse_unknownCommand_errorResponseReturned() {
        Bos bos = new Bos(temporaryDirectory.resolve("tasks.txt").toString());

        assertEquals(
                "OOPS!!! I'm sorry, but I don't know what that means :-(",
                bos.getResponse("sing"));
    }
}
