package client;

import model.Message;

import java.net.Socket;
import java.util.Scanner;

public class Client {
    public final static int PORT = 25000;
    final static String ip = "localhost";

    public static void main(String[] args) {
        Socket clientSocket;
        Scanner scanner = new Scanner(System.in);
        Message clientMsg = new Message();
        // start
        System.out.println("start");

        //connect to server
        try {
            clientSocket = new Socket(ip, PORT);
            System.out.println("connected");

            // declare receiving & sending thread
            Recvhandle recvhandle = new Recvhandle(clientSocket, clientMsg);
            SendingHandle sendingHandle = new SendingHandle(clientSocket,clientMsg, scanner);

            Thread sender = new Thread(sendingHandle);
            sender.start();
            Thread receiver = new Thread(recvhandle);
            receiver.start();
        } catch (Exception e){
            System.err.println("unable to connect to server");
            e.printStackTrace();
        }

    }
}
