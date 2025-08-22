import java.util.ArrayList;
import java.util.Scanner;

public class xiaoDu {
    public static void main(String[] args) {
        System.out.println("Hello I'm xiaoDu\nWhat can I do for you?");
        Scanner scanner = new Scanner(System.in);
        String input;
        ArrayList<Task> tasks = new ArrayList<>();

        while (true) {
            input = scanner.nextLine();

            if (input.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                break;
            } else if (input.equals("list")) {
                if (tasks.isEmpty()) {
                    System.out.println("The list is empty.");
                } else {
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println((i + 1) + "." + tasks.get(i));
                    }
                }
            } else if (input.startsWith("mark ")) {
                try {
                    int taskNumber = Integer.parseInt(input.substring(5)) - 1;
                    if (taskNumber >= 0 && taskNumber < tasks.size()) {
                        tasks.get(taskNumber).markAsDone();
                        System.out.println("Nice! I've marked this task as done:");
                        System.out.println("  " + tasks.get(taskNumber));
                    } else {
                        System.out.println("Invalid task number!");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please provide a valid task number!");
                }
            } else if (input.startsWith("unmark ")) {
                try {
                    int taskNumber = Integer.parseInt(input.substring(7)) - 1;
                    if (taskNumber >= 0 && taskNumber < tasks.size()) {
                        tasks.get(taskNumber).markAsNotDone();
                        System.out.println("OK, I've marked this task as not done yet:");
                        System.out.println("  " + tasks.get(taskNumber));
                    } else {
                        System.out.println("Invalid task number!");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please provide a valid task number!");
                }
            } else {
                Task newTask = new Task(input);
                tasks.add(newTask);
                System.out.println("added: " + input);
            }
        }

        scanner.close();
    }
}