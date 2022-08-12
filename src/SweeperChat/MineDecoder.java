package SweeperChat;

public class MineDecoder {
    private MineMap decodedMap;
    public MineDecoder(String encoded){
        decodedMap = decodeMap(encoded);
    }
    public MineMap getMap(){
        return decodedMap;
    }

    /**
     * This creates the map and stores it.
     * @param value is the encoded map.
     * @return the decoded Map.
     */
    private MineMap decodeMap(String value){
        String[] details = value.split(",");
        int width = Integer.valueOf(details[0]);
        double ratio = Double.valueOf(details[1]);
        MineMap newMap = new MineMap(width,ratio);
        int currRow = 0;
        for (String row : details[2].split("N")){
            String[] fullRow = row.split(" ");
            int currCol = 0;
            while (currCol < width){
                if (fullRow[currCol].equals(MineMap.mine + "X")){
                    MyTuple<Integer> newMine = new MyTuple<>(currRow,currCol);
                    newMap.placeMine(newMine,true);
                }
                currCol++;
            }currRow++;
        }
        return newMap;
    }

    /**
     * This returns the Specifications of the Map
     * @return the map's details;
     */
    public String toString(){
        String message = "Map Details:\n";
        message += "\t" + getMap().getSize() + " x " + getMap().getSize() + " grid" + "\n";
        message += "\tMine Ratio: " + getMap().getMineRatio() + "\n";
        message += "\tMine Count: " + (int) (getMap().getMineRatio() * getMap().getSize()) + "\n";
        double maxPoints = getScore();
        message += "\tMaximum Points: " + maxPoints;
        return message;
    }
    public double getScore(){
        return decodedMap.getScore();
    }
    public static void main(String [] args){
        MineMap test = new MineMap(20,0.37);
        test.random();
        MineDecoder tester = new MineDecoder(test.getSpecs());
        System.out.println(tester);
        tester.getMap().playGame();
    }

}
