package xiaoDu;//same package as the class being tested

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeadlineTest {
    @Test
    public void dummyTest(){
        assertEquals(null, new Deadline(null,null,null).toString());
    }

}
