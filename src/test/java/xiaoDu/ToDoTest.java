package xiaoDu;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ToDoTest {
    @Test
    public void nullToDoTest(){
        assertEquals("[T][ ] null", new ToDo(null).toString());
    }

    @Test
    public void trivialToDoTest(){
        assertEquals("[T][ ] todo eat apples",new ToDo("todo eat apples").toString());
    }

}