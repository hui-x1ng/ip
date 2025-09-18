package xiaoDu;

import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeParseException; // AI recommend: Added for specific exception handling

/**
 * Main class for xiaoDu - modified to work with both CLI and GUI
 */
public class xiaoDu {
    // AI recommend: Added constants for better maintainability
    private static final String EMPTY_TASK_LIST_MESSAGE = "Your task list is empty! Try adding some tasks.";
    private static final String INVALID_TASK_NUMBER_MESSAGE = "Invalid task number!";
    private static final String INVALID_NUMBER_FORMAT_MESSAGE = "Please provide a valid task number!";
    private static final String EMPTY_TODO_MESSAGE = "The description of a todo cannot be empty.\nExample: todo read book";
    private static final String EMPTY_DEADLINE_MESSAGE = "The description of a deadline cannot be empty.\nExample: deadline homework /by 2023-12-01";
    private static final String EMPTY_EVENT_MESSAGE = "The description of an event cannot be empty.\nExample: event meeting /from 2pm /to 4pm";
    private static final String DEADLINE_FORMAT_ERROR = "Please specify the deadline using /by.\nExample: deadline homework /by 2023-12-01";
    private static final String EVENT_FORMAT_ERROR = "Please specify the time using /from and /to.\nExample: event meeting /from 2pm /to 4pm";
    private static final String EMPTY_FIND_MESSAGE = "Please provide a keyword to search for.\nExample: find book";

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
                            " deadline [task] /by [date]\n" +
                            " event [task] /from [time] /to [time]\n" +
                            " schedule [YYYY-MM-DD] (or empty for today)\n" + // AI recommend: Added schedule command
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

    // AI recommend: Extracted common logic for task operations
    private TaskOperationResult markTask(String arguments) {
        if (arguments == null || arguments.trim().isEmpty()) { // AI recommend: Added null check
            return new TaskOperationResult(false, "Please provide a task number to mark.");
        }

        try {
            int taskNumber = Integer.parseInt(arguments.trim()) - 1; // AI recommend: Added trim()
            if (tasks.isValidIndex(taskNumber)) {
                tasks.get(taskNumber).markAsDone();
                storage.save(tasks);
                return new TaskOperationResult(true, "Task marked successfully", tasks.get(taskNumber));
            } else {
                return new TaskOperationResult(false, "Invalid task number! Please enter a number between 1 and " + tasks.size());
            }
        } catch (NumberFormatException e) {
            return new TaskOperationResult(false, INVALID_NUMBER_FORMAT_MESSAGE);
        }
    }

    private TaskOperationResult unmarkTask(String arguments) {
        if (arguments == null || arguments.trim().isEmpty()) { // AI recommend: Added null check
            return new TaskOperationResult(false, "Please provide a task number to unmark.");
        }

        try {
            int taskNumber = Integer.parseInt(arguments.trim()) - 1; // AI recommend: Added trim()
            if (tasks.isValidIndex(taskNumber)) {
                tasks.get(taskNumber).markAsNotDone();
                storage.save(tasks);
                return new TaskOperationResult(true, "Task unmarked successfully", tasks.get(taskNumber));
            } else {
                return new TaskOperationResult(false, "Invalid task number! Please enter a number between 1 and " + tasks.size());
            }
        } catch (NumberFormatException e) {
            return new TaskOperationResult(false, INVALID_NUMBER_FORMAT_MESSAGE);
        }
    }

    private TaskOperationResult deleteTask(String arguments) {
        if (arguments == null || arguments.trim().isEmpty()) { // AI recommend: Added null check
            return new TaskOperationResult(false, "Please provide a task number to delete.");
        }

        try {
            int taskNumber = Integer.parseInt(arguments.trim()) - 1; // AI recommend: Added trim()
            if (tasks.isValidIndex(taskNumber)) {
                Task removedTask = tasks.remove(taskNumber);
                storage.save(tasks);
                return new TaskOperationResult(true, "Task deleted successfully", removedTask, tasks.size());
            } else {
                return new TaskOperationResult(false, "Invalid task number! Please enter a number between 1 and " + tasks.size());
            }
        } catch (NumberFormatException e) {
            return new TaskOperationResult(false, INVALID_NUMBER_FORMAT_MESSAGE);
        }
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
        TaskOperationResult result = markTask(arguments); // AI recommend: Use extracted method
        return result.isSuccess() ? "Nice! I've marked this task as done:\n  " + result.getTask()
                : result.getMessage();
    }

    private String handleUnmark(String arguments) {
        TaskOperationResult result = unmarkTask(arguments); // AI recommend: Use extracted method
        return result.isSuccess() ? "OK, I've marked this task as not done yet:\n  " + result.getTask()
                : result.getMessage();
    }

