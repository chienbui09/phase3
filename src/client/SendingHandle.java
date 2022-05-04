package client;

import model.Message;
import model.Type;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SendingHandle implements Runnable{
    Socket socket;
    Message clientMsg;
    Type msgType;
    Scanner scanner;
    String username;
    String password;
    String message;

    int selector;
    //Constructor
    public SendingHandle(){
        socket = new Socket();
        clientMsg = new Message();
        scanner = new Scanner(System.in);
    }

    public SendingHandle(Socket socket, Message clientMsg, Scanner scanner) {
        this.socket = socket;
        this.clientMsg = clientMsg;
        this.scanner = scanner;
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
        System.out.println("\n");
        try {
            // declare output stream with ObjectOutputStream
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            while (true) {
                        System.out.println("chose the action:");
                System.out.println("\n1. REGISTER\n" +
                        "2. LOGIN\n" +
                        "3. ECHO\n" +
                        "4. EXIT");

                // loop until a correct selection is made
                while (true) {

                    selector = Integer.parseInt(String.valueOf(scanner.nextLine().charAt(0)));

                    if (selector >= 1 && selector <= 4) {
                        break;
                    }
                    // otherwise, repeat the loop
                    System.out.println("entered number is greater than 1 and smaller than or equal 4");
                }

                // if client chose "EXIT", then break the first While and exit program
                if(selector == 4){
                    System.out.println("****EXIT****");
                    outputStream.writeUTF("exit");
                    outputStream.flush();
                    break;
                }
                switch (selector) {
                    case 1 -> {
                        System.out.println("selected: "+ selector);
                        clientMsg.setMsgType(Type.REGISTER);
                        while (true) {
                            System.out.println("****REGISTER****");
                            outputStream.writeUTF("register");
                            outputStream.flush();
                            System.out.println("enter the username and password:");

                            while (true) {
                                System.out.print("Username: ");
                                //get input from keyboard
                                username = scanner.nextLine();
                                clientMsg.getUser().setUserName(username);

                                //send message to server
                                outputStream.writeObject(clientMsg);
                                outputStream.flush();
                                Thread.sleep(1000);

                                //wait for server check whether username is existed or not
                                if(clientMsg.getMessage().equalsIgnoreCase("Username is existed")){
                                    System.out.println("reenter Username:");
                                    continue;
                                }
                                break;
                            }
                            //if username is available, then enter and check whether is correct or not
                            while (true) {
                                System.out.print("enter password: ");
                                password = scanner.nextLine();
                                if (!checkPwd(password)){
                                    System.out.println("password contain at least 8 character, " +
                                            "both upper-lowercase and digit");
                                    continue;
                                }
                                //if password is passed checking
                                System.out.println("password create successfully!");
                                clientMsg.getUser().setPassword(password);
                                outputStream.writeObject(clientMsg);
                                outputStream.flush();
                                Thread.sleep(1000);
                                break;
                            }

                            // ask client if whether want to register again or not
//                            if(!isContinued(scanner)){
//                                System.out.println("Register session closed");
//                                break;
//                            }
//                            System.out.println("re-register");
                        }
                    }
                    //end of case 1

                    case 2 -> {
                        System.out.println("selected: "+ selector);
                        clientMsg.setMsgType(Type.LOGIN);
                        System.out.println("****LOGIN****");
                        outputStream.writeUTF("login");
                        while (true) {
                            System.out.println("enter your username and password:");

                            System.out.print("username: ");
                            username = scanner.nextLine();
                            clientMsg.getUser().setUserName(username);

                            System.out.println("password: ");
                            password = scanner.nextLine();
                            clientMsg.getUser().setPassword(password);

                            //request to server
                            outputStream.writeObject(clientMsg);
                            outputStream.flush();

                            // wait for response from server
                            Thread.sleep(1000);

                            // if client login successfully, break the while loop
                            if(clientMsg.getMessage().equalsIgnoreCase("Login successfully")){
                                break;
                            }

                        }
                    }
                    // end of case 2

                    case 3 ->{
                        System.out.println("selected: "+ selector);
                        clientMsg.setMsgType(Type.ECHO);
                        System.out.println("****ECHO****");
                        outputStream.writeUTF("echo");
                        System.out.println("enter \"exit\" to exit");
                        while (true){
                            System.out.println("message: >> ");
                            message = scanner.nextLine();
                            clientMsg.setMessage(message);

                            //push to server
                            outputStream.writeObject(clientMsg);
                            outputStream.flush();

                            // check whether client want to send message again or not
                            if(message.equalsIgnoreCase("exit")){
                                System.out.println("terminate \"ECHO\" session.");
                                break;
                            }
                        }
                    }

                }

                // check whether client want to continue or not
                if(!isContinued(scanner)){
                    System.out.println("EXIT");
                    break;
                }
                System.out.println("continue");
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }


}
