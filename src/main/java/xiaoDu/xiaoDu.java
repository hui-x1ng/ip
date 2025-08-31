/**
 * Main class for xiaoDu
 */
package xiaoDu;

import java.util.ArrayList;

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
     * The method to run xiaoDu
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
                    handleMark(command.getArguments());
                    break;

                case UNMARK:
                    handleUnmark(command.getArguments());
                    break;

                case DELETE:
                    handleDelete(command.getArguments());
                    break;

                case TODO:
                    handleTodo(command.getArguments());
                    break;

                case DEADLINE:
                    handleDeadline(command.getArguments());
                    break;

                case EVENT:
                    handleEvent(command.getArguments());
                    break;

                case FIND:
                    handleFind(command.getArguments());
                    break;

                case UNKNOWN:
                    ui.showError("I'm sorry, but I don't know what that means :-(");
                    break;
            }
        }
    }

    /**
     * handle mark command
     * @param arguments num of task to be marked
     */
    private void handleMark(String arguments) {
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

    /**
     * Fulfill unmark command
     * @param arguments num of task to be unmarked
     */
    private void handleUnmark(String arguments) {
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

    private void handleDelete(String arguments) {
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

    private void handleTodo(String arguments) {
        if (arguments.trim().isEmpty()) {
            ui.showError("The description of a todo cannot be empty.");
        } else {
            Task newTask = Parser.parseTask(CommandType.TODO, arguments);
            tasks.add(newTask);
            ui.showTaskAdded(newTask, tasks.size());
            storage.save(tasks);
        }
    }

    private void handleDeadline(String arguments) {
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

    private void handleEvent(String arguments) {
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

    private void handleFind(String arguments){
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