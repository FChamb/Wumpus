import java.util.ArrayList;
import java.util.Arrays;

public class Cave {
    int size;
    int numOfPits;
    int numOfSupBats;
    private final Room[][] cave;
    private final Player player;
    private final Wumpus wumpus;

    public Cave(int size, int numOfPits, int numOfSupBats, Player player) {
        this.size = size;
        this.numOfPits = numOfPits;
        this.numOfSupBats = numOfSupBats;
        this.cave = new Room[this.size][this.size];
        this.player = player;
        this.wumpus = new Wumpus();
        generateCave();
    }

    public void generateCave() {
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                this.cave[i][j] = new Room();
            }
        }
        ArrayList<int[]> path = createPath();
        generateObstacles(path);
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

    public void generateObstacles(ArrayList<int[]> path) {
        int numPits = 0;
        int numBats = 0;
        boolean wumpus = true;
        int x = path.get(0)[0];
        int y = path.get(0)[1];
        this.cave[x][y].setPlayerInRoom(true);
        x = path.get(path.size() / 2)[0];
        y = path.get(path.size() / 2)[1];
        this.cave[x][y].setType("G");
        x = path.get(path.size() - 1)[0];
        y = path.get(path.size() - 1)[1];
        this.cave[x][y].setType("X");
        this.player.setCoords(x, y);
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                for (int[] coord : path) {
                    if (coord[0] == i && coord[1] == j) {
                        continue;
                    }
                }
                int choice = (int) (Math.random() * 3);
                switch (choice) {
                    case 0:
                        if (numPits < this.numOfPits) {
                            this.cave[i][j].setType("o");
                            numPits++;
                            break;
                        }
                    case 1:
                        if (numBats < this.numOfSupBats) {
                            this.cave[i][j].setType("w");
                            numBats++;
                            break;
                        }
                    case 2:
                        if (wumpus) {
                            this.cave[i][j].setWumpusInRoom(true);
                            this.wumpus.setCoords(i, j);
                            wumpus = false;
                            break;
                        }
                }
            }
        }
        if (numPits != this.numOfPits || numBats != this.numOfSupBats || wumpus) {
            //System.out.println("redo");
            clear();
            generateObstacles(path);
        }
    }

    public int[] getRandom() {
        int x = (int) (Math.random() * this.size);
        int y = (int) (Math.random() * this.size);
        if (!this.cave[x][y].getType().equals(".")) {
            getRandom();
        }
        return new int[]{x, y};
    }

    public void clear() {
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                this.cave[i][j] = new Room();
            }
        }
    }

    public String toString() {
        String output = "Hunt the Wumpus \n";
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
        Cave cave = new Cave(5, 8, 2, test);
        System.out.println(cave);
    }
}
