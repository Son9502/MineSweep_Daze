package SweeperChat;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * This is the class for the MineSweeper Game Object.
 */
public class MineMap {
    private int size;
    private int[][] mapKey;
    private int[][] map;
    private ArrayList<MyTuple<Integer>> mines;
    private int mineLeft;
    private int spacesLeft;
    private double mineRatio;
    private final double defRat = 0.5;
    private boolean inPlay;
    private double score;
    public static final String zero = "\u001B[0m";
    public static final String one = "\u001b[36m";
    public static final String two = "\u001b[34m";
    public static final String three = "\u001B[35m";
    public static final String four = "\u001b[32m";
    public static final String five = "\u001b[33m";
    public static final String six = "\u001b[38;5;208m";
    public static final String seven = "\u001b[38;5;208m";
    public static final String eight = "\u001b[38;5;130m";
    public static final String nine = "\u001B[31m";
    public static final String mine = "\u001b[38;5;124m";
    public static final String reset = "\u001B[0m";
    public static final String question = "\u001b[38;5;195m";
    public static final String spotted = "\u001b[47m" + mine;
    private Scanner scan = new Scanner(System.in);
    private boolean mapFull = false;

    /**
     * This is the main constructor for the Minesweeper Object.
     *
     * @param dim   is the size of the square grid (must be above 5)
     * @param ratio is the ratio of mines to total spaces (between 0% - 60%)
     */
    public MineMap(int dim, double ratio) {
        int defSize = 5;
        size = dim >= defSize ? dim : defSize;
        mineRatio = (ratio >= 0 && ratio <= 0.6) ? ratio : defRat;
        resetMyMap(size);
        mapKey = new int[size][size];
        mineLeft = (int) (size * size * mineRatio);
        spacesLeft = (size * size) - mineLeft;
        mines = new ArrayList<>();
        inPlay = false;
        score = (int) ((1 + (mineRatio/(1.0 - mineRatio))) * size * 100);
    }

    /**
     * This is a constructor for an object without a ratio.
     *
     * @param dim is the size of the square grid (must be above 5)
     */
    public MineMap(int dim) {
        this(dim, 0.23);
    }

    /**
     * This is a constructor for an object without a grid size.
     *
     * @param ratio is the ratio of mines to total spaces (between 0% - 60%)
     */
    public MineMap(double ratio) {
        this(5, ratio);
    }

    /**
     * This is a constructor for an object without a ratio and size.
     */
    public MineMap() {
        this(5, 0.23);
    }

    /**
     * This sets the color for the different numbers displayed on the map.
     *
     * @param num is the number on the map
     * @return the color picked.
     */
    public String fillColors(int num) {
        switch (num) {
            case -4:
                return spotted;
            case -2:
                return question;
            case 0:
                return zero;
            case 1:
                return one;
            case 2:
                return two;
            case 3:
                return three;
            case 4:
                return four;
            case 5:
                return five;
            case 6:
                return six;
            case 7:
                return seven;
            case 8:
                return eight;
            case 9:
                return nine;
            default:
                return mine;
        }
    }

    /**
     * This lets you guess to see whether the space is free or a mine
     *
     * @param newMine is your guess.
     * @param mine    is to enter Mine Mark/Unmark Mode.
     */
    public void guess(MyTuple<Integer> newMine, boolean mine) {
        if (0 > newMine.getX() | size <= newMine.getX() | 0 > newMine.getY() | size <= newMine.getY()) {
            System.out.println("Coordinates out of Range");
        } else if (map[newMine.getX()][newMine.getY()] > -2) {
            System.out.println("Spot picked already");
        } else {
            if (!mine) {
                if (mapKey[newMine.getX()][newMine.getY()] == -1) {
                    System.out.println("Mine found :(");
                    map = mapKey;
                    map[newMine.getX()][newMine.getY()] = -4;
                    System.out.println(this);
                    inPlay = false;
                } else {
                    map[newMine.getX()][newMine.getY()] = mapKey[newMine.getX()][newMine.getY()];
                    if (spacesLeft == 0) {
                        System.out.println("You win!");
                        inPlay = false;
                    } else {
                        spacesLeft--;
                        System.out.println("There are " + spacesLeft + " spaces left. Keep looking");
                        System.out.println(this);
                    }
                }
            } else {
                map[newMine.getX()][newMine.getY()] += map[newMine.getX()][newMine.getY()] == -3 ? 1 : -1;
                System.out.println(this);
            }

        }
    }

