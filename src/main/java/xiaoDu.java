import java.util.Scanner;

public class xiaoDu {
    public static void main(String[] args) {
        System.out.println("Hello I'm xiaoDu\nWhat can I do for you?");
        Scanner scanner = new Scanner(System.in);
        String input;

        while (true) {
            input = scanner.nextLine();

            if (input.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                break;
            } else {
                System.out.println(input);
            }
        }

        scanner.close();
    }
}
