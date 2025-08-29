public class xiaoDu {
    private TaskList tasks;
    private Ui ui;
    private Storage storage;

    public xiaoDu(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        tasks = storage.load();
    }

    public void run() {
        ui.showWelcome();

        boolean isExit = false;
        while (!isExit) {
            String input = ui.readCommand();
            Command command = Parser.parse(input);

            switch (command.getType()) {
                case BYE:
                    isExit = true;
                    ui.showBye();
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

                case UNKNOWN:
                    ui.showError("I'm sorry, but I don't know what that means :-(");
                    break;
            }
        }

        ui.close();
    }

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

    public static void main(String[] args) {
        new xiaoDu("./data/duke.txt").run();
    }
}