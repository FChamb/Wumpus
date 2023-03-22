import java.util.Arrays;

public class Cave {
    int size;
    int numOfPits;
    int numOfSupBats;
    private final Room[][] cave;

    public Cave(int size, int numOfPits, int numOfSupBats) {
        this.size = size;
        this.numOfPits = numOfPits;
        this.numOfSupBats = numOfSupBats;
        this.cave = new Room[this.size][this.size];
        generateCave();
    }

    public void generateCave() {
        //Arrays.stream(cave).forEach(room -> Arrays.fill(room, new Room(room, room.length)));
    }
}
