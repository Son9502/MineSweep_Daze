package SweeperChat;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

/**
 * This is like the MultiServer class, but it limits how many people can be logged in at a time
 */
public class RestrictServer extends MultiServer{
    protected int clientLimit;
    protected boolean game;
    public RestrictServer(){
        super();
        Scanner scan = new Scanner(System.in);
        String ans;
        int limit;
        do{
            System.out.println("Do you want a limit of clients to join?(y/n)");
            ans = scan.nextLine().toLowerCase();
        } while (!ans.equals("y") && !ans.equals("n"));
        if (ans.equals("n")){
            limit = -1;
        }
        else{
            do{
                System.out.println("Limits of Clients: ");
                limit = scan.nextInt();
            } while (limit < 1);
            scan.nextLine();
        }
        do{
            System.out.println("Will users be playing games on this? (y/n)");
            ans = scan.nextLine().toLowerCase();
        } while (!ans.equals("y") && !ans.equals("n"));
        game = ans.equals("y") ? true: false;
        clientLimit = limit;
    }
    /**
     * Same as the super(), but actually manages the
     * @throws IOException
     */
    protected void letIn() throws IOException {
        serSoc = new ServerSocket(portNum);
        System.out.println("*This is Server " + portNum + "*");
        if (clientLimit < 0){
            System.out.println("*Client Limit: None*");
        }
        else{
            System.out.println("*Client Limit: " + clientLimit + "*");
        }
        command();
        while (!serSoc.isClosed()){
            try{
                checkThreads();
                if (messenger == null){
                    setMessengerUp();
                }
                soc = serSoc.accept();
                checkThreads();
                if (activate) {
                    if (clientLimit == -1 || numClients < clientLimit) {
                        numClients += 1;
                        System.out.println("A new client has joined");
                        System.out.println("Clients Logged in: " + numClients);
                        Thread thread;
                        if (game) {
                            MineHandler clientH = new MineHandler(soc, portNum);
                            thread = new Thread(clientH);
                            thread.start();
                            clients.add(clientH);
                            messenger.getClients().add(clientH);
                        } else {
                            ClientHandler clientH = new ClientHandler(soc, portNum);
                            thread = new Thread(clientH);
                            thread.start();
                            clients.add(clientH);
                            messenger.getClients().add(clientH);
                        }
                        if (numClients == clientLimit) {
                            System.out.println("Max # of clients reached");
                        }
                    } else {
                        soc.close();
                    }
                }
                else {activate = true;}
            }
            catch (IOException error){
                closeSockets();
            }
            catch (NullPointerException error){
                checkThreads();
            }
            catch (Exception error){
                System.out.println(error.getMessage());
                closeSockets();
            }
        }

    }

    /**
     * This starts and maintains the Server.
     * @throws IOException
     */
    public void runServer() throws IOException {
        begin();
        letIn();
    }

    /**
     * This helps the Server interact with the details of the chat.
     */
    protected void command(){
        new Thread(() -> {
            String con;
            Scanner scan = new Scanner(System.in);
                while (!serSoc.isClosed()){
                    String command = scan.nextLine();
                    switch (command.toLowerCase()){
                        case "close":
                            do{
                               System.out.println("Do you want to close the server?");
                               con = scan.nextLine();
                            } while(!con.equalsIgnoreCase("y") && !con.equalsIgnoreCase("n"));
                            if (con.equalsIgnoreCase("y")){
                                try{
                                    closeSockets();
                                    System.out.println("Closing Server...");
                                }
                                catch (IOException e){
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case "limit change":
                            int limit;
                            do{
                                System.out.println("Do you want to change the limit?");
                                con = scan.nextLine();
                            } while(!con.equalsIgnoreCase("y") && !con.equalsIgnoreCase("n"));
                            if (con.equalsIgnoreCase("y")){
                                do{
                                    System.out.println("Limits of Clients: ");
                                    limit = scan.nextInt();
                                } while (limit < -1 || limit == 0);
                                clientLimit = limit;
                                System.out.println("New Limit: " + (clientLimit == -1 ? "None" : clientLimit));
                            }
                            break;
                        case "message":
                            if (numClients >= 1){
                                do{
                                    System.out.println("Do you want to send a message?");
                                    con = scan.nextLine();
                                } while(!con.equalsIgnoreCase("y") && !con.equalsIgnoreCase("n"));
                                if (con.equalsIgnoreCase("y")){
                                    con = "";
                                    String message;
                                    do{
                                        System.out.println("What's your message?");
                                        message = scan.nextLine();
                                        System.out.println("Your Message: " + message);
                                        System.out.println("Do you want to send?(y for Yes)");
                                        con = scan.nextLine();
                                    } while(!con.equalsIgnoreCase("y"));
                                    messenger.sendMessage(message);
                                }
                            }
                            break;
                    }
                }
        }).start();
    }
    public static void main(String[] args) throws IOException{
        RestrictServer main = new RestrictServer();
        main.runServer();
    }
}
