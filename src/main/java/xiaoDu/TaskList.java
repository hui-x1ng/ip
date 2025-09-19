/**
 * tasklist class to store all command about tasklist
 */

package xiaoDu;

import java.util.ArrayList;

public class TaskList {
    private ArrayList<Task> tasks;

    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Add task
     * @param task
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Remove the given index task in tasklist
     * @param index
     * @return the deleted task
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * return the length of the tasklist
     * @return the length of the tasklist
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Return if the tasklist is empty
     * @return is empty
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

}