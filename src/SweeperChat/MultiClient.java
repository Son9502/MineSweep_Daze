package SweeperChat;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class MultiClient {
    protected BufferedReader buffRead;
    protected BufferedWriter buffWrite;
    protected BufferedReader buffScan;
    protected Socket soc;
    protected String name;
    protected static final int pn = 1465;
    protected String iPAddress;
    protected int portNum;
    protected Thread readThread;
    protected Thread sendThread;
    protected Scanner scan;
    public MultiClient(){
        createClient();
    }
    public void sendMess(String message) throws IOException{
        try{
            buffWrite.write(message);
            buffWrite.newLine();
            buffWrite.flush();
        }
        catch (IOException e){
            closeAll();
        }
    }
    protected void setUpThreads() throws Exception{
        readThread = receiveMessage();
        sendThread = sendMessage();
    }
    protected Thread sendMessage() throws Exception{
        return new Thread(() -> {
            try{
                while (soc.isConnected()){
                    String newMessage = buffScan.readLine().strip();
                    sendMess(name + ":" + newMessage);
                }
            }
            catch (IOException e){
                closeAll();
            }
        });
    }
    protected Thread receiveMessage(){
        return new Thread(() -> {
            String newGroupMessage;
            while(soc.isConnected()){
                try{
                    newGroupMessage = buffRead.readLine();
                    System.out.println(newGroupMessage);
                    if (newGroupMessage.equals("Goodbye")){
                        closeAll();
                        System.exit(0);
                    }
                }
                catch (Exception e){
                    closeAll();
                }
            }
        });
    }

    protected void closeAll(){
        try{
            if (buffRead != null){
                buffRead.close();
            }
            if (buffWrite != null){
                buffWrite.close();
            }
            if (soc != null){
                soc.close();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    protected void setUpClient(Socket s, String n){
        try{
            this.name = n;
            this.soc = s;
            this.buffWrite = new BufferedWriter(new OutputStreamWriter((soc.getOutputStream())));
            this.buffRead = new BufferedReader(new InputStreamReader((soc.getInputStream())));
            this.buffScan = new BufferedReader(new InputStreamReader(System.in));
            setUpThreads();
        }
        catch (IOException e){
            closeAll();
        }
        catch (Exception e){
            closeAll();
        }
    }
    protected void setServer(){
        Scanner scan = new Scanner(System.in);
        String ans;
        String finalCon;
        do{
            do{
                System.out.println("Do you have a Server's IP Address? (y/n)");
                ans = scan.nextLine().toLowerCase();
            } while (!"y".equalsIgnoreCase(ans) && !"n".equalsIgnoreCase(ans));
            if (ans.equals("y")){
                System.out.println("What is the IP Address of the Server?");
                iPAddress = scan.nextLine();
            }
            else{
                iPAddress = "localhost";
            }
            do{
                System.out.println("Do you have a particular server you want to join? (y/n)");
                ans = scan.nextLine().toLowerCase();
            } while (!"y".equalsIgnoreCase(ans) && !"n".equalsIgnoreCase(ans));
            if (ans.equals("y")){
                System.out.println("Port Number?");
                portNum = scan.nextInt();
                scan.nextLine();
            }
            else{
                portNum = pn;
            }
            System.out.println("IP Address: " + iPAddress);
            System.out.println("Port Number: " + portNum);
            System.out.println("Does this look correct to you? (y for Yes)");
            finalCon = scan.nextLine();
        } while(!finalCon.equalsIgnoreCase("y"));
    }
    protected void createClient(){
        setServer();
        try{
            soc = new Socket(iPAddress,portNum);
            System.out.println("(Connected Successfully to Server)");
            System.out.println("Your name/username is: ");
            this.scan = new Scanner(System.in);
            String newName = scan.nextLine().strip();
            String name = !newName.equals("") ? newName : "User";
            setUpClient(soc,name);
            sendMess(name);
            setName();
            this.run();
        }
        catch(IOException e){
            System.out.println("Couldn't connect to Server");
            e.printStackTrace();

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    protected void setName(){
        String dataBase;
        try{
            dataBase = buffRead.readLine();
            if (dataBase.startsWith("New Name:")){
                name = dataBase.substring(9);
                return;
            }
            while (dataBase.equals("This name has been taken") ){
                System.out.println(dataBase + ". Choose a new name");
                System.out.println("New name/username is: ");
                scan = new Scanner(System.in);
                String newName = scan.nextLine();
                name = !newName.equals("") ? newName.strip() : "User";
                this.sendMess(name);
                dataBase = buffRead.readLine();
            }
        }
        catch(IOException e){
            closeAll();
        }
    }
    public void run() {
        readThread.start();
        sendThread.start();
    }
    public static void main(String[] args){
        MultiClient newClient = new MultiClient();
    }
}
