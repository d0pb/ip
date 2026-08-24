/**
 * Starts Bos and displays its greeting and farewell messages.
 */
public class Bos {
    private static final String DIVIDER = "____________________________________________________________";

    /**
     * Prints Bos's welcome message, then exits with a farewell.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        String banner = " ____            \n"
                + "| __ )  ___  ___ \n"
                + "|  _ \\ / _ \\/ __|\n"
                + "| |_) | (_) \\__ \\\n"
                + "|____/ \\___/|___/";

        System.out.println(DIVIDER);
        System.out.println(banner);
        System.out.println("Hello! I'm Bos.");
        System.out.println("What can I do for you?");
        System.out.println(DIVIDER);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(DIVIDER);
    }
}
