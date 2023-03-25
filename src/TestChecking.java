import java.util.ArrayList;
import java.util.Random;

// created to write the checking code without worrying about merge conflicts in the main cave class
public class TestChecking {

    // attributes
    private Cave cave = new Cave(20, 0, 0, new Player("player")); // cave object that the game works around
    private DisplayGame display;
    private ArrayList<ArrayList<Integer>> roomNumbers = new ArrayList<>();
    // booleans the game will need to run:
    private Boolean wumpusAlive = true;
    private Boolean treasureFound = false;
    private Boolean lost = false;
    private Boolean won = false;

    
    // Things this class needs:
        // method to check the content of the cell that the player wants to move to
        // method to check the content of all the surrounding cells and notify the player where appropriate + get a list of room numbers
        // probably make the cave system into a three dimentional array where the third dimention is used to store the room number
            // currently just make another array of room numbers to work with before integrating the two together 
            // using room types is actually good, as it allows the wumpus to be in the same place as other things so it should stay

    public static void main(String[] args){
        TestChecking test = new TestChecking();
        test.createNumbers();
        test.playGame();
    }

    public DisplayGame getDisplay() {
        return this.getDisplay();
    }

    public Cave getCave() {
        return this.cave;
    }

    // Method to play through the motions of the game
    public void playGame(){
        this.display = new DisplayGame(this); // board is set up in the constructor
        //display.printBoard();
        while(!won && !lost){
            // get the players current position
            int[] coords = cave.getPlayer().getCoords();

            // check current cell is safe
            checkCell(coords[0], coords[1]);
            // Stop playing if checking the players position determined a loss (can honestly just move this outside the loop)
            if(won || lost){
                return;
            }

            // print the cave (including the players position)
            display.updateDisplayBoard(cave.getPlayer());
            display.printBoard(cave.getPlayer());

            // print out the room number the player is in
            int roomNumber = roomNumbers.get(coords[0]).get(coords[1]);
            this.display.printRoom(roomNumber);

            // print the cave details for testing purposes
            //printCaveDetails();

            // check content of neighbouring cells
            ArrayList<Integer> neighbours = checkNeighbours(coords[0], coords[1]);

            // print out the neighbouring cells
            this.display.printNeighbours(neighbours);

            // get the players next move
            this.display.getMove(neighbours);
            System.out.println();
        }
    }
    
    
    // Method to create the roomNumbers based on the cave (needs to be integrated with the board)
    public void createNumbers(){
        int numbers = 1;
        for(int i = 0; i < cave.size; i++){
            roomNumbers.add(new ArrayList<>());
            for(int j = 0; j < cave.size; j++){
                roomNumbers.get(i).add(numbers);
                numbers++;
            }
        }
    }

    public void printCaveDetails(){
        for(int i = 0; i < cave.getLayout().length; i++){
            for(int j = 0; j < cave.getLayout()[i].length; j++){
                //System.out.print(cave.getLayout()[i][j].toString().substring(1));
                System.out.print(roomNumbers.get(i).get(j) + "\t");
            }
            System.out.println();
        }
    }
    
    // Method to check the current cell
    public void checkCell(int row, int column){
        // get the specific room in the cave + its type
        Room room = cave.getLayout()[row][column];
        if(room.getWumpusInRoom()){
            // action performed if the wumpus is in the room
            if(wumpusAlive){
                this.display.printWumpusLoss();
                lost = true; // set the game as having been lost
            }
        }

        // Check the different room types
        String type = room.getType();
        if(type.equals("o")){ // pit room
            this.display.printPitLoss();
            lost = true; // set the game as having been lost
        }
        if(type.equals("w")){ // superbat room
            this.display.printBat();
            // update the players position
            placePlayer();
        }
        if(type.equals("G")){ // treasure room
            this.display.printTreasureFound();
            treasureFound = true; // set the treasure as having been found
        }
        if(type.equals("X")){ // exit room
            if(treasureFound){
                this.display.printVictory();
                won = true;
            }
        }
    }

    public ArrayList<Integer> checkNeighbours(int row, int column){
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
                            this.display.printWumpus();
                        }
                    }
                    
                    // Check the different room types
                    String type = room.getType();
                    if(type.equals("o")){
                        if(!pit){
                            this.display.printPit();
                        }
                    }
                    if(type.equals("G")){
                        this.display.printTreasure();
                    }

                }
            }
        }
        return neighbours;
    }

    public int validateRow(int row){
        if(row < 0){
            row = cave.size-1;
        }
        if(row >= cave.size){
            row = 0;
        }
        return row;
    }

    // possibly change this later
    public int validateColumn(int column){
        if(column < 0){
            column = cave.size-1;
        }
        if(column >= cave.size){
            column = 0;
        }
        return column;
    }

    // Method to place the player and the wumpus in the cave
    public void placePlayer(){
        int[] coords = this.cave.getRandom();
        cave.getPlayer().setCoords(coords[0], coords[1]);
    }
    public void placeWumpus(){
        int[] coords = this.cave.getRandom();
        cave.getWumpus().setCoords(coords[0], coords[1]);
    }

    // Method to move with a provided room number (there is definitely a better way to do this that i will think of another time)
    public void moveRoom(int roomNumber){
        for(int i = 0; i < roomNumbers.size(); i++){
            for(int j = 0; j < roomNumbers.size(); j++){
                if(roomNumbers.get(i).get(j) == roomNumber){
                    cave.getPlayer().setCoords(i, j);
                    return;
                }
            }
        }
    }

    // Method to shoot with a provided room number
    public void shootRoom(int roomNumber){
        // Shooting does nothing if the wumpus is dead
        if(!wumpusAlive){
            return;
        }
        // Shooting does nothing if the player is out of arrows
        if(!cave.getPlayer().useArrow()){
            return;
        }
        for(int i = 0; i < roomNumbers.size(); i++){
            for(int j = 0; j < roomNumbers.size(); j++){
                if(roomNumbers.get(i).get(j) == roomNumber){
                    if(cave.getLayout()[i][j].getWumpusInRoom()){
                        this.display.printWumpusKill();
                        wumpusAlive = false;
                    }
                    else {
                        this.display.printWumpusMiss();
                        // move the wumpus to a random other room
                        placeWumpus();
                    }
                    return;
                }
            }
        }
    }
}
