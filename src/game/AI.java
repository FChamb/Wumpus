package game;

import java.util.*;

public class AI{

    public static void main(String[] args){
        AI ai = new AI(20, 20);
    }
    
    // Create a player object that is called 'AI'
    public AI(int caveRows, int caveColumns){
        previousRooms = new ArrayList<>();
        this.caveRows = caveRows;
        this.caveColumns = caveColumns;
        setUpNumbers();
    }

    // Method to get the information about a specific round:
    public void setInfo(int roomNumber, boolean[] surroundings, String nsew, boolean smell){
        this.roomNumber = roomNumber;
        this.nearWumpus = surroundings[0];
        this.nearPit = surroundings[1];
        this.nearTreasure = surroundings[2];
        this.nsew = nsew;
        this.smell = smell; // only sets whether the player has been made unable to smell this round

        // add the room number to the list of rooms the player has been to
        previousRooms.add(roomNumber);
        randomCount = 0; // reset the number of random numbers generated to 0 at the start of every round


        // update information about the location of things at the start of every round
        int[] coords = getCoords(roomNumber);
        wumpus.put(roomNumber, false); // Make the current cell safe (if this info is wrong the game is already lost so who cares)
        find(wumpus, coords[0], coords[1], nearWumpus);
        checkWumpusMap();
        //System.out.println(wumpus);
        find(treasure, coords[0], coords[1], nearTreasure);
        checkTreasureMap();
        System.out.println(treasure);
        find(pits, coords[0], coords[1], nearPit);
        wumpus.put(roomNumber, false);
    }

    // Attributes to store all the information about a round that the game is providing
    private int roomNumber;
    private boolean nearWumpus; // first index of surroundings
    private boolean nearPit; // second index of surroundings
    private boolean nearTreasure; // third index of surroundings
    private String nsew;
    private boolean smell;

    // Attributes that the logic operates around
    private HashMap<Integer, String> roomNumbers;
    private HashMap<Integer, Boolean> wumpus = new HashMap<>();
    private HashMap<Integer, Boolean> pits = new HashMap<>(); 
    private HashMap<Integer, Boolean> treasure = new HashMap<>();
    private int caveRows;
    private int caveColumns;
    private ArrayList<Integer> previousRooms;
    private int[] wumpusLocation = null;
    private int[] treasureLocation = null;
    // Counter to stop it from trying to select a random number forever if it is not possible for it to be unique
    private int randomCount = 0;

    // basic make move method to test that the player actually interacts with the game
    public String makeMove(){
        System.out.println("m");
        return "m";
    }

    // just gets a random direction to work out if the game can actually interact with the computer
    public String chooseDirection(){
        randomCount++; // everytime the method is called it should increase the number of random numbers it generates
        Random random = new Random();
        String safeMoves = getSafeMoves(this.nsew);
        int index = random.nextInt(safeMoves.split("-").length);
        String direction = safeMoves.split("-")[index];

        // Get another move if it is not unique (but stop after 10 times)
        if(!checkUnique(getRoomMove(direction)) && randomCount < 10){    
            direction = chooseDirection();
        }
        else{
            System.out.println(direction);
        }
        return direction;
    }

    // Method to get coordinates from the room number (allows all the code to revolve around using room numbers)
    public void setUpNumbers(){
        roomNumbers = new HashMap<>();
        for(int i = 0; i < caveRows; i++){
            for(int j = 0; j < caveColumns; j++){
                roomNumbers.put(caveColumns*i + j + 1, i + "x" + j);
            }
        }
    }

    public int[] getCoords(int roomNumber){
        String coords = roomNumbers.get(roomNumber);
        return getCoords(coords);
    }

    public String getSafeMoves(String nsew){
        String[] part = nsew.split("-");
        HashMap<Integer, String> safeMoves = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        // only take the ones that are safe
        for(int i = 0; i < part.length; i++){
            if(checkSafe(getRoomMove(part[i]))){
                builder.append(part[i] + "-");
                safeMoves.put(getRoomMove(part[i]), part[i]);
            }
        }

        return builder.toString().substring(0, builder.toString().length()-1);
    }

