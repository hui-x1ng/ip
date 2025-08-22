import java.util.Scanner;

public class xiaoDu {
    public static void main(String[] args) {
        System.out.println("Hello I'm xiaoDu\nWhat can I do for you?");
        Scanner scanner = new Scanner(System.in);
        String input;
        int i =1;
        String result="";

        while (true) {
            input = scanner.nextLine();

            if (input.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                break;
            } else if (input.equals("list")){
                System.out.println(result);
            }else {
                System.out.println("added: "+input);
                if(result.isEmpty()){
                    result+=i+". "+input;
                    i++;
                }else {
                    result +="\n"+ i + ". " + input;
                    i++;
                }
            }
        }

        scanner.close();
    }
}
