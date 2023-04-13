package game;

public class Room {

    public static final char BSC = '.';
    public static final char WLL = ' ';
    public static final char PIT = 'o';
    public static final char BAT = 'w';
    public static final char TSR = 'G';
    public static final char EXT = 'X';


    /**
     * A room can be one of four different types.
     * "." basic room with no special properties
     * "o" pit room - immediate death to player
     * "w" super bat room - carries the player to a random location
     * "G" treasure room - pt 1 of winning
     * "X" exit - pt 2 of winning
     * " " wall
     */
    private final String Type;
    private Artifact artifact = null;

    public Room() {
        this.Type = ""+BSC;
    }
    public Room(char Type) {
        this.Type = ""+Type;
    }
    public Room(String Type) {
        this.Type = Type;
    }

    public Room(String Type, Artifact artifact) {
        this(Type);
        this.artifact = artifact;
    }

    public String getType() {
        return this.Type;
    }

    public Artifact getArtifact() {
        return this.artifact;
    }

    public void setArtifact(Artifact artifact) {
        this.artifact = artifact;
    }

    public String toString() {
        if (artifact == null) {
            return " " + this.Type + " ";
        } else {
            return this.artifact + "";
        }
    }
}
