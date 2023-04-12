package game;

import java.util.ArrayList;

public class Cave {
    int xSize;
    int ySize;
    int numOfPits;
    int numOfSupBats;
    int numOfArtifacts;
    int numWalls;
    private final Room[][] cave;
    private final Player player;
    private final Wumpus wumpus;
    private ArrayList<String> validRooms;

    /**
     * Cave constructor takes seven parameters and creates a cave based on this given instructions. First
     * a check sees if the provided values, in total, are greater than the number of available spaces. If
     * so a new exception is thrown. Each of the private attributes in this class are then set to the
     * specified rule. Lastly generateCave() is called which creates the layout for the 2D cave.
     * @param xSize - height of the cave
     * @param ySize - width of the cave
     * @param numOfPits - number of pits in the cave
     * @param numOfSupBats - number of super bats in the cave
     * @param numWalls - number of walls in the cave
     * @param numOfArtifacts - number of artifacts in the cave
     * @param player - a player
     */
    public Cave(int xSize, int ySize, int numOfPits, int numOfSupBats, int numWalls, int numOfArtifacts, Player player) {
        if (numOfPits + numOfSupBats + numWalls + numOfArtifacts + 1 + xSize + ySize > xSize * ySize) {
            throw new ArrayIndexOutOfBoundsException("Too many objects defined for the size of cave!");
        }
        this.xSize = xSize;
        this.ySize = ySize;
        this.numOfPits = numOfPits;
        this.numOfSupBats = numOfSupBats;
        this.numWalls = numWalls;
        this.cave = new Room[this.xSize][this.ySize];
        this.player = player;
        this.wumpus = new Wumpus();
        this.numOfArtifacts = numOfArtifacts;
        this.validRooms = new ArrayList<>();
        generateCave();
    }

    /**
     * Getter for returning the layout of the cave
     * @return - the 2D array of rooms which contain the cave
     */
    public Room[][] getLayout(){
        return this.cave;
    }


    /**
     * Getter for the player specified to this instance of cave
     * @return - the current player
     */
    public Player getPlayer(){
        return this.player;
    }

    /**
     * Getter for the Wumpus specified to this instance of cave
     * @return - the current Wumpus
     */
    public Wumpus getWumpus(){
        return this.wumpus;
    }

    /**
     * GenerateCave uses a nested for loops to fill the 2D array
     * of the cave with new empty rooms. The arraylist of valid rooms
     * is also filled with every object. Create path and generate
     * obstacles are called after.
     */
    public void generateCave() {
        for (int i = 0; i < this.xSize; i++) {
            for (int j = 0; j < this.ySize; j++) {
                this.cave[i][j] = new Room();
                this.validRooms.add(i + "x" + j);
            }
        }
        createPath();
        generateObstacles();
    }

    /**
     * Create path makes a valid path from the start to the end in which no objects can spawn, therefore making
     * the game beatable. It first creates an arraylist of strings, path, which will contain the coordinates
     * of each room on the valid path. Then the start of the path is found by calling getRandom. This room is
     * removed from the validRooms list. A for loop cycles up the length of the path. In each instance of the loop
     * the last room is stored and a while loop grabs the next direction ensuring that it is not equal to where the
     * path has come from. A switch loop looks at the next direction to travel and sets the x/y coords properly,
     * taking into account the torus layout of the cave. Finally, the player is set in the first room of the path, the
     * treasure is set halfway through, and the exit is put at the end of the path.
     */
    public void createPath() {
        ArrayList<String> path = new ArrayList<>();
        int[] start = getRandom();
        int length = this.xSize + this.ySize;
        int lastRoom = -1;
        for (int i = 1; i <= length; i++) {
            int nextRoom = (int) (Math.random() * 4);
            while (lastRoom != -1 && nextRoom == lastRoom) {
                nextRoom = (int) (Math.random() * 4);
            }
            int x = start[0];
            int y = start[1];
            switch (nextRoom) {
                case 0:
                    lastRoom = 1;
                    if (x == 0) {
                        x = this.xSize - 1;
                    } else {
                        x--;
                    }
                    break;
                case 1:
                    lastRoom = 0;
                    if (x == this.xSize - 1) {
                        x = 0;
                    } else {
                        x++;
                    }
                    break;
                case 2:
                    lastRoom = 3;
                    if (y == 0) {
                        y = this.ySize - 1;
                    } else {
                        y--;
                    }
                    break;
                case 3:
                    lastRoom = 2;
                    if (y == this.ySize - 1) {
                        y = 0;
                    } else {
                        y++;
                    }
                    break;
            }
            // if (path.contains(x + "x" + y)) {
            //     i--;
            //     continue;
            // }
            path.add(x + "x" + y);
            this.validRooms.remove(x + "x" + y);
            start = new int[]{x, y};
        }
        int x = Integer.parseInt(path.get(0).split("x")[0]);
        int y = Integer.parseInt(path.get(0).split("x")[1]);
        this.player.setCoords(x, y);
        x = Integer.parseInt(path.get(path.size() / 2).split("x")[0]);
        y = Integer.parseInt(path.get(path.size() / 2).split("x")[1]);
        this.cave[x][y] = new Room( "G");
        x = Integer.parseInt(path.get(path.size() - 1).split("x")[0]);
        y = Integer.parseInt(path.get(path.size() - 1).split("x")[1]);
        this.cave[x][y] = new Room("X");
    }

