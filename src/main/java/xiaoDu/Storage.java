package xiaoDu;

import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Storage {
    private String filePath;

    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Save the task
     * @param tasks task to be saved
     */
    public void save(TaskList tasks) {
        try {
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
                System.out.println("Created directory: " + parentDir.getPath());
            }

            if (!file.exists()) {
                file.createNewFile();
                System.out.println("Created data file: " + filePath);
            }

            FileWriter writer = new FileWriter(file);
            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
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
            System.err.println("Failed to save data: " + e.getMessage());
        }
    }

    public TaskList load() {
        TaskList tasks = new TaskList();
        try {
            File file = new File(filePath);

            if (!file.exists()) {
                File parentDir = file.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    parentDir.mkdirs();
                }
                file.createNewFile();
                System.out.println("Created new data file: " + filePath);
                return tasks;
            }

            Scanner fileScanner = new Scanner(file);
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
            System.err.println("Failed to load data: " + e.getMessage());
        }
        return tasks;
    }

    /**
     * Change the String format date into LocalDate
     * @param dateString date
     * @return LocalDate date
     */
    private LocalDate parseDate(String dateString) {
        try {
            return LocalDate.parse(dateString);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}