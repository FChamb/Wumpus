import java.util.ArrayList;

public class Cave {
    int size;
    int numOfPits;
    int numOfSupBats;
    int numOfArtifacts = 0;
    private final Room[][] cave;
    private final Player player;
    private final Wumpus wumpus;
    private ArrayList<int[]> path;

    public Cave(int size, int numOfPits, int numOfSupBats, Player player) {
        this.size = size;
        this.numOfPits = numOfPits;
        this.numOfSupBats = numOfSupBats;
        this.cave = new Room[this.size][this.size];
        this.player = player;
        this.wumpus = new Wumpus();
        generateCave();
    }

    public Cave(int size, int numOfPits, int numOfSupBats, int numOfArtifacts, Player player) {
        this.size = size;
        this.numOfPits = numOfPits;
        this.numOfSupBats = numOfSupBats;
        this.numOfArtifacts = numOfArtifacts;
        this.cave = new Room[this.size][this.size];
        this.player = player;
        this.wumpus = new Wumpus();
        generateCave();
    }

    // getter for the layout
    public Room[][] getLayout(){
        return cave;
    }
    // getter for the player
    public Player getPlayer(){
        return player;
    }
    // getter for the wumpus
    public Wumpus getWumpus(){
        return wumpus;
    }

    public void generateCave() {
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                this.cave[i][j] = new Room();
            }
        }
        this.path = createPath();
        generateObstacles();
    }

    public ArrayList<int[]> createPath() {
        int[] start = getRandom();
        int length = this.size * 2;
        ArrayList<int[]> path = new ArrayList<>();
        path.add(start);
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
                        x = this.size - 1;
                    } else {
                        x--;
                    }
                    path.add(new int[]{x, y});
                    start = new int[]{x, y};
                    break;
                case 1:
                    lastRoom = 0;
                    if (x == this.size - 1) {
                        x = 0;
                    } else {
                        x++;
                    }
                    path.add(new int[]{x, y});
                    start = new int[]{x, y};
                    break;
                case 2:
                    lastRoom = 3;
                    if (y == 0) {
                        y = this.size - 1;
                    } else {
                        y--;
                    }
                    path.add(new int[]{x, y});
                    start = new int[]{x, y};
                    break;
                case 3:
                    lastRoom = 2;
                    if (y == this.size - 1) {
                        y = 0;
                    } else {
                        y++;
                    }
                    path.add(new int[]{x, y});
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
        int total = this.numOfPits + this.numOfSupBats + 1;
        if (this.numOfArtifacts > 0) {
            total += this.numOfArtifacts;
        }
        int x = this.path.get(0)[0];
        int y = this.path.get(0)[1];
        this.cave[x][y].setPlayerInRoom(true);
        this.player.setCoords(x, y);
        x = this.path.get(this.path.size() / 2)[0];
        y = this.path.get(this.path.size() / 2)[1];
        this.cave[x][y] = new Room("G");
        x = this.path.get(this.path.size() - 1)[0];
        y = this.path.get(this.path.size() - 1)[1];
        this.cave[x][y] = new Room( "X");
        while (total > 0) {
            int[] location = getRandom();
            checkRandom(location);
            x = location[0]; y = location[1];
            int choice;
            if (this.numOfArtifacts > 0) {
                choice = (int) (Math.random() * 4);
            } else {
                choice = (int) (Math.random() * 3);
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
                        this.cave[x][y].setArtifact(new Artifact("Shield", "one hit protection"));
                        numOfItems++;
                        total--;
                        break;
                    }
            }
        }
    }

    public int[] getRandom() {
        int x = (int) (Math.random() * this.size);
        int y = (int) (Math.random() * this.size);
        if (this.cave[x][y].getType().equals(".")) {
            return new int[]{x, y};
        } else {
            return getRandom();
        }
    }

    public void checkRandom(int[] location) {
        for (int[] coord : this.path) {
            if (coord[0] == location[0] && coord[1] == location[1]) {
                location = getRandom();
                checkRandom(location);
            }
        }
    }

    public String toString() {
        String output = "";
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
        Cave cave = new Cave(10, 8, 2, 1, test);
        System.out.println(cave);
    }
}
