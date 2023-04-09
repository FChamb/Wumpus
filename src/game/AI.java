package game;

import java.util.*;

public class AI{
    
    // Create a player object that is called 'AI'
    public AI(int caveRows, int caveColumns){
        //previousRooms = new ArrayList<>();
        this.caveRows = caveRows;
        this.caveColumns = caveColumns;
        setUpNumbers();
    }

    // Method to get the information about a specific round:
    public void setInfo(int roomNumber, boolean[] surroundings, String nsew, int wumpusLives){
        this.roomNumber = roomNumber;
        this.surroundings = surroundings;
        this.nsew = nsew;
        this.wumpusLives = wumpusLives;

        // add the room number to the list of rooms the player has been to
        previousRooms.add(roomNumber);
        randomCount = 0; // reset the number of random numbers generated to 0 at the start of every round

        // Check senses before updating maps
        checkBlind();
        checkNose();
        checkBats();
        checkShot();
        updateInfo();
    }

    public void updateInfo(){
        // update information about the location of things at the start of every round
        int[] coords = roomNumbers.get(roomNumber);
        wumpus.put(roomNumber, false); // Make the current cell safe (if this info is wrong the game is already lost so who cares)
        // Only update info about the wumpus when able to smell
        if(blockedNose < 1){
            find(wumpus, coords[0], coords[1], surroundings[0], beenNearWumpus);
            checkWumpusMap();
        }
        // Only update info about the treasure when not blind
        if(blind < 1){
            find(treasure, coords[0], coords[1], surroundings[2], beenNearTreasure);
            checkTreasureMap();
        }
        if(!surroundings[6]){ // if the player has not found the treasure then the current room is false
            treasure.put(roomNumber, false);
        }
        find(pits, coords[0], coords[1], surroundings[1], false);
        pits.put(roomNumber, false);

        // Enter treasure finding mode if near the treasure
        if(surroundings[2]){
            treasureMode = true;
        }
        // Leave treasure finding mode after finding the treasure
        if(surroundings[6]){
            treasureMode = false;
        }
    }

    // Method to check the success of shooting
    public void checkShot(){
        if(shot){
            // if the wumpus is still alive then clear all information about its location because it has moved
            if(wumpusLives > 0){
                clearWumpus();
            }
        }
    }

    // Attributes to store all the information about a round that the game is providing
    private int roomNumber;
    private boolean[] surroundings;
    private String nsew;
    private int wumpusLives;

    // Attributes that the logic operates around
    private HashMap<Integer, int[]> roomNumbers;
    private HashMap<Integer, Boolean> wumpus = new HashMap<>();
    private HashMap<Integer, Boolean> pits = new HashMap<>(); 
    private HashMap<Integer, Boolean> treasure = new HashMap<>();
    private ArrayList<Integer> bats = new ArrayList<>(); // List of rooms that contain super bats so they can be avoided
    private int caveRows;
    private int caveColumns;
    private ArrayList<Integer> previousRooms = new ArrayList<>();
    private ArrayList<String> previousMoves = new ArrayList<>();
    private int[] wumpusLocation = null;
    // Counter to stop it from trying to select a random number forever if it is not possible for it to be unique
    private int randomCount = 0;
    // Counters to work out when the player is blind/blocked nose so the ai can ask accordingly
    private int blind = 0;
    private int blockedNose = 0;
    private boolean treasureMode = false;
    private boolean shooting = false;
    private boolean shot = false;
    private boolean beenNearTreasure = false;
    private boolean beenNearWumpus = false;

    // basic make move method to test that the player actually interacts with the game
    public String makeMove(){
        // Shoot if the wumpus is guaranteed to be in an adjacent room
        if(getWumpusAdjacent()){
            // Print for testing purposes
            int[] coords = roomNumbers.get(roomNumber);
            System.out.println("player coords: " + coords[0] + "    " + coords[1]);
            System.out.println("Wumpus coords: " + wumpusLocation[0] + "    " + wumpusLocation[1]);
            shooting = true;
            System.out.println("S");
            return "S";
        }
        System.out.println("M");
        return "M";
    }


    public String chooseDirection(){
        String move;
        if(shooting){
            move = shootWumpus();
            System.out.println(move);
            shooting = false;
            shot = true;
            return move;
        }
        String safeMoves = getSafeMoves(nsew);
        // If unable to smell or see then walk back and forth for 5 moves
        if(blind > 0 || blockedNose > 0){
            move = moveBackwards();
        }
        else if(treasureMode){
            move = moveToTreasure(safeMoves);
        }
        else{
            move = chooseRandom(safeMoves);
        }
        previousMoves.add(move);
        // Count down how long the ai has been blind for
        blind--;
        blockedNose--;
        System.out.println(move); // print the move to make it look like the AI typed it out
        return move;
    }

