package SweeperChat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This is a server created to manage clients signing on to servers
 **/
public class MultiServer {
    protected int numClients;
    protected static final int pn = 1465;
    protected int portNum;
    protected ServerSocket serSoc;
    protected Socket soc;
    protected ArrayList<ClientHandler> clients;
    protected final String color;
    protected ServerHandler messenger;
    protected boolean activate;
    public MultiServer() {
        numClients = 0;
        clients = new ArrayList<>();
        Random rand = new Random();
        color = "\u001b[38;5;" + (rand.nextInt(255) + 1) + "m";
    }

    /**
     * This start the process by determining the port # of the CliServe.Server
     */
    protected void begin() {
        Scanner scan = new Scanner(System.in);
        String ans;
        do {
            System.out.println(color + "Do you have a particular port #? (y/n)");
            ans = scan.nextLine().toLowerCase();
        } while (!"y".equals(ans) && !"n".equals(ans));
        if (ans.equals("y")) {
            System.out.println("Port Number?");
            portNum = scan.nextInt();
        } else {
            portNum = pn;
        }
    }
    protected void setMessengerUp() throws IOException{
        try{
            Socket bird = new Socket("localhost",portNum);
            messenger = new ServerHandler(bird,portNum);
        }
        catch (IOException e){
            closeSockets();
        }
    }
    /**
     * This creates ClientHandlers and logs how many have logged in. The Handler represent clients
     *
     * @throws IOException when an error occurs
     */
    protected void letIn() throws IOException {
        serSoc = new ServerSocket(portNum);
        System.out.println("*This is Server " + portNum + "*");
        command();
        while (!serSoc.isClosed()) {
            try {
                checkThreads();
                if (messenger == null){
                    setMessengerUp();
                }
                soc = serSoc.accept();
                if (activate){
                    checkThreads();
                    numClients += 1;
                    System.out.println("A new client has joined");
                    System.out.println("Clients Logged in: " + numClients);
                    ClientHandler clientH = new ClientHandler(soc, portNum);
                    clients.add(clientH);
                    messenger.getClients().add(clientH);
                    clientH.myThread.start();
                }
                else {activate = true;}
            } catch (IOException error) {
                closeSockets();
            } catch (NullPointerException error) {
                checkThreads();
            }
        }

    }

    /**
     * This closes sockets to ensure nothing cna get corrupted
     *
     * @throws IOException if anything's wrong with the sockets
     */
    public void closeSockets() throws IOException {
        try {
            if (serSoc != null) {
                serSoc.close();
            }
            if (soc != null) {
                soc.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This gets rid of the clients who have left the server safely.
     * @throws IOException to make sure all actions are legal
     */
    protected void checkThreads() throws IOException{
        boolean left = false;
        for (ClientHandler handler : clients){
            if (handler.getExit()){
                left = true;
                String name = handler.getName();
                clients.remove(handler);
                messenger.getClients().remove(handler);
                numClients -= 1;
                System.out.println("*" + name + " has left Server " + portNum + "*");
            }
        }
        if (left){
            System.out.println("Remaining Clients: " + numClients);
        }
    }
    /**
     * A blanket function that runs the server.
     *
     * @throws IOException when something's amiss
     */
    public void runServer() throws IOException {
        begin();
        letIn();
    }
    protected void command(){
        new Thread(() -> {
            String con;
            Scanner scan = new Scanner(System.in);
            while (!serSoc.isClosed()){
                String command = scan.nextLine();
                switch (command.toLowerCase()) {
                    case "close":
                        do {
                            System.out.println("Do you want to close the server?");
                            con = scan.nextLine();
                        } while (!con.equalsIgnoreCase("y") && !con.equalsIgnoreCase("n"));
                        if (con.equalsIgnoreCase("y")) {
                            try {
                                closeSockets();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case "message":
                        if (numClients >= 1) {
                            do {
                                System.out.println("Do you want to send a message?");
                                con = scan.nextLine();
                            } while (!con.equalsIgnoreCase("y") && !con.equalsIgnoreCase("n"));
                            if (con.equalsIgnoreCase("y")) {
                                String message;
                                do {
                                    System.out.println("What's your message?");
                                    message = scan.nextLine();
                                    System.out.println("Your Message: " + message);
                                    System.out.println("Do you want to send?(y for Yes)");
                                } while (!con.equalsIgnoreCase("y"));
                                messenger.sendMessage(message);
                            }
                        }
                        break;
                }
            }
        }).start();
    }

    public static void main(String[] args) throws IOException {
        MultiServer main = new MultiServer();
        main.runServer();
    }
}
