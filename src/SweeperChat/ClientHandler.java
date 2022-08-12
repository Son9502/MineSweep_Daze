package SweeperChat;

import java.io.*;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.net.Socket;

public class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clientList = new ArrayList<>();
    public static int noNameCount = 1;
    protected Socket socket;
    protected BufferedReader reader;
    protected BufferedWriter writer;
    protected String name;
    protected volatile boolean exit;
    protected int portNum;
    protected Thread myThread;
    protected int past = 0;
    public ClientHandler(Socket s, int n){
        try{
            socket = s;
            writer = new BufferedWriter(new OutputStreamWriter((socket.getOutputStream())));
            reader = new BufferedReader(new InputStreamReader((socket.getInputStream())));
            name = reader.readLine();
            boolean flag = false;
            while (!userExists(name)){
                if (name.equals("User")) {
                    name += noNameCount++;
                    sendMessage(this,"New Name:" + name);
                    flag = true;
                }
                else{
                    sendMessage(this,"This name has been taken");
                    name = reader.readLine();
                }
            }
            if(!flag){
                sendMessage(this,"Name is Good");
            }
            portNum = n;
            exit = false;
            clientList.add(this);
            myThread = new Thread(this);
            String message = "*Welcome to Server " + portNum + ", " + name + "!* \n*Please use the 'exit' keyword if you want to leave*";
            sendMessage(this,message);
            int size = clientList.size();
            switch (size){
                case 1:
                    sendMessage(this, "*You are the only one in this Server*");
                    break;
                default:
                    String mess = "(Other people in chat: ";
                    for (ClientHandler client : clientList){
                        mess += !client.name.equals(name) ? client.name + ", " : "";
                    }
                    mess = mess.substring(0,mess.length() - 2) + ")";
                    sendMessage(this,mess);
                    break;
            }
            if (name != ""){
                groupMessage( name + " has just joined Server " + portNum + "!");
            }
        }
        catch (IOException e ){
            closeAll();
        }
    }
    protected void groupMessage(String newMess) throws IOException{
        for (ClientHandler client : clientList){
            if (!(client.name).equals(name)){
                sendMessage(client,newMess);
            }
        }
    }
    protected void sendMessage(ClientHandler client, String newMess) throws IOException{
        try{
            client.writer.write(newMess);
            client.writer.newLine();
            client.writer.flush();
        }
        catch (IOException e){
            closeAll();
        }
    }
    protected boolean userExists(String newName){
        for (ClientHandler client : clientList){
            if (client.name.equals(newName)){
                return false;
            }
        }
        return true;
    }
    public Boolean getExit(){
        return exit;
    }
    public String getName(){
        return name;
    }
    protected void removeClient(){
        try{
            ClientHandler removed = this;
            clientList.remove(this);
            exit = true;
            if (clientList.size() > 1){
                groupMessage(removed.name + " has left. :(");
            }
        }
        catch (IOException e){
            closeAll();
        }
    }
    protected void closeAll(){
        if (clientList.contains(this)){
            removeClient();
        }
        try{
            if (reader != null){
                reader.close();
            }
            if (writer != null){
                writer.close();
            }
            if (socket != null){
                socket.close();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    protected int checkMessage(String newMessage) { //edit time
        if (newMessage != null){
            if (!exit){
                if (newMessage.equalsIgnoreCase(name + ":exit")){
                    exit = true;
                    return 1;
                }
            }
            else{
                String response = newMessage.substring(name.length() + 1).toLowerCase();
                if(response.equals("y")){
                    return 2;
                }
                else if(!response.equals("n")){
                    return 1;
                }
                exit = false;
            }
        }
        return 0;
    }
    public void run(){
        String newMessage;
        while (socket.isConnected() && !myThread.isInterrupted()){
            try{
                newMessage = reader.readLine();
                int check = checkMessage(newMessage);
                switch(check){
                    case 0:
                        if (past != 1){
                            groupMessage(newMessage);
                        }
                        else{
                            sendMessage(this,"Ok. be careful what you send.");
                        }
                        break;
                    case 1:
                        sendMessage(this,"Are you sure?");
                        break;
                    case 2:
                        if (exit){
                            sendMessage(this,"Goodbye :(");
                            removeClient();
                            myThread.interrupt();
                            break;
                        }
                }
                past = check;
            }
            catch(IOException e){
                closeAll();
                break;
            }
            catch (NullPointerException e){
                removeClient();
                break;
            }
        }
    }
    public static void main(String [] args) throws IOException{
        /*ServerSocket serverSocket = new ServerSocket(1465);
        Socket soc = serverSocket.accept();
        MineHandler test = new MineHandler(soc,serverSocket.getLocalPort());
        test.myThread.start();*/
    }
}