    /**
     * GenerateObstacles first creates a new attribute for every obstacle. Each of these are set to
     * zero or true to allow the program to know how many have been placed. A total value is calculated
     * from all the user defines numbers. A while loop cycles while the total is greater than zero. In this
     * loop a new random location is chosen from the valid options. A random value is chosen to decide which
     * object should be placed. And then a check ensures that the object has less placed than the total number
     * required. If this is true the cave sets the appropriate type and that objects value is increased, and total
     * is decreased. The valid room is also removed.
     */
    public void generateObstacles() {
        int numPits = 0;
        int numBats = 0;
        boolean wumpus = true;
        int numOfItems = 0;
        int numWalls = 0;
        int total = this.numOfPits + this.numOfSupBats + this.numWalls + this.numOfArtifacts + 1;
        int x; int y;
        while (total > 0) {
            int[] location = getRandom();
            x = location[0]; y = location[1];
            int choice = (int) (Math.random() * 5);
            switch (choice) {
                case 0:
                    if (numPits < this.numOfPits) {
                        this.cave[x][y] = new Room("o");
                        numPits++;
                        total--;
                        this.validRooms.remove(x + "x" + y);
                    }
                    break;
                case 1:
                    if (numBats < this.numOfSupBats) {
                        this.cave[x][y] = new Room("w");
                        numBats++;
                        total--;
                        this.validRooms.remove(x + "x" + y);
                    }
                    break;
                case 2:
                    if (wumpus) {
                        this.wumpus.setCoords(x, y);
                        wumpus = false;
                        total--;
                        this.validRooms.remove(x + "x" + y);
                    }
                    break;
                case 3:
                    if (numOfItems < this.numOfArtifacts) {
                        this.cave[x][y].setArtifact(Artifact.getRandom());
                        numOfItems++;
                        total--;
                        this.validRooms.remove(x + "x" + y);
                    }
                    break;
                case 4:
                    if (numWalls < this.numWalls) {
                        this.cave[x][y] = new Room(Room.WLL);
                        numWalls++;
                        total--;
                        this.validRooms.remove(x + "x" + y);
                    }
                    break;
            }
        }
    }

    /**
     * GetRandom find a random value from the private array list of valid rooms. Then
     * the coordinates of that room are grabbed and returned in an array.
     * @return - an array of ints containing the coords
     */
    public int[] getRandom() {
        int i = (int) (Math.random() * this.validRooms.size());
        int x = Integer.parseInt(this.validRooms.get(i).split("x")[0]);
        int y = Integer.parseInt(this.validRooms.get(i).split("x")[1]);
        return new int[]{x, y};
    }

    /**
     * SetPlayer takes a random room in the cave, ensures it is not a wall, pit, or other super bat
     * and sets the player to that new location.
     */
    public void setPlayer() {
        int x = (int) (Math.random() * this.cave.length);
        int y = (int) (Math.random() * this.cave[1].length);
        if (this.cave[x][y].getType().matches("w|o| ") || (this.wumpus.getCoords()[0] == x && this.wumpus.getCoords()[1] == y)) {
            setPlayer();
        } else {
            this.player.setCoords(x, y);
        }
    }

    /**
     * toString() uses a nested for loop to fill a string values with the layout of the
     * current game state. This value is returned.
     * @return - a string with the layout of the board
     */
    public String toString() {
        String output = "\n";
        for (int i = 0; i < this.cave.length; i++) {
            for (int j = 0; j < this.cave[0].length; j++) {
                if (this.player.getCoords()[0] == i && this.player.getCoords()[1] == j) {
                    output += this.player.toString();
                } else if (this.wumpus.getCoords()[0] == i && this.wumpus.getCoords()[1] == j) {
                    output += this.wumpus.toString();
                } else {
                    output += this.cave[i][j].toString();
                }
            }
            output += "\n";
        }
        return output;
    }

    /**
     * The main method, purely used for testing that this class functions properly.
     * @param args - command line arguments, never used
     */
    public static void main(String[] args) {
        Player test = new Player("Finnegan");
        Cave cave = new Cave(10, 10, 0, 0, 0, 0, test);
        System.out.println(cave);
    }
}
