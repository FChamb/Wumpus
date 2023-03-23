public class Cave {
    int size;
    int numOfPits;
    int numOfSupBats;
    private final Room[][] cave;
    private final Player player;

    public Cave(int size, int numOfPits, int numOfSupBats, Player player) {
        this.size = size;
        this.numOfPits = numOfPits;
        this.numOfSupBats = numOfSupBats;
        this.cave = new Room[this.size][this.size];
        this.player = player;
        generateCave();
    }

    public void generateCave() {
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                this.cave[i][j] = new Room();
            }
        }
        generateObstacles();
    }

    public void generateObstacles() {
        int numOfObs = this.numOfPits + this.numOfSupBats + 3;
        int numPits = 0;
        int numBats = 0;
        boolean treasure = true;
        boolean wumpus = true;
        boolean player = true;
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                int choice = (int) (Math.random() * 5);
                switch (choice) {
                    case 0:
                        if (numPits < this.numOfPits) {
                            this.cave[i][j].setAttribute(1);
                            numPits++;
                            break;
                        }
                    case 1:
                        if (numBats < this.numOfSupBats) {
                            this.cave[i][j].setAttribute(2);
                            numBats++;
                            break;
                        }
                    case 2:
                        if (treasure) {
                            this.cave[i][j].setAttribute(3);
                            treasure = false;
                            break;
                        }
                    case 3:
                        if (wumpus) {
                            this.cave[i][j].setAttribute(4);
                            wumpus = false;
                            break;
                        }
                    case 4:
                        if (player) {
                            this.cave[i][j].setPlayerInRoom(true);
                            this.player.setCoords(i, j);
                            player = false;
                            break;
                        }
                }
            }
        }
        if (numPits != this.numOfPits || numBats != this.numOfSupBats || treasure || wumpus || player) {
            clear();
            generateObstacles();
        }
    }

    public boolean validPath() {
        int x = this.player.getCoords()[0];
        int y = this.player.getCoords()[1];
        boolean valid = false;
        while (!valid) {

        }
        return false;
    }

    public void clear() {
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                this.cave[i][j] = new Room();
            }
        }
    }

    public String toString() {
        String output = "       Hunt the Wumpus \n";
        for (Room[] row : this.cave) {
            for (Room col : row) {
                if (col.getPlayerInRoom()) {
                    output += this.player.toString();
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
