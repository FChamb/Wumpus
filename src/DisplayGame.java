import java.util.*;

// Class to display the game
public class DisplayGame {

    // attribute containing an arraylist of booleans representing where the player
    // has been
    private ArrayList<ArrayList<Boolean>> displayBoard = new ArrayList<>();

    // associated cave being displayed:
    private Cave board;

    // scanner to ask for moves
    private Scanner scanner = new Scanner(System.in);

    private TestChecking test;

    public static void main(String[] args) {
        /*
         * placeholderBoard(10, 10);
         * setPlaceSeen(2, 3);
         * setPlaceSeen(2, 3);
         * setPlaceSeen(3, 4);
         * setPlaceSeen(4, 5);
         * setPlaceSeen(5, 3);
         * printBoard();
         */
        // printNeighbours(new int[]{2, 3, 4, 5});
        // getMove(new int[]{2, 3, 4, 5});

        DisplayGame game = new DisplayGame(new TestChecking());
        game.getDirection(false);
    }

    public DisplayGame(TestChecking test) {
        this.test = test;
        board = test.getCave();
        setUpBoard(board);
        // placeholderBoard(this.test.getCave().size, this.test.getCave().size);
    }

    // Method to set up the display board based on the board size
    public void setUpBoard(Cave cave) {
        int rows = cave.getLayout().length;
        int columns = cave.getLayout()[0].length;
        for (int i = 0; i < rows; i++) {
            displayBoard.add(new ArrayList<>());
            for (int j = 0; j < columns; j++) {
                displayBoard.get(i).add(false);
            }
        }
    }

    // updates the display board to show where the player has been
    public void updateDisplayBoard(Player player) {
        int[] coords = player.getCoords();
        displayBoard.get(coords[0]).set(coords[1], true);
    }

    // Place holder method to generate an all false board of a set size
    public void placeholderBoard(int rows, int columns) {
        for (int i = 0; i < rows; i++) {
            displayBoard.add(new ArrayList<>());
            for (int j = 0; j < columns; j++) {
                displayBoard.get(i).add(false);
            }
        }
    }

    // Method to display the basic board
    public void printBoard(Player player) {
        int[] playerCoords = player.getCoords();
        for (int i = 0; i < displayBoard.size(); i++) {
            for (int j = 0; j < displayBoard.get(i).size(); j++) {
                // if the player is in that position print "O"
                if (i == playerCoords[0] && j == playerCoords[1]) {
                    System.out.print("O ");
                }
                // if the cave is true, the player has been there
                else if (displayBoard.get(i).get(j)) {
                    System.out.print("X "); // X
                }
                // if the cave is false, the player has not been there
                else if (!displayBoard.get(i).get(j)) {
                    System.out.print(". "); // .
                }
            }
            System.out.println();
        }
    }

    // Method to set caves in the board to true (when the player moves there)
    public void setPlaceSeen(int row, int column) {
        displayBoard.get(row).set(column, true);
    }

    // Methods to get the different bits of input from the user:
    // "You are in room [roomNumber]"
    // "Tunnels lead to [roomNumber, roomNumber, roomNumber, optional roomNumber of
    // where you came from]"
    // "Shoot or Move (S-M)?"
    // "Where to?" <- if move is pressed
    // "Which room?" <- if shoot is pressed (not the original text but the original
    // makes no sense)

    // Method to ask if the player wants to move or shoot
    public void getMove(ArrayList<Integer> neighbours) {
        System.out.println("Shoot or Move (S-M)?");
        String decision = scanner.nextLine();
        if (decision.equalsIgnoreCase("S")) {
            // print the number of arrows the player has
            printArrows(board.getPlayer());
            // call the method for shooting
            if (board.getPlayer().getNumOfArrows() > 0) {
                int roomNumber = getAdjacentCell(neighbours, false); // only gets the next room, need to do something
                                                                     // with it
                test.shootRoom(roomNumber);
            }
        } else if (decision.equalsIgnoreCase("M")) {
            // call the method for moving
            int roomNumber = getAdjacentCell(neighbours, true); // need to do something with this, currently it just
                                                                // gets the move and nothing else
            test.moveRoom(roomNumber);
        } else {
            // call the method again if the input is invalid
            getMove(neighbours);
        }
    }

    // Method to ask where the player wants to move to <- should do this as compas
    // directions instead
    public int getAdjacentCell(ArrayList<Integer> neighbours, Boolean move) {
        // Passed true if the player has chosen to move
        if (move) {
            System.out.println("Where to?");
        }
        // Passed false if the player has chosen to shoot
        else {
            System.out.println("Which room?");
        }
        String room = scanner.nextLine();

        // Make sure the input is an integer
        int roomNumber = 0;
        try {
            roomNumber = Integer.parseInt(room);
        } catch (NumberFormatException e) {
            // call the method again if the input is not an integer
            roomNumber = getAdjacentCell(neighbours, move);
            return roomNumber;
        }

        // Check that the input is a valid room number
        Boolean found = false;
        for (int i = 0; i < neighbours.size(); i++) {
            if (roomNumber == neighbours.get(i)) {
                found = true;
                break;
            }
        }
        // If it is not a valid number, ask for another input
        if (!found) {
            roomNumber = getAdjacentCell(neighbours, move);
        }
        return roomNumber;
    }

    // Method for taking N/S/E/W instead of room numbers
    public String getDirection(boolean move) {
        if (move) {
            System.out.println("What direction do you want to move (N-E-S-W)?");
        } else {
            System.out.println("What direction do you want to shoot (N-E-S-W)?");
        }

        String direction = scanner.nextLine().toLowerCase();
        // call the method again if it is not a valid direction
        if (!direction.equals("n") && !direction.equals("s") && !direction.equals("e") && !direction.equals("w")) {
            direction = getDirection(move);
            return direction;
        }

        return direction;
    }

    // Methods to print out everything the player needs to know:
    // wumpus nearby
    // treasure nearby
    // pit nearby
    // current room number
    // numbers of the rooms the player can move to

    public void printWumpus() {
        System.out.println("You smell the wumpus");
    }

    public void printPit() {
        System.out.println("You feel a breeze");
    }

    public void printTreasure() {
        System.out.println("You see a shiny glitteringness");
    }

    public void printRoom(int roomNumber) {
        System.out.println("You are in room " + roomNumber);
    }

    // this one could use a little work and formatting
    public void printNeighbours(ArrayList<Integer> neighbours) {
        System.out.print("Tunnels lead to rooms ");
        for (int i = 0; i < neighbours.size(); i++) {
            System.out.print(neighbours.get(i) + ". ");
        }
        // start a new line after printing out all the adjacent rooms
        System.out.println();
    }

    // Messages to print when the player losses the game
    public void printPitLoss() {
        System.out.println("You have fallen in a bottomless pit and cannot escape");
    }

    public void printWumpusLoss() {
        System.out.println("The wumpus has found you");
    }

    public void printWumpusKill() {
        System.out.println("You here hear a terrible cry. You're arrow must have hit the wumpus");
    }

    public void printVictory() {
        System.out.println("You have found the treasure and escaped the cave");
        System.out.println("Congratulations on winning the game");
    }

    public void printBat() {
        System.out.println("A superbat has picked you up");
    }

    public void printTreasureFound() {
        System.out.println("You have found the treasure");
    }

    public void printWumpusMiss() {
        System.out.println("You here the sound of an arrow hitting stone");
    }

    public void printNoArrows() {
        System.out.println("You have run out of arrows");
    }

    public void printArrows(Player player) {
        System.out.println("You have " + player.getNumOfArrows() + " arrows");
    }
}
