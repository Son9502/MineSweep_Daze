Readme File for the MineSweep Project 

Files/Classes included: 
- MultiServer 
- RestrictServer (child of MultiServer) 
- ClientHandler  
- MineHandler (child of ClientHandler)
- MultiClient 
- MineClient (child of MultiClient) 
- MineMap  
- MyTuple 
- MineDecoder 

Prerequisites:
- Install the latest version of Java
- If necessary to debug, also download IntelliJ Idea CE (Community Edition) (*recommended to check errors) 

Running Code in Terminal: 
- Navigate to the folder holding the code 
- Compile the code about to be run (javax ClassName.java) 
- Run the code (java ClassName.java) 

Setting up Code in IntelliJ CE: 
- Click 'Open Project' in the File 
- Find where you unzipped this file and click on the folder 
- For the Server and Client Files, find the class name in the box next to the green hammer and make sure the name of the class is in that box 
- Click the box again and click Edit Configurations. 
- Click modify options, make sure the "Allow Multiple Instances" is checked and press Apply after if not. 

Summary: This project simulates a chat room between users, where they can talk but also construct Minesweeper maps and send them to one another to play for points/glory(Only one can send it at a time). 


Running the Project: 
 - Part 1/3 (Involved Classes: MultiServer, ClientHandler, MultiClient): First, run the MultiServer file(You can put a sample port number if needed, but easier to use the default). After setting that up, open 2+ MultiClient instances and follow instructions there. You can try sending messages through both instances to see whether they appear in the other instance to show that the message is being received. 'exit' is a keyword that helps a client leave the program. 

 - Part 2/3 (Involved Classes: MineMap, MyTuple): Run an instance of the MineMap file and set up a map, and play the game. You may see a single line out of nowhere giving the specs of the map you just built. (When they ask for MineRatio, it means the ratio of mines to spaces) 

 - Part 3/3 (Involved Classes: Practically everything; just need to run RestrictServer and MineClient instances): First, run the RestrictServer file and set it up (You should make it have no limit of clients, be available for games, and port number is up to you. Next, run 2+ instances of the MineClient class and set them up. In one of the MineClient files, put in the 'build' keyword to set up your map. At first, you have to send the grid size twice to proceed(Major error). There's also the 'send' keyword to make sure that the other clients can get your map. 

Major errors: 
- When a Client is setting up their map, you have to send the grid name in twice to proceed. The number is sent as a message to the other clients when it's not supposed to. Ideally, the ReadMessage method shouldn't be listening at this time. 