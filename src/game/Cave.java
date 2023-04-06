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
    private ArrayList<String> path;

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
            }
        }
        this.path = createPath();
        generateObstacles();
    }

    public ArrayList<String> createPath() {
        int[] start = getRandom();
        int length = this.xSize + this.ySize;
        ArrayList<String> path = new ArrayList<>();
        path.add(start[0] + "x" + start[1]);
        int lastRoom = -1;
        for (int i = 0; i < length; i++) {
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
                    path.add(x + "x" + y);
                    start = new int[]{x, y};
                    break;
                case 1:
                    lastRoom = 0;
                    if (x == this.xSize - 1) {
                        x = 0;
                    } else {
                        x++;
                    }
                    path.add(x + "x" + y);
                    start = new int[]{x, y};
                    break;
                case 2:
                    lastRoom = 3;
                    if (y == 0) {
                        y = this.ySize - 1;
                    } else {
                        y--;
                    }
                    path.add(x + "x" + y);
                    start = new int[]{x, y};
                    break;
                case 3:
                    lastRoom = 2;
                    if (y == this.ySize - 1) {
                        y = 0;
                    } else {
                        y++;
                    }
                    path.add(x + "x" + y);
                    start = new int[]{x, y};
                    break;
            }
        }
        return path;
    }

    public void generateObstacles() {
        int numPits = 0;
        int numBats = 0;
        boolean wumpus = true;
        int numOfItems = 0;
        int numWalls = 0;
        int total = this.numOfPits + this.numOfSupBats + 1 + this.numWalls;
        if (this.numOfArtifacts > 0) {
            total += this.numOfArtifacts;
        }
        int x = Integer.parseInt(this.path.get(0).split("x")[0]);
        int y = Integer.parseInt(this.path.get(0).split("x")[1]);
        this.cave[x][y].setPlayerInRoom(true);
        this.player.setCoords(x, y);
        x = Integer.parseInt(this.path.get(this.path.size() / 2).split("x")[0]);
        y = Integer.parseInt(this.path.get(this.path.size() / 2).split("x")[1]);
        this.cave[x][y] = new Room("G");
        x = Integer.parseInt(this.path.get(this.path.size() - 1).split("x")[0]);
        y = Integer.parseInt(this.path.get(this.path.size() - 1).split("x")[1]);
        this.cave[x][y] = new Room( "X");
        while (total > 0) {
            int[] location = getRandom();
            x = location[0]; y = location[1];
            int choice;
            if (this.numOfArtifacts > 0) {
                choice = (int) (Math.random() * 5);
            } else {
                choice = (int) (Math.random() * 4);
            }
            switch (choice) {
                case 0:
                    if (numPits < this.numOfPits) {
                        this.cave[x][y] = new Room("o");
                        numPits++;
                        total--;
                        break;
                    }
                case 1:
                    if (numBats < this.numOfSupBats) {
                        this.cave[x][y] = new Room("w");
                        numBats++;
                        total--;
                        break;
                    }
                case 2:
                    if (wumpus) {
                        this.cave[x][y].setWumpusInRoom(true);
                        this.wumpus.setCoords(x, y);
                        wumpus = false;
                        total--;
                        break;
                    }
                case 3:
                    if (numOfItems < this.numOfArtifacts) {
                        this.cave[x][y].setArtifact(Artifact.getRandom());
                        numOfItems++;
                        total--;
                        break;
                    }
                case 4:
                    if (numWalls < this.numWalls) {
                        this.cave[x][y] = new Room("#"); 
                        numWalls++;
                        total--;
                        break;
                    }
            }
        }
    }

    public int[] getRandom() {
        int x = (int) (Math.random() * this.xSize);
        int y = (int) (Math.random() * this.ySize);
        if (this.path != null && this.path.contains(x + "x" + y)) {
            return getRandom();
        } else if (this.cave[x][y].getType().matches("-|o|w") || this.cave[x][y].getArtifact() != null || this.cave[x][y].getWumpusInRoom()) {
            return getRandom();
        } else {
            return new int[]{x, y};
        }
    }

    public String toString() {
        String output = "\n";
        for (Room[] row : this.cave) {
            for (Room col : row) {
                if (col.getPlayerInRoom()) {
                    output += this.player.toString();
                } else if (col.getWumpusInRoom()) {
                    output += this.wumpus.toString();
                } else {
                    output += col.toString();
                }
            }
            output += "\n";
        }
        return output;
    }

    public static void main(String[] args) {
        Player test = new Player("Finnegan");
        Cave cave = new Cave(10, 5, 8, 2, 15, 3, test);
        System.out.println(cave);
    }
}
