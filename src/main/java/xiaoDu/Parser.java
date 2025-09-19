/**
 * Parse command and time with comprehensive validation
 */


package xiaoDu;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Parser {
    // AI recommend: Added constants for validation
    private static final int MAX_DESCRIPTION_LENGTH = 100;

    /**
     * split the command first
     * @param fullCommand full command
     * @return parsed command
     */
    public static Command parse(String fullCommand) {
        if (fullCommand == null || fullCommand.trim().isEmpty()) { // AI recommend: Added null check
            return new Command(CommandType.UNKNOWN);
        }

        String[] parts = fullCommand.trim().split(" ", 2); // AI recommend: Added trim
        String commandWord = parts[0];
        String arguments = parts.length > 1 ? parts[1] : "";

        switch (commandWord) {
            case "bye":
                return new Command(CommandType.BYE);
            case "list":
                return new Command(CommandType.LIST);
            case "mark":
                return new Command(CommandType.MARK, arguments);
            case "unmark":
                return new Command(CommandType.UNMARK, arguments);
            case "delete":
                return new Command(CommandType.DELETE, arguments);
            case "todo":
                return new Command(CommandType.TODO, arguments);
            case "deadline":
                return new Command(CommandType.DEADLINE, arguments);
            case "event":
                return new Command(CommandType.EVENT, arguments);
            case "find":
                return new Command(CommandType.FIND, arguments);
            case "schedule": // AI recommend: Added schedule command
                return new Command(CommandType.VIEWSCHEDULE, arguments);
            default:
                return new Command(CommandType.UNKNOWN);
        }
    }

    // AI recommend: Added validation result class
    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;

        public ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public boolean isValid() { return valid; }
        public String getErrorMessage() { return errorMessage; }
    }

    /**
     * deal with different command with enhanced validation
     * @param type command type
     * @param arguments the argument of the command
     * @return task or null if invalid
     */
    public static Task parseTask(CommandType type, String arguments) {
        switch (type) {
            case TODO:
                ValidationResult todoValidation = validateTodoInput(arguments);
                if (!todoValidation.isValid()) {
                    return null; // Return null for invalid input - caller should handle
                }
                return new ToDo(arguments.trim());

            case DEADLINE:
                ValidationResult deadlineValidation = validateDeadlineInput(arguments);
                if (!deadlineValidation.isValid()) {
                    return null;
                }

                int byIndex = arguments.indexOf("/by");
                String description = arguments.substring(0, byIndex).trim();
                String byString = arguments.substring(byIndex + 3).trim();
                LocalDate byDate = parseDate(byString);
                return new Deadline(description, byString, byDate);

            case EVENT:
                ValidationResult eventValidation = validateEventInput(arguments);
                if (!eventValidation.isValid()) {
                    return null;
                }

                int fromIndex = arguments.indexOf("/from");
                int toIndex = arguments.indexOf("/to");
                String eventDescription = arguments.substring(0, fromIndex).trim();
                String from = arguments.substring(fromIndex + 5, toIndex).trim();
                String to = arguments.substring(toIndex + 3).trim();
                return new Event(eventDescription, from, to);
        }
        return null;
    }

    // AI recommend: Added comprehensive validation methods
    public static ValidationResult validateTodoInput(String arguments) {
        if (arguments == null || arguments.trim().isEmpty()) {
            return new ValidationResult(false, "The description of a todo cannot be empty.\nExample: todo read book");
        }

        String description = arguments.trim();
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            return new ValidationResult(false, "Task description is too long! Please keep it under " + MAX_DESCRIPTION_LENGTH + " characters.");
        }

        if (description.contains("/by") || description.contains("/from") || description.contains("/to")) {
            return new ValidationResult(false, "Todo tasks should not contain time keywords. Use 'deadline' or 'event' instead.");
        }

        return new ValidationResult(true, null);
    }

    public static ValidationResult validateDeadlineInput(String arguments) {
        if (arguments == null || arguments.trim().isEmpty()) {
            return new ValidationResult(false, "The description of a deadline cannot be empty.\nExample: deadline homework /by 2023-12-01");
        }

        if (!arguments.contains("/by")) {
            return new ValidationResult(false, "Invalid format! Use: deadline [description] /by [YYYY-MM-DD]");
        }

        String[] parts = arguments.split("/by", 2);
        if (parts.length != 2) {
            return new ValidationResult(false, "Invalid format! Use: deadline [description] /by [YYYY-MM-DD]");
        }

        String description = parts[0].trim();
        String dateString = parts[1].trim();

        if (description.isEmpty()) {
            return new ValidationResult(false, "Task description cannot be empty!");
        }

        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            return new ValidationResult(false, "Task description is too long! Please keep it under " + MAX_DESCRIPTION_LENGTH + " characters.");
        }

        if (dateString.isEmpty()) {
            return new ValidationResult(false, "Deadline date cannot be empty!");
        }

        try {
            LocalDate deadlineDate = LocalDate.parse(dateString);
        } catch (DateTimeParseException e) {
            return new ValidationResult(false, "Invalid date format! Please use YYYY-MM-DD format (e.g., 2025-01-20)");
        }

        return new ValidationResult(true, null);
    }

    public static ValidationResult validateEventInput(String arguments) {
        if (arguments == null || arguments.trim().isEmpty()) {
            return new ValidationResult(false, "The description of an event cannot be empty.\nExample: event meeting /from 2pm /to 4pm");
        }

        if (!arguments.contains("/from") || !arguments.contains("/to")) {
            return new ValidationResult(false, "Invalid format! Use: event [description] /from [time] /to [time]");
        }

        int fromIndex = arguments.indexOf("/from");
        int toIndex = arguments.indexOf("/to");

        if (fromIndex >= toIndex) {
            return new ValidationResult(false, "Invalid format! /from must come before /to");
        }

        String description = arguments.substring(0, fromIndex).trim();
        if (description.isEmpty()) {
            return new ValidationResult(false, "Event description cannot be empty!");
        }

        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            return new ValidationResult(false, "Event description is too long! Please keep it under " + MAX_DESCRIPTION_LENGTH + " characters.");
        }

        String fromTime = arguments.substring(fromIndex + 5, toIndex).trim();
        String toTime = arguments.substring(toIndex + 3).trim();

        if (fromTime.isEmpty() || toTime.isEmpty()) {
            return new ValidationResult(false, "Start time and end time cannot be empty!");
        }

        return new ValidationResult(true, null);
    }

    public static ValidationResult validateTaskNumber(String arguments, int taskListSize) {
        if (arguments == null || arguments.trim().isEmpty()) {
            return new ValidationResult(false, "Please provide a task number.");
        }

        try {
            int taskNumber = Integer.parseInt(arguments.trim());
            if (taskNumber < 1 || taskNumber > taskListSize) {
                return new ValidationResult(false, "Invalid task number! Please enter a number between 1 and " + taskListSize);
            }
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "Please provide a valid task number!");
        }

        return new ValidationResult(true, null);
    }

    public static ValidationResult validateFindInput(String arguments) {
        if (arguments == null || arguments.trim().isEmpty()) {
            return new ValidationResult(false, "Please provide a keyword to search for.\nExample: find book");
        }

        String keyword = arguments.trim();
        if (keyword.length() < 2) {
            return new ValidationResult(false, "Search keyword must be at least 2 characters long.");
        }

        return new ValidationResult(true, null);
    }

    public static ValidationResult validateScheduleInput(String arguments) {
        if (arguments == null || arguments.trim().isEmpty()) {
            return new ValidationResult(false, "Please specify a date to view schedule.\nExample: schedule 2025-09-20");
        }

        try {
            LocalDate.parse(arguments.trim());
        } catch (DateTimeParseException e) {
            return new ValidationResult(false, "Invalid date format! Please use YYYY-MM-DD format (e.g., 2025-09-20)");
        }

        return new ValidationResult(true, null);
    }

    private static LocalDate parseDate(String dateString) {
        try {
            return LocalDate.parse(dateString);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}

enum CommandType {
    BYE, LIST, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, FIND, VIEWSCHEDULE, UNKNOWN // AI recommend: Added VIEWSCHEDULE
}

class Command {
    private CommandType type;
    private String arguments;

    public Command(CommandType type) {
        this.type = type;
        this.arguments = "";
    }

    public Command(CommandType type, String arguments) {
        this.type = type;
        this.arguments = arguments;
    }

    public CommandType getType() {
        return type;
    }

    public String getArguments() {
        return arguments;
    }
}