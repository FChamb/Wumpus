package game;

import java.util.*;
public class AI{

    public static void main(String[] args){
        AI ai = new AI(20, 20);
        ai.previousRooms.add(12);
        ai.previousRooms.add(44);
        ArrayList<Integer> numbers = new ArrayList<>();
        numbers.add(44);
        numbers.add(89);
        numbers.add(7);
        numbers.add(12);
        System.out.println(ai.makeMove(numbers));
        //ai.makeMove(numbers);
    }
    
    // Create a player object that is called 'AI'
    public AI(int caveRows, int caveColumns){
        previousRooms = new ArrayList<>();
        this.caveRows = caveRows;
        this.caveColumns = caveColumns;
    }

    // Method to get the information about a specific round:
    public void setInfo(int roomNumber, boolean[] surroundings, String nsew, boolean smell){
        this.roomNumber = roomNumber;
        this.nearWumpus = surroundings[0];
        this.nearPit = surroundings[1];
        this.nearTreasure = surroundings[2];
        this.nsew = nsew;
        this.smell = smell; // only sets whether the player has been made unable to smell this round
    }

    // Attributes to store all the information about a round that the game is providing
    private int roomNumber;
    private boolean nearWumpus; // first index of surroundings
    private boolean nearPit; // second index of surroundings
    private boolean nearTreasure; // third index of surroundings
    private String nsew;
    private boolean smell;

    // basic make move method to test that the player actually interacts with the game
    public String makeMove(){
        System.out.println("m");
        return "m";
    }

    // just gets a random direction to work out if the game can actually interact with the computer
    public String chooseDirection(){
        Random random = new Random();
        int index = random.nextInt(nsew.split("-").length);
        String direction = nsew.split("-")[index];
        System.out.println(direction);
        return direction;
    }

    //private ArrayList<ArrayList<Integer>> wumpusLocation;
    private ArrayList<ArrayList<Integer>> treasureLocation;
    private HashMap<String, Boolean> wumpus;
    private int caveRows;
    private int caveColumns;
    private ArrayList<Integer> previousRooms;
    private int[] wumpusLocation = null;

    // Method to move randomly from a list of room numbers
    public int makeMove(ArrayList<Integer> neighbours){
        Random random = new Random();
        int neighbour = random.nextInt(neighbours.size());
        // Get a new number if the room numbers is one the AI has already been in
        while(!checkUnique(neighbours.get(neighbour))){
            neighbour = random.nextInt(neighbours.size());
        }

        return neighbours.get(neighbour);
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

    // Method to wumpus hunt
    public void findWumpus(int row, int column, boolean unsafe){
        // get all the coordinates of surrounding squares + current square
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 2; j++){
                int validRow = validateRow(row + i);
                int validColumn = validateColumn(column + j);
                // Construct the name of the cell
                String cellName = validRow + "x" + validColumn;
                // Update whether that cell is safe to walk into
                updateWumpusMap(cellName, unsafe);
            }
        }
    }

    // Method to do something with the map containing all the cells that it has been next to
    public void updateWumpusMap(String cellName, boolean unsafe){
        if(wumpus.containsKey(cellName)){
            // if it is in the map as a safe cell then leave it as false
            if(!wumpus.get(cellName)){
                return;
            }
        }
        // otherwise update the map to have the new value
        wumpus.put(cellName, unsafe);
    }

    // Method to check the map <- what i am about to say might sound crazy but if there is only one unsafe square in the map then the wumpus must be there
    // either there are no unsafe squares (player has never been next to the wumpus) or there is more than one unsafe square (player has not
    // pinpointed the wumpus' location). once there are unsafe squares, if there is only one then the wumpus must be there
    public void checkWumpusMap(){
        ArrayList<String> names = new ArrayList<>();
        for(String name : wumpus.keySet()){
            if(wumpus.get(name)){
                names.add(name);
            }
        }
        // as per the explanation above if there is only one safe square then that square contains the wumpus
        if(names.size() == 1){
            wumpusLocation = getCoords(names.get(0));
        }
    }

    // Method to be called after the wumpus has moved (meaning every cell that was previously safe, is back to being unknown)
    public void clearWumpus(){
        wumpus.clear();
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