    // Method to get a random location for the list of safe moves
    public String chooseRandom(String safeMoves){
        randomCount++; // everytime the method is called it should increase the number of random numbers it generates
        Random random = new Random();
        int index = random.nextInt(safeMoves.split("-").length);
        String direction = safeMoves.split("-")[index];

        // Get another move if it is not unique (but stop after 10 times)
        if(!checkUnique(getRoomMove(direction)) && randomCount < 10){    
            direction = chooseRandom(safeMoves);
        }
        return direction;
    }

    // Method to get coordinates from the room number (allows all the code to revolve around using room numbers)
    public void setUpNumbers(){
        roomNumbers = new HashMap<>();
        for(int i = 0; i < caveRows; i++){
            for(int j = 0; j < caveColumns; j++){
                roomNumbers.put(caveColumns*i + j + 1, new int[]{i, j});
            }
        }
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
        if(safeMoves.size() == 0){
            return nsew; // if there are no safe moves then just move randomly
        }

        return builder.toString().substring(0, builder.toString().length()-1);
    }

    // Method to get the room associated with each move
    public int getRoomMove(String move){
        int[] coords = roomNumbers.get(roomNumber);
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
        // If the room contains a bat return false
        if(bats.contains(roomNumber)){
            return false;
        }
        if(wumpus.containsKey(roomNumber) && pits.containsKey(roomNumber)){
            return !(wumpus.get(roomNumber) || pits.get(roomNumber)); // if either of them are true will return false (not safe)
        }
        // in theory it should not be possible to only be in one map and not the other so i dont need any more checks
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
        if(roomNumbers.size() > 0){
            beenNearWumpus = true;
        }
        // as per the explanation above if there is only one safe square then that square contains the wumpus
        if(roomNumbers.size() == 1){
            wumpusLocation = this.roomNumbers.get(roomNumbers.get(0));
        }
    }

    // General method for finding something
    public void find(HashMap<Integer, Boolean> map, int row, int column, boolean near, boolean beenNear){
        // get all the coordinates of surrounding squares
        for(int i = -1; i < 2; i++){
            for(int j = -1; j < 2; j++){
                // Update whether that cell is safe to walk into (not including middle cell)
                if(!(i == 0 && j == 0)){
                    updateMap(map, validateRow(row + i)*caveRows + validateColumn(column + j) + 1, near, beenNear);
                }
            }
        }
    }
    
    // Method to do something with the map (for wumpus and treasure)
    // Map needs extra logic that if there are already true values in the map, then anything that is now true is actually false (does not apply to pits)
    public void updateMap(HashMap<Integer, Boolean> map, int roomNumber, boolean near, boolean beenNear){
        // Check if the player has already been near the treasure/wumpus (if so discard all new true values)
        if(beenNear){
            // If the room is not already in a map that contains true values then it is false
            if(!map.containsKey(roomNumber)){
                map.put(roomNumber, false);
            }
            // If the room is already in the map and false then return
            else if (map.containsKey(roomNumber) && !map.get(roomNumber)){
                return;
            }
            // If it is in the map and true then update the value to be the new value
            else{
                map.put(roomNumber, near);
            }
            return;
        }

        // If the map does not have any true values
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
        if(roomNumbers.size() > 0){
            beenNearTreasure = true;
        }
    }

    // Method to be called after the wumpus has moved (meaning every cell that was previously safe, is back to being unknown)
    public void clearWumpus(){
        wumpus.clear();
    }

