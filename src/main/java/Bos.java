import java.util.Scanner;

public class Bos {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String BANNER = " ____            \n"
                + "| __ )  ___  ___ \n"
                + "|  _ \\ / _ \\/ __|\n"
                + "| |_) | (_) \\__ \\\n"
                + "|____/ \\___/|___/";
    private static final String END_ECHO = "bye";
    public static void main(String[] args) {
        printGreeting();
        Scanner scanner = new Scanner(System.in);
        echoUserInput(scanner);
        printExit();
    }

    private static void printGreeting() {
        System.out.println(DIVIDER);
        System.out.println(BANNER);
        System.out.println("Hello! I'm Bos.");
        System.out.println("What can I do for you?");
        System.out.println(DIVIDER);
    }

    private static void printExit() {
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(DIVIDER);
    }

    private static void echoUserInput(Scanner scanner) {
        String input = "";
        while (!END_ECHO.equals(input)) {
            input = scanner.nextLine();
            System.out.println(DIVIDER);
            if (!END_ECHO.equals(input)) {
                System.out.println(input);
                System.out.println(DIVIDER);
            }
        }
    }
}
