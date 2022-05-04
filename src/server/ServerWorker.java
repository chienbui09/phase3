package server;

import com.sun.source.tree.WhileLoopTree;

import java.io.IOException;
import java.net.PortUnreachableException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerWorker implements Runnable{

    private ServerSocket listener;
    private ExecutorService executorService;

    public static final int NUM_OF_THREAD = 5;
    public static final int SERVER_PORT = 25000;

    public ServerWorker(){

    }

    @Override
    public void run() {
        System.out.println("Binding to port: " + SERVER_PORT + ", please wait");
        try {
            listener = new ServerSocket(SERVER_PORT);
            System.out.println("Server started: " + listener);
            System.out.println("waiting for client: ...");
            executorService = Executors.newFixedThreadPool(NUM_OF_THREAD);

            while (true){
                Socket socket = listener.accept();
                System.out.println("connected: " + socket.getInetAddress());
                executorService.execute(new ServerThread(socket));
            }
        } catch (IOException e) {
            System.err.println("cant bind to port\nport is in use!");
            throw new RuntimeException(e);
        }



    }
}
