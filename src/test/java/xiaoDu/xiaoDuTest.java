package xiaoDu;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class xiaoDuTest {
    xiaoDu bot = new xiaoDu("./test-data.txt");
    @Test
    public void getResponseTest1() {
        assertEquals("I'm sorry, but I don't know what that means :-(\n\n" +
                "Try commands like:\n" +
                " todo [task]\n" +
                " deadline [task] /by [YYYY-MM-DD]\n" +
                " event [task] /from [time] /to [time]\n" +
                " schedule [YYYY-MM-DD]\n" +
                " find [keyword]\n" +
                " list\n" +
                " mark [number]", bot.getResponse("eat apples"));
    }

    @Test
    public void getResponseTest2(){
        String response=bot.getResponse("delete 999");
        assertTrue(response.contains("Invalid task number! Please enter a number between"));
    }
}
