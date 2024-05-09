package Client.Service;

import Client.dao.UserRepoImpl;

import java.util.Scanner;

public class UserServiceImpl implements UserService {
    public static void main(String[] args) {
        UserServiceImpl userService = new UserServiceImpl();
        userService.run();
    }

    Scanner input = new Scanner(System.in);

    @Override
    public void showUserMenu() {
        System.out.println("+--------------------+");
        System.out.println("| 1. Create User     |");
        System.out.println("| 2. Login User      |");
        System.out.println("| 3. Quit            |");
        System.out.println("+--------------------+");
    }

    @Override
    public void showChatMenu() {
        System.out.println("+--------------------+");
        System.out.println("| 1. Enter Chat      |");
        System.out.println("| 2. View Chat       |");
        System.out.println("| 3. Quit            |");
        System.out.println("+--------------------+");
    }

    @Override
    public void run() {
        while (true) {
            showUserMenu();

            String userChoice = input.nextLine();

            switch (userChoice) {
                case "1": {
                    UserRepoImpl user = new UserRepoImpl();
                    System.out.println("Please enter your username : ");
                    String username = input.nextLine();
                    System.out.println("Please enter your password : ");
                    String password = input.nextLine();
                    user.createUser(username, password);
                    break;
                }
                case "2": {
                    UserRepoImpl user = new UserRepoImpl();
                    System.out.println("Please enter your username : ");
                    String username = input.nextLine();
                    System.out.println("Please enter your password : ");
                    String password = input.nextLine();
                    user.userLogin(username, password);

                    showChatMenu();

                    userChoice = input.nextLine();

                    switch (userChoice) {
                        case "1": {
                            System.out.println("Now entering the chat");
                            try {
                                user.connectToServer(username, password);
                                break;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        case "2": {
                            System.out.println("Now viewing the chat");
                            user.viewMessages();
                            break;
                        }
                        case "3": {
                            System.out.println("Quitting");
                            break;
                        }
                    }
                }
                case "3": {
                    System.out.println("Quitting");
                    break;
                }
            }
        }
    }
}
