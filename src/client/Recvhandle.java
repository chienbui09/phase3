package client;

import model.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class Recvhandle implements Runnable{
    private final Socket socket;
    private final ObjectInputStream inputStream;
    // constructor
    public Recvhandle(Socket socket, ObjectInputStream inputStream) {
        this.socket = socket;
        this.inputStream = inputStream;
    }


    @Override
    public void run() {
        Message respMsg;
        while (socket.isConnected()) {
            try {
                // initialize and receive response Stream from server
                    respMsg = (Message) inputStream.readObject();

                // show the response from server
                    System.out.println("Server:>> " + respMsg.getMessage());
                    if(respMsg.getMessage().equalsIgnoreCase("exit")){
                        socket.close();
                        break;
                    }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                break;

            }
        }

    }
}
