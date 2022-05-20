package client;

import model.HotType;
import model.Message;
import model.Type;
import model.User;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SendingHandle implements Runnable{
    private Message clientMsg;
    private final Scanner scanner;
    private final ObjectOutputStream outputStream;
//    private HotType subscribedTopic;
//    private final Socket socket;
    //Constructor
    public SendingHandle(Scanner scanner, ObjectOutputStream outputStream) {
        this.clientMsg = new Message();
        this.scanner = scanner;
//        this.socket = socket;
        this.outputStream = outputStream;
    }

    public Message getClientMsg() {
        return clientMsg;
    }

    // a method to get input from keyboard
    public String getMessage(Scanner scanner){
        System.out.print("message:>> ");
        return scanner.nextLine();
    }

    // A method return client option, is continued or not?
    public boolean isContinued(Scanner scanner){
        System.out.println("Do you want to continue?\nY/N");
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
    public int topicOption(Scanner scanner) throws IOException {
        System.out.println("---- title ----");
        System.out.println("""
                1. GRADUATED
                2. NOT GRADUATED
                """);
        int action = 0;
        do{
            action = Integer.parseInt(String.valueOf(scanner.nextLine().charAt(0)));
            if(action == 1 || action == 2){
                break;
            }
            else {
                System.out.println("Not available number.");
            }
        } while (true);
            return action;
    }

    @Override
    public void run() {
        System.out.println("***START***");
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
                    System.out.print("chose the action: ");

                    selector = scanner.nextInt();
                    scanner.nextLine();
                    if (selector >= 1 && selector <= 10) {
                        break;
                    }
                    // otherwise, repeat the loop
                    System.out.println("entered number is greater than 1 and smaller than or equal 10");
                }

                // if client chose "EXIT", then break the first While and exit program
                if(selector == 10){
                    System.out.println("----EXIT----");
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
//                        System.out.println("selected: "+ selector);
                        while (true) {

                            User user = new User();

                            System.out.println("----REGISTER----");
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
//                            Scanner scForOption = new Scanner(System.in);
//                            boolean isContinued = isContinued(scForOption);
//                            if(!isContinued){
//                                break;
//                            }
                        }
                    }
                    //end of case 1

                    case 2 -> {
                        System.out.println("----LOGIN----");

//                        while (true) {
                            User user = new User();
                            System.out.println("enter your username and password:");

                            System.out.print("username: ");
                            String username = scanner.nextLine();
                            user.setUserName(username);
                            System.out.print("password: ");
                            String password = scanner.nextLine();
                            user.setPassword(password);

                            //request to server
                            clientMsg = new Message(Type.LOGIN, user);
                            outputStream.writeObject(clientMsg);
                            outputStream.flush();

                            // wait for response from server
                            Thread.sleep(500);

//                            boolean isContinued = isContinued(new Scanner(System.in));
//                            if(!isContinued){
//                                break;
//                            }
//                        }
                    }
                    // end of case 2

                    case 3 ->{
                        System.out.println("****ECHO****");
                        clientMsg.setMsgType(Type.ECHO);
                        System.out.println("0: back");
                        while (true){
//                            System.out.println("message: >> ");
                            String message = getMessage(scanner);
                            clientMsg = new Message(Type.ECHO, message);

                            //push to server
                            outputStream.writeObject(clientMsg);
                            outputStream.flush();
                            Thread.sleep(500);
                            // check whether client want to send message again or not
                            if(message.equalsIgnoreCase("0")){
                                System.out.println("terminate \"ECHO\" session.");
                                break;
                            }
                        }
                        break;
                    }
                    case 4 ->{
                        System.out.println("----BROADCAST----");
                        System.out.println("1: back");
                        clientMsg.setMsgType(Type.BROADCAST);
//                        Scanner sc1 = new Scanner(System.in);
                        String broadcastMessage = getMessage(scanner);

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
                        System.out.println("----SLEEP----");
                        clientMsg.setMsgType(Type.SLEEP);
                        outputStream.writeObject(new Message(Type.SLEEP,"change to sleep state"));
                        outputStream.flush();
                        Thread.sleep(500);
                        break;
                    }

                    case 6 ->{
                        System.out.println("----WAKE----");
                        clientMsg.setMsgType(Type.WAKE);
                        outputStream.writeObject(new Message(Type.WAKE, "wake up the client"));
                        outputStream.flush();
                        Thread.sleep(500);
                        break;
                    }

                    case 7 ->{
                        System.out.println("----LOGOUT----");
                        clientMsg.setMsgType(Type.LOGOUT);
                        outputStream.writeObject(new Message(Type.LOGOUT,"logout"));
                        outputStream.flush();
                        Thread.sleep(500);
                        break;
                    }
                    case 8 ->{
                        System.out.println("----SUBSCRIBE----");
                        selector = topicOption(scanner);
                        HotType topic;

                        clientMsg.setMsgType(Type.SUBSCRIBE);

                        if(selector == 1){
                            topic = HotType.GRADUATED;
                            clientMsg.setTopic(HotType.GRADUATED);
                        } else {
                            topic = HotType.NOT_GRADUATED;
                            clientMsg.setTopic(HotType.NOT_GRADUATED);
                        }
                        outputStream.writeObject(new Message(Type.SUBSCRIBE,topic));
//                        outputStream.writeObject(new Message(Type.SUBSCRIBE, clientMsg.getTopic()));
                        outputStream.flush();
                        Thread.sleep(500);
//
                        break;
                    }
                    case 9 ->{
                        System.out.println("----HOT----");
                        System.out.println("Subscribed topic: " +
                                            clientMsg.getTopic().toString());
//                        clientMsg.setMsgType(Type.HOT);
//                        clientMsg.setMessage(getMessage(scanner));
//                        outputStream.writeObject(clientMsg);
                        outputStream.writeObject(new Message(Type.HOT, clientMsg.getTopic(), getMessage(scanner)));
                        outputStream.flush();
                        Thread.sleep(500);
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
