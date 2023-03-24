import java.util.ArrayList;

// created to write the checking code without worrying about merge conflicts in the main cave class
public class TestChecking {

    // attributes
    private static Cave cave = new Cave(20, 0, 0, new Player("player")); // cave object that the game works around
    private static ArrayList<ArrayList<Integer>> roomNumbers = new ArrayList<>();
    // booleans the game will need to run:
    private static Boolean wumpusAlive = true;
    private static Boolean treasureFound = false;
    private static Boolean lost = false;

    
    // Things this class needs:
        // method to check the content of the cell that the player wants to move to
        // method to check the content of all the surrounding cells and notify the player where appropriate + get a list of room numbers
        // probably make the cave system into a three dimentional array where the third dimention is used to store the room number
            // currently just make another array of room numbers to work with before integrating the two together 
            // using room types is actually good, as it allows the wumpus to be in the same place as other things so it should stay

    public static void main(String[] args){
        createNumbers();
    }
    
    
    // Method to create the roomNumbers based on the cave (needs to be integrated with the board)
    public static void createNumbers(){
        int numbers = 1;
        for(int i = 0; i < cave.size; i++){
            roomNumbers.add(new ArrayList<>());
            for(int j = 0; j < cave.size; j++){
                roomNumbers.get(i).add(numbers);
                numbers++;
            }
        }
    }
    
    // Method to check the current cell
    public static void checkCell(int row, int column){
        // get the specific room in the cave + its type
        Room room = cave.getLayout()[row][column];
        if(room.getWumpusInRoom()){
            // action performed if the wumpus is in the room
            if(wumpusAlive){
                lost = true; // set the game as having been lost
            }
        }

        // Check the different room types
        String type = room.getType();
        if(type.equals("o")){
            // action performed if it is a pit room
            lost = true; // set the game as having been lost
        }
        if(type.equals("w")){
            // action performed if it is a super bat room
        }
        if(type.equals("G")){
            // action performed if it is a treasure room
            treasureFound = true; // set the treasure as having been found
        }
        if(type.equals("X")){
            // action performed if it the exit
            if(treasureFound){
                // action to be performed if that game has been won
            }
        }
    }

    public static ArrayList<Integer> checkNeighbours(int row, int column){
        ArrayList<Integer> neighbours = new ArrayList<>();
        Boolean pit = false; // Keeps track of if the pit message has already been printed to avoid printing it twice
        for(int i = -1; i < 2; i++){
            for(int j = -1; j < 2; j++){
                // discard the middle cell from checking
                if(!(i == 0 && j == 0)){
                    // Make the board toroidal
                    int checkRow = validateRow(row + i);
                    int checkColumn = validateColumn(column + j);

                    // If the cell is in the four adjacent cells then add its number to the neighbours list
                    if(i == 0 || j == 0){
                        neighbours.add(roomNumbers.get(checkRow).get(checkColumn));
                    }

                    // Perform the actual checks on each cell
                    Room room = cave.getLayout()[checkRow][checkColumn];
                    if(room.getWumpusInRoom()){
                        if(wumpusAlive){
                            DisplayGame.printWumpus();
                        }
                    }
                    
                    // Check the different room types
                    String type = room.getType();
                    if(type.equals("o")){
                        if(!pit){
                            DisplayGame.printPit();
                        }
                    }
                    if(type.equals("G")){
                        DisplayGame.printTreasure();
                    }

                }
            }
        }
        return neighbours;
    }

    public static int validateRow(int row){
        if(row < 0){
            row = cave.size-1;
        }
        if(row >= cave.size){
            row = 0;
        }
        return row;
    }

    // possibly change this later
    public static int validateColumn(int column){
        if(column < 0){
            column = cave.size-1;
        }
        if(column >= cave.size){
            column = 0;
        }
        return column;
    }
}
