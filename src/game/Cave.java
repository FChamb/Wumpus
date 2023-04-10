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

    public Cave(int xSize, int ySize, int numOfPits, int numOfSupBats, int numWalls, int numOfArtifacts, Player player) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.numOfPits = numOfPits;
        this.numOfSupBats = numOfSupBats;
        this.numWalls = numWalls;
        this.cave = new Room[this.xSize][this.ySize];
        this.player = player;
        this.wumpus = new Wumpus();
        this.numOfArtifacts = numOfArtifacts;
        if (numOfPits + numOfSupBats + numWalls + numOfArtifacts + xSize + ySize > xSize * ySize) {
            throw new ArrayIndexOutOfBoundsException("Too many objects defined for the size of cave!");
        }
        this.validRooms = new ArrayList<>();
        generateCave();
    }

    // getter for the layout
    public Room[][] getLayout(){
        return this.cave;
    }
    // getter for the player
    public Player getPlayer(){
        return this.player;
    }
    // getter for the wumpus
    public Wumpus getWumpus(){
        return this.wumpus;
    }

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

    public void createPath() {
        ArrayList<String> path = new ArrayList<>();
        int[] start = getRandom();
        this.validRooms.remove(start[0] + "x" + start[1]);
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
            if (path.contains(x + "x" + y)) {
                i--;
                continue;
            }
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
        System.out.println(x + " " + y);
    }

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
                        break;
                    }
                case 1:
                    if (numBats < this.numOfSupBats) {
                        this.cave[x][y] = new Room("w");
                        numBats++;
                        total--;
                        this.validRooms.remove(x + "x" + y);
                        break;
                    }
                case 2:
                    if (wumpus) {
                        this.wumpus.setCoords(x, y);
                        wumpus = false;
                        total--;
                        this.validRooms.remove(x + "x" + y);
                        break;
                    }
                case 3:
                    if (numOfItems < this.numOfArtifacts) {
                        this.cave[x][y].setArtifact(Artifact.getRandom());
                        numOfItems++;
                        total--;
                        this.validRooms.remove(x + "x" + y);
                        break;
                    }
                case 4:
                    if (numWalls < this.numWalls) {
                        this.cave[x][y] = new Room(" ");
                        numWalls++;
                        total--;
                        this.validRooms.remove(x + "x" + y);
                        break;
                    }
            }
        }
    }

    public int[] getRandom() {
        int i = (int) (Math.random() * this.validRooms.size());
        int x = Integer.parseInt(this.validRooms.get(i).split("x")[0]);
        int y = Integer.parseInt(this.validRooms.get(i).split("x")[1]);
        return new int[]{x, y};
    }

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

    public static void main(String[] args) {
        Player test = new Player("Finnegan");
        Cave cave = new Cave(10, 10, 15, 5, 32, 5, test);
        System.out.println(cave);
    }
}