    private String handleDelete(String arguments) {
        TaskOperationResult result = deleteTask(arguments); // AI recommend: Use extracted method
        return result.isSuccess() ? "Noted. I've removed this task:\n  " + result.getTask() +
                "\nNow you have " + result.getTaskCount() + " tasks in the list."
                : result.getMessage();
    }

    private String handleTodo(String arguments) {
        if (arguments == null || arguments.trim().isEmpty()) { // AI recommend: Added null check
            return EMPTY_TODO_MESSAGE;
        } else {
            Task newTask = Parser.parseTask(CommandType.TODO, arguments);
            tasks.add(newTask);
            storage.save(tasks);
            return "Got it. I've added this task:\n  " + newTask +
                    "\nNow you have " + tasks.size() + " tasks in the list.";
        }
    }

    private String handleDeadline(String arguments) {
        if (arguments == null || arguments.trim().isEmpty()) { // AI recommend: Added null check
            return EMPTY_DEADLINE_MESSAGE;
        } else {
            Task newTask = Parser.parseTask(CommandType.DEADLINE, arguments);
            if (newTask != null) {
                tasks.add(newTask);
                storage.save(tasks);
                return "Got it. I've added this task:\n  " + newTask +
                        "\nNow you have " + tasks.size() + " tasks in the list.";
            } else {
                return DEADLINE_FORMAT_ERROR;
            }
        }
    }

    private String handleEvent(String arguments) {
        if (arguments == null || arguments.trim().isEmpty()) { // AI recommend: Added null check
            return EMPTY_EVENT_MESSAGE;
        } else {
            Task newTask = Parser.parseTask(CommandType.EVENT, arguments);
            if (newTask != null) {
                tasks.add(newTask);
                storage.save(tasks);
                return "Got it. I've added this task:\n  " + newTask +
                        "\nNow you have " + tasks.size() + " tasks in the list.";
            } else {
                return EVENT_FORMAT_ERROR;
            }
        }
    }

    private String handleFind(String arguments) {
        if (arguments == null || arguments.trim().isEmpty()) { // AI recommend: Added null check
            return EMPTY_FIND_MESSAGE;
        }

        ArrayList<Task> matchingTasks = new ArrayList<>();
        int taskCount = tasks.size(); // AI recommend: Avoid repeated method calls
        for (int i = 0; i < taskCount; i++) {
            Task task = tasks.get(i);
            if (task.getDescription().toLowerCase().contains(arguments.toLowerCase())) {
                matchingTasks.add(task);
            }
        }

        if (matchingTasks.isEmpty()) {
            return "No matching tasks found for: " + arguments;
        }

        StringBuilder result = new StringBuilder("Here are the matching tasks in your list:\n");
        int matchingCount = matchingTasks.size(); // AI recommend: Avoid repeated method calls
        for (int i = 0; i < matchingCount; i++) {
            result.append((i + 1)).append(". ").append(matchingTasks.get(i)).append("\n");
        }
        return result.toString();
    }

    private String handleViewSchedule(String arguments) {
        LocalDate targetDate;
        try {
            if (arguments == null || arguments.trim().isEmpty()) { // AI recommend: Added null check
                targetDate = LocalDate.now();
            } else {
                targetDate = LocalDate.parse(arguments.trim());
            }
            return getScheduleForDate(targetDate);
        } catch (DateTimeParseException e) { // AI recommend: More specific exception handling
            return "Invalid date format! Please use YYYY-MM-DD format (e.g., 2025-01-20) or leave empty for today.";
        } catch (Exception e) {
            return "Error processing date: " + e.getMessage();
        }
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

    // CLI version methods (use UI) - AI recommend: Refactored to use common logic
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
        if (arguments == null || arguments.trim().isEmpty()) { // AI recommend: Added null check
            ui.showError("The description of a todo cannot be empty.");
        } else {
            Task newTask = Parser.parseTask(CommandType.TODO, arguments);
            tasks.add(newTask);
            ui.showTaskAdded(newTask, tasks.size());
            storage.save(tasks);
        }
    }

    private void handleDeadlineCLI(String arguments) {
        if (arguments == null || arguments.trim().isEmpty()) { // AI recommend: Added null check
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
        if (arguments == null || arguments.trim().isEmpty()) { // AI recommend: Added null check
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
        if (arguments == null || arguments.trim().isEmpty()) { // AI recommend: Added input validation
            ui.showError("Please provide a keyword to search for.");
            return;
        }

        ArrayList<Task> matchingTasks = new ArrayList<>();
        int taskCount = tasks.size(); // AI recommend: Avoid repeated method calls
        for (int i = 0; i < taskCount; i++) {
            Task task = tasks.get(i);
            if (task.getDescription().toLowerCase().contains(arguments.toLowerCase())) {
                matchingTasks.add(task);
            }
        }

        if (matchingTasks.isEmpty()) { // AI recommend: Handle no results case
            System.out.println("No matching tasks found for: " + arguments);
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