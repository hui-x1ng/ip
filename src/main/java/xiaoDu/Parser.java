/**
 * Parse command and time
 */

package xiaoDu;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Parser {

    /**
     * split the command first
     * @param fullCommand full command
     * @return parsed command
     */
    public static Command parse(String fullCommand) {
        String[] parts = fullCommand.split(" ", 2);
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
            case "schedule":
                return new Command(CommandType.VIEWSCHEDULE, arguments);
            default:
                return new Command(CommandType.UNKNOWN);
        }
    }

    /**
     * deal with different command
     * @param type command type
     * @param arguments the argument of the command
     * @return task
     */
    public static Task parseTask(CommandType type, String arguments) {
        switch (type) {
            case TODO:
                return new ToDo(arguments.trim());

            case DEADLINE:
                int byIndex = arguments.indexOf("/by");
                if (byIndex != -1) {
                    String description = arguments.substring(0, byIndex).trim();
                    String byString = arguments.substring(byIndex + 3).trim();
                    LocalDate byDate = parseDate(byString);
                    return new Deadline(description, byString, byDate);
                }
                break;

            case EVENT:
                int fromIndex = arguments.indexOf("/from");
                int toIndex = arguments.indexOf("/to");
                if (fromIndex != -1 && toIndex != -1) {
                    String description = arguments.substring(0, fromIndex).trim();
                    String from = arguments.substring(fromIndex + 5, toIndex).trim();
                    String to = arguments.substring(toIndex + 3).trim();
                    return new Event(description, from, to);
                }
                break;

        }
        return null;
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
    BYE, LIST, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, FIND, UNKNOWN,VIEWSCHEDULE
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