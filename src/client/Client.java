package client;

import model.Message;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public final static int PORT = 25000;
    final static String ip = "localhost";

    public static void main(String[] args) {
        Socket clientSocket;
        Scanner scanner = new Scanner(System.in);
        Message clientMsg = new Message();
        ObjectOutputStream outputStream;
        ObjectInputStream inputStream;
        // start
        System.out.println("start");

        //connect to server
        try {
            clientSocket = new Socket(ip, PORT);
            System.out.println("connected");

            // declare receiving & sending thread
            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            inputStream = new ObjectInputStream(clientSocket.getInputStream());

            SendingHandle sendingHandle = new SendingHandle(scanner, outputStream);
            Thread sender = new Thread(sendingHandle);
            sender.start();

            Recvhandle recvhandle = new Recvhandle(clientSocket, inputStream);
            Thread receiver = new Thread(recvhandle);
            receiver.start();

        } catch (Exception e){
            System.err.println("unable to connect to server");
            e.printStackTrace();
        }

    }
}
