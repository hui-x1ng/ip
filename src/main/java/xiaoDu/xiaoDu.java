package xiaoDu;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class xiaoDu {
    public static void main(String[] args) {
        System.out.println("Hello I'm xiaoDu\nWhat can I do for you?");
        Scanner scanner = new Scanner(System.in);
        String input;
        ArrayList<Task> tasks = new ArrayList<>();

        loadTasks(tasks);

        while (true) {
            input = scanner.nextLine().trim();

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
                        saveTasks(tasks);
                    } else {
                        System.out.println("OOPS!!! Invalid task number!");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("OOPS!!! Please provide a valid task number!");
                } catch (Exception e) {
                    System.out.println("OOPS!!! Something went wrong with marking the task!");
                }
            } else if (input.startsWith("unmark ")) {
                try {
                    int taskNumber = Integer.parseInt(input.substring(7)) - 1;
                    if (taskNumber >= 0 && taskNumber < tasks.size()) {
                        tasks.get(taskNumber).markAsNotDone();
                        System.out.println("OK, I've marked this task as not done yet:");
                        System.out.println("  " + tasks.get(taskNumber));
                        saveTasks(tasks);
                    } else {
                        System.out.println("OOPS!!! Invalid task number!");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("OOPS!!! Please provide a valid task number!");
                } catch (Exception e) {
                    System.out.println("OOPS!!! Something went wrong with unmarking the task!");
                }
            } else if (input.startsWith("delete ")) {
                try {
                    int taskNumber = Integer.parseInt(input.substring(7)) - 1;
                    if (taskNumber >= 0 && taskNumber < tasks.size()) {
                        Task removedTask = tasks.remove(taskNumber);
                        System.out.println("Noted. I've removed this task:");
                        System.out.println("  " + removedTask);
                        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                        saveTasks(tasks);
                    } else {
                        System.out.println("OOPS!!! Invalid task number!");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("OOPS!!! Please provide a valid task number!");
                } catch (Exception e) {
                    System.out.println("OOPS!!! Something went wrong with deleting the task!");
                }
            } else if (input.equals("todo")) {
                System.out.println("OOPS!!! The description of a todo cannot be empty.");
            } else if (input.startsWith("todo ")) {
                String description = input.substring(5).trim();
                if (!description.isEmpty()) {
                    Task newTask = new ToDo(description);
                    tasks.add(newTask);
                    System.out.println("Got it. I've added this task:");
                    System.out.println("  " + newTask);
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                    saveTasks(tasks);
                } else {
                    System.out.println("OOPS!!! The description of a todo cannot be empty.");
                }
            } else if (input.equals("deadline")) {
                System.out.println("OOPS!!! The description of a deadline cannot be empty.");
            } else if (input.startsWith("deadline ")) {
                String remaining = input.substring(9).trim();
                int byIndex = remaining.indexOf("/by");
                if (byIndex != -1) {
                    String description = remaining.substring(0, byIndex).trim();
                    String byString = remaining.substring(byIndex + 3).trim();
                    if (!description.isEmpty() && !byString.isEmpty()) {
                        // 尝试解析日期
                        LocalDate by = parseDate(byString);
                        Task newTask = new Deadline(description, byString, by);
                        tasks.add(newTask);
                        System.out.println("Got it. I've added this task:");
                        System.out.println("  " + newTask);
                        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                        saveTasks(tasks);
                    } else {
                        System.out.println("OOPS!!! The description and deadline cannot be empty.");
                    }
                } else {
                    System.out.println("OOPS!!! Please specify the deadline using /by.");
                }
            } else if (input.equals("event")) {
                System.out.println("OOPS!!! The description of an event cannot be empty.");
            } else if (input.startsWith("event ")) {
                String remaining = input.substring(6).trim();
                int fromIndex = remaining.indexOf("/from");
                int toIndex = remaining.indexOf("/to");
                if (fromIndex != -1 && toIndex != -1) {
                    String description = remaining.substring(0, fromIndex).trim();
                    String from = remaining.substring(fromIndex + 5, toIndex).trim();
                    String to = remaining.substring(toIndex + 3).trim();
                    if (!description.isEmpty() && !from.isEmpty() && !to.isEmpty()) {
                        Task newTask = new Event(description, from, to);
                        tasks.add(newTask);
                        System.out.println("Got it. I've added this task:");
                        System.out.println("  " + newTask);
                        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                        saveTasks(tasks); // 自动保存
                    } else {
                        System.out.println("OOPS!!! The description, start time and end time cannot be empty.");
                    }
                } else {
                    System.out.println("OOPS!!! Please specify the time using /from and /to.");
                }
            } else {
                System.out.println("OOPS!!! I'm sorry, but I don't know what that means :-(");
            }
        }

        scanner.close();
    }

    private static LocalDate parseDate(String dateString) {
        try {
            return LocalDate.parse(dateString);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static void saveTasks(ArrayList<Task> tasks) {
        try {
            FileWriter writer = new FileWriter("./data/duke.txt");
            for (Task task : tasks) {
                String line = "";
                if (task instanceof ToDo) {
                    line = "T | " + (task.isDone() ? "1" : "0") + " | " + task.getDescription();
                } else if (task instanceof Deadline) {
                    Deadline d = (Deadline) task;
                    line = "D | " + (task.isDone() ? "1" : "0") + " | " + task.getDescription() + " | " + d.by;
                } else if (task instanceof Event) {
                    Event e = (Event) task;
                    line = "E | " + (task.isDone() ? "1" : "0") + " | " + task.getDescription() + " | " + e.from + " to " + e.to;
                }
                writer.write(line + "\n");
            }
            writer.close();
        } catch (IOException e) {
        }
    }

    private static void loadTasks(ArrayList<Task> tasks) {
        try {
            Scanner fileScanner = new Scanner(new File("./data/duke.txt"));
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(" \\| ");
                if (parts.length >= 3) {
                    String type = parts[0];
                    boolean isDone = parts[1].equals("1");
                    String description = parts[2];

                    Task task = null;
                    if (type.equals("T")) {
                        task = new ToDo(description);
                    } else if (type.equals("D") && parts.length >= 4) {
                        LocalDate byDate = parseDate(parts[3]);
                        task = new Deadline(description, parts[3], byDate);
                    } else if (type.equals("E") && parts.length >= 4) {
                        String[] timeParts = parts[3].split(" to ");
                        if (timeParts.length == 2) {
                            task = new Event(description, timeParts[0], timeParts[1]);
                        }
                    }

                    if (task != null) {
                        if (isDone) task.markAsDone();
                        tasks.add(task);
                    }
                }
            }
            fileScanner.close();
        } catch (Exception e) {
        }
    }
}