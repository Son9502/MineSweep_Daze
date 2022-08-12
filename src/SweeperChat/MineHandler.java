package SweeperChat;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class MineHandler extends ClientHandler {
    private int score;
    protected volatile boolean send; // Checking to see if you want to sent your map
    protected volatile boolean create; // Checking to see if you want to create your map
    protected volatile static boolean mapSent; // Checking to see if the map was sent
    protected volatile static int mapScore;
    protected static String sender;
    protected volatile static int count;
    protected AtomicInteger play; // 0 = notSet, 1 = playing, 2 = notPlaying;
    protected volatile boolean readBusy;
    protected static String currMap;
    public MineHandler(Socket s, int n) {
        super(s,n);
        send = false;
        create = false;
        play = new AtomicInteger(0);
        readBusy = false;
        count = 0;
    }

    protected void sendMessage(ClientHandler client, String newMess) throws IOException {
        if (client.past != 2) {
            try {
                client.writer.write(newMess);
                client.writer.newLine();
                client.writer.flush();
            } catch (IOException e) {
                closeAll();
            }
        }
    }

    protected void groupMessage(String newMess) throws IOException {
        for (ClientHandler client : clientList) {
            if (!(client.name).equals(name)){// && !busy){
                sendMessage(client, newMess);
            }
        }
    }
    /*protected boolean isInt(String num){
        if (num.isEmpty()){
            return false;
        }
        for (int i = 0; i < num.length();i++){
            if (num.charAt(i) - '0' > 9 || num.charAt(i) - '0' < 0){
                return false;
            }
        }
        return true;
    }*/
    protected int checkMessage(String newMessage) {
        int ret = 0;
        if (newMessage != null && past != 2) {
            if (mapSent && play.get() == 0){ // If a map has been sent;
                if (newMessage.equalsIgnoreCase(name + ":y")){
                    play.addAndGet(1);
                    ret = 2;
                }
                else{
                    play.addAndGet(2);
                    ret = 0;
                }
                count++;
                if (count >= clientList.size()){
                    mapSent = false;
                }
            }
            else if (!exit && !send && !create) { //Checking for keywords
                if (newMessage.equalsIgnoreCase(name + ":exit")) {
                    exit = true;
                    ret =  1;
                } else if (newMessage.equalsIgnoreCase(name + ":build")) {
                    create = true;
                    ret = 1;
                } else if (newMessage.equalsIgnoreCase(name + ":send")) {
                    send = true;
                    ret = 1;
                }
            } else {
                String response = newMessage.substring(name.length() + 1).toLowerCase();
                if (response.equals("y") && (exit || send || create)) {
                    ret = 2;
                } else if (response.equals("n")) {
                    exit = false;
                    create = false;
                    send = false;
                } else {
                    ret = 1;
                }
            }
        }
        return ret;
    }

    public void run() {
        String newMessage = "";
        int check;
        while (socket.isConnected() && !myThread.isInterrupted()) {
            try {
                newMessage = reader.readLine();
                if (readBusy){
                    readBusy = false;
                }
                else{
                    check = checkMessage(newMessage);
                    switch (check) {
                        case 0:
                            if (past == 1) {
                                sendMessage(this, "Ok. be careful what you send.");
                            } else if (past == 2 && play.get() == 0) {
                                if (create) {
                                    groupMessage("*" + name + " has finished building their map*");
                                    create = false;
                                }
                                if (send) {
                                    if (sender.equals(name)){
                                        currMap = newMessage;
                                        mapScore = (int) (new MineDecoder(currMap).getMap().getScore());
                                        groupMessage("Maximum points from map: " + mapScore);
                                        sender = "";
                                    }
                                    send = false;
                                }
                            } else {
                                if (play.get() == 1){
                                    double gain = 1.0 * MineHandler.mapScore;
                                    if (newMessage.startsWith(name + " Lost:")){
                                        groupMessage("Map Score:" + mapScore);
                                        gain *= Double.parseDouble(newMessage.substring(name.length() + 6));
                                        score += gain;
                                        newMessage = name + " lost and gained " + (int) gain;
                                    }
                                    else if (newMessage.startsWith(name + " Won:")){
                                        gain *= Double.parseDouble(newMessage.substring(name.length() + 5));
                                        score += gain;
                                        newMessage = name + " won and gained " + (int) gain;
                                    }
                                    newMessage += " points (Current Score: " + score + ")";
                                }
                                play.addAndGet(play.get() * -1);
                                groupMessage(newMessage);
                            }
                            break;
                        case 1:
                            sendMessage(this, "Are you sure?");
                            break;
                        case 2:
                            //busy = true;
                            if (exit) {
                                sendMessage(this, "Goodbye :(");
                                removeClient();
                                myThread.interrupt();
                                break;
                            } else if (create) {
                                groupMessage("*" + name + " is building their map*");
                                sendMessage(this, "Building Map...");
                                readBusy = true;
                            } else if (send) {
                                if (!mapSent) {
                                    mapSent = true;
                                    sender = name;
                                    sendMessage(this, "Acquiring Map...");
                                    groupMessage(name + " sent their map. Do you want to play?(y for Yes)");
                                } else {
                                    sendMessage(this, "Sorry, but someone already sent their map");
                                    send = false;
                                    check = 0;
                                }
                            }
                            else if (play.get() == 1){
                                groupMessage("*" + name + " is playing the map*");
                                sendMessage(this, "Loading Map..." + currMap);
                                readBusy = true;
                            }
                            else {
                                check = 1;
                            }
                            break;
                    }
                    past = check;
                }
            } catch (IOException e) {
                closeAll();
                break;
            }
            catch (NullPointerException e){
                closeAll();
                break;
            }
        }
    }
    /*public static void main(String [] args) throws IOException{
        ServerSocket serverSocket = new ServerSocket(1465);
        while (1 == 1){
            Socket soc = serverSocket.accept();
            MineHandler test = new MineHandler(soc,serverSocket.getLocalPort());
            test.myThread.start();
        }
    }*/
}
