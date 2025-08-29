package xiaoDu;//same package as the class being tested

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ToDoTest {
    @Test
    public void ToDoTest(){
        assertEquals("[T][ ] null", new ToDo(null).toString());
    }

}