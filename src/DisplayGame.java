import java.util.*;
// Class to display the game
public class DisplayGame {
    
    // attribute containing an arraylist of booleans representing where the player has been
    private static ArrayList<ArrayList<Boolean>> displayBoard = new ArrayList<>();

    // associated cave being displayed:
    private static Cave board;

    // scanner to ask for moves
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args){
        /*placeholderBoard(10, 10);
        setPlaceSeen(2, 3);
        setPlaceSeen(2, 3);
        setPlaceSeen(3, 4);
        setPlaceSeen(4, 5);
        setPlaceSeen(5, 3);
        printBoard();*/
        printNeighbours(new int[]{2, 3, 4, 5});
        //getMove(new int[]{2, 3, 4, 5});
    }

    // Method to set up the display board based on the board size
    public static void setUpBoard(){
        // going to need a getter of some sort for the Cave class so this will have to wait
    }

    // Place holder method to generate an all false board of a set size
    public static void placeholderBoard(int rows, int columns){
        for(int i = 0; i < rows; i++){
            displayBoard.add(new ArrayList<>());
            for(int j = 0; j < columns; j++){
                displayBoard.get(i).add(false);
            }
        }
    }

    // Method to display the basic board
    public static void printBoard(){
        for(int i = 0; i < displayBoard.size(); i++){
            for(int j = 0; j < displayBoard.get(i).size(); j++){
                // if the cave is true, the player has been there
                if(displayBoard.get(i).get(j)){
                    System.out.print("X "); // X
                }
                // if the cave is false, the player has not been there
                if(!displayBoard.get(i).get(j)){
                    System.out.print(". "); // .
                }
            }
            System.out.println();
        }
    }

    // Method to set caves in the board to true (when the player moves there)
    public static void setPlaceSeen(int row, int column){
        displayBoard.get(row).set(column, true);
    }
    
    // Methods to get the different bits of input from the user:
        // "You are in room [roomNumber]"
        // "Tunnels lead to [roomNumber, roomNumber, roomNumber, optional roomNumber of where you came from]"
        // "Shoot or Move (S-M)?"
        // "Where to?" <- if move is pressed 
        // "Which room?" <- if shoot is pressed (not the original text but the original makes no sense)
    
    // Method to ask if the player wants to move or shoot
    public static void getMove(int[] neighbours){
        System.out.println("Shoot or Move (S-M)?");
        String decision = scanner.nextLine();
        if(decision.equalsIgnoreCase("S")){
            // call the method for shooting
            System.out.println("You have decided to shoot");
            getAdjacentCell(neighbours, false); // only gets the next room, need to do something  with it
        }
        else if(decision.equalsIgnoreCase("M")){
            // call the method for moving
            System.out.println("You have decided to move");
            getAdjacentCell(neighbours, true); // need to do something with this, currently it just gets the move and nothing else
        }
        else{
            // call the method again if the input is invalid
            getMove(neighbours);
        }
    }

    // Method to ask where the player wants to move to <- should do this as compas directions instead
    public static int getAdjacentCell(int[] neighbours, Boolean move){
        // Passed true if the player has chosen to move
        if(move){
            System.out.println("Where to?");
        }
        // Passed false if the player has chosen to shoot
        else{
            System.out.println("Which room?");
        }
        String room = scanner.nextLine();

        // Make sure the input is an integer
        int roomNumber = 0;
        try{
            roomNumber = Integer.parseInt(room);
        } catch (NumberFormatException e){
            // call the method again if the input is not an integer
            roomNumber = getAdjacentCell(neighbours, move);
            return roomNumber;
        }

        // Check that the input is a valid room number
        Boolean found = false;
        for(int i = 0; i < neighbours.length; i++){
            if(roomNumber == neighbours[i]){
                System.out.println(roomNumber + " is a valid room number");
                found = true;
                break;
            }
        }
        // If it is not a valid number, ask for another input
        if(!found){
            roomNumber = getAdjacentCell(neighbours, move);
        }
        return roomNumber;
    }

    // Methods to print out everything the player needs to know:
        // wumpus nearby
        // treasure nearby
        // pit nearby
        // current room number
        // numbers of the rooms the player can move to

    public static void printWumpus(){
        System.out.println("You smell the wumpus");
    }
    public static void printPit(){
        System.out.println("You feel a breeze");
    }
    public static void printTreasure(){
        System.out.println("You see a shiny glitteringness");
    }
    public static void printRoom(int roomNumber){
        System.out.println("You are in room " + roomNumber);
    }
    // this one could use a little work and formatting
    public static void printNeighbours(int[] neighbours){
        System.out.print("Tunnels lead to rooms "); 
        for(int i = 0; i < neighbours.length - 1; i++){
            System.out.print(neighbours[i] + ". ");
        }
        // start a new line after printing out all the adjacent rooms
        System.out.println();
    }
}
