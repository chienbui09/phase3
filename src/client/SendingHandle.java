package client;

import model.Message;
import model.Type;
import model.User;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SendingHandle implements Runnable{
    private Message clientMsg;
    private final Scanner scanner;
    private final ObjectOutputStream outputStream;
//    private final Socket socket;
    //Constructor
    public SendingHandle(Scanner scanner, ObjectOutputStream outputStream) {
        this.scanner = scanner;
//        this.socket = socket;
        this.outputStream = outputStream;
    }

    public Message getClientMsg() {
        return clientMsg;
    }
    public void setClientMsg(Message clientMsg) {
        this.clientMsg = clientMsg;
    }

    // a method to get input from keyboard
    public String getMessage(Scanner scanner){
        System.out.println("message:>> ");
        return scanner.nextLine();
    }

    // A method return client option, is continued or not?
    public boolean isContinued(Scanner scanner){
        System.out.println("Do you want to contitnue?\nY/N");
        String option = scanner.nextLine();
        if(option.equalsIgnoreCase("Y")){
            return true;
        } else {
            return false;
        }
    }

    // Method to check password whether is correct or not
    public boolean checkPwd(String password){

        // if password is not available, return false
        if(password == null){
            return false;
        }

        // else design a pattern to check password
//        String regex = "^(?=.{8,}$)(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).*$";
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{8,}$";
        /*
        * ^             :Begin of the string.
        * (?=.*[a-z])   :presents at least 1 lowercase.
        * (?=.*[A-Z])   :presents at least 1 Uppercase.
        * (?=.*[0-9])   :presents at least 1 digit.
        * .             :presents any character except line break.
        * {8,}          :presents the length of string, at least 8 characters.
        * $             :end of string
        * */

        //design pattern
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }

    @Override
    public void run() {
        System.out.println("******************START******************");
        try {
            // declare output stream with ObjectOutputStream
            while (true) {
                System.out.println("***MENU***");
                System.out.println("""
                        1: REGISTER
                        2: LOGIN
                        3: ECHO
                        4: BROADCAST
                        5: SLEEP
                        6: WAKE
                        7: LOGOUT
                        8: SUBSCRIBE
                        9: HOT
                        10: EXIT""");

                // loop until a correct selection is made
                int selector = 0;
                while (true) {
                    System.out.println("chose the action:");

                    selector = Integer.parseInt(String.valueOf(scanner.nextLine().charAt(0)));
                    if (selector >= 1 && selector <= 10) {
                        break;
                    }
                    // otherwise, repeat the loop
                    System.out.println("entered number is greater than 1 and smaller than or equal 10");
                }

                // if client chose "EXIT", then break the first While and exit program
                if(selector == 10){
                    System.out.println("****EXIT****");
                    clientMsg = new Message(Type.EXIT, "");
                    outputStream.writeObject(clientMsg);
                    outputStream.flush();
                    Thread.sleep(1000);
//                    socket.close();
                    scanner.close();
                    break;
                }
                switch (selector) {
                    case 1 -> {
                        System.out.println("selected: "+ selector);
                        while (true) {

                            User user = new User();

                            System.out.println("****REGISTER****");
                            System.out.println("enter the username and password:");

                            System.out.print("Username: ");
                            String username = scanner.nextLine();
                            user.setUserName(username);

                            // enter and check password
                            while (true) {
                                System.out.print("enter password: ");
                                String password = scanner.nextLine();
                                if (!checkPwd(password)) {
                                    System.out.println("password contain at least 8 character, " +
                                            "both upper-lowercase and digit");
                                    continue;
                                }
                                //if password is passed checking
                                System.out.println("password create successfully!");
                                user.setPassword(password);
                                clientMsg = new Message(Type.REGISTER, user);

                                outputStream.writeObject(clientMsg);
                                outputStream.flush();
                                Thread.sleep(500);
                                break;
                            }
                            Scanner scForOption = new Scanner(System.in);
                            boolean isContinued = isContinued(scForOption);
                            if(!isContinued){
                                break;
                            }
                        }
                    }
                    //end of case 1

                    case 2 -> {
                        System.out.println("selected: "+ selector);
//                        clientMsg.setMsgType(Type.LOGIN);
                        System.out.println("****LOGIN****");

                        while (true) {
                            User user = new User();
                            System.out.println("enter your username and password:");

                            System.out.print("username: ");
                            String username = scanner.nextLine();
//                            clientMsg.getUser().setUserName(username);
                            user.setUserName(username);
                            System.out.print("password: ");
                            String password = scanner.nextLine();
//                            clientMsg.getUser().setPassword(password);
                            user.setPassword(password);
                            //request to server
                            clientMsg = new Message(Type.LOGIN, user);
                            outputStream.writeObject(clientMsg);
                            outputStream.flush();

                            // wait for response from server
                            Thread.sleep(500);

                            boolean isContinued = isContinued(new Scanner(System.in));
                            if(!isContinued){
                                break;
                            }
                        }
                        break;
                    }
                    // end of case 2

                    case 3 ->{
                        System.out.println("****ECHO****");

                        System.out.println("enter \"exit\" to exit");
                        while (true){
                            System.out.println("message: >> ");
                            String message = scanner.nextLine();
                            clientMsg = new Message(Type.ECHO, message);

                            //push to server
                            outputStream.writeObject(clientMsg);
                            outputStream.flush();
                            Thread.sleep(500);
                            // check whether client want to send message again or not
                            if(message.equalsIgnoreCase("exit")){
                                System.out.println("terminate \"ECHO\" session.");
                                break;
                            }
                        }
                        break;
                    }
                    case 4 ->{
                        System.out.println("****BROADCAST****");
                        System.out.println("1: back");
                        Scanner sc1 = new Scanner(System.in);
                        String broadcastMessage = sc1.nextLine();

                        if (broadcastMessage.equals("")) {
                            continue;
                        }
                        if (broadcastMessage.equalsIgnoreCase("1")) {
                            break;
                        }

                        outputStream.writeObject(new Message(Type.BROADCAST, broadcastMessage));
                        outputStream.flush();
                        Thread.sleep(500);
                        break;
                    }
                    case 5 ->{
                        System.out.println("****SLEEP****");
                        outputStream.writeObject(new Message(Type.SLEEP,"change to sleep state"));
                        outputStream.flush();
                        Thread.sleep(500);
                        break;
                    }

                    case 6 ->{
                        System.out.println("****WAKE****");
                        outputStream.writeObject(new Message(Type.WAKE, "wake up the client"));
                        outputStream.flush();
                        Thread.sleep(500);
                        break;
                    }

                    case 7 ->{
                        System.out.println("****LOGOUT****");
                        outputStream.writeObject(new Message(Type.LOGOUT,"logout"));
                        outputStream.flush();
                        Thread.sleep(500);
                        break;
                    }
                    case 8 ->{
                        System.out.println("****SUBSCRIBE****");

                        break;
                    }
                    case 9 ->{
                        System.out.println("****HOT****");

                        break;
                    }

                }

                // check whether client want to continue or not
                if(!isContinued(scanner)){
                    System.out.println("EXIT");
                    clientMsg = new Message(Type.EXIT, "");
                    outputStream.writeObject(clientMsg);
                    outputStream.flush();
                    Thread.sleep(1000);
//                    socket.close();
                    scanner.close();
                    break;
                }
                System.out.println("continue");
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