    /**
     * This returns the String version of the map, depending on whether you're playing or not
     *
     * @return map (the key if not playing, the current map if you are).
     */
    public String toString() {
        return inPlay ? curr() : key();
    }

    /**
     * This resets the current map.
     *
     * @param dim is the size of the map.
     */
    public void resetMyMap(int dim) {
        map = new int[dim][dim];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                map[i][j] = -2;
            }
        }

    }

    /**
     * This prints the key of the map(Where the mines are and the spaces interacting)
     *
     * @return the key.
     */
    public String key() {
        String minefield = "";
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                minefield += fillColors(mapKey[i][j]);
                minefield += mapKey[i][j] != -1 ? mapKey[i][j] : "X";
                minefield += j != size - 1 ? " " : "";
            }

            minefield += i != size - 1 ? (reset + " N ") : "";
        }
        minefield += reset;
        return minefield.strip();
    }

    /**
     * This prints out the current map so far.
     *
     * @return the current map
     */
    public String curr() {
        String minefield = "";
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                minefield += fillColors(map[i][j]);
                if (map[i][j] == -4 || map[i][j] == -1) {
                    minefield += "X" + reset;
                } else if (map[i][j] == -3) {
                    minefield += "ø";
                } else if (map[i][j] == -2) {
                    minefield += "?";
                } else if (map[i][j] != -1) {
                    minefield += +map[i][j];
                }
                minefield += j != size - 1 ? " " : "";
            }
            minefield += i != size - 1 ? "\n" : "";
        }
        minefield += reset;
        return minefield.strip();
    }

    /**
     * This prints the actual key.
     *
     * @return the actual key
     */
    public String getKey() {
        String minefield = "";
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                minefield += fillColors(mapKey[i][j]);
                minefield += mapKey[i][j] != -1 ? mapKey[i][j] : "X";
                minefield += j != size - 1 ? " " : "";
            }
            minefield += i != size - 1 ? (reset + "\n") : "";
        }
        minefield += reset;
        return minefield.strip();
    }

    /**
     * This sets the inPlay attribute on.
     */
    public void play() {
        inPlay = true;
    }

    /**
     * This return the size, ratio and mapKey so that others can decode it.
     *
     * @return the String representing the map aspects
     */
    public String getSpecs() {
        boolean currPlay = inPlay;
        inPlay = false;
        String message = size + "," + mineRatio + "," + this;
        inPlay = currPlay;
        return message;
    }

    /**
     * This creates a new Mine and places it on the Map if possible
     *
     * @param newMine is the coordinates of the new mine
     * @param auto    is whether the creator is a real person or the computer
     */
    public void placeMine(MyTuple<Integer> newMine, Boolean auto) {
        if (0 > newMine.getX() | size <= newMine.getX() | 0 > newMine.getY() | size <= newMine.getY()) {
            if (!auto) {
                System.out.println("Coordinates out of Range");
            }
        } else if (mines.contains(newMine)) {
            if (!auto) {
                System.out.println("Mine already exists");
            }
        } else if (mineLeft == 0) {
            if (!auto) {
                System.out.println("Already have enough mines");
            }
        } else {
            updateMap(newMine);
            if (!auto) {
                System.out.println("Successful Placement");
            }
            if (mineLeft == 0) {
                mapFull = true;
            }
        }
    }

    /**
     * Gets the score of the map.
     * @return the score.
     */
    public double getScore(){
        return score;
    }
    /**
     * This updates the Map everytime a mine is created.
     *
     * @param newMine is the new mine being placed.
     */
    public void updateMap(MyTuple<Integer> newMine) {
        int startX = newMine.getX();
        int startY = newMine.getY();
        mapKey[startX][startY] = -1;
        startX--;
        startY--;
        for (int i = 0; i < 3; i++) {
            if (startX + i >= 0 && startX + i < size) {
                for (int j = 0; j < 3; j++) {
                    if (startY + j >= 0 && startY + j < size && mapKey[startX + i][startY + j] != -1) {
                        mapKey[startX + i][startY + j]++;
                    }
                }
            }
        }
        mines.add(newMine);
        mineLeft--;
    }

    /**
     * This fills up the map with the remaining map by choosing random coordinates for the mines.
     */
    public void random() {
        Random rand = new Random();
        while (mineLeft > 0) {
            int x = rand.nextInt(size);
            int y = rand.nextInt(size);
            MyTuple<Integer> newMine = new MyTuple<>(x, y);
            placeMine(newMine, true);
        }
    }

    /**
     * This is the bulider function of this Map object.
     */
    public void createMap() {
        String cont;
        String answ;
        MyTuple<Integer> mine;
        int x;
        int y;
        do {
            System.out.println("Current Map: \n" + getKey());
            do {
                System.out.println("There are " + mineLeft + " mines left. Do you want to still place mines?(y/n)");
                cont = scan.nextLine();
            } while (!cont.equalsIgnoreCase("y") && !cont.equalsIgnoreCase("n"));
            if (cont.equalsIgnoreCase("y")) {
                do {
                    System.out.println("Enter X:");
                    x = scan.nextInt();
                    System.out.println("Enter Y:");
                    y = scan.nextInt();
                    scan.nextLine();
                    do {
                        System.out.printf("Coordinates: (%d,%d)? (y/n)\n", x, y);
                        answ = scan.nextLine();
                    } while (!answ.equalsIgnoreCase("y") && !answ.equalsIgnoreCase("n"));
                } while (!answ.equalsIgnoreCase("y"));
                mine = new MyTuple<>(y - 1, x - 1);
                placeMine(mine, false);
            }
        } while (cont.equalsIgnoreCase("y"));
        if (mineLeft != 0) {
            random();
        }
    }

    /**
     * This returns the size of the map.
     * @return the size
     */
    public int getSize(){
        return size;
    }

    /**
     * This returns the ratio of mines to available spaces.
     * @return the ratio
     */
    public double getMineRatio(){
        return mineRatio;
    }
    /**
     * This is the MineSweeper Game function
     *
     * @return whether you win or not.
     */
    public double playGame() {
        if (mineLeft != 0) {
            random();
        }
        play();
        int x;
        int y;
        String ans = "";
        boolean mine;
        while (inPlay && spacesLeft > 0) {
            do {
                System.out.println("Place/Unplace a mine? (y/n):");
                ans = scan.nextLine();
            } while (!ans.equalsIgnoreCase("y") && !ans.equalsIgnoreCase("n"));
            mine = ans.equalsIgnoreCase("y");
            do {
                System.out.println("Enter X:");
                x = scan.nextInt();
                System.out.println("Enter Y:");
                y = scan.nextInt();
                scan.nextLine();
                do {
                    System.out.printf("Coordinates: (%d,%d)? (y/n)\n", x, y);
                    ans = scan.nextLine();
                } while (!ans.equalsIgnoreCase("y") && !ans.equalsIgnoreCase("n"));

            } while (!ans.equalsIgnoreCase("y"));
            guess(new MyTuple<>(y - 1, x - 1), mine);
        }
        int maxSize = size * size - mines.size();
        double perCent = (1.0 * maxSize - spacesLeft)/maxSize;
        reset(0);
        return perCent;
    }

    /**
     * This resets the object based on the erase (Space/Key Erase)
     *
     * @param type determines how much we erasing (Space + Key = 1)
     */
    public void reset(int type) {
        resetMyMap(size);
        spacesLeft = (size * size) - mines.size();
        inPlay = false;
        if (type == 1) {
            mapKey = new int[size][size];
            mineLeft = (int) (mineRatio * size * size);
            mines = new ArrayList<>();
            mapFull = false;
        }
    }

    public boolean isMapFull() {
        return mapFull;
    }

    public static void main(String[] args) {
        MineMap test = new MineMap(5, 0.45);
        //test.createMap();
        System.out.println(1 + Double.parseDouble("123.23322323"));
        for (int i = 0; i < 60; i++){
            MineMap trial = new MineMap(5, i / 100.0);
            trial.random();
            System.out.println((i/100.0+ ":" + trial.getScore()));
        }
        for (int i = 0; i < 60; i++){
            MineMap trialTwo = new MineMap(i, 0.25);
            trialTwo.random();
            System.out.printf("%d by %d: %f\n", trialTwo.size, trialTwo.size, 1.0 * trialTwo.getScore());
        }
        System.out.println(test.getSpecs());
        System.out.println(1 + Double.parseDouble("123.23322323"));
        test.playGame();
    }
}