    public String moveTowards(int[] coords, String safeMoves){
        int[] location = roomNumbers.get(roomNumber);
        // if they are not in the same row
        if(location[0] != coords[0]){
            if(location[0] > coords [0]){ // location is further down (right basically)
                if(Math.abs(location[0] - coords[0]) > (coords[0] + caveRows - location[0])){ // if it is further through the board
                    if(safeMoves.contains("S")){
                        return "S";
                    }
                } else{
                    if(safeMoves.contains("N")){
                        return "N";
                    }
                }
            } else{
                if(Math.abs(location[0] - coords[0]) > (caveRows - coords[0] + location[0])){ // if it is further through the board
                    if(safeMoves.contains("N")){
                        return "N";
                    }
                } else{
                    if(safeMoves.contains("S")){
                        return "S";
                    }
                }
            }
        }
        // if they are not in the same column
        if(location[1] != coords[1]){
            if(location[1] > coords [1]){ // location is further right
                if(Math.abs(location[1] - coords[1]) > (coords[1] + caveColumns - location[1])){ // if it is further through the board
                    if(safeMoves.contains("E")){
                        return "E";
                    }
                } else{
                    if(safeMoves.contains("W")){
                        return "W";
                    }
                }
            } else{
                if(Math.abs(location[1] - coords[1]) > (caveColumns - coords[1] + location[1])){ // if it is further through the board
                    if(safeMoves.contains("W")){
                        return "W";
                    }
                } else{
                    if(safeMoves.contains("E")){
                        return "E";
                    }
                }
            }
        }

        // If none of the optimal moves are safe then move randomly
        return chooseRandom(safeMoves);
    }

    public void checkBats(){
        if(surroundings[4]){
            // Get the coords from the second to last room
            int[] previousCoords = roomNumbers.get(previousRooms.get(previousRooms.size()-2));
            int[] batCoords = {previousCoords[0], previousCoords[1]};
            String move = previousMoves.get(previousMoves.size()-1);
            if(move.equalsIgnoreCase("N")){
                batCoords[0] = validateRow(previousCoords[0]-1);
            }
            if(move.equalsIgnoreCase("S")){
                batCoords[0] = validateRow(previousCoords[0]+1);
            }
            if(move.equalsIgnoreCase("E")){
                batCoords[1] = validateColumn(previousCoords[1]+1);
            }
            if(move.equalsIgnoreCase("W")){
                batCoords[1] = validateColumn(previousCoords[1]-1);
            }

            bats.add(batCoords[0]*caveRows + batCoords[1]+1); // add the room where the bat is to the list of rooms containing bats
        }
    }

    public void checkBlind(){
        // Start counting rounds if the player cannot see
        if(!surroundings[5]){
            blind = 5;
        }
    }

    public void checkNose(){
        // Start counting rounds if the player cannot smell
        if(!surroundings[3]){
            blockedNose = 5;
        }
    }

    // Method that controls behaviour when the ai cannot see or smell (just walk back and forward)
    public String moveBackwards(){
        // in theory if it literally just makes the reverse of its last move then it will go back
        String previousMove = previousMoves.get(previousMoves.size()-1);
        if(previousMove.equalsIgnoreCase("N")){
            return "S";
        }
        if(previousMove.equalsIgnoreCase("S")){
            return "N";
        }
        if(previousMove.equalsIgnoreCase("E")){
            return "W";
        }
        if(previousMove.equalsIgnoreCase("W")){
            return "E";
        }

        // if unable to get previous move then just get random move
        return chooseRandom(getSafeMoves(nsew));
    }

    public String moveToTreasure(String safeMoves){
        // basically going to want to move towards a square that might possibly contain the treasure (but is still safe)

        // Create a list of all the possible rooms that the treasure could be in
        ArrayList<Integer> possibleRooms = new ArrayList<>();
        for(int room : treasure.keySet()){
            if(treasure.get(room)){
                possibleRooms.add(room);
            }
        }

        // Pick a random one to move towards <- this can be improved later
        Random random = new Random();
        int index = random.nextInt(possibleRooms.size());
        int[] coords = roomNumbers.get(possibleRooms.get(index));
        return moveTowards(coords, safeMoves); // get a move in the direction of the rooms where the treasure might be
    }

    // Method to work out if the wumpus is in an adjacent room
    public boolean getWumpusAdjacent(){
        // If the wumpus has not been found then it is not in an adjacent square
        if(wumpusLocation == null){
            return false;
        }
        String[] directions = nsew.split("-");
        for(int i = 0; i < directions.length; i++){
            int[] coords = roomNumbers.get(getRoomMove(directions[i]));
            if(coords[0] == wumpusLocation[0] && coords[1] == wumpusLocation[1]){
                return true;
            }
        }
        return false;
    }

    public String shootWumpus(){
        String[] directions = nsew.split("-");
        for(int i = 0; i < directions.length; i++){
            int[] coords = roomNumbers.get(getRoomMove(directions[i]));
            if(coords[0] == wumpusLocation[0] && coords[1] == wumpusLocation[1]){
                return directions[i];
            }
        }
        // should never reach this because method is always called after establishing the wumpus is in an adjacent room
        return chooseRandom(nsew); // if wumpus is not there then shoot in a random direction
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

}
