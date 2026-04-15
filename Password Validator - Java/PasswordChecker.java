import java.util.Scanner;

public class PasswordChecker {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        while (true) {

            System.out.print("Enter Password: ");
            String userInput = input.nextLine();

            String result = checkPassword(userInput);

            if ("VALID".equals(result)) {
                System.out.println("Password Accepted");
                break;
            } else {
                System.out.println(result);
                System.out.println("Please try again");
            }
        }

        input.close();
    }

    private static String checkPassword(String pwd) {

        boolean upperFlag = false;
        boolean digitFlag = false;

        StringBuilder message = new StringBuilder();

        if (pwd.length() < 8) {
            message.append("Password should have atleast 8 characters \n");
        }

        for (char c : pwd.toCharArray()) {

            if (Character.isUpperCase(c)) {
                upperFlag = true;
            }

            if (Character.isDigit(c)) {
                digitFlag = true;
            }
        }

        if (!upperFlag) {
            message.append("Password should have atleast 1 Uppercase letter \n");
        }

        if (!digitFlag) {
            message.append("Password should have atleast 1 digit \n");
        }

        return message.length() == 0 ? "VALID" : message.toString();
    }
}