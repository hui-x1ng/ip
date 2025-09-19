package xiaoDu;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeadlineTest {
    @Test
    public void createTest(){
        assertEquals(null, new Deadline(null,null,null).toString());
    }

}
