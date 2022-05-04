package client;

import model.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class Recvhandle implements Runnable{
    Socket socket;
    Message respMsg = new Message();
    // constructor
    public Recvhandle(Socket socket, Message respMsg) {
        this.socket = socket;
        this.respMsg = respMsg;
    }


    @Override
    public void run() {
        try {
            // initialize and receive response Stream from server
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            while (inputStream.available() > 0) {
                respMsg = (Message) inputStream.readObject();

                System.out.println("Server:>> " + respMsg.getMessage());
            }
            socket.close();
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }
}