    public int getRoomMove(String move){
        int[] coords = getCoords(roomNumber);
        int[] roomCoords = new int[2];
        if(move.equalsIgnoreCase("N")){
            roomCoords = new int[]{validateRow(coords[0] - 1), coords[1]};
        }
        if(move.equalsIgnoreCase("S")){
            roomCoords = new int[]{validateRow(coords[0] + 1), coords[1]};
        }
        if(move.equalsIgnoreCase("E")){
            roomCoords = new int[]{coords[0], validateColumn(coords[1] + 1)};
        }
        if(move.equalsIgnoreCase("W")){
            roomCoords = new int[]{coords[0], validateColumn(coords[1] - 1)};
        }
        // Return the room number of the requested room
        return roomCoords[0]*caveRows + roomCoords[1] + 1;
    }

    // Method to check if the AI has been in a room previously
    public boolean checkUnique(int roomNumber){
        for(int i = 0; i < previousRooms.size(); i++){
            // if the Ai has already been in a certain room
            if(previousRooms.get(i) == roomNumber){
                return false;
            }
        }
        return true;
    }

    // Method to check if the square is safe (true if safe, false if not)
    public boolean checkSafe(int roomNumber){
        if(wumpus.containsKey(roomNumber)){
            return !wumpus.get(roomNumber);
        }
        return true;
    }

    // Method to check the wumpus map to see if the wumpus' location has been pinpointed
    public void checkWumpusMap(){
        ArrayList<Integer> roomNumbers = new ArrayList<>();
        for(int name : wumpus.keySet()){
            if(wumpus.get(name)){
                roomNumbers.add(name);
            }
        }
        // as per the explanation above if there is only one safe square then that square contains the wumpus
        if(roomNumbers.size() == 1){
            wumpusLocation = getCoords(roomNumbers.get(0));
            //System.out.println(wumpusLocation[0] + " " + wumpusLocation[1]);
        }
    }

    // General method for finding something
    public void find(HashMap<Integer, Boolean> map, int row, int column, boolean near){
        // get all the coordinates of surrounding squares
        for(int i = -1; i < 2; i++){
            for(int j = -1; j < 2; j++){
                // Update whether that cell is safe to walk into (not including middle cell)
                if(!(i == 0 && j == 0)){
                    updateMap(map, validateRow(row + i)*caveRows + validateColumn(column + j) + 1, near);
                }
            }
        }
    }
    
    // Method to do something with the map
    public void updateMap(HashMap<Integer, Boolean> map, int roomNumber, boolean near){
        if(map.containsKey(roomNumber)){
            // if it is in the map as a safe cell then leave it as false
            if(!map.get(roomNumber)){
                return;
            }
        }
        // otherwise update the map to have the new value
        map.put(roomNumber, near);
    }
    
    // Method to the map for the location of the treasure
    public void checkTreasureMap(){
        ArrayList<Integer> roomNumbers = new ArrayList<>();
        for(int roomNumber : treasure.keySet()){
            if(treasure.get(roomNumber)){
                roomNumbers.add(roomNumber);
            }
        }
        // as per the explanation above if there is only one safe square then that square contains the wumpus
        if(roomNumbers.size() == 1){
            treasureLocation = getCoords(roomNumbers.get(0));
            System.out.println(treasureLocation[0] + " " + treasureLocation[1]);
        }
    }

    // Method to be called after the wumpus has moved (meaning every cell that was previously safe, is back to being unknown)
    public void clearWumpus(){
        wumpus.clear();
    }

    // Method to walk towards specific coordinates
    public void moveTowards(int[] coords){
        
    }

    public int validateRow(int row){
        if(row < 0){
            row = caveRows-1;
        }
        if(row >= caveRows){
            row = 0;
        }
        return row;
    }

    public int validateColumn(int column){
        if(column < 0){
            column = caveColumns-1;
        }
        if(column >= caveColumns){
            column = 0;
        }
        return column;
    }

    // Takes a square name in the form 0x0 and splits it into its parts
    public int[] getCoords(String string){
        String[] parts = string.split("x");
        int[] coords = new int[2];
        coords[0] = Integer.parseInt(parts[0]);
        coords[1] = Integer.parseInt(parts[1]);
        return coords;
    }

}
