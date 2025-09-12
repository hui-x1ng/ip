package xiaoDu;

import java.util.ArrayList;

/**
 * Main class for xiaoDu - modified to work with both CLI and GUI
 */
public class xiaoDu {
    private TaskList tasks;
    private Ui ui;
    private Storage storage;

    public xiaoDu(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        tasks = storage.load();
    }

    /**
     * Get response for GUI - processes command and returns response string
     */
    public String getResponse(String input) {
        try {
            Command command = Parser.parse(input);

            switch (command.getType()) {
                case BYE:
                    return "Bye. Hope to see you again soon!";

                case LIST:
                    return getTaskListString();

                case MARK:
                    return handleMark(command.getArguments());

                case UNMARK:
                    return handleUnmark(command.getArguments());

                case DELETE:
                    return handleDelete(command.getArguments());

                case TODO:
                    return handleTodo(command.getArguments());

                case DEADLINE:
                    return handleDeadline(command.getArguments());

                case EVENT:
                    return handleEvent(command.getArguments());

                case FIND:
                    return handleFind(command.getArguments());

                default:
                    return "I'm sorry, but I don't know what that means :-(\n\n" +
                            "Try commands like:\n" +
                            " todo [task]\n" +
                            " deadline [task] /by [date]\n" +
                            " list\n" +
                            " mark [number]";
            }
        } catch (Exception e) {
            return "Sorry, I encountered an error: " + e.getMessage();
        }
    }

    // GUI version methods (return strings)
    private String getTaskListString() {
        if (tasks.size() == 0) {
            return "Your task list is empty! Try adding some tasks.";
        }
        StringBuilder result = new StringBuilder("Here are the tasks in your list:\n");
        for (int i = 0; i < tasks.size(); i++) {
            result.append((i + 1)).append(". ").append(tasks.get(i)).append("\n");
        }
        return result.toString();
    }

    private String handleMark(String arguments) {
        try {
            int taskNumber = Integer.parseInt(arguments) - 1;
            if (tasks.isValidIndex(taskNumber)) {
                tasks.get(taskNumber).markAsDone();
                storage.save(tasks);
                return "Nice! I've marked this task as done:\n  " + tasks.get(taskNumber);
            } else {
                return "Invalid task number! Please enter a number between 1 and " + tasks.size();
            }
        } catch (NumberFormatException e) {
            return "Please provide a valid task number!";
        }
    }

    private String handleUnmark(String arguments) {
        try {
            int taskNumber = Integer.parseInt(arguments) - 1;
            if (tasks.isValidIndex(taskNumber)) {
                tasks.get(taskNumber).markAsNotDone();
                storage.save(tasks);
                return "OK, I've marked this task as not done yet:\n  " + tasks.get(taskNumber);
            } else {
                return "Invalid task number! Please enter a number between 1 and " + tasks.size();
            }
        } catch (NumberFormatException e) {
            return "Please provide a valid task number!";
        }
    }

    private String handleDelete(String arguments) {
        try {
            int taskNumber = Integer.parseInt(arguments) - 1;
            if (tasks.isValidIndex(taskNumber)) {
                Task removedTask = tasks.remove(taskNumber);
                storage.save(tasks);
                return "Noted. I've removed this task:\n  " + removedTask +
                        "\nNow you have " + tasks.size() + " tasks in the list.";
            } else {
                return "Invalid task number! Please enter a number between 1 and " + tasks.size();
            }
        } catch (NumberFormatException e) {
            return "Please provide a valid task number!";
        }
    }

    private String handleTodo(String arguments) {
        if (arguments.trim().isEmpty()) {
            return "The description of a todo cannot be empty.\nExample: todo read book";
        } else {
            Task newTask = Parser.parseTask(CommandType.TODO, arguments);
            tasks.add(newTask);
            storage.save(tasks);
            return "Got it. I've added this task:\n  " + newTask +
                    "\nNow you have " + tasks.size() + " tasks in the list.";
        }
    }

    private String handleDeadline(String arguments) {
        if (arguments.trim().isEmpty()) {
            return "The description of a deadline cannot be empty.\nExample: deadline homework /by 2023-12-01";
        } else {
            Task newTask = Parser.parseTask(CommandType.DEADLINE, arguments);
            if (newTask != null) {
                tasks.add(newTask);
                storage.save(tasks);
                return "Got it. I've added this task:\n  " + newTask +
                        "\nNow you have " + tasks.size() + " tasks in the list.";
            } else {
                return "Please specify the deadline using /by.\nExample: deadline homework /by 2023-12-01";
            }
        }
    }

    private String handleEvent(String arguments) {
        if (arguments.trim().isEmpty()) {
            return "The description of an event cannot be empty.\nExample: event meeting /from 2pm /to 4pm";
        } else {
            Task newTask = Parser.parseTask(CommandType.EVENT, arguments);
            if (newTask != null) {
                tasks.add(newTask);
                storage.save(tasks);
                return "Got it. I've added this task:\n  " + newTask +
                        "\nNow you have " + tasks.size() + " tasks in the list.";
            } else {
                return "Please specify the time using /from and /to.\nExample: event meeting /from 2pm /to 4pm";
            }
        }
    }

