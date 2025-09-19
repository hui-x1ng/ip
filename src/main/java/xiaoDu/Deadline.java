package xiaoDu;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Deadline class extends Task
 * Has one time argument to mark ddl
 */
public class Deadline extends Task {
    public String by;
    public LocalDate byDate;

    public Deadline(String description, String by) {
        super(description);
        this.by = by;
        this.byDate = null;
    }

    public Deadline(String description, String by, LocalDate byDate) {
        super(description);
        this.by = by;
        this.byDate = byDate;
    }

    @Override
    public String toString() {
        if (byDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d yyyy");
            return "[D]" + super.toString() + " (by: " + byDate.format(formatter) + ")";
        } else {
            return "[D]" + super.toString() + " (by: " + by + ")";
        }
    }
}