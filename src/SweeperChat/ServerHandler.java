package SweeperChat;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ServerHandler {
    public static ArrayList<ServerHandler> serverList = new ArrayList<>();
    protected Socket socket;
    protected BufferedReader reader;
    protected BufferedWriter writer;
    protected String name;
    protected volatile boolean exit;
    protected int portNum;
    protected ArrayList<ClientHandler> clients;
    //protected Thread myThread;
    public ServerHandler(Socket s, int n) {
        if (!exists(n)){
            try {
                socket = s;
                writer = new BufferedWriter(new OutputStreamWriter((socket.getOutputStream())));
                reader = new BufferedReader(new InputStreamReader((socket.getInputStream())));
                portNum = n;
                name = "Server " + portNum;
                exit = false;
                serverList.add(this);
                clients = new ArrayList<>();
                //myThread = new Thread(this);
                System.out.println("(Connected to " + name + ")");
            }
            catch (IOException e){
                closeAll();
            }
        }
    }
    protected void removeClient(){
        serverList.remove(this);
        exit = true;
        closeAll();
    }
    public ArrayList<ClientHandler> getClients(){
        return clients;
    }
    protected void closeAll(){
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
    protected boolean exists(int pn){
        for (ServerHandler server : serverList){
            if (server.portNum == pn){
                return true;
            }
        }
        return false;
    }
    public void sendMessage(String message){
        try{
            for (ClientHandler client : clients){
                client.writer.write(name + ": " +message);
                client.writer.newLine();
                client.writer.flush();
            }
        }
        catch (IOException e){
            closeAll();
        }
    }
    /*public void run(){

    }*/

}