    private String handleFind(String arguments) {
        if (arguments.trim().isEmpty()) {
            return "Please provide a keyword to search for.\nExample: find book";
        }

        ArrayList<Task> matchingTasks = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.getDescription().toLowerCase().contains(arguments.toLowerCase())) {
                matchingTasks.add(task);
            }
        }

        if (matchingTasks.isEmpty()) {
            return "No matching tasks found for: " + arguments;
        }

        StringBuilder result = new StringBuilder("Here are the matching tasks in your list:\n");
        for (int i = 0; i < matchingTasks.size(); i++) {
            result.append((i + 1)).append(". ").append(matchingTasks.get(i)).append("\n");
        }
        return result.toString();
    }

    /**
     * The method to run xiaoDu in CLI mode
     */
    public void run() {
        ui.showWelcome();
        while (true) {
            String input = ui.readCommand();
            Command command = Parser.parse(input);

            switch (command.getType()) {
                case BYE:
                    ui.showBye();
                    ui.close();
                    break;

                case LIST:
                    ui.showTaskList(tasks);
                    break;

                case MARK:
                    handleMarkCLI(command.getArguments());
                    break;

                case UNMARK:
                    handleUnmarkCLI(command.getArguments());
                    break;

                case DELETE:
                    handleDeleteCLI(command.getArguments());
                    break;

                case TODO:
                    handleTodoCLI(command.getArguments());
                    break;

                case DEADLINE:
                    handleDeadlineCLI(command.getArguments());
                    break;

                case EVENT:
                    handleEventCLI(command.getArguments());
                    break;

                case FIND:
                    handleFindCLI(command.getArguments());
                    break;

                case UNKNOWN:
                    ui.showError("I'm sorry, but I don't know what that means :-(");
                    break;
            }
        }
    }

    // CLI version methods (use UI) - keep your original methods
    private void handleMarkCLI(String arguments) {
        try {
            int taskNumber = Integer.parseInt(arguments) - 1;
            if (tasks.isValidIndex(taskNumber)) {
                tasks.get(taskNumber).markAsDone();
                ui.showTaskMarked(tasks.get(taskNumber));
                storage.save(tasks);
            } else {
                ui.showError("Invalid task number!");
            }
        } catch (NumberFormatException e) {
            ui.showError("Please provide a valid task number!");
        }
    }

    private void handleUnmarkCLI(String arguments) {
        try {
            int taskNumber = Integer.parseInt(arguments) - 1;
            if (tasks.isValidIndex(taskNumber)) {
                tasks.get(taskNumber).markAsNotDone();
                ui.showTaskUnmarked(tasks.get(taskNumber));
                storage.save(tasks);
            } else {
                ui.showError("Invalid task number!");
            }
        } catch (NumberFormatException e) {
            ui.showError("Please provide a valid task number!");
        }
    }

    private void handleDeleteCLI(String arguments) {
        try {
            int taskNumber = Integer.parseInt(arguments) - 1;
            if (tasks.isValidIndex(taskNumber)) {
                Task removedTask = tasks.remove(taskNumber);
                ui.showTaskDeleted(removedTask, tasks.size());
                storage.save(tasks);
            } else {
                ui.showError("Invalid task number!");
            }
        } catch (NumberFormatException e) {
            ui.showError("Please provide a valid task number!");
        }
    }

    private void handleTodoCLI(String arguments) {
        if (arguments.trim().isEmpty()) {
            ui.showError("The description of a todo cannot be empty.");
        } else {
            Task newTask = Parser.parseTask(CommandType.TODO, arguments);
            tasks.add(newTask);
            ui.showTaskAdded(newTask, tasks.size());
            storage.save(tasks);
        }
    }

    private void handleDeadlineCLI(String arguments) {
        if (arguments.trim().isEmpty()) {
            ui.showError("The description of a deadline cannot be empty.");
        } else {
            Task newTask = Parser.parseTask(CommandType.DEADLINE, arguments);
            if (newTask != null) {
                tasks.add(newTask);
                ui.showTaskAdded(newTask, tasks.size());
                storage.save(tasks);
            } else {
                ui.showError("Please specify the deadline using /by.");
            }
        }
    }

    private void handleEventCLI(String arguments) {
        if (arguments.trim().isEmpty()) {
            ui.showError("The description of an event cannot be empty.");
        } else {
            Task newTask = Parser.parseTask(CommandType.EVENT, arguments);
            if (newTask != null) {
                tasks.add(newTask);
                ui.showTaskAdded(newTask, tasks.size());
                storage.save(tasks);
            } else {
                ui.showError("Please specify the time using /from and /to.");
            }
        }
    }

    private void handleFindCLI(String arguments) {
        ArrayList<Task> matchingTasks = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.getDescription().toLowerCase().contains(arguments.toLowerCase())) {
                matchingTasks.add(task);
            }
        }
        System.out.println("Here are the matching tasks in your list:");
        for (int i = 0; i < matchingTasks.size(); i++) {
            System.out.println((i + 1) + "." + matchingTasks.get(i));
        }
    }

    public static void main(String[] args) {
        new xiaoDu("./data/duke.txt").run();
    }
}