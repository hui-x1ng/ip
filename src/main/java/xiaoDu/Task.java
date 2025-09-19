/**
 * Create Task class to easier achieve mark function
 */

package xiaoDu;

public class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * return statusmark
     * @return X or " "
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Mark tha task
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Unmark the task
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * return the description
     * @return description of the command
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether the task is done
     * @return boolean
     */
    public boolean isDone() {
        return isDone;
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}