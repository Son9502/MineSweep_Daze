package SweeperChat;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class MineClient extends MultiClient {
    private MineMap myMap;
    private MineDecoder otherMap;
    private volatile AtomicBoolean newBusy = new AtomicBoolean();
    public MineClient(){
        super();
    }
    public void sendMess(String message) throws IOException{
        try{
            buffWrite.write(message);
            buffWrite.newLine();
            buffWrite.flush();
        }
        catch (IOException e){
            System.out.println("SendMess: " + e.getMessage());
            closeAll();
        }
    }
    protected Thread sendMessage() throws Exception {
        return new Thread(() -> {
            while (soc.isConnected()){
                try {
                    if (newBusy != null && !newBusy.get()){
                        sendMess(name + ":" + buffScan.readLine().strip());
                    }
                } catch (Exception e) {
                    System.out.println("SendMessage: " + e.getMessage());
                    closeAll();
                    break;
                }
            }
        });
    }
    private void buildMyMap() {
        try{
            String ans;
            int size;
            double ratio;
            do {
                System.out.println("What size do you want the grid to be?");
                //sendMess("Begin");
                //buffScan.readLine();
                size = Integer.parseInt(buffScan.readLine());
                size = size >= 5 ? size : 5;
                do {
                    System.out.printf("You want to build a %d x %d map. Are you sure?(y/n)\n", size, size);
                    ans = buffScan.readLine();
                } while (!ans.equalsIgnoreCase("y") && !ans.equalsIgnoreCase("n"));
            } while (!ans.equalsIgnoreCase("y"));
            do {
                System.out.println("What ratio of mines do you want? 0-60(max)");
                ratio = Integer.parseInt(buffScan.readLine());
                ratio = ratio <= 60 ? ratio : 60;
                do {
                    System.out.printf("You want to have a ratio of %d. Are you sure?(y/n)\n", (int) ratio);
                    ans = buffScan.readLine();
                } while (!ans.equalsIgnoreCase("y") && !ans.equalsIgnoreCase("n"));
            } while (!ans.equalsIgnoreCase("y"));
            myMap = new MineMap(size, ratio / 100);
            myMap.createMap();
            sendMess("End");
        }
        catch (IOException e){
            System.out.println("BuildMyMap: " + e.getMessage());
            closeAll();
        }
    }

    protected Thread receiveMessage() {
        return new Thread(() -> {
            String newGroupMessage;
            String message = "";
            while (soc.isConnected()) {
                try {
                    newGroupMessage = buffRead.readLine();
                    if (newGroupMessage.startsWith("Loading Map...") && !newGroupMessage.equals("Loading Map...")){
                        System.out.println("Loading Map...");
                        newBusy.compareAndSet(false,true);
                        otherMap = new MineDecoder(newGroupMessage.substring(14));
                        double ratio = otherMap.getMap().playGame();
                        System.out.println("You gained " + (int) (ratio * otherMap.getScore()) + " points");
                        message = name + (ratio == 1 ? " Won: " : " Lost: ") + ratio;
                    }
                    else{
                        System.out.println(newGroupMessage);
                        switch (newGroupMessage) {
                            case "Goodbye":
                                closeAll();
                                System.exit(0);
                                break;
                            case "Building Map...":
                                newBusy.compareAndSet(false,true);
                                //sendMess("Begin");
                                buildMyMap();
                                break;
                            case "Acquiring Map...":
                                newBusy.compareAndSet(false,true);
                                message = myMap.getSpecs();
                                break;
                        }
                    }
                    if (newBusy != null && newBusy.get()){
                        if (!message.isEmpty()) {
                            sendMess(message);
                            message = "";
                        }
                        newBusy.compareAndSet(true,false);
                    }
                } catch (Exception e) {
                    System.out.println("RecieveMessage: " + e.getLocalizedMessage());
                    closeAll();
                    break;
                }
            }
        });
    }
    public static void main(String[] args) {
        MineClient newClient = new MineClient();
    }
}
