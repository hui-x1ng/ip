package xiaoDu;

import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Main class for xiaoDu - modified to work with both CLI and GUI
 */
public class xiaoDu {
    // AI recommend: Added constants for better maintainability
    private static final String EMPTY_TASK_LIST_MESSAGE = "Your task list is empty! Try adding some tasks.";

    private TaskList tasks;
    private Ui ui;
    private Storage storage;

    public xiaoDu(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        tasks = storage.load();

        assert ui != null : "fail to initialize UI";
    }

    // AI recommend: Added result class to reduce code duplication
    private static class TaskOperationResult {
        private final boolean success;
        private final String message;
        private final Task task;
        private final int taskCount;

        public TaskOperationResult(boolean success, String message) {
            this(success, message, null, 0);
        }

        public TaskOperationResult(boolean success, String message, Task task) {
            this(success, message, task, 0);
        }

        public TaskOperationResult(boolean success, String message, Task task, int taskCount) {
            this.success = success;
            this.message = message;
            this.task = task;
            this.taskCount = taskCount;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Task getTask() { return task; }
        public int getTaskCount() { return taskCount; }
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

                case VIEWSCHEDULE:
                    return handleViewSchedule(command.getArguments());

                default:
                    return "I'm sorry, but I don't know what that means :-(\n\n" +
                            "Try commands like:\n" +
                            " todo [task]\n" +
                            " deadline [task] /by [YYYY-MM-DD]\n" +
                            " event [task] /from [time] /to [time]\n" +
                            " schedule [YYYY-MM-DD] (or empty for today)\n" +
                            " find [keyword]\n" +
                            " list\n" +
                            " mark [number]";
            }
        } catch (Exception e) {
            // AI recommend: Added error logging for better debugging
            System.err.println("Error processing command: " + e.getMessage());
            return "Sorry, I encountered an error: " + e.getMessage();
        }
    }

    // AI recommend: Extracted common logic for task operations using Parser validation
    private TaskOperationResult markTask(String arguments) {
        Parser.ValidationResult validation = Parser.validateTaskNumber(arguments, tasks.size());
        if (!validation.isValid()) {
            return new TaskOperationResult(false, validation.getErrorMessage());
        }

        int taskNumber = Integer.parseInt(arguments.trim()) - 1;
        tasks.get(taskNumber).markAsDone();
        storage.save(tasks);
        return new TaskOperationResult(true, "Task marked successfully", tasks.get(taskNumber));
    }

    private TaskOperationResult unmarkTask(String arguments) {
        Parser.ValidationResult validation = Parser.validateTaskNumber(arguments, tasks.size());
        if (!validation.isValid()) {
            return new TaskOperationResult(false, validation.getErrorMessage());
        }

        int taskNumber = Integer.parseInt(arguments.trim()) - 1;
        tasks.get(taskNumber).markAsNotDone();
        storage.save(tasks);
        return new TaskOperationResult(true, "Task unmarked successfully", tasks.get(taskNumber));
    }

    private TaskOperationResult deleteTask(String arguments) {
        Parser.ValidationResult validation = Parser.validateTaskNumber(arguments, tasks.size());
        if (!validation.isValid()) {
            return new TaskOperationResult(false, validation.getErrorMessage());
        }

        int taskNumber = Integer.parseInt(arguments.trim()) - 1;
        Task removedTask = tasks.remove(taskNumber);
        storage.save(tasks);
        return new TaskOperationResult(true, "Task deleted successfully", removedTask, tasks.size());
    }

    // GUI version methods (return strings)
    private String getTaskListString() {
        if (tasks.size() == 0) {
            return EMPTY_TASK_LIST_MESSAGE;
        }
        StringBuilder result = new StringBuilder("Here are the tasks in your list:\n");
        int taskCount = tasks.size(); // AI recommend: Avoid repeated method calls
        for (int i = 0; i < taskCount; i++) {
            result.append((i + 1)).append(". ").append(tasks.get(i)).append("\n");
        }
        return result.toString();
    }

    private String handleMark(String arguments) {
        TaskOperationResult result = markTask(arguments);
        return result.isSuccess() ? "Nice! I've marked this task as done:\n  " + result.getTask()
                : result.getMessage();
    }

    private String handleUnmark(String arguments) {
        TaskOperationResult result = unmarkTask(arguments);
        return result.isSuccess() ? "OK, I've marked this task as not done yet:\n  " + result.getTask()
                : result.getMessage();
    }

    private String handleDelete(String arguments) {
        TaskOperationResult result = deleteTask(arguments);
        return result.isSuccess() ? "Noted. I've removed this task:\n  " + result.getTask() +
                "\nNow you have " + result.getTaskCount() + " tasks in the list."
                : result.getMessage();
    }

    private String handleTodo(String arguments) {
        // AI recommend: Use Parser validation
        Parser.ValidationResult validation = Parser.validateTodoInput(arguments);
        if (!validation.isValid()) {
            return validation.getErrorMessage();
        }

        Task newTask = Parser.parseTask(CommandType.TODO, arguments);
        if (newTask == null) {
            return "Failed to create todo task. Please check your input format.";
        }

        tasks.add(newTask);
        storage.save(tasks);
        return "Got it. I've added this task:\n  " + newTask +
                "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    private String handleDeadline(String arguments) {
        // AI recommend: Use Parser validation
        Parser.ValidationResult validation = Parser.validateDeadlineInput(arguments);
        if (!validation.isValid()) {
            return validation.getErrorMessage();
        }

        Task newTask = Parser.parseTask(CommandType.DEADLINE, arguments);
        if (newTask == null) {
            return "Failed to create deadline task. Please check your input format.";
        }

        tasks.add(newTask);
        storage.save(tasks);
        return "Got it. I've added this task:\n  " + newTask +
                "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    private String handleEvent(String arguments) {
        // AI recommend: Use Parser validation
        Parser.ValidationResult validation = Parser.validateEventInput(arguments);
        if (!validation.isValid()) {
            return validation.getErrorMessage();
        }

        Task newTask = Parser.parseTask(CommandType.EVENT, arguments);
        if (newTask == null) {
            return "Failed to create event task. Please check your input format.";
        }

        tasks.add(newTask);
        storage.save(tasks);
        return "Got it. I've added this task:\n  " + newTask +
                "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    private String handleFind(String arguments) {
        // AI recommend: Use Parser validation
        Parser.ValidationResult validation = Parser.validateFindInput(arguments);
        if (!validation.isValid()) {
            return validation.getErrorMessage();
        }

        ArrayList<Task> matchingTasks = new ArrayList<>();
        String keyword = arguments.trim();
        int taskCount = tasks.size(); // AI recommend: Avoid repeated method calls
        for (int i = 0; i < taskCount; i++) {
            Task task = tasks.get(i);
            if (task.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                matchingTasks.add(task);
            }
        }

        if (matchingTasks.isEmpty()) {
            return "No matching tasks found for: " + keyword;
        }

        StringBuilder result = new StringBuilder("Here are the matching tasks in your list:\n");
        int matchingCount = matchingTasks.size(); // AI recommend: Avoid repeated method calls
        for (int i = 0; i < matchingCount; i++) {
            result.append((i + 1)).append(". ").append(matchingTasks.get(i)).append("\n");
        }
        return result.toString();
    }

    private String handleViewSchedule(String arguments) {
        // AI recommend: Use Parser validation
        Parser.ValidationResult validation = Parser.validateScheduleInput(arguments);
        if (!validation.isValid()) {
            return validation.getErrorMessage();
        }

        LocalDate targetDate;
        if (arguments == null || arguments.trim().isEmpty()) {
            targetDate = LocalDate.now();
        } else {
            targetDate = LocalDate.parse(arguments.trim());
        }
        return getScheduleForDate(targetDate);
    }

    private String getScheduleForDate(LocalDate date) {
        StringBuilder result = new StringBuilder("Schedule for " + date + ":\n");
        boolean hasTasksForDate = false;
        int taskCount = 0; // AI recommend: Added task counter for better user experience

        int totalTasks = tasks.size(); // AI recommend: Avoid repeated method calls
        for (int i = 0; i < totalTasks; i++) {
            Task task = tasks.get(i);

            if (task instanceof Deadline) {
                Deadline deadline = (Deadline) task;
                if (deadline.byDate != null && deadline.byDate.equals(date)) {
                    taskCount++;
                    String status = task.isDone() ? "[DONE]" : "[PENDING]"; // AI recommend: Show task status
                    result.append(taskCount).append(". ").append(status)
                            .append(" [DEADLINE] ").append(task.getDescription())
                            .append(" (by: ").append(deadline.by).append(")\n");
                    hasTasksForDate = true;
                }
            }
        }

        if (!hasTasksForDate) {
            result.append("No tasks scheduled for this date.");
        } else {
            result.append("\nTotal: ").append(taskCount).append(" task(s)"); // AI recommend: Added summary
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
                    return; // AI recommend: Use return instead of break to exit method

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

                case VIEWSCHEDULE:
                    handleViewScheduleCLI(command.getArguments());
                    break;

                case UNKNOWN:
                    ui.showError("I'm sorry, but I don't know what that means :-(");
                    break;
            }
        }
    }

    // CLI version methods (use UI) - AI recommend: Refactored to use common logic and Parser validation
    private void handleMarkCLI(String arguments) {
        TaskOperationResult result = markTask(arguments);
        if (result.isSuccess()) {
            ui.showTaskMarked(result.getTask());
        } else {
            ui.showError(result.getMessage());
        }
    }

    private void handleUnmarkCLI(String arguments) {
        TaskOperationResult result = unmarkTask(arguments);
        if (result.isSuccess()) {
            ui.showTaskUnmarked(result.getTask());
        } else {
            ui.showError(result.getMessage());
        }
    }

    private void handleDeleteCLI(String arguments) {
        TaskOperationResult result = deleteTask(arguments);
        if (result.isSuccess()) {
            ui.showTaskDeleted(result.getTask(), result.getTaskCount());
        } else {
            ui.showError(result.getMessage());
        }
    }

    private void handleTodoCLI(String arguments) {
        Parser.ValidationResult validation = Parser.validateTodoInput(arguments);
        if (!validation.isValid()) {
            ui.showError(validation.getErrorMessage());
            return;
        }

        Task newTask = Parser.parseTask(CommandType.TODO, arguments);
        if (newTask == null) {
            ui.showError("Failed to create todo task. Please check your input format.");
            return;
        }

        tasks.add(newTask);
        ui.showTaskAdded(newTask, tasks.size());
        storage.save(tasks);
    }

    private void handleDeadlineCLI(String arguments) {
        Parser.ValidationResult validation = Parser.validateDeadlineInput(arguments);
        if (!validation.isValid()) {
            ui.showError(validation.getErrorMessage());
            return;
        }

        Task newTask = Parser.parseTask(CommandType.DEADLINE, arguments);
        if (newTask == null) {
            ui.showError("Failed to create deadline task. Please check your input format.");
            return;
        }

        tasks.add(newTask);
        ui.showTaskAdded(newTask, tasks.size());
        storage.save(tasks);
    }

    private void handleEventCLI(String arguments) {
        Parser.ValidationResult validation = Parser.validateEventInput(arguments);
        if (!validation.isValid()) {
            ui.showError(validation.getErrorMessage());
            return;
        }

        Task newTask = Parser.parseTask(CommandType.EVENT, arguments);
        if (newTask == null) {
            ui.showError("Failed to create event task. Please check your input format.");
            return;
        }

        tasks.add(newTask);
        ui.showTaskAdded(newTask, tasks.size());
        storage.save(tasks);
    }

    private void handleFindCLI(String arguments) {
        Parser.ValidationResult validation = Parser.validateFindInput(arguments);
        if (!validation.isValid()) {
            ui.showError(validation.getErrorMessage());
            return;
        }

        ArrayList<Task> matchingTasks = new ArrayList<>();
        String keyword = arguments.trim();
        int taskCount = tasks.size(); // AI recommend: Avoid repeated method calls
        for (int i = 0; i < taskCount; i++) {
            Task task = tasks.get(i);
            if (task.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                matchingTasks.add(task);
            }
        }

        if (matchingTasks.isEmpty()) {
            System.out.println("No matching tasks found for: " + keyword);
            return;
        }

        System.out.println("Here are the matching tasks in your list:");
        int matchingCount = matchingTasks.size(); // AI recommend: Avoid repeated method calls
        for (int i = 0; i < matchingCount; i++) {
            System.out.println((i + 1) + "." + matchingTasks.get(i));
        }
    }

    private void handleViewScheduleCLI(String arguments) {
        String result = handleViewSchedule(arguments);
        System.out.println(result);
    }

    public static void main(String[] args) {
        new xiaoDu("./data/duke.txt").run();
    }
}